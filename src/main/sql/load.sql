DROP TABLE IF EXISTS "NameBasics" CASCADE;
CREATE TABLE "NameBasics" (
	nconst VARCHAR,
	"primaryName" VARCHAR,
	"birthYear" INT,
	"deathYear" INT,
	"primaryProfession" VARCHAR, -- split
	"knownForTitles" VARCHAR); -- split
COPY "NameBasics" FROM '/Users/lapaesleme/OneDrive/Data/dumps/IMDb/name.basics.tsv' DELIMITER E'\t' NULL '\N' CSV HEADER QUOTE E'\b';
ALTER TABLE "NameBasics" ADD PRIMARY KEY (nconst);


DROP TABLE IF EXISTS "TitleAkas" CASCADE;
CREATE TABLE "TitleAkas" (
    tconst VARCHAR,
    ordering INT,
    title VARCHAR,
    region VARCHAR,
    "language" VARCHAR,
    types VARCHAR,
    attributes VARCHAR,
    "isOriginalTitle" boolean);
COPY "TitleAkas" FROM '/Users/lapaesleme/OneDrive/Data/dumps/IMDb/title.akas.tsv' DELIMITER E'\t' NULL '\N' CSV HEADER QUOTE E'\b';
ALTER TABLE "TitleAkas" ADD PRIMARY KEY (tconst,ordering);


DROP TABLE IF EXISTS "TitleBasics" CASCADE;
CREATE TABLE "TitleBasics" (
    tconst VARCHAR,
    "titleType" VARCHAR,
    "primaryTitle" VARCHAR,
    "originalTitle" VARCHAR,
    "isAdult" boolean,
    "startYear" INT,
    "endYear" INT,
    "runtimeMinutes" INT,
    genres VARCHAR); -- split
COPY "TitleBasics" FROM '/Users/lapaesleme/OneDrive/Data/dumps/IMDb/title.basics.tsv' WITH DELIMITER E'\t' NULL '\N' CSV HEADER QUOTE E'\b';
ALTER TABLE "TitleBasics" ADD PRIMARY KEY (tconst);


DROP TABLE IF EXISTS "TitleCrew" CASCADE;
CREATE TABLE "TitleCrew" (
    tconst VARCHAR,
    directors VARCHAR, --split
    writers VARCHAR); -- split
COPY "TitleCrew" FROM '/Users/lapaesleme/OneDrive/Data/dumps/IMDb/title.crew.tsv' WITH DELIMITER E'\t' NULL '\N' CSV HEADER QUOTE E'\b';
ALTER TABLE "TitleCrew" ADD PRIMARY KEY (tconst);


DROP TABLE IF EXISTS "Episode" CASCADE;
CREATE TABLE "Episode" (
    tconst VARCHAR,
    "parentTconst" VARCHAR,
    "seasonNumber" INT,
    "episodeNumber" INT);
COPY "Episode" FROM '/Users/lapaesleme/OneDrive/Data/dumps/IMDb/title.episode.tsv' WITH DELIMITER E'\t' NULL '\N' CSV HEADER QUOTE E'\b';
ALTER TABLE "Episode" ADD PRIMARY KEY (tconst);


DROP TABLE IF EXISTS "Principals" CASCADE;
CREATE TABLE "Principals" (
    tconst VARCHAR,
    ordering VARCHAR,
    nconst VARCHAR,
    category VARCHAR,
    job VARCHAR,
    characters VARCHAR);
COPY "Principals" FROM '/Users/lapaesleme/OneDrive/Data/dumps/IMDb/title.principals.tsv' WITH DELIMITER E'\t' NULL '\N' CSV HEADER QUOTE E'\b';
ALTER TABLE "Principals" ADD PRIMARY KEY (tconst,nconst,ordering);


DROP TABLE IF EXISTS "Rating" CASCADE;
CREATE TABLE "Rating" (
    tconst VARCHAR,
    "averageRating" FLOAT,
    "numVotes" INT);
COPY "Rating" FROM '/Users/lapaesleme/OneDrive/Data/dumps/IMDb/title.ratings.tsv' WITH DELIMITER E'\t' NULL '\N' CSV HEADER QUOTE E'\b';
ALTER TABLE "Rating" ADD PRIMARY KEY (tconst);
-------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS "Name" CASCADE;
CREATE TABLE "Name" AS
SELECT nconst FROM "NameBasics";
ALTER TABLE "Name" ADD PRIMARY KEY (nconst);

INSERT INTO "Name"(nconst)  (SELECT DISTINCT t1.nconst FROM "Principals" t1 LEFT JOIN "Name" t2 ON t1.nconst = t2.nconst WHERE t2.nconst IS NULL);

ALTER TABLE "Principals" ADD FOREIGN KEY (nconst) REFERENCES "Name";
-------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS "Title" CASCADE;
CREATE TABLE "Title" AS
SELECT tconst FROM "TitleBasics"
UNION SELECT tconst FROM "TitleAkas";
ALTER TABLE "Title" ADD PRIMARY KEY (tconst);

INSERT INTO "Title"(tconst)  (SELECT DISTINCT t1."parentTconst" FROM "Episode" t1 LEFT JOIN "Title" t2 ON t1."parentTconst" = t2.tconst WHERE t2.tconst IS NULL);
INSERT INTO "Title"(tconst)  (SELECT DISTINCT t1.tconst FROM "Principals" t1 LEFT JOIN "Title" t2 ON t1.tconst = t2.tconst WHERE t2.tconst IS NULL);

