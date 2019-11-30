--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: borders; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE borders (
    country1 character varying(4) NOT NULL,
    country2 character varying(4) NOT NULL,
    length numeric,
    __search_id integer NOT NULL,
    CONSTRAINT borders_length_check CHECK ((length > (0)::numeric))
);


--
-- Name: city; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE city (
    name character varying(35) NOT NULL,
    country character varying(4) NOT NULL,
    province character varying(32) NOT NULL,
    population numeric,
    longitude numeric,
    latitude numeric,
    __search_id integer NOT NULL,
    CONSTRAINT citylat CHECK (((latitude >= (-90)::numeric) AND (latitude <= (90)::numeric))),
    CONSTRAINT citylon CHECK (((longitude >= (-180)::numeric) AND (longitude <= (180)::numeric))),
    CONSTRAINT citypop CHECK ((population >= (0)::numeric))
);


--
-- Name: continent; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE continent (
    name character varying(20) NOT NULL,
    area numeric(10,0),
    __search_id integer NOT NULL
);


--
-- Name: country; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE country (
    name character varying(32) NOT NULL,
    code character varying(4) NOT NULL,
    capital character varying(35),
    province character varying(32),
    area numeric,
    population numeric,
    __search_id integer NOT NULL,
    CONSTRAINT countryarea CHECK ((area >= (0)::numeric)),
    CONSTRAINT countrypop CHECK ((population >= (0)::numeric))
);


--
-- Name: desert; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE desert (
    name character varying(25) NOT NULL,
    area numeric,
    __search_id integer NOT NULL
);


--
-- Name: economy; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE economy (
    country character varying(4) NOT NULL,
    gdp numeric,
    agriculture numeric,
    service numeric,
    industry numeric,
    inflation numeric,
    __search_id integer NOT NULL,
    CONSTRAINT economygdp CHECK ((gdp >= (0)::numeric))
);


--
-- Name: encompasses; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE encompasses (
    country character varying(4) NOT NULL,
    continent character varying(20) NOT NULL,
    percentage numeric,
    __search_id integer NOT NULL,
    CONSTRAINT encompasses_percentage_check CHECK (((percentage > (0)::numeric) AND (percentage <= (100)::numeric)))
);


--
-- Name: ethnic_group; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE ethnic_group (
    country character varying(4) NOT NULL,
    name character varying(50) NOT NULL,
    percentage numeric,
    __search_id integer NOT NULL,
    CONSTRAINT ethnicpercent CHECK (((percentage > (0)::numeric) AND (percentage <= (100)::numeric)))
);


--
-- Name: geo_desert; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE geo_desert (
    desert character varying(25) NOT NULL,
    country character varying(4) NOT NULL,
    province character varying(32) NOT NULL,
    __search_id integer NOT NULL
);


--
-- Name: geo_island; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE geo_island (
    island character varying(25) NOT NULL,
    country character varying(4) NOT NULL,
    province character varying(32) NOT NULL,
    __search_id integer NOT NULL
);


--
-- Name: geo_lake; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE geo_lake (
    lake character varying(25) NOT NULL,
    country character varying(4) NOT NULL,
    province character varying(32) NOT NULL,
    __search_id integer NOT NULL
);


--
-- Name: geo_mountain; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE geo_mountain (
    mountain character varying(20) NOT NULL,
    country character varying(4) NOT NULL,
    province character varying(32) NOT NULL,
    __search_id integer NOT NULL
);


--
-- Name: geo_river; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE geo_river (
    river character varying(20) NOT NULL,
    country character varying(4) NOT NULL,
    province character varying(32) NOT NULL,
    __search_id integer NOT NULL
);


--
-- Name: geo_sea; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE geo_sea (
    sea character varying(25) NOT NULL,
    country character varying(4) NOT NULL,
    province character varying(32) NOT NULL,
    __search_id integer NOT NULL
);


--
-- Name: geocoord; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE geocoord AS (
	longitude numeric,
	latitude numeric
);


--
-- Name: is_member; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE is_member (
    country character varying(4) NOT NULL,
    organization character varying(12) NOT NULL,
    type character varying(30) DEFAULT 'member'::character varying,
    __search_id integer NOT NULL
);


