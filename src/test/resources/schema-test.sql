-- USERS
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT uq_users_email UNIQUE (email)
);

-- ITEMS
CREATE TABLE IF NOT EXISTS items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1000),
  /* ВАЖНО: см. пояснение ниже про колонку доступности */
  is_available BOOLEAN NOT NULL,
  owner_id BIGINT NOT NULL,
  request_id BIGINT
);
CREATE INDEX IF NOT EXISTS idx_items_owner ON items(owner_id);
CREATE INDEX IF NOT EXISTS idx_items_available ON items(is_available);

-- BOOKINGS
CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  start_date TIMESTAMP NOT NULL,
  end_date TIMESTAMP NOT NULL,
  item_id BIGINT NOT NULL,
  booker_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_bookings_item ON bookings(item_id);
CREATE INDEX IF NOT EXISTS idx_bookings_booker ON bookings(booker_id);
CREATE INDEX IF NOT EXISTS idx_bookings_start ON bookings(start_date);
CREATE INDEX IF NOT EXISTS idx_bookings_end ON bookings(end_date);

-- COMMENTS
CREATE TABLE IF NOT EXISTS comments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  text VARCHAR(2000) NOT NULL,
  created TIMESTAMP NOT NULL,
  item_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_comments_item ON comments(item_id);
CREATE INDEX IF NOT EXISTS idx_comments_time ON comments(created);

-- REQUESTS
CREATE TABLE IF NOT EXISTS requests (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  description VARCHAR(2000) NOT NULL,
  requestor_id BIGINT NOT NULL,
  created TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_requests_requestor ON requests(requestor_id);
CREATE INDEX IF NOT EXISTS idx_requests_created ON requests(created);