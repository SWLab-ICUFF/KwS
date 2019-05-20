
--
-- Name: borders; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table borders cascade constraints;

CREATE TABLE borders (
    country1 varchar(4) NOT NULL,
    country2 varchar(4) NOT NULL,
    length numeric,
    search_id integer NOT NULL,
    CONSTRAINT borders_length_check CHECK (length > 0)
);


--
-- Name: city; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table city cascade constraints;

CREATE TABLE city (
    name varchar(35) NOT NULL,
    country varchar(4) NOT NULL,
    province varchar(32) NOT NULL,
    population numeric,
    longitude numeric,
    latitude numeric,
    search_id integer NOT NULL,
    CONSTRAINT citylat CHECK (latitude >= -90 AND latitude <= 90),
    CONSTRAINT citylon CHECK (longitude >= -180 AND longitude <= 180),
    CONSTRAINT citypop CHECK (population >= 0)
);


--
-- Name: continent; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--
drop table continent cascade constraints;

CREATE TABLE continent (
    name varchar(20) NOT NULL,
    area numeric(10,0),
    search_id integer NOT NULL
);


--
-- Name: country; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table country cascade constraints;

CREATE TABLE country (
    name varchar(32) NOT NULL,
    code varchar(4) NOT NULL,
    capital varchar(35),
    province varchar(32),
    area numeric,
    population numeric,
    search_id integer NOT NULL,
    CONSTRAINT countryarea CHECK (area >= 0),
    CONSTRAINT countrypop CHECK (population >= 0)
);


--
-- Name: desert; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table desert cascade constraints;

CREATE TABLE desert (
    name varchar(25) NOT NULL,
    area numeric,
    search_id integer NOT NULL
);


--
-- Name: economy; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table economy cascade constraints;

CREATE TABLE economy (
    country varchar(4) NOT NULL,
    gdp numeric,
    agriculture numeric,
    service numeric,
    industry numeric,
    inflation numeric,
    search_id integer NOT NULL,
    CONSTRAINT economygdp CHECK (gdp >= 0)
);


--
-- Name: encompasses; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table encompasses cascade constraints;

CREATE TABLE encompasses (
    country varchar(4) NOT NULL,
    continent varchar(20) NOT NULL,
    percentage numeric,
    search_id integer NOT NULL,
    CONSTRAINT encompasses_percentage_check CHECK (percentage > 0 AND percentage <= 100)
);


--
-- Name: ethnic_group; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table ethnic_group cascade constraints;

CREATE TABLE ethnic_group (
    country varchar(4) NOT NULL,
    name varchar(50) NOT NULL,
    percentage numeric,
    search_id integer NOT NULL,
    CONSTRAINT ethnicpercent CHECK (percentage > 0 AND percentage <= 100)
);


--
-- Name: geo_desert; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table geo_desert cascade constraints;

CREATE TABLE geo_desert (
    desert varchar(25) NOT NULL,
    country varchar(4) NOT NULL,
    province varchar(32) NOT NULL,
    search_id integer NOT NULL
);


--
-- Name: geo_island; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table geo_island cascade constraints;

CREATE TABLE geo_island (
    island varchar(25) NOT NULL,
    country varchar(4) NOT NULL,
    province varchar(32) NOT NULL,
    search_id integer NOT NULL
);


--
-- Name: geo_lake; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table geo_lake cascade constraints;

CREATE TABLE geo_lake (
    lake varchar(25) NOT NULL,
    country varchar(4) NOT NULL,
    province varchar(32) NOT NULL,
    search_id integer NOT NULL
);


--
-- Name: geo_mountain; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table geo_mountain cascade constraints;

CREATE TABLE geo_mountain (
    mountain varchar(20) NOT NULL,
    country varchar(4) NOT NULL,
    province varchar(32) NOT NULL,
    search_id integer NOT NULL
);


--
-- Name: geo_river; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table geo_river cascade constraints;

CREATE TABLE geo_river (
    river varchar(20) NOT NULL,
    country varchar(4) NOT NULL,
    province varchar(32) NOT NULL,
    search_id integer NOT NULL
);


--
-- Name: geo_sea; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table geo_sea cascade constraints;

