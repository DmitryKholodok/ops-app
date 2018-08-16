create table if not exists `Operator` (
    `id` int unsigned not null auto_increment,
    `name` varchar(255) not null,
    `is_billable` boolean not null,

    primary key (`id`)
);