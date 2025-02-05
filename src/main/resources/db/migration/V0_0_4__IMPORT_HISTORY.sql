create type ImportStatus as enum ('SUCCESS', 'FAILED');

create table Import_History
(
    id                      bigserial        primary key,
    status                  ImportStatus,
    user_id                 bigint,
    added_count             bigint,
    foreign key (user_id) REFERENCES Users (id) ON DELETE cascade ON UPDATE cascade
);