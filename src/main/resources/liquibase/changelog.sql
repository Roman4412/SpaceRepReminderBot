--liquibase formatted sql

--changeset rpustovalov:1

create table notification_tasks
(
    id                serial       not null,
    chat_id           bigint       not null,
    text              varchar(255) not null,
    notification_date timestamp    not null
);