CREATE TABLE geo_sea (
    sea varchar(25) NOT NULL,
    country varchar(4) NOT NULL,
    province varchar(32) NOT NULL,
    search_id integer NOT NULL
);


--
-- Name: is_member; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table is_member cascade constraints;

CREATE TABLE is_member (
    country varchar(4) NOT NULL,
    organization varchar(12) NOT NULL,
    type varchar(30) DEFAULT 'member',
    search_id integer NOT NULL
);


--
-- Name: island; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table island cascade constraints;

CREATE TABLE island (
    name varchar(25) NOT NULL,
    islands varchar(25),
    area numeric,
  --  coordinates geocoord,
    search_id integer NOT NULL,
    CONSTRAINT islandar CHECK (area >= 0 AND area <= 2175600)
    --CONSTRAINT islandcoord CHECK (coordinates.longitude >= -180 
    --AND coordinates.longitude <= 180 
    --AND coordinates.latitude >= -90 
    --AND coordinates.latitude <= 90)
);


--
-- Name: lake; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table lake cascade constraints;

CREATE TABLE lake (
    name varchar(25) NOT NULL,
    area numeric,
    search_id integer NOT NULL,
    CONSTRAINT lakear CHECK (area >= 0)
);


--
-- Name: language; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table language cascade constraints;

CREATE TABLE language (
    country varchar(4) NOT NULL,
    name varchar(50) NOT NULL,
    percentage numeric,
    search_id integer NOT NULL,
    CONSTRAINT languagepercent CHECK (percentage > 0 AND percentage <= 100)
);


--
-- Name: located; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table located cascade constraints;

CREATE TABLE located (
    city varchar(35),
    province varchar(32),
    country varchar(4),
    river varchar(20),
    lake varchar(25),
    sea varchar(25),
    search_id integer NOT NULL
);


--
-- Name: merges_with; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table merges_with cascade constraints;

CREATE TABLE merges_with (
    sea1 varchar(25) NOT NULL,
    sea2 varchar(25) NOT NULL,
    search_id integer NOT NULL
);


--
-- Name: mountain; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table mountain cascade constraints;

CREATE TABLE mountain (
    name varchar(20) NOT NULL,
    height numeric,
  --  coordinates geocoord,
    search_id integer NOT NULL,
    --CONSTRAINT mountaincoord CHECK (coordinates.longitude >= -180
   -- AND coordinates.longitude <= 180
   -- AND coordinates.latitude >= -90
    --AND coordinates.latitude <= 90),
    CONSTRAINT mountainheight CHECK (height >= 0)
);


--
-- Name: organization; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table organization cascade constraints;

CREATE TABLE organization (
    abbreviation varchar(12) NOT NULL,
    name varchar(80) NOT NULL,
    city varchar(35),
    country varchar(4),
    province varchar(32),
    established date,
    search_id integer NOT NULL
);


--
-- Name: politics; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table politics cascade constraints;

CREATE TABLE politics (
    country varchar(4) NOT NULL,
    independence date,
    government varchar(120),
    search_id integer NOT NULL
);


--
-- Name: population; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table population cascade constraints;

CREATE TABLE population (
    country varchar(4) NOT NULL,
    population_growth numeric,
    infant_mortality numeric,
    search_id integer NOT NULL
);


--
-- Name: province; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table province cascade constraints;

CREATE TABLE province (
    name varchar(32) NOT NULL,
    country varchar(4) NOT NULL,
    population numeric,
    area numeric,
    capital varchar(35),
    capprov varchar(32),
    search_id integer NOT NULL,
    CONSTRAINT prar CHECK (area >= 0),
    CONSTRAINT prpop CHECK (population >= 0)
);


--
-- Name: religion; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table religion cascade constraints;

CREATE TABLE religion (
    country varchar(4) NOT NULL,
    name varchar(50) NOT NULL,
    percentage numeric,
    search_id integer NOT NULL,
    CONSTRAINT religionpercent CHECK (percentage > 0 AND percentage <= 100)
);


--
-- Name: river; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--
drop table river cascade constraints;

CREATE TABLE river (
    name varchar(20) NOT NULL,
    river varchar(20),
    lake varchar(20),
    sea varchar(25),
    length numeric,
    search_id integer NOT NULL,
    CONSTRAINT riverlength CHECK (length >= 0)
);


