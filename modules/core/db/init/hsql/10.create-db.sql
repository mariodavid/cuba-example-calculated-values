-- begin CECV_CUSTOMER
create table CECV_CUSTOMER (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    PERSISTET_TOTAL_TURNOVER double precision,
    --
    primary key (ID)
)^
-- end CECV_CUSTOMER
-- begin CECV_ORDER
create table CECV_ORDER (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORDER_DATE date,
    ORDER_AMOUNT double precision,
    CUSTOMER_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end CECV_ORDER