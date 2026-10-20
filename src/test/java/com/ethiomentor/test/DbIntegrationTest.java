package com.ethiomentor.test;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.config.DBInit;
import com.ethiomentor.dao.*;
import com.ethiomentor.model.*;
import com.ethiomentor.service.*;
import com.ethiomentor.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * End-to-end tests of the data layer and services against a REAL PostgreSQL.
 * Run with DB_* variables pointing at a scratch database and
 * ADMIN_INITIAL_PASSWORD=Adm1n-Test-Pass. DBInit creates the database if missing.
 */
public class DbIntegrationTest {

    static final String ADMIN_PW = "Adm1n-Test-Pass";
    static final String tag = Long.toString(System.currentTimeMillis() % 1_000_000);

    static final UserService users = new UserService();
    static final UserDAO userDAO = new UserDAO();
    static final AdminService admin = new AdminService();
    static final StudyGroupDAO groups = new StudyGroupDAO();
    static final ChatService chat = new ChatService();
    static final MentorService mentorService = new MentorService();
    static final StatsDAO stats = new StatsDAO();

    static User student, mentor, other;
    static int groupId;

    static User register(String name, String role, String expertise) {
        User u = new User();
        u.setUsername(name + tag);
        u.setFullName("Test " + name);
        u.setEmail(name + tag + "@test.edu.et");
        u.setPassword("Passw0rd-" + name);
        u.setRole(role);
        u.setUniversity("Addis Ababa University");
        String err = users.registerUser(u, expertise);
        Check.eq(null, err, "register " + name);
        Check.isTrue(u.getId() > 0, "generated id set");
        return u;
    }

    public static void main(String[] args) {
        System.out.println("DbIntegrationTest against " + DBConfig.url() + " as " + DBConfig.user());

        Check.run("DBInit creates database + schema, and is idempotent", () -> {
            DBInit.init();
            DBInit.init();
        });

        Check.run("seeded admin can log in with ADMIN_INITIAL_PASSWORD; hash is PBKDF2; no hash returned", () -> {
            User a = userDAO.authenticate("admin", ADMIN_PW);
            Check.isTrue(a != null, "admin login");
            Check.eq("admin", a.getRole(), "role");
            Check.eq(null, a.getPassword(), "password hash not exposed");
            Check.isTrue(userDAO.authenticate("admin@ethiomentor.com", ADMIN_PW) != null, "login by email");
            Check.eq(null, userDAO.authenticate("admin", "admin123"), "old default password rejected");
            Check.isTrue(PasswordUtil.verify(ADMIN_PW, userDAO.findByUsername("admin").getPassword()), "stored hash verifies");
            Check.isTrue(userDAO.findByUsername("admin").getPassword().startsWith("pbkdf2_sha256$"), "pbkdf2");
        });

        Check.run("registration: student, mentor, role escalation impossible, validation, duplicates", () -> {
            student = register("stud", "MENTEE", null);
            mentor = register("ment", "MENTOR", "Java , SQL,, ");
            other = register("othr", "ADMIN", null);               // client asks for admin
            Check.eq("student", student.getRole(), "student role");
            Check.eq("mentor", mentor.getRole(), "mentor role");
            Check.eq("student", other.getRole(), "ADMIN request becomes student");
            Check.eq("PENDING", userDAO.findById(mentor.getId()).getMentorStatus(), "mentor pending");

            User dup = new User();
            dup.setUsername("someoneelse" + tag); dup.setFullName("Dup"); dup.setEmail(student.getEmail());
            dup.setPassword("Passw0rd-dup"); dup.setRole("MENTEE"); dup.setUniversity("AAU");
            Check.isTrue(users.registerUser(dup, null) != null, "duplicate email rejected");

            User bad = new User();
            bad.setUsername("x"); bad.setFullName("B"); bad.setEmail("nope"); bad.setPassword("1"); bad.setUniversity("");
            Check.isTrue(users.registerUser(bad, null) != null, "invalid input rejected");
        });

        Check.run("login: right/wrong password, by email and username", () -> {
            Check.isTrue(userDAO.authenticate("stud" + tag, "Passw0rd-stud") != null, "by username");
            Check.isTrue(userDAO.authenticate(student.getEmail(), "Passw0rd-stud") != null, "by email");
            Check.eq(null, userDAO.authenticate("stud" + tag, "wrong"), "wrong password");
            Check.eq(null, userDAO.authenticate("nobody" + tag, "x"), "unknown user");
            Check.eq(null, userDAO.authenticate("stud" + tag, null), "null password");
        });

        Check.run("REGRESSION profile edit no longer locks the user out (double-hash bug)", () -> {
            User session = userDAO.authenticate("stud" + tag, "Passw0rd-stud");
            Check.eq(null, users.updateProfile(session, "Renamed Student", null, null, null), "update w/o password");
            Check.isTrue(userDAO.authenticate("stud" + tag, "Passw0rd-stud") != null, "still logs in with same password");
            Check.eq("Renamed Student", userDAO.findById(student.getId()).getFullName(), "name saved");
            Check.eq(null, users.updateProfile(session, null, null, null, "N3w-Passw0rd"), "change password");
            Check.eq(null, userDAO.authenticate("stud" + tag, "Passw0rd-stud"), "old password rejected");
            Check.isTrue(userDAO.authenticate("stud" + tag, "N3w-Passw0rd") != null, "new password works");
            Check.isTrue(users.updateProfile(session, null, mentor.getEmail(), null, null) != null, "email collision rejected");
            Check.isTrue(users.updateProfile(session, null, null, null, "short") != null, "weak password rejected");
            Check.eq("student", userDAO.findById(student.getId()).getRole(), "role untouched");
        });

        Check.run("legacy SHA-256 hash logs in and is upgraded to PBKDF2", () -> {
            try (Connection c = DBConfig.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "INSERT INTO users (username, full_name, email, password, role, university) VALUES (?,?,?,?,?,?)")) {
                ps.setString(1, "legacy" + tag); ps.setString(2, "Legacy User"); ps.setString(3, "legacy" + tag + "@t.et");
                ps.setString(4, PasswordUtil.sha256Hex("oldpass123")); ps.setString(5, "student"); ps.setString(6, "AAU");
                ps.executeUpdate();
            }
            Check.isTrue(userDAO.authenticate("legacy" + tag, "oldpass123") != null, "legacy login");
            Check.isTrue(userDAO.findByUsername("legacy" + tag).getPassword().startsWith("pbkdf2_sha256$"), "upgraded");
            Check.isTrue(userDAO.authenticate("legacy" + tag, "oldpass123") != null, "login after upgrade");
        });

