SET FOREIGN_KEY_CHECKS = 0;

    drop table if exists ABSTRACT_ACCOUNT;

    drop table if exists API_KEY;

    drop table if exists BUSINESS_ACCOUNT;

    drop table if exists FEDERATED_ACCOUNT;

    create table ABSTRACT_ACCOUNT (
        id bigint not null auto_increment,
        dateCreated datetime not null,
        dateModified datetime not null,
        OPTLOCK integer,
        blocked bit not null,
        primary key (id)
    );

    create table API_KEY (
        id bigint not null auto_increment,
        contactName varchar(255),
        email varchar(255),
        dateCreated datetime not null,
        dateModified datetime,
        comments varchar(255),
        referrerList varchar(255),
        allowStats bit not null,
        useRegularExpressionWithReferrer bit not null,
        KEY_VALUE varchar(255) not null unique,
        enabled bit not null,
        OPTLOCK integer,
        description varchar(255),
        account_fk bigint not null,
        primary key (id)
    );

    create table BUSINESS_ACCOUNT (
        id bigint not null,
        contactName varchar(255) not null,
        email varchar(255) not null unique,
        businessName varchar(255) not null,
        dateLastSignIn datetime,
        signInCount integer not null,
        sha256Password varchar(255) not null,
        primary key (id)
    );

    create table FEDERATED_ACCOUNT (
        id bigint not null,
        federatedId bigint not null,
        domainName varchar(255) not null,
        primary key (id)
    );

    alter table API_KEY 
        add index FKFB29C27A92496C7C (account_fk), 
        add constraint FKFB29C27A92496C7C 
        foreign key (account_fk) 
        references ABSTRACT_ACCOUNT (id);

    alter table BUSINESS_ACCOUNT 
        add index FK6CADAC8EC22B5D80 (id), 
        add constraint FK6CADAC8EC22B5D80 
        foreign key (id) 
        references ABSTRACT_ACCOUNT (id);

    alter table FEDERATED_ACCOUNT 
        add index FK7A1882B2C22B5D80 (id), 
        add constraint FK7A1882B2C22B5D80 
        foreign key (id) 
        references ABSTRACT_ACCOUNT (id);

SET FOREIGN_KEY_CHECKS = 1;