package com.ethiomentor.test;

import com.ethiomentor.config.DBConfig;
import com.ethiomentor.config.DBInit;
import com.ethiomentor.dao.AdminDAO;
import com.ethiomentor.dao.StudyGroupDAO;
import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Starts DBInit against a database created by the OLD schema (see old-schema.sql,
 * loaded by scripts/test.sh) and checks it is upgraded in place without losing data.
 */
public class MigrationTest {

    static int count(String sql) throws Exception {
        try (Connection c = DBConfig.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public static void main(String[] args) {
        System.out.println("MigrationTest against " + DBConfig.url());
        UserDAO dao = new UserDAO();

        Check.run("DBInit upgrades the old schema in place (idempotent)", () -> {
            DBInit.init();
            DBInit.init();
            Check.eq(1, count("SELECT COUNT(*) FROM information_schema.columns WHERE table_name='users' AND column_name='is_active'"), "is_active added");
            Check.eq(1, count("SELECT COUNT(*) FROM information_schema.columns WHERE table_name='messages' AND column_name='conversation_id'"), "conversation_id added");
        });
        Check.run("existing rows are preserved and active", () -> {
            Check.eq(2, count("SELECT COUNT(*) FROM users"), "users untouched (no extra admin seeded)");
            Check.eq(2, count("SELECT COUNT(*) FROM users WHERE is_active"), "old users are active");
            Check.eq(1, count("SELECT COUNT(*) FROM messages WHERE content='hello from the old schema'"), "old message kept");
        });
        Check.run("old admin/admin123 hash still verifies, then is upgraded to PBKDF2 on login", () -> {
            User a = dao.authenticate("admin", "admin123");
            Check.isTrue(a != null, "legacy admin login");
            Check.isTrue(dao.findByUsername("admin").getPassword().startsWith("pbkdf2_sha256$"), "upgraded");
            Check.isTrue(dao.authenticate("oldstudent", "student123") != null, "legacy student login");
        });
        Check.run("admin list (needs is_active) works on the migrated DB", () -> {
            Check.eq(1, new AdminDAO().getAllUsersExcept(1).size(), "lists the other user");
        });
        Check.run("stale members_count=0 is ignored: the real member count (2) is shown", () -> {
            Check.eq(2, new StudyGroupDAO().getAllGroups(1).get(0).getMembersCount(), "computed count");
        });
        Check.finish("MigrationTest");
    }
}
