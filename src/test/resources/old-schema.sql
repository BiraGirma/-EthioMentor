-- Schema as created by the ORIGINAL (pre-fix) DBInit, plus sample rows, used by MigrationTest.
-- It has no users.is_active, and messages has no type/edited/conversation_id columns.
CREATE TABLE users (
  id SERIAL PRIMARY KEY, username VARCHAR(50) UNIQUE NOT NULL, full_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL, password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL CHECK (role IN ('student','mentor','admin')),
  university VARCHAR(100), mentor_status VARCHAR(20) DEFAULT 'PENDING', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE mentors (id SERIAL PRIMARY KEY, user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE, expertise VARCHAR(255));
CREATE TABLE study_groups (id SERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL, description TEXT,
  created_by INT REFERENCES users(id), members_count INT DEFAULT 0);
CREATE TABLE study_group_members (group_id INT REFERENCES study_groups(id) ON DELETE CASCADE,
  user_id INT REFERENCES users(id) ON DELETE CASCADE, PRIMARY KEY (group_id, user_id));
CREATE TABLE conversations (id SERIAL PRIMARY KEY, type VARCHAR(20) NOT NULL CHECK (type IN ('PRIVATE','GROUP')),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE conversation_participants (conversation_id INT REFERENCES conversations(id) ON DELETE CASCADE,
  user_id INT REFERENCES users(id) ON DELETE CASCADE, joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (conversation_id, user_id));
CREATE TABLE messages (id SERIAL PRIMARY KEY, sender_id INT REFERENCES users(id) ON DELETE CASCADE,
  recipient_id INT REFERENCES users(id) ON DELETE CASCADE, content TEXT NOT NULL, timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

-- sample data: legacy SHA-256 hashes (admin123 and student123)
INSERT INTO users (username, full_name, email, password, role, university) VALUES
 ('admin','Admin User','admin@ethiomentor.com','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','admin','Addis Ababa University'),
 ('oldstudent','Old Student','old@student.et','2f2c1b0c3f5ab1ef5bf0d1b3ac0f4cf8f6c0b3a5ebd7a1f9dc6ce2d0c0b8a4d1','student','Jimma University');
UPDATE users SET password = encode(sha256('student123'::bytea),'hex') WHERE username='oldstudent';
INSERT INTO study_groups (name, description, created_by, members_count) VALUES ('Legacy Group','from v1', 2, 0);
INSERT INTO study_group_members (group_id, user_id) VALUES (1,1),(1,2);
INSERT INTO messages (sender_id, recipient_id, content) VALUES (2,1,'hello from the old schema');
