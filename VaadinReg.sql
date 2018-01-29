-- Database: "VaadinReg"

-- DROP DATABASE "VaadinReg";

CREATE DATABASE "VaadinReg"
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'Russian_Russia.1251'
       LC_CTYPE = 'Russian_Russia.1251'
       CONNECTION LIMIT = -1;

CREATE TABLE Users(
	id INT PRIMARY KEY NOT NULL,
	username VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL,
	email VARCHAR(255) NOT NULL,
	avatar bytea
);

CREATE SEQUENCE hibernate_sequence START 1;

CREATE SEQUENCE my_seq_gen START 1;

CREATE TABLE Roles(
	id INT PRIMARY KEY NOT NULL,
	name VARCHAR(255) NOT NULL
);

CREATE TABLE User_Roles(
	user_id INT REFERENCES Users(id),
	role_id INT REFERENCES Roles(id)
);

INSERT INTO Roles VALUES(1, 'ROLE_ADMIN');
INSERT INTO Roles VALUES(2, 'ROLE_USER');

