create table if not exists subscribe_dictionaries (
    id bigint primary key generated always as identity unique,
    user_id bigint not null,
    dictionary_id bigint not null
)