-- begin CECV_ORDER
alter table CECV_ORDER add constraint FK_CECV_ORDER_ON_CUSTOMER foreign key (CUSTOMER_ID) references CECV_CUSTOMER(ID)^
create index IDX_CECV_ORDER_ON_CUSTOMER on CECV_ORDER (CUSTOMER_ID)^
-- end CECV_ORDER
