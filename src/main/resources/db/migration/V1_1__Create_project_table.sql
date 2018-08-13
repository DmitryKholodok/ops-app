create table if not exists `Project` (
    `id` int unsigned not null auto_increment,
    `name` varchar(255) not null,
    `alternative_name` varchar(255),
    `people_count` int unsigned not null,

    primary key (`id`)
);