--
-- Name: island; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE island (
    name character varying(25) NOT NULL,
    islands character varying(25),
    area numeric,
    coordinates geocoord,
    __search_id integer NOT NULL,
    CONSTRAINT islandar CHECK (((area >= (0)::numeric) AND (area <= (2175600)::numeric))),
    CONSTRAINT islandcoord CHECK ((((((coordinates).longitude >= (-180)::numeric) AND ((coordinates).longitude <= (180)::numeric)) AND ((coordinates).latitude >= (-90)::numeric)) AND ((coordinates).latitude <= (90)::numeric)))
);


--
-- Name: lake; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE lake (
    name character varying(25) NOT NULL,
    area numeric,
    __search_id integer NOT NULL,
    CONSTRAINT lakear CHECK ((area >= (0)::numeric))
);


--
-- Name: language; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE language (
    country character varying(4) NOT NULL,
    name character varying(50) NOT NULL,
    percentage numeric,
    __search_id integer NOT NULL,
    CONSTRAINT languagepercent CHECK (((percentage > (0)::numeric) AND (percentage <= (100)::numeric)))
);


--
-- Name: located; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE located (
    city character varying(35),
    province character varying(32),
    country character varying(4),
    river character varying(20),
    lake character varying(25),
    sea character varying(25),
    __search_id integer NOT NULL
);


--
-- Name: merges_with; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE merges_with (
    sea1 character varying(25) NOT NULL,
    sea2 character varying(25) NOT NULL,
    __search_id integer NOT NULL
);


--
-- Name: mountain; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE mountain (
    name character varying(20) NOT NULL,
    height numeric,
    coordinates geocoord,
    __search_id integer NOT NULL,
    CONSTRAINT mountaincoord CHECK ((((((coordinates).longitude >= (-180)::numeric) AND ((coordinates).longitude <= (180)::numeric)) AND ((coordinates).latitude >= (-90)::numeric)) AND ((coordinates).latitude <= (90)::numeric))),
    CONSTRAINT mountainheight CHECK ((height >= (0)::numeric))
);


--
-- Name: organization; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE organization (
    abbreviation character varying(12) NOT NULL,
    name character varying(80) NOT NULL,
    city character varying(35),
    country character varying(4),
    province character varying(32),
    established date,
    __search_id integer NOT NULL
);


--
-- Name: politics; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE politics (
    country character varying(4) NOT NULL,
    independence date,
    government character varying(120),
    __search_id integer NOT NULL
);


--
-- Name: population; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE population (
    country character varying(4) NOT NULL,
    population_growth numeric,
    infant_mortality numeric,
    __search_id integer NOT NULL
);


--
-- Name: province; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE province (
    name character varying(32) NOT NULL,
    country character varying(4) NOT NULL,
    population numeric,
    area numeric,
    capital character varying(35),
    capprov character varying(32),
    __search_id integer NOT NULL,
    CONSTRAINT prar CHECK ((area >= (0)::numeric)),
    CONSTRAINT prpop CHECK ((population >= (0)::numeric))
);


--
-- Name: religion; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE religion (
    country character varying(4) NOT NULL,
    name character varying(50) NOT NULL,
    percentage numeric,
    __search_id integer NOT NULL,
    CONSTRAINT religionpercent CHECK (((percentage > (0)::numeric) AND (percentage <= (100)::numeric)))
);


--
-- Name: river; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE river (
    name character varying(20) NOT NULL,
    river character varying(20),
    lake character varying(20),
    sea character varying(25),
    length numeric,
    __search_id integer NOT NULL,
    CONSTRAINT riverlength CHECK ((length >= (0)::numeric))
);


--
-- Name: sea; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE sea (
    name character varying(25) NOT NULL,
    depth numeric,
    __search_id integer NOT NULL,
    CONSTRAINT seadepth CHECK ((depth >= (0)::numeric))
);


--
-- Name: __search_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE __search_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- Name: borderkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY borders
    ADD CONSTRAINT borderkey PRIMARY KEY (country1, country2);


--
-- Name: borders___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY borders
    ADD CONSTRAINT borders___search_id_key UNIQUE (__search_id);


--
-- Name: city___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY city
    ADD CONSTRAINT city___search_id_key UNIQUE (__search_id);


--
-- Name: citykey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY city
    ADD CONSTRAINT citykey PRIMARY KEY (name, country, province);


