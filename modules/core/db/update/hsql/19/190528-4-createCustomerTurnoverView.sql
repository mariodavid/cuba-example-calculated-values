create view CECV_CUSTOMER_TURNOVER_VIEW as
select
sum(o.order_amount) TOTAL_TURNOVER,
c.id ID,
c.id CUSTOMER_ID,
c.VERSION,
c.CREATE_TS,
c.CREATED_BY,
c.UPDATE_TS,
c.UPDATED_BY,
c.DELETE_TS,
c.DELETED_BY
from   CECV_CUSTOMER c
join   CECV_ORDER o
on     c.id = o.customer_id
group by c.id;