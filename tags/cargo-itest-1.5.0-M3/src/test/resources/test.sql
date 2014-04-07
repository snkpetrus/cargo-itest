    drop table if exists ABSTRACT_ACCOUNT;

    create table ABSTRACT_ACCOUNT (
        id bigint not null auto_increment,
        dateCreated datetime not null,
        dateModified datetime not null,
        OPTLOCK integer,
        blocked bit not null,
        primary key (id)
    );
