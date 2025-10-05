DROP TABLE IF EXISTS netflix;

CREATE TABLE netflix (
    show_id VARCHAR(10) NOT NULL PRIMARY KEY,
    type VARCHAR(30),
    title VARCHAR(150),
    directors VARCHAR(500),
    cast_members VARCHAR(1000),
    country VARCHAR(200),
    date_added DATE,
    release_year INTEGER,
    rating VARCHAR(20),
    duration VARCHAR(20),
    listed_in VARCHAR(500),
    description TEXT
);