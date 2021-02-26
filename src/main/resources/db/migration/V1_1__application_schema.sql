create sequence hibernate_sequence start 1 increment 1;

create table account (
    id  bigserial not null,
    amount numeric not null,
    primary key (id)
);

create table transaction_history (
    id bigserial not null,
    account_id bigint not null,
    type varchar(64) not null,
    amount numeric not null,
    date_time timestamp,
    primary key (id),
    foreign key (account_id) references account
);

create index transaction_history_account_id_index on transaction_history (account_id);