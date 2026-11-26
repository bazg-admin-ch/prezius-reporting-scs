update report_item
set cost = 0.00
where id in (
    select id from (
        select id, row_number() over (partition by order_id, cost order by id) as row_num
        from report_item
        where cost is not null
    ) sub
    where sub.row_num > 1
);
