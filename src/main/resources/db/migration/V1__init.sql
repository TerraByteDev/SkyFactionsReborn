create table if not exists auditLogs
(
    factionName  clob not null,
    type         clob not null,
    uuid         clob not null,
    replacements clob not null,
    timestamp    int8 not null
);

create table if not exists defenceLocations
(
    uuid        clob not null,
    type        clob not null,
    factionName clob not null,
    x           int  not null,
    y           int  not null,
    z           int  not null
);

create table if not exists factionBans
(
    factionName clob not null,
    uuid        clob not null
);

create table if not exists factionInvites
(
    factionName clob    not null,
    uuid        clob    not null,
    inviter     clob    not null,
    type        clob    not null,
    accepted    boolean not null,
    timestamp   int8    not null
);

create table if not exists factionIslands
(
    id           int
        primary key,
    factionName  clob not null,
    runes        int  not null,
    defenceCount int  not null,
    gems         int  not null,
    last_raided  int8 not null,
    last_raider  clob not null
);

create table if not exists factionMembers
(
    factionName clob not null,
    uuid        clob not null
        primary key,
    rank        clob not null
);

create table if not exists factions
(
    name      clob not null
        primary key,
    motd      clob not null,
    level     int  not null,
    last_raid int  not null,
    locale    clob not null
);

create table if not exists islands
(
    id           int
        primary key,
    uuid         clob not null,
    level        int  not null,
    gems         int  not null,
    runes        int  not null,
    defenceCount int  not null,
    last_raided  int8 not null,
    last_raider  clob not null
);

create table if not exists notifications
(
    uuid         clob not null,
    type         clob not null,
    replacements clob not null,
    timestamp    int8 not null
);

create table if not exists playerData
(
    uuid       clob not null
        primary key,
    faction    clob not null,
    discord_id clob not null,
    last_raid  int8 not null,
    locale     clob not null
);


create table if not exists trustedPlayers
(
    island_id int  not null
        primary key,
    uuid      clob not null
);

