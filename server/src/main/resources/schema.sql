DROP TABLE IF EXISTS
users,
requests,
items,
bookings,
comments;

CREATE TABLE IF NOT EXISTS users
(
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(128)    NOT NULL,
    email           VARCHAR(128)    UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description     VARCHAR(128)    NOT NULL,
    created         TIMESTAMP       NOT NULL,
    requester_id    BIGINT          REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items
(
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(128)    NOT NULL,
    description     VARCHAR(128)    NOT NULL,
    available       BOOLEAN         DEFAULT TRUE NOT NULL,
    owner_id        BIGINT          REFERENCES users(id) ON DELETE CASCADE,
    request_id      BIGINT          REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date      TIMESTAMP       NOT NULL,
    end_date        TIMESTAMP       NOT NULL,
    item_id         BIGINT          REFERENCES items(id) ON DELETE CASCADE,
    booker_id       BIGINT          REFERENCES users(id) ON DELETE CASCADE,
    status          VARCHAR(24)     NOT NULL
    CHECK (status IN (
                     'WAITING',
                     'APPROVED',
                     'REJECTED',
                     'CANCELED'
                     ))
);

CREATE TABLE IF NOT EXISTS comments
(
    id              BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text            VARCHAR(128)    NOT NULL,
    item_id         BIGINT          REFERENCES items(id) ON DELETE CASCADE,
    author_id       BIGINT          REFERENCES users(id) ON DELETE CASCADE,
    created         TIMESTAMP       NOT NULL
);