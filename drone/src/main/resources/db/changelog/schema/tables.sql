create table drone (
    drone_id bigint generated by default as identity primary key,
    battery_level integer not null check (battery_level <= 100 AND battery_level >= 0),
    model varchar(255) not null,
    serial_number varchar(100) not null,
    state varchar(255) not null,
    weight_limit_gram integer not null check (weight_limit_gram <= 500 AND weight_limit_gram >= 1)
);

create table load (
    load_id bigint generated by default as identity primary key,
    created_at timestamp(6),
    loaded_at timestamp(6),
    unloaded_at timestamp(6),
    drone_id bigint not null
);

create table load_medications (
    load_load_id bigint not null,
    code varchar(255),
    image blob,
    name varchar(255),
    weight_gram integer not null
);