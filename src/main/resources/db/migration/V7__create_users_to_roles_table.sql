CREATE TABLE user_to_sec_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (role_id) REFERENCES sec_role(id)
);
CREATE INDEX idx_user_id ON user_to_sec_role(user_id);
CREATE INDEX idx_role_id ON user_to_sec_role(role_id);
