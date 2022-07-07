drop table if exists population_state_csv;
create table population_state_csv (
    population bigint,
    housing bigint,
    name text,
    state text primary key
);

\copy population_state_csv from 'population.state.csv' WITH DELIMITER ',' CSV HEADER;

drop table if exists population_block_csv;
create table population_block_csv (
    population bigint,
    housing bigint,
    name text,
    state text,
    county text,
    tract text,
    block text,
    primary key (county, tract, block)
);

\copy population_block_csv from 'population.block.csv' WITH DELIMITER ',' CSV HEADER;

drop table if exists population_congressional_district_csv;
create table population_congressional_district_csv (
    population bigint,
    housing bigint,
    name text,
    state text,
    congressional_district text  primary key
);

\copy population_congressional_district_csv from 'population.congressional_district.csv' WITH DELIMITER ',' CSV HEADER;

drop table if exists population_county_csv;
create table population_county_csv (
    population bigint,
    housing bigint,
    name text,
    state text,
    county text primary key
);

\copy population_county_csv from 'population.county.csv' WITH DELIMITER ',' CSV HEADER;

drop table if exists population_place_csv;
create table population_place_csv (
    population bigint,
    housing bigint,
    name text,
    state text,
    place text  primary key
);

\copy population_place_csv from 'population.place.csv' WITH DELIMITER ',' CSV HEADER;

drop table if exists population_tract_csv;
create table population_tract_csv (
    population bigint,
    housing bigint,
    name text,
    state text,
    county text,
    tract text,
    primary key (county, tract)
);

\copy population_tract_csv from 'population.tract.csv' WITH DELIMITER ',' CSV HEADER;

drop table if exists population_urban_area_csv;
create table population_urban_area_csv (
    population bigint,
    housing bigint,
    name text,
    state text,
    urban_area text  primary key
);

\copy population_urban_area_csv from 'population.urban_area.csv' WITH DELIMITER ',' CSV HEADER;
