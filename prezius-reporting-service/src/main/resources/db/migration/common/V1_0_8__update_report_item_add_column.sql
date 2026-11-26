alter table report_item add if not exists type varchar not null default 'HALLMARKING';
alter table report_item add if not exists analysis_type varchar;