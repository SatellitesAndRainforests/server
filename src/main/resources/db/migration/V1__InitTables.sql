
CREATE TABLE captures (
    id SERIAL PRIMARY KEY,
    epoch_time VARCHAR(255),
    species VARCHAR(255),
    id_status VARCHAR(255) NOT NULL,
    notes VARCHAR(255),
    moon_phase VARCHAR(255),
    temperature REAL,
    humidity REAL,
    longitude DOUBLE PRECISION NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    geolocation GEOGRAPHY(point) -- optional 2nd param default is SRID 4326, WGS 84
);

CREATE TABLE images (
    id SERIAL PRIMARY KEY,
    capture_id INT NOT NULL,
    fileURL VARCHAR(255) NOT NULL,
    FOREIGN KEY (capture_id) REFERENCES captures ON DELETE CASCADE
);

CREATE TABLE users (
   id SERIAL PRIMARY KEY,
   name VARCHAR(33),
   user_name VARCHAR(33) NOT NULL,
   password VARCHAR(33) NOT NULL,
   role VARCHAR(33) NOT NULL
);
