create table if not exists faction_elections
(
    id           integer
        constraint faction_elections_pk primary key autoincrement,
    faction_name clob
        constraint faction_elections_uk_faction_name
            unique
        constraint faction_elections_factions_name_fk
            references factions
            on delete cascade,
    end_date     datetime
);

create index faction_elections_faction_name_index
    on faction_elections (faction_name);

create table election_votes
(
    election  integer
        constraint election_votes_pk
            primary key
        constraint election_votes_faction_elections_id_fk
            references faction_elections
            on delete cascade,
    uuid      clob,
    voted_for clob
);

