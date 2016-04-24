use pokerai;
create table preflop_equity
(
hero_hand char(4) not null,
villain_hand char(4) not null,
p_win decimal(15,14) not null,
p_tie decimal(15,14) not null
);

select * from preflop_equity;