--
-- Name: continent___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY continent
    ADD CONSTRAINT continent___search_id_key UNIQUE (__search_id);


--
-- Name: continentkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY continent
    ADD CONSTRAINT continentkey PRIMARY KEY (name);


--
-- Name: country___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY country
    ADD CONSTRAINT country___search_id_key UNIQUE (__search_id);


--
-- Name: country_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY country
    ADD CONSTRAINT country_name_key UNIQUE (name);


--
-- Name: countrykey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY country
    ADD CONSTRAINT countrykey PRIMARY KEY (code);


--
-- Name: desert___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY desert
    ADD CONSTRAINT desert___search_id_key UNIQUE (__search_id);


--
-- Name: desertkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY desert
    ADD CONSTRAINT desertkey PRIMARY KEY (name);


--
-- Name: economy___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY economy
    ADD CONSTRAINT economy___search_id_key UNIQUE (__search_id);


--
-- Name: economykey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY economy
    ADD CONSTRAINT economykey PRIMARY KEY (country);


--
-- Name: encompasses___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY encompasses
    ADD CONSTRAINT encompasses___search_id_key UNIQUE (__search_id);


--
-- Name: encompasseskey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY encompasses
    ADD CONSTRAINT encompasseskey PRIMARY KEY (country, continent);


--
-- Name: ethnic_group___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY ethnic_group
    ADD CONSTRAINT ethnic_group___search_id_key UNIQUE (__search_id);


--
-- Name: ethnickey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY ethnic_group
    ADD CONSTRAINT ethnickey PRIMARY KEY (name, country);


--
-- Name: gdesertkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY geo_desert
    ADD CONSTRAINT gdesertkey PRIMARY KEY (province, country, desert);


--
-- Name: geo_desert___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY geo_desert
    ADD CONSTRAINT geo_desert___search_id_key UNIQUE (__search_id);


--
-- Name: geo_island___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY geo_island
    ADD CONSTRAINT geo_island___search_id_key UNIQUE (__search_id);


--
-- Name: geo_lake___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY geo_lake
    ADD CONSTRAINT geo_lake___search_id_key UNIQUE (__search_id);


--
-- Name: geo_mountain___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY geo_mountain
    ADD CONSTRAINT geo_mountain___search_id_key UNIQUE (__search_id);


--
-- Name: geo_river___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY geo_river
    ADD CONSTRAINT geo_river___search_id_key UNIQUE (__search_id);


--
-- Name: geo_sea___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY geo_sea
    ADD CONSTRAINT geo_sea___search_id_key UNIQUE (__search_id);


--
-- Name: gislandkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY geo_island
    ADD CONSTRAINT gislandkey PRIMARY KEY (province, country, island);


--
-- Name: glakekey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY geo_lake
    ADD CONSTRAINT glakekey PRIMARY KEY (province, country, lake);


--
-- Name: gmountainkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY geo_mountain
    ADD CONSTRAINT gmountainkey PRIMARY KEY (province, country, mountain);


--
-- Name: griverkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY geo_river
    ADD CONSTRAINT griverkey PRIMARY KEY (province, country, river);


--
-- Name: gseakey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY geo_sea
    ADD CONSTRAINT gseakey PRIMARY KEY (province, country, sea);


--
-- Name: is_member___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY is_member
    ADD CONSTRAINT is_member___search_id_key UNIQUE (__search_id);


--
-- Name: island___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY island
    ADD CONSTRAINT island___search_id_key UNIQUE (__search_id);


--
-- Name: islandkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY island
    ADD CONSTRAINT islandkey PRIMARY KEY (name);


--
-- Name: lake___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY lake
    ADD CONSTRAINT lake___search_id_key UNIQUE (__search_id);


--
-- Name: lakekey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY lake
    ADD CONSTRAINT lakekey PRIMARY KEY (name);


--
-- Name: language___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY language
    ADD CONSTRAINT language___search_id_key UNIQUE (__search_id);


--
-- Name: languagekey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY language
    ADD CONSTRAINT languagekey PRIMARY KEY (name, country);


--
-- Name: located___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY located
    ADD CONSTRAINT located___search_id_key UNIQUE (__search_id);


