CREATE DATABASE GrabberDB;

CREATE TABLE posts (
	id serial primary key,
	name varchar(1000),
	text text,
	link varchar(1000) unique,
	created timestamp
);