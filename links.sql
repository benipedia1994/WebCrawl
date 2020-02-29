create table if NOT EXISTS LINKS (
id bigint auto_increment,
link varchar(2083),
title varchar(2083),
links varchar(MAX),
body varchar(MAX),
UNIQUE (link)
);

delete FROM LINKS;