--
-- Name: sea; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

drop table sea cascade constraints;

CREATE TABLE sea (
    name varchar(25) NOT NULL,
    depth numeric,
    search_id integer NOT NULL,
    CONSTRAINT seadepth CHECK (depth >= 0)
);

--
-- Name: borderkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  borders
    ADD CONSTRAINT borderkey PRIMARY KEY (country1, country2);


--
-- Name: borders_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  borders
    ADD CONSTRAINT borders_search_id_key UNIQUE (search_id);


--
-- Name: city_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  city
    ADD CONSTRAINT city_search_id_key UNIQUE (search_id);


--
-- Name: citykey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  city
    ADD CONSTRAINT citykey PRIMARY KEY (name, country, province);


--
-- Name: continent_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  continent
    ADD CONSTRAINT continent_search_id_key UNIQUE (search_id);


--
-- Name: continentkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  continent
    ADD CONSTRAINT continentkey PRIMARY KEY (name);


--
-- Name: country_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  country
    ADD CONSTRAINT country_search_id_key UNIQUE (search_id);


--
-- Name: country_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  country
    ADD CONSTRAINT country_name_key UNIQUE (name);


--
-- Name: countrykey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  country
    ADD CONSTRAINT countrykey PRIMARY KEY (code);


--
-- Name: desert_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  desert
    ADD CONSTRAINT desert_search_id_key UNIQUE (search_id);


--
-- Name: desertkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  desert
    ADD CONSTRAINT desertkey PRIMARY KEY (name);


--
-- Name: economy_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  economy
    ADD CONSTRAINT economy_search_id_key UNIQUE (search_id);


--
-- Name: economykey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  economy
    ADD CONSTRAINT economykey PRIMARY KEY (country);


--
-- Name: encompasses_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  encompasses
    ADD CONSTRAINT encompasses_search_id_key UNIQUE (search_id);


--
-- Name: encompasseskey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  encompasses
    ADD CONSTRAINT encompasseskey PRIMARY KEY (country, continent);


--
-- Name: ethnic_group_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  ethnic_group
    ADD CONSTRAINT ethnic_group_search_id_key UNIQUE (search_id);


--
-- Name: ethnickey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  ethnic_group
    ADD CONSTRAINT ethnickey PRIMARY KEY (name, country);


--
-- Name: gdesertkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  geo_desert
    ADD CONSTRAINT gdesertkey PRIMARY KEY (province, country, desert);


--
-- Name: geo_desert_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  geo_desert
    ADD CONSTRAINT geo_desert_search_id_key UNIQUE (search_id);


--
-- Name: geo_island_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  geo_island
    ADD CONSTRAINT geo_island_search_id_key UNIQUE (search_id);


--
-- Name: geo_lake_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  geo_lake
    ADD CONSTRAINT geo_lake_search_id_key UNIQUE (search_id);


--
-- Name: geo_mountain_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  geo_mountain
    ADD CONSTRAINT geo_mountain_search_id_key UNIQUE (search_id);


--
-- Name: geo_river_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  geo_river
    ADD CONSTRAINT geo_river_search_id_key UNIQUE (search_id);


--
-- Name: geo_sea_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  geo_sea
    ADD CONSTRAINT geo_sea_search_id_key UNIQUE (search_id);


--
-- Name: gislandkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  geo_island
    ADD CONSTRAINT gislandkey PRIMARY KEY (province, country, island);


--
-- Name: glakekey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  geo_lake
    ADD CONSTRAINT glakekey PRIMARY KEY (province, country, lake);


--
-- Name: gmountainkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  geo_mountain
    ADD CONSTRAINT gmountainkey PRIMARY KEY (province, country, mountain);


--
-- Name: griverkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  geo_river
    ADD CONSTRAINT griverkey PRIMARY KEY (province, country, river);


--
-- Name: gseakey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  geo_sea
    ADD CONSTRAINT gseakey PRIMARY KEY (province, country, sea);


--
-- Name: is_member_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  is_member
    ADD CONSTRAINT is_member_search_id_key UNIQUE (search_id);