--
-- Name: memberkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY is_member
    ADD CONSTRAINT memberkey PRIMARY KEY (country, organization);


--
-- Name: merges_with___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY merges_with
    ADD CONSTRAINT merges_with___search_id_key UNIQUE (__search_id);


--
-- Name: mergeswithkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY merges_with
    ADD CONSTRAINT mergeswithkey PRIMARY KEY (sea1, sea2);


--
-- Name: mountain___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY mountain
    ADD CONSTRAINT mountain___search_id_key UNIQUE (__search_id);


--
-- Name: mountainkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY mountain
    ADD CONSTRAINT mountainkey PRIMARY KEY (name);


--
-- Name: organization___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT organization___search_id_key UNIQUE (__search_id);


--
-- Name: organization_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (abbreviation);


--
-- Name: orgnameunique; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT orgnameunique UNIQUE (name);


--
-- Name: politics___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY politics
    ADD CONSTRAINT politics___search_id_key UNIQUE (__search_id);


--
-- Name: politicskey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY politics
    ADD CONSTRAINT politicskey PRIMARY KEY (country);


--
-- Name: popkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY population
    ADD CONSTRAINT popkey PRIMARY KEY (country);


--
-- Name: population___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY population
    ADD CONSTRAINT population___search_id_key UNIQUE (__search_id);


--
-- Name: prkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY province
    ADD CONSTRAINT prkey PRIMARY KEY (name, country);


--
-- Name: province___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY province
    ADD CONSTRAINT province___search_id_key UNIQUE (__search_id);


--
-- Name: religion___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY religion
    ADD CONSTRAINT religion___search_id_key UNIQUE (__search_id);


--
-- Name: religionkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY religion
    ADD CONSTRAINT religionkey PRIMARY KEY (name, country);


--
-- Name: river___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY river
    ADD CONSTRAINT river___search_id_key UNIQUE (__search_id);


--
-- Name: riverkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY river
    ADD CONSTRAINT riverkey PRIMARY KEY (name);


--
-- Name: sea___search_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY sea
    ADD CONSTRAINT sea___search_id_key UNIQUE (__search_id);


--
-- Name: seakey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY sea
    ADD CONSTRAINT seakey PRIMARY KEY (name);


--
-- Name: city_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX city_name_idx ON city USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: continent_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX continent_name_idx ON continent USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: country_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX country_name_idx ON country USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: desert_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX desert_name_idx ON desert USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: ethnic_group_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX ethnic_group_name_idx ON ethnic_group USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: island_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX island_name_idx ON island USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: lake_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX lake_name_idx ON lake USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: language_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX language_name_idx ON language USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: mountain_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX mountain_name_idx ON mountain USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: organization_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX organization_name_idx ON organization USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: politics_government_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX politics_government_idx ON politics USING gin (to_tsvector('english'::regconfig, (government)::text));


--
-- Name: province_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX province_name_idx ON province USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: religion_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX religion_name_idx ON religion USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: river_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX river_name_idx ON river USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: sea_name_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX sea_name_idx ON sea USING gin (to_tsvector('english'::regconfig, (name)::text));


--
-- Name: borders_country1_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY borders
    ADD CONSTRAINT borders_country1_fkey FOREIGN KEY (country1) REFERENCES country(code);


--
-- Name: borders_country2_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY borders
    ADD CONSTRAINT borders_country2_fkey FOREIGN KEY (country2) REFERENCES country(code);


--
-- Name: city_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY city
    ADD CONSTRAINT city_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name) DEFERRABLE;


--
-- Name: country_code_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY country
    ADD CONSTRAINT country_code_fkey FOREIGN KEY (code, capital, province) REFERENCES city(country, name, province) DEFERRABLE;


--
-- Name: economy_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY economy
    ADD CONSTRAINT economy_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: encompasses_continent_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY encompasses
    ADD CONSTRAINT encompasses_continent_fkey FOREIGN KEY (continent) REFERENCES continent(name);


--
-- Name: encompasses_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY encompasses
    ADD CONSTRAINT encompasses_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: ethnic_group_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY ethnic_group
    ADD CONSTRAINT ethnic_group_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: geo_desert_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geo_desert
    ADD CONSTRAINT geo_desert_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name);


