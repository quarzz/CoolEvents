CREATE TABLE "Access"
(
    id INTEGER PRIMARY KEY NOT NULL,
    user_id INTEGER,
    event_id INTEGER,
    access INTEGER,
    CONSTRAINT "Access_user_id_fkey" FOREIGN KEY (user_id) REFERENCES "Users" (id),
    CONSTRAINT "Access_event_id_fkey" FOREIGN KEY (event_id) REFERENCES "Events" (id)
);
CREATE TABLE "Events"
(
    id INTEGER PRIMARY KEY NOT NULL,
    title VARCHAR(50),
    description TEXT,
    date TIMESTAMP
);
CREATE TABLE pins
(
    id INTEGER PRIMARY KEY NOT NULL,
    token VARCHAR(64) NOT NULL,
    pin INTEGER NOT NULL,
    CONSTRAINT pins_tokens_token_fk FOREIGN KEY (token) REFERENCES tokens (token)
);
CREATE UNIQUE INDEX table_name_token_uindex ON pins (token);
CREATE TABLE tokens
(
    id INTEGER PRIMARY KEY NOT NULL,
    token VARCHAR(64) NOT NULL,
    user_id INTEGER NOT NULL,
    timestamp TIMESTAMP DEFAULT now(),
    stage INTEGER NOT NULL,
    CONSTRAINT tokens_users_id_fk FOREIGN KEY (user_id) REFERENCES "Users" (id)
);
CREATE UNIQUE INDEX "Tokens_token_uindex" ON tokens (token);
CREATE UNIQUE INDEX unique_token ON tokens (token);
CREATE INDEX tokens_token_index ON tokens (token);
CREATE TABLE "Users"
(
    id INTEGER PRIMARY KEY NOT NULL,
    "firstName" VARCHAR(50),
    "lastName" VARCHAR(50),
    login VARCHAR(50) NOT NULL,
    password VARCHAR(256)
);
CREATE UNIQUE INDEX "Users_login_uindex" ON "Users" (login);