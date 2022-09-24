create table person
(
    id        bigserial primary key,
    full_name varchar(255),
    title     varchar(255),
    age       int
);

create table book
(
    id bigserial primary key,
    title     varchar(255),
    author varchar(255),
    page_count int,
    person_id bigint references person (id)
);