--
-- Name: island_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  island
    ADD CONSTRAINT island_search_id_key UNIQUE (search_id);


--
-- Name: islandkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  island
    ADD CONSTRAINT islandkey PRIMARY KEY (name);


--
-- Name: lake_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  lake
    ADD CONSTRAINT lake_search_id_key UNIQUE (search_id);


--
-- Name: lakekey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  lake
    ADD CONSTRAINT lakekey PRIMARY KEY (name);


--
-- Name: language_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  language
    ADD CONSTRAINT language_search_id_key UNIQUE (search_id);


--
-- Name: languagekey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  language
    ADD CONSTRAINT languagekey PRIMARY KEY (name, country);


--
-- Name: located_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  located
    ADD CONSTRAINT located_search_id_key UNIQUE (search_id);


--
-- Name: memberkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  is_member
    ADD CONSTRAINT memberkey PRIMARY KEY (country, organization);


--
-- Name: merges_with_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  merges_with
    ADD CONSTRAINT merges_with_search_id_key UNIQUE (search_id);


--
-- Name: mergeswithkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  merges_with
    ADD CONSTRAINT mergeswithkey PRIMARY KEY (sea1, sea2);


--
-- Name: mountain_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  mountain
    ADD CONSTRAINT mountain_search_id_key UNIQUE (search_id);


--
-- Name: mountainkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  mountain
    ADD CONSTRAINT mountainkey PRIMARY KEY (name);


--
-- Name: organization_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  organization
    ADD CONSTRAINT organization_search_id_key UNIQUE (search_id);


--
-- Name: organization_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (abbreviation);


--
-- Name: orgnameunique; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  organization
    ADD CONSTRAINT orgnameunique UNIQUE (name);


--
-- Name: politics_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  politics
    ADD CONSTRAINT politics_search_id_key UNIQUE (search_id);


--
-- Name: politicskey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  politics
    ADD CONSTRAINT politicskey PRIMARY KEY (country);


--
-- Name: popkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  population
    ADD CONSTRAINT popkey PRIMARY KEY (country);


--
-- Name: population_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  population
    ADD CONSTRAINT population_search_id_key UNIQUE (search_id);


--
-- Name: prkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  province
    ADD CONSTRAINT prkey PRIMARY KEY (name, country);


--
-- Name: province_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  province
    ADD CONSTRAINT province_search_id_key UNIQUE (search_id);


--
-- Name: religion_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  religion
    ADD CONSTRAINT religion_search_id_key UNIQUE (search_id);


--
-- Name: religionkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  religion
    ADD CONSTRAINT religionkey PRIMARY KEY (name, country);


--
-- Name: river_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  river
    ADD CONSTRAINT river_search_id_key UNIQUE (search_id);


--
-- Name: riverkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  river
    ADD CONSTRAINT riverkey PRIMARY KEY (name);


--
-- Name: sea_search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  sea
    ADD CONSTRAINT sea_search_id_key UNIQUE (search_id);


--
-- Name: seakey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE  sea
    ADD CONSTRAINT seakey PRIMARY KEY (name);

--
-- Name: borders_country1_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  borders
    ADD CONSTRAINT borders_country1_fkey FOREIGN KEY (country1) REFERENCES country(code);


--
-- Name: borders_country2_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  borders
    ADD CONSTRAINT borders_country2_fkey FOREIGN KEY (country2) REFERENCES country(code);


--
-- Name: city_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  city
    ADD CONSTRAINT city_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name) DEFERRABLE;


--
-- Name: country_code_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  country
    ADD CONSTRAINT country_code_fkey FOREIGN KEY (code, capital, province) REFERENCES city(country, name, province) DEFERRABLE;


--
-- Name: economy_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  economy
    ADD CONSTRAINT economy_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: encompasses_continent_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  encompasses
    ADD CONSTRAINT encompasses_continent_fkey FOREIGN KEY (continent) REFERENCES continent(name);


--
-- Name: encompasses_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  encompasses
    ADD CONSTRAINT encompasses_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: ethnic_group_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  ethnic_group
    ADD CONSTRAINT ethnic_group_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: geo_desert_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  geo_desert
    ADD CONSTRAINT geo_desert_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name);


