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

COMMENT ON TABLE "NameBasics" IS 'Contains the following information for names';
COMMENT ON COLUMN "NameBasics".nconst IS 'alphanumeric unique identifier of the name/person';
COMMENT ON COLUMN "NameBasics"."primaryName" IS 'name by which the person is most often credited';
COMMENT ON COLUMN "NameBasics"."birthYear" IS 'in YYYY format';
COMMENT ON COLUMN "NameBasics"."deathYear" IS 'in YYYY format if applicable, else \N';


DROP TABLE IF EXISTS "Akas" CASCADE;
CREATE TABLE "Akas" (
    tconst VARCHAR,
    ordering INT,
    title VARCHAR,
    region VARCHAR,
    "language" VARCHAR,
    types VARCHAR,
    attributes VARCHAR,
    "isOriginalTitle" boolean);
COPY "Akas" FROM '/Users/lapaesleme/OneDrive/Data/dumps/IMDb/title.akas.tsv' DELIMITER E'\t' NULL '\N' CSV HEADER QUOTE E'\b';
ALTER TABLE "Akas" ADD PRIMARY KEY (tconst,ordering);

COMMENT ON TABLE "Akas" IS 'Contains the following information for titles';
COMMENT ON COLUMN "Akas".tconst IS 'a tconst, an alphanumeric unique identifier of the title';
COMMENT ON COLUMN "Akas".ordering IS 'a number to uniquely identify rows for a given titleId';
COMMENT ON COLUMN "Akas".title IS 'the localized title';
COMMENT ON COLUMN "Akas".region IS 'the region for this version of the title';
COMMENT ON COLUMN "Akas"."language" IS 'the language of the title'; 
COMMENT ON COLUMN "Akas".types IS 'Enumerated set of attributes for this alternative title. One or more of the following: "alternative", "dvd", "festival", "tv", "video", "working", "original", "imdbDisplay". New values may be added in the future without warning'; 
COMMENT ON COLUMN "Akas".attributes IS 'Additional terms to describe this alternative title, not enumerated'; 
COMMENT ON COLUMN "Akas"."isOriginalTitle" IS '0: not original title; 1: original title'; 


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

COMMENT ON TABLE "TitleBasics" IS 'Contains the following information for titles';
COMMENT ON COLUMN "TitleBasics".tconst IS 'alphanumeric unique identifier of the title';
COMMENT ON COLUMN "TitleBasics"."titleType" IS 'the type/format of the title (e.g. movie, short, tvseries, tvepisode, video, etc)';
COMMENT ON COLUMN "TitleBasics"."primaryTitle" IS 'the more popular title / the title used by the filmmakers on promotional materials at the point of release';
COMMENT ON COLUMN "TitleBasics"."originalTitle" IS 'original title, in the original language';
COMMENT ON COLUMN "TitleBasics"."isAdult" IS '0: non-adult title; 1: adult title';
COMMENT ON COLUMN "TitleBasics"."startYear" IS 'represents the release year of a title. In the case of TV Series, it is the series start year';
COMMENT ON COLUMN "TitleBasics"."endYear" IS 'TV Series end year. ‘\N’ for all other title types';
COMMENT ON COLUMN "TitleBasics"."runtimeMinutes" IS 'primary runtime of the title, in minutes';


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

COMMENT ON TABLE "Episode" IS 'Contains the tv episode information.';
COMMENT ON COLUMN "Episode".tconst IS 'alphanumeric unique identifier of the title';
COMMENT ON COLUMN "Episode"."parentTconst" IS 'alphanumeric identifier of the parent TV Series';
COMMENT ON COLUMN "Episode"."seasonNumber" IS 'season number the episode belongs to';
COMMENT ON COLUMN "Episode"."episodeNumber" IS 'episode number of the tconst in the TV series';


DROP TABLE IF EXISTS "Principal" CASCADE;
CREATE TABLE "Principal" (
    tconst VARCHAR,
    ordering VARCHAR,
    nconst VARCHAR,
    category VARCHAR,
    job VARCHAR,
    characters VARCHAR);
COPY "Principal" FROM '/Users/lapaesleme/OneDrive/Data/dumps/IMDb/title.principals.tsv' WITH DELIMITER E'\t' NULL '\N' CSV HEADER QUOTE E'\b';
ALTER TABLE "Principal" ADD PRIMARY KEY (tconst,nconst,ordering);

