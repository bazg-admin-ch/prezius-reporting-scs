with temp_table as (
    select id, row_number() over (partition by order_id, cost order by id) as row_num
    from report_item
    where cost is not null
)

update report_item
set cost = 0.00
where report_item.id in (select id from temp_table where row_num > 1);