--
-- Name: geo_desert_desert_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  geo_desert
    ADD CONSTRAINT geo_desert_desert_fkey FOREIGN KEY (desert) REFERENCES desert(name);


--
-- Name: geo_island_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  geo_island
    ADD CONSTRAINT geo_island_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name);


--
-- Name: geo_island_island_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  geo_island
    ADD CONSTRAINT geo_island_island_fkey FOREIGN KEY (island) REFERENCES island(name);


--
-- Name: geo_lake_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  geo_lake
    ADD CONSTRAINT geo_lake_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name);


--
-- Name: geo_lake_lake_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  geo_lake
    ADD CONSTRAINT geo_lake_lake_fkey FOREIGN KEY (lake) REFERENCES lake(name);


--
-- Name: geo_mountain_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  geo_mountain
    ADD CONSTRAINT geo_mountain_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name);


--
-- Name: geo_mountain_mountain_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  geo_mountain
    ADD CONSTRAINT geo_mountain_mountain_fkey FOREIGN KEY (mountain) REFERENCES mountain(name);


--
-- Name: geo_river_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  geo_river
    ADD CONSTRAINT geo_river_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name);


--
-- Name: geo_river_river_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  geo_river
    ADD CONSTRAINT geo_river_river_fkey FOREIGN KEY (river) REFERENCES river(name);


--
-- Name: geo_sea_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  geo_sea
    ADD CONSTRAINT geo_sea_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name);


--
-- Name: geo_sea_sea_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  geo_sea
    ADD CONSTRAINT geo_sea_sea_fkey FOREIGN KEY (sea) REFERENCES sea(name);


--
-- Name: is_member_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  is_member
    ADD CONSTRAINT is_member_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: is_member_organization_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  is_member
    ADD CONSTRAINT is_member_organization_fkey FOREIGN KEY (organization) REFERENCES organization(abbreviation);


--
-- Name: language_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  language
    ADD CONSTRAINT language_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: located_city_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  located
    ADD CONSTRAINT located_city_fkey FOREIGN KEY (city, country, province) REFERENCES city(name, country, province);


--
-- Name: located_lake_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  located
    ADD CONSTRAINT located_lake_fkey FOREIGN KEY (lake) REFERENCES lake(name);


--
-- Name: located_river_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  located
    ADD CONSTRAINT located_river_fkey FOREIGN KEY (river) REFERENCES river(name);


--
-- Name: located_sea_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  located
    ADD CONSTRAINT located_sea_fkey FOREIGN KEY (sea) REFERENCES sea(name);


--
-- Name: merges_with_sea1_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  merges_with
    ADD CONSTRAINT merges_with_sea1_fkey FOREIGN KEY (sea1) REFERENCES sea(name);


--
-- Name: merges_with_sea2_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  merges_with
    ADD CONSTRAINT merges_with_sea2_fkey FOREIGN KEY (sea2) REFERENCES sea(name);


--
-- Name: organization_city_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  organization
    ADD CONSTRAINT organization_city_fkey FOREIGN KEY (city, country, province) REFERENCES city(name, country, province);


--
-- Name: politics_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  politics
    ADD CONSTRAINT politics_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: population_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  population
    ADD CONSTRAINT population_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: province_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  province
    ADD CONSTRAINT province_country_fkey FOREIGN KEY (country) REFERENCES country(code) DEFERRABLE;


--
-- Name: province_country_fkey1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  province
    ADD CONSTRAINT province_country_fkey1 FOREIGN KEY (country, capital, capprov) REFERENCES city(country, name, province) DEFERRABLE;


--
-- Name: religion_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  religion
    ADD CONSTRAINT religion_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: river_lake_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  river
    ADD CONSTRAINT river_lake_fkey FOREIGN KEY (lake) REFERENCES lake(name) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: river_river_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  river
    ADD CONSTRAINT river_river_fkey FOREIGN KEY (river) REFERENCES river(name) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: river_sea_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE  river
    ADD CONSTRAINT river_sea_fkey FOREIGN KEY (sea) REFERENCES sea(name) DEFERRABLE INITIALLY DEFERRED;


--
-- PostgreSQL database dump complete
--

