CREATE TABLE IF NOT EXISTS factionElections (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    factionName  CLOB UNIQUE,
    endDate      DATETIME
);

CREATE INDEX factionElectionsFactionNameIndex ON factionElections (factionName);

CREATE TABLE electionVotes (
    election  INTEGER PRIMARY KEY,
    uuid      CLOB,
    votedFor  CLOB,
    FOREIGN KEY (election) REFERENCES factionElections(id) ON DELETE CASCADE
);