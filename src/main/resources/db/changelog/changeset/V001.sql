create table if not exists translations (
    id bigint primary key generated always as identity unique,
    origin_text varchar(128) not null,
    translation_text varchar(128) not null,
    dictionary_id bigint not null
)