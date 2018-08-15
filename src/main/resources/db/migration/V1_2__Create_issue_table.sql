create table if not exists `Issue` (
    `id` int unsigned not null auto_increment,
    `description` varchar(255) not null,
    `project_id` int unsigned not null,

    primary key (`id`),

    constraint `fk_project_id` foreign key (`project_id`) references `Project`(`id`)
);