ALTER TABLE "TitleAkas" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "TitleBasics" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "TitleCrew" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "Episode" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "Episode" ADD FOREIGN KEY ("parentTconst") REFERENCES "Title";
ALTER TABLE "Principals" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "Rating" ADD FOREIGN KEY (tconst) REFERENCES "Title";
-------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS "Profession" CASCADE;
CREATE TABLE "Profession" AS
WITH
    t1 AS (SELECT DISTINCT trim(regexp_split_to_table("primaryProfession", ',+')) AS pconst FROM "NameBasics")
SELECT pconst, pconst AS label FROM t1 WHERE pconst is not null and pconst != '';
ALTER TABLE "Profession" ADD PRIMARY KEY (pconst);

DROP TABLE IF EXISTS "primaryProfession" CASCADE;
CREATE TABLE "primaryProfession" AS
WITH
    t1 AS (SELECT DISTINCT nconst, trim(regexp_split_to_table("primaryProfession", ',+')) AS pconst FROM "NameBasics")
SELECT DISTINCT nconst,pconst FROM t1 WHERE pconst is not null and pconst != '';

ALTER TABLE "Principals" ADD COLUMN IF NOT EXISTS pconst VARCHAR;
UPDATE  "Principals" SET pconst = 'id-' || md5(trim(category))::uuid;
INSERT INTO "Profession"(pconst,label)  (SELECT DISTINCT 'id-' || md5(trim(category))::uuid, trim(category) FROM "Principals" t1 LEFT JOIN "Profession" t2 ON 'id-' || md5(trim(category))::uuid = t2.pconst WHERE t2.pconst IS NULL);

ALTER TABLE "primaryProfession" ADD PRIMARY KEY (nconst,pconst);
ALTER TABLE "primaryProfession" ADD FOREIGN KEY (nconst) REFERENCES "NameBasics";
ALTER TABLE "primaryProfession" ADD FOREIGN KEY (pconst) REFERENCES "Profession";
ALTER TABLE "Principals" ADD FOREIGN KEY (pconst) REFERENCES "Profession";
-------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS "knownForTitle" CASCADE;
CREATE TABLE "knownForTitle" AS
with
    t1 AS (SELECT DISTINCT nconst,trim(regexp_split_to_table("knownForTitles", ',+')) AS tconst FROM "NameBasics")
SELECT nconst,tconst FROM t1 WHERE tconst is not null and tconst != '';

INSERT INTO "Title"(tconst)  (SELECT DISTINCT t1.tconst FROM "knownForTitle" t1 LEFT JOIN "Title" t2 ON t1.tconst = t2.tconst WHERE t2.tconst IS NULL);

ALTER TABLE "knownForTitle" ADD PRIMARY KEY (nconst,tconst);
ALTER TABLE "knownForTitle" ADD FOREIGN KEY (nconst) REFERENCES "NameBasics";
ALTER TABLE "knownForTitle" ADD FOREIGN KEY (tconst) REFERENCES "Title";
-------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS "Genre" CASCADE;
CREATE TABLE "Genre" AS
WITH
    t1 AS (SELECT DISTINCT trim(regexp_split_to_table(genres, ',+')) AS gconst FROM "TitleBasics")
SELECT gconst, gconst AS label FROM t1 WHERE gconst is not null and gconst != '';
ALTER TABLE "Genre" ADD PRIMARY KEY (gconst);

DROP TABLE IF EXISTS "hasGenre" CASCADE;
CREATE TABLE "hasGenre" AS
SELECT DISTINCT tconst,trim(regexp_split_to_table(genres, ',+')) AS gconst FROM "TitleBasics";
ALTER TABLE "hasGenre" ADD PRIMARY KEY (tconst,gconst);

ALTER TABLE "hasGenre" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "hasGenre" ADD FOREIGN KEY (gconst) REFERENCES "Genre";
-------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS "Crew" CASCADE;
CREATE TABLE "Crew" AS
WITH
    t1 AS (SELECT DISTINCT tconst,trim(regexp_split_to_table(directors, ',+')) AS nconst, 'director' AS pconst FROM "TitleCrew"
           UNION SELECT DISTINCT tconst,trim(regexp_split_to_table(writers, ',+')) AS nconst, 'writer' AS pconst FROM "TitleCrew")
SELECT tconst,nconst,pconst FROM t1 WHERE nconst is not null and nconst != '';
ALTER TABLE "Crew" ADD PRIMARY KEY (tconst,nconst,pconst);

INSERT INTO "Name"(nconst)  (SELECT DISTINCT t1.nconst FROM "Crew" t1 LEFT JOIN "Name" t2 ON t1.nconst = t2.nconst WHERE t2.nconst IS NULL);

ALTER TABLE "Crew" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "Crew" ADD FOREIGN KEY (nconst) REFERENCES "Name";
ALTER TABLE "Crew" ADD FOREIGN KEY (pconst) REFERENCES "Profession";
-------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------
ALTER TABLE "NameBasics" DROP COLUMN IF EXISTS "primaryProfession";
ALTER TABLE "NameBasics" DROP COLUMN IF EXISTS "knownForTitles";
ALTER TABLE "TitleBasics" DROP COLUMN IF EXISTS genres;
ALTER TABLE "Principals" DROP COLUMN IF EXISTS category;
DROP TABLE IF EXISTS "TitleCrew" CASCADE;
-------------------------------------------------------------------------------------------------------------------------