--
-- Name: geo_desert_desert_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geo_desert
    ADD CONSTRAINT geo_desert_desert_fkey FOREIGN KEY (desert) REFERENCES desert(name);


--
-- Name: geo_island_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geo_island
    ADD CONSTRAINT geo_island_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name);


--
-- Name: geo_island_island_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geo_island
    ADD CONSTRAINT geo_island_island_fkey FOREIGN KEY (island) REFERENCES island(name);


--
-- Name: geo_lake_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geo_lake
    ADD CONSTRAINT geo_lake_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name);


--
-- Name: geo_lake_lake_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geo_lake
    ADD CONSTRAINT geo_lake_lake_fkey FOREIGN KEY (lake) REFERENCES lake(name);


--
-- Name: geo_mountain_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geo_mountain
    ADD CONSTRAINT geo_mountain_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name);


--
-- Name: geo_mountain_mountain_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geo_mountain
    ADD CONSTRAINT geo_mountain_mountain_fkey FOREIGN KEY (mountain) REFERENCES mountain(name);


--
-- Name: geo_river_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geo_river
    ADD CONSTRAINT geo_river_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name);


--
-- Name: geo_river_river_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geo_river
    ADD CONSTRAINT geo_river_river_fkey FOREIGN KEY (river) REFERENCES river(name);


--
-- Name: geo_sea_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geo_sea
    ADD CONSTRAINT geo_sea_country_fkey FOREIGN KEY (country, province) REFERENCES province(country, name);


--
-- Name: geo_sea_sea_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY geo_sea
    ADD CONSTRAINT geo_sea_sea_fkey FOREIGN KEY (sea) REFERENCES sea(name);


--
-- Name: is_member_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY is_member
    ADD CONSTRAINT is_member_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: is_member_organization_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY is_member
    ADD CONSTRAINT is_member_organization_fkey FOREIGN KEY (organization) REFERENCES organization(abbreviation);


--
-- Name: language_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY language
    ADD CONSTRAINT language_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: located_city_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY located
    ADD CONSTRAINT located_city_fkey FOREIGN KEY (city, country, province) REFERENCES city(name, country, province);


--
-- Name: located_lake_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY located
    ADD CONSTRAINT located_lake_fkey FOREIGN KEY (lake) REFERENCES lake(name);


--
-- Name: located_river_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY located
    ADD CONSTRAINT located_river_fkey FOREIGN KEY (river) REFERENCES river(name);


--
-- Name: located_sea_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY located
    ADD CONSTRAINT located_sea_fkey FOREIGN KEY (sea) REFERENCES sea(name);


--
-- Name: merges_with_sea1_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY merges_with
    ADD CONSTRAINT merges_with_sea1_fkey FOREIGN KEY (sea1) REFERENCES sea(name);


--
-- Name: merges_with_sea2_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY merges_with
    ADD CONSTRAINT merges_with_sea2_fkey FOREIGN KEY (sea2) REFERENCES sea(name);


--
-- Name: organization_city_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT organization_city_fkey FOREIGN KEY (city, country, province) REFERENCES city(name, country, province);


--
-- Name: politics_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY politics
    ADD CONSTRAINT politics_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: population_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY population
    ADD CONSTRAINT population_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: province_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY province
    ADD CONSTRAINT province_country_fkey FOREIGN KEY (country) REFERENCES country(code) DEFERRABLE;


--
-- Name: province_country_fkey1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY province
    ADD CONSTRAINT province_country_fkey1 FOREIGN KEY (country, capital, capprov) REFERENCES city(country, name, province) DEFERRABLE;


--
-- Name: religion_country_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY religion
    ADD CONSTRAINT religion_country_fkey FOREIGN KEY (country) REFERENCES country(code);


--
-- Name: river_lake_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY river
    ADD CONSTRAINT river_lake_fkey FOREIGN KEY (lake) REFERENCES lake(name) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: river_river_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY river
    ADD CONSTRAINT river_river_fkey FOREIGN KEY (river) REFERENCES river(name) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: river_sea_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY river
    ADD CONSTRAINT river_sea_fkey FOREIGN KEY (sea) REFERENCES sea(name) DEFERRABLE INITIALLY DEFERRED;


--
-- PostgreSQL database dump complete
--

