.open chat.db

create table users(
id integer primary key autoincrement,
username text not null unique,
password text,
status text
);

create table messages(
id integer primary key autoincrement,
sender_id integer,
receiver_id integer,
message text,
date_sent datetime default current_timestamp
);