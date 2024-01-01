CREATE TABLE Person(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username   VARCHAR(64),
    info       VARCHAR(64),
    password   VARCHAR(64),
    roles      TEXT[],
    first_name VARCHAR(64),
    last_name  VARCHAR(64),
    enabled    BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE Note(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(50),
    txt VARCHAR(50),
    person_id BIGINT REFERENCES Person(id)    
);