COMMENT ON TABLE "Principal" IS 'Contains the principal cast/crew for titles.';
COMMENT ON COLUMN "Principal".tconst IS 'alphanumeric unique identifier of the title';
COMMENT ON COLUMN "Principal".ordering IS 'a number to uniquely identify rows for a given titleId';
COMMENT ON COLUMN "Principal".nconst IS 'alphanumeric unique identifier of the name/person';
COMMENT ON COLUMN "Principal".job IS 'the specific job title if applicable, else \N';
COMMENT ON COLUMN "Principal".characters IS 'the name of the character played if applicable, else \N';


DROP TABLE IF EXISTS "Rating" CASCADE;
CREATE TABLE "Rating" (
    tconst VARCHAR,
    "averageRating" FLOAT,
    "numVotes" INT);
COPY "Rating" FROM '/Users/lapaesleme/OneDrive/Data/dumps/IMDb/title.ratings.tsv' WITH DELIMITER E'\t' NULL '\N' CSV HEADER QUOTE E'\b';
ALTER TABLE "Rating" ADD PRIMARY KEY (tconst);

COMMENT ON TABLE "Rating" IS 'Contains the IMDb rating and votes information for titles.';
COMMENT ON COLUMN "Rating".tconst IS 'alphanumeric unique identifier of the title';
COMMENT ON COLUMN "Rating"."averageRating" IS 'weighted average of all the individual user ratings';
COMMENT ON COLUMN "Rating"."numVotes" IS 'number of votes the title has received';
-------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS "Name" CASCADE;
CREATE TABLE "Name" AS
SELECT nconst FROM "NameBasics";
ALTER TABLE "Name" ADD PRIMARY KEY (nconst);

INSERT INTO "Name"(nconst)  (SELECT DISTINCT t1.nconst FROM "Principal" t1 LEFT JOIN "Name" t2 ON t1.nconst = t2.nconst WHERE t2.nconst IS NULL);

ALTER TABLE "Principal" ADD FOREIGN KEY (nconst) REFERENCES "Name";
-------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS "Title" CASCADE;
CREATE TABLE "Title" AS
SELECT tconst FROM "TitleBasics"
UNION SELECT tconst FROM "Akas";
ALTER TABLE "Title" ADD PRIMARY KEY (tconst);

INSERT INTO "Title"(tconst)  (SELECT DISTINCT t1."parentTconst" FROM "Episode" t1 LEFT JOIN "Title" t2 ON t1."parentTconst" = t2.tconst WHERE t2.tconst IS NULL);
INSERT INTO "Title"(tconst)  (SELECT DISTINCT t1.tconst FROM "Principal" t1 LEFT JOIN "Title" t2 ON t1.tconst = t2.tconst WHERE t2.tconst IS NULL);

ALTER TABLE "Akas" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "TitleBasics" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "TitleCrew" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "Episode" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "Episode" ADD FOREIGN KEY ("parentTconst") REFERENCES "Title";
ALTER TABLE "Principal" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "Rating" ADD FOREIGN KEY (tconst) REFERENCES "Title";
-------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS "Profession" CASCADE;
CREATE TABLE "Profession" AS
WITH
    t1 AS (SELECT DISTINCT trim(regexp_split_to_table("primaryProfession", ',+')) AS pconst FROM "NameBasics")
SELECT pconst, pconst AS label FROM t1 WHERE pconst is not null and pconst != '';
ALTER TABLE "Profession" ADD PRIMARY KEY (pconst);
COMMENT ON TABLE "Profession" IS 'Contains professions.';
COMMENT ON COLUMN "Profession".pconst IS 'alphanumeric unique identifier of the profession';

DROP TABLE IF EXISTS "primaryProfession" CASCADE;
CREATE TABLE "primaryProfession" AS
WITH
    t1 AS (SELECT DISTINCT nconst, trim(regexp_split_to_table("primaryProfession", ',+')) AS pconst FROM "NameBasics")
SELECT DISTINCT nconst,pconst FROM t1 WHERE pconst is not null and pconst != '';
COMMENT ON TABLE "primaryProfession" IS 'professions of a person';
COMMENT ON COLUMN "NameBasics".nconst IS 'alphanumeric unique identifier of the name/person';
COMMENT ON COLUMN "primaryProfession".pconst IS 'alphanumeric unique identifier of the profession';

