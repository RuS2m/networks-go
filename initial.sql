CREATE TABLE users (
    username VARCHAR(16) PRIMARY KEY,
    password TEXT,
    role TEXT,
    additional_info TEXT
);

CREATE TABLE games (
    session_id BIGINT PRIMARY KEY,
    game_history TEXT,
    step BIGINT NOT NULL,
    state TEXT NOT NULL
);

CREATE TABLE lobbies (
    lobby_id BIGINT PRIMARY KEY,
    lobby_name TEXT NOT NULL,
    current_session_id BIGINT,
    state TEXT NOT NULL
);

CREATE TABLE participations (
    username VARCHAR(16) REFERENCES users(username),
    lobby_id BIGINT REFERENCES lobbies(lobby_id),
    session_id BIGINT,
    state TEXT NOT NULL,
    PRIMARY KEY(username, lobby_id)
);