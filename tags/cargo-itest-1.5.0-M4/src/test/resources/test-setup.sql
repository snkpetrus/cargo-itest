    drop table if exists ABSTRACT_ACCOUNT;

    create table ABSTRACT_ACCOUNT (
        id bigint not null,
        primary key (id)
    );
    
    insert into ABSTRACT_ACCOUNT VALUES (1);