ALTER TABLE "Principal" ADD COLUMN IF NOT EXISTS pconst VARCHAR;
COMMENT ON COLUMN "Principal"."pconst" IS 'alphanumeric unique identifier of the category of job that person was in';
UPDATE  "Principal" SET pconst = 'id-' || md5(trim(category))::uuid;
INSERT INTO "Profession"(pconst,label)  (SELECT DISTINCT 'id-' || md5(trim(category))::uuid, trim(category) FROM "Principal" t1 LEFT JOIN "Profession" t2 ON 'id-' || md5(trim(category))::uuid = t2.pconst WHERE t2.pconst IS NULL);


ALTER TABLE "primaryProfession" ADD PRIMARY KEY (nconst,pconst);
ALTER TABLE "primaryProfession" ADD FOREIGN KEY (nconst) REFERENCES "NameBasics";
ALTER TABLE "primaryProfession" ADD FOREIGN KEY (pconst) REFERENCES "Profession";
ALTER TABLE "Principal" ADD FOREIGN KEY (pconst) REFERENCES "Profession";
-------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS "knownForTitle" CASCADE;
CREATE TABLE "knownForTitle" AS
with
    t1 AS (SELECT DISTINCT nconst,trim(regexp_split_to_table("knownForTitles", ',+')) AS tconst FROM "NameBasics")
SELECT nconst,tconst FROM t1 WHERE tconst is not null and tconst != '';
COMMENT ON TABLE "knownForTitle" IS 'itles the person is known for.';
COMMENT ON COLUMN "knownForTitle".nconst IS 'alphanumeric unique identifier of the name/person';
COMMENT ON COLUMN "knownForTitle".tconst IS 'alphanumeric unique identifier of the title';

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
COMMENT ON TABLE "Genre" IS 'genres of titles';
COMMENT ON COLUMN "Genre".gconst IS 'alphanumeric unique identifier of the genre';
COMMENT ON COLUMN "Genre".label IS 'label of the genre';

DROP TABLE IF EXISTS "hasGenre" CASCADE;
CREATE TABLE "hasGenre" AS
SELECT DISTINCT tconst,trim(regexp_split_to_table(genres, ',+')) AS gconst FROM "TitleBasics";
ALTER TABLE "hasGenre" ADD PRIMARY KEY (tconst,gconst);
COMMENT ON TABLE "hasGenre" IS 'genres associated with titles';
COMMENT ON COLUMN "knownForTitle".tconst IS 'alphanumeric unique identifier of the title';
COMMENT ON COLUMN "Genre".gconst IS 'alphanumeric unique identifier of the genre';

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
COMMENT ON TABLE "Crew" IS 'Contains the director and writer information for all the titles in IMDb.';
COMMENT ON COLUMN "Crew".tconst IS 'alphanumeric unique identifier of the title';
COMMENT ON COLUMN "knownForTitle".nconst IS 'alphanumeric unique identifier of the name/person';
COMMENT ON COLUMN "Crew".pconst IS 'alphanumeric unique identifier of the profession';

INSERT INTO "Name"(nconst)  (SELECT DISTINCT t1.nconst FROM "Crew" t1 LEFT JOIN "Name" t2 ON t1.nconst = t2.nconst WHERE t2.nconst IS NULL);

ALTER TABLE "Crew" ADD FOREIGN KEY (tconst) REFERENCES "Title";
ALTER TABLE "Crew" ADD FOREIGN KEY (nconst) REFERENCES "Name";
ALTER TABLE "Crew" ADD FOREIGN KEY (pconst) REFERENCES "Profession";
-------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------
ALTER TABLE "NameBasics" DROP COLUMN IF EXISTS "primaryProfession";
ALTER TABLE "NameBasics" DROP COLUMN IF EXISTS "knownForTitles";
ALTER TABLE "TitleBasics" DROP COLUMN IF EXISTS genres;
ALTER TABLE "Principal" DROP COLUMN IF EXISTS category;
DROP TABLE IF EXISTS "TitleCrew" CASCADE;
-------------------------------------------------------------------------------------------------------------------------
UPDATE "Akas" SET types = trim(regexp_replace(replace(replace(replace(replace(replace(replace(replace(replace(types,'alternative','alternative '),'dvd','dvd '),'festival','festival '),'tv','tv '),'video','video '),'working','working '),'original','original '),'imdbDisplay','imdbDisplay '),'\s+',' ', 'g'));