create table if not exists dictionaries (
    id bigint primary key generated always as identity unique,
    name varchar(128) not null,
    description text,
    is_public boolean not null,
    created_at timestamptz default current_timestamp,
    author_id bigint not null
)