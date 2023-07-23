CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS item_requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
  description VARCHAR(1000) NOT NULL,
  requestor_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created TIMESTAMP WITHOUT TIME ZONE default NOW(),
  CONSTRAINT pk_item_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(1000) NOT NULL,
  is_available BOOLEAN DEFAULT 0,
  owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  request_id BIGINT
);

CREATE TYPE IF NOT EXISTS booking_status as ENUM('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
  booker_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  status booking_status NOT NULL DEFAULT 'WAITING'
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
  text VARCHAR(1000) NOT NULL,
  item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
  author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP()
);