        Check.run("deactivated users cannot log in, vanish from chat list; reactivation restores", () -> {
            admin.deactivateUser(other.getId());
            Check.eq(null, userDAO.authenticate("othr" + tag, "Passw0rd-othr"), "login blocked");
            Check.isFalse(userDAO.isActive(other.getId()), "isActive false");
            boolean listed = userDAO.getAllOtherUsers(student.getId()).stream().anyMatch(u -> u.getId() == other.getId());
            Check.isFalse(listed, "not in contact list");
            admin.activateUser(other.getId());
            Check.isTrue(userDAO.authenticate("othr" + tag, "Passw0rd-othr") != null, "login restored");
        });

        Check.run("admin user list works (is_active column) and exposes the flag", () -> {
            List<User> all = admin.getAllUsers(0);
            Check.isTrue(all.size() >= 4, "lists users");
            Check.isTrue(all.stream().allMatch(User::isActive), "all active here");
        });

        Check.run("mentor matching shows only APPROVED mentors, with expertise", () -> {
            MentorDAO md = new MentorDAO();
            Check.isFalse(md.getAllMentors().stream().anyMatch(m -> m.getId() == mentor.getId()), "pending hidden");
            admin.approveMentor(student.getId());   // wrong role: must be ignored
            Check.eq("PENDING", userDAO.findById(student.getId()).getMentorStatus(), "approve ignores non-mentors");
            Check.isTrue(admin.getPendingMentorRequests().stream().anyMatch(u -> u.getId() == mentor.getId()), "in pending list");
            admin.approveMentor(mentor.getId());
            MentorProfile p = md.getAllMentors().stream().filter(m -> m.getId() == mentor.getId()).findFirst().orElse(null);
            Check.isTrue(p != null, "approved shown");
            Check.eq("Java|SQL", String.join("|", p.getExpertise()), "expertise parsed");
            admin.rejectMentor(mentor.getId());
            Check.isFalse(md.getAllMentors().stream().anyMatch(m -> m.getId() == mentor.getId()), "rejected hidden");
            admin.approveMentor(mentor.getId());
        });

