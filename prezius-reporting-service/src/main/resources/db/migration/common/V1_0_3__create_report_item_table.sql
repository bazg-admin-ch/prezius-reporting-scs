create table report_item
(
    id uuid not null primary key,
    goods_type varchar,
    hallmarking_method varchar,
    certified_material boolean,
    ag int,
    au int,
    pd int,
    pt int,
    hallmarking_location varchar,
    cost varchar,
    office varchar,
    date timestamp,
    business_partner varchar,
    hallmarking_type varchar
);
