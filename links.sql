create table if NOT EXISTS LINKS (
id bigint auto_increment,
link varchar(2083),
content varchar(MAX)
);

delete FROM LINKS;