        Check.run("study groups: create, join, no double-count, unknown group, flags, counts", () -> {
            groupId = groups.createGroup("Java Masters", "Backend study", mentor.getId());
            Check.eq(1, groups.getMembersCount(groupId), "creator counted");
            Check.isTrue(groups.joinGroup(groupId, student.getId()), "join");
            Check.eq(2, groups.getMembersCount(groupId), "REGRESSION: members_count now increments");
            Check.isTrue(groups.joinGroup(groupId, student.getId()), "join again is harmless");
            Check.eq(2, groups.getMembersCount(groupId), "no double count");
            Check.isFalse(groups.joinGroup(99999999, student.getId()), "unknown group -> false");
            Check.isTrue(groups.getJoinedGroups(student.getId()).stream().anyMatch(g -> g.getId() == groupId && g.isMember() && g.getMembersCount() == 2), "joined list");
            Check.isTrue(groups.getCreatedGroups(mentor.getId()).stream().anyMatch(g -> g.getId() == groupId), "created list");
            StudyGroup seen = groups.getAllGroups(other.getId()).stream().filter(g -> g.getId() == groupId).findFirst().get();
            Check.isFalse(seen.isMember(), "non-member flag");
            Check.isTrue(mentorService.getMentorStudyGroups(mentor.getId()).contains("Java Masters"), "mentor sees own group");
            Check.isTrue(admin.getAllGroups().stream().anyMatch(g -> g.getId() == groupId && g.getMembersCount() == 2), "admin list count");
        });

        int[] convId = new int[1];
        Check.run("chat: private conversation reuse, participant checks, save + load, status rows", () -> {
            convId[0] = chat.getOrCreatePrivateConversation(student.getId(), mentor.getId());
            Check.eq(convId[0], chat.getOrCreatePrivateConversation(mentor.getId(), student.getId()), "same conversation both ways");
            Check.isTrue(chat.isParticipant(convId[0], student.getId()), "student participant");
            Check.isTrue(chat.isParticipant(convId[0], mentor.getId()), "mentor participant");
            Check.isFalse(chat.isParticipant(convId[0], other.getId()), "outsider is not a participant");

            ChatMessage m = new ChatMessage();
            m.setConversationId(convId[0]); m.setSenderId(student.getId()); m.setRecipientId(mentor.getId());
            m.setContent("Selam! <b>ሰላም</b>"); m.setType("TEXT");
            ChatMessage saved = chat.sendMessage(m);
            Check.isTrue(saved.getId() > 0 && saved.getTimestamp() != null, "saved");
            List<ChatMessage> hist = chat.loadMessages(convId[0], 100, 0);
            Check.eq(1, hist.size(), "history size");
            Check.eq("Selam! <b>ሰላም</b>", hist.get(0).getContent(), "UTF-8 round trip");
            Check.isTrue(admin.getTotalMessages() >= 1, "admin message count");
        });

        Check.run("mentor dashboard data: mentees derived from conversations; stats", () -> {
            List<User> mentees = mentorService.getAssignedMentees(mentor.getId());
            Check.isTrue(mentees.stream().anyMatch(u -> u.getId() == student.getId()), "student is a mentee");
            Check.isFalse(mentees.stream().anyMatch(u -> u.getId() == other.getId()), "no conversation -> not a mentee");
            Check.isTrue(stats.countMessagesFor(mentor.getId()) >= 1, "messages for mentor");
            Check.eq(0, stats.countMessagesFor(other.getId()), "none for outsider");
            Check.isTrue(stats.countGroupsJoinedBy(mentor.getId()) >= 1, "groups joined");
            Check.isTrue(stats.countStudyGroups() >= 1 && stats.countStudents() >= 2 && stats.countApprovedMentors() >= 1
                    && stats.countConversations() >= 1, "student dashboard counters");
        });

        Check.run("deleting a user who created a group keeps the group (created_by NULL) and drops their membership", () -> {
            admin.deleteUser(mentor.getId());
            Check.eq(null, userDAO.findById(mentor.getId()), "mentor gone");
            StudyGroup g = groups.getAllGroups(student.getId()).stream().filter(x -> x.getId() == groupId).findFirst().orElse(null);
            Check.isTrue(g != null, "group survives");
            Check.eq(0, g.getCreatedBy(), "created_by cleared");
            Check.eq(1, g.getMembersCount(), "member count follows cascade (REGRESSION: used to fail on FK)");
            Check.eq(0, chat.loadMessages(convId[0], 100, 0).size() == 0 ? 0 : 1, "conversation messages cascade or remain consistent");
        });

        Check.run("admin group delete", () -> {
            admin.deleteGroup(groupId);
            Check.isFalse(admin.getAllGroups().stream().anyMatch(g -> g.getId() == groupId), "group deleted");
        });

        Check.finish("DbIntegrationTest");
    }
}
