----------------------------------------------------------------------
--1)Build a new database and make sure to create the postgis extention

--2)Download census 2010 joint shapefiles from https://www.census.gov/geo/maps-data/data/tiger-data.html.
----The joint shapefile might not be available for urbans. Download the shapefile and population data separately
----and build the the joint table manually. 
----The files are available at national level for all geographic areas beside blocks. Blocks' shapefiles are available at state level.

--4)Import the shapefiles into the database (using latin1 encoding).

--5)Build census tables.
-----------------------------------------------------------------------

------------------------------------------
-- Table: census_states_ref

DROP TABLE IF EXISTS census_states_ref;

CREATE TABLE census_states_ref
(
  stateid character varying(2) NOT NULL,
  sname character varying(255),
  housing bigint,
  population bigint,
  landarea bigint,
  waterarea bigint,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  shape geometry(MultiPolygon),
  population2010 integer,
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT census_states_ref_pkey PRIMARY KEY (stateid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_states_ref
  OWNER TO postgres;
  
INSERT INTO census_states_ref (stateid, sname, population,
				landarea, waterarea, lat, lon, shape)  
SELECT 	geoid10, name10, dp0010001,
		aland10, awater10, intptlat10::double precision, intptlon10::double precision, geom
FROM state_2010census_dp1;
	
update census_states_ref set population2010 = population;	
update census_states_ref set shape = st_setsrid(shape,4326)

------------------------------------------
-- Table: census_states

DROP TABLE IF EXISTS census_states;

CREATE TABLE census_states
(
  stateid character varying(2) NOT NULL,
  sname character varying(255),
  housing bigint,
  population bigint,
  landarea bigint,
  waterarea bigint,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  shape geometry(MultiPolygon),
  population2010 integer,
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT census_state_pkey PRIMARY KEY (stateid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_states
  OWNER TO postgres;

------------------------------------------
-- Table: census_congdists_ref

DROP TABLE IF EXISTS census_congdists_ref;

CREATE TABLE census_congdists_ref
(
  congdistid character varying(4) NOT NULL,
  stateid character varying(3),				--new
  cname character varying(255),
  housing bigint,
  population bigint,
  landarea bigint,
  waterarea bigint,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  shape geometry(MultiPolygon),
  population2010 integer,
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT census_congdists_ref_pkey PRIMARY KEY (congdistid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_congdists_ref
  OWNER TO postgres;

INSERT INTO census_congdists_ref (congdistid, stateid, cname, population,
				landarea, waterarea, lat, lon, shape)  
SELECT 	geoid10, left(geoid10,2), namelsad10, dp0010001,
	aland10, awater10, intptlat10::double precision, intptlon10::double precision, geom
FROM cd111_2010census_dp1;

update census_congdists_ref set population2010 = population;	
update census_congdists_ref set shape = st_setsrid(shape,4326);

-- Index: stateidsy

-- DROP INDEX stateidsy;

CREATE INDEX stateidsy_r
  ON census_congdists_ref
  USING btree
  (stateid COLLATE pg_catalog."default");
  
-- Index: congdistidsy

-- DROP INDEX congdistidsy;

CREATE INDEX congdistidsy_r
  ON census_congdists_ref
  USING btree
  (congdistid COLLATE pg_catalog."default");

  ------------------------------------------
-- Table: census_congdists

DROP TABLE IF EXISTS census_congdists;

CREATE TABLE census_congdists
(
  congdistid character varying(4) NOT NULL,
  stateid character varying(3),				--new
  cname character varying(255),
  housing bigint,
  population bigint,
  landarea bigint,
  waterarea bigint,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  shape geometry(MultiPolygon),
  population2010 integer,
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT census_congdist_pkey PRIMARY KEY (congdistid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_congdists
  OWNER TO postgres;
  
-- Index: stateidsy

-- DROP INDEX stateidsy;

CREATE INDEX stateidsy
  ON census_congdists
  USING btree
  (stateid COLLATE pg_catalog."default");
  
-- Index: congdistidsy

-- DROP INDEX congdistidsy;

CREATE INDEX congdistidsy
  ON census_congdists
  USING btree
  (congdistid COLLATE pg_catalog."default");

------------------------------------------
-- Table: census_counties_ref

DROP TABLE IF EXISTS census_counties_ref;

CREATE TABLE census_counties_ref
(
  countyid character varying(5) NOT NULL,
  cname character varying(50) NOT NULL,
  stateid character varying(3),				--new
  population bigint,
  housing bigint,
  landarea bigint,
  waterarea bigint,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  regionid character varying(1),
  regionname character varying(50),
  shape geometry(MultiPolygon),
  odotregionid character varying(2),
  odotregionname character varying(9),
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  population2010 integer,
  CONSTRAINT gtfs_counties_pkey PRIMARY KEY (countyid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_counties_ref
  OWNER TO postgres;
  
INSERT INTO census_counties_ref (countyid, stateid, cname, population,
				landarea, waterarea, lat, lon, shape)  
SELECT 	geoid10, left(geoid10,2), namelsad10, dp0010001,
		aland10, awater10, intptlat10::double precision, intptlon10::double precision, geom
FROM county_2010census_dp1;

update census_counties_ref set population2010 = population;	
update census_counties_ref set shape = st_setsrid(shape,4326);

-- Index: stateidrx

-- DROP INDEX stateidrx;

CREATE INDEX stateidrx_r
  ON census_counties_ref
  USING btree
  (stateid COLLATE pg_catalog."default");
  
-- Index: countyidrx

-- DROP INDEX countyidrx;

CREATE INDEX countyidrx_r
  ON census_counties_ref
  USING btree
  (countyid COLLATE pg_catalog."default");

------------------------------------------
-- Table: census_counties

DROP TABLE IF EXISTS census_counties;

CREATE TABLE census_counties
(
  countyid character varying(5) NOT NULL,
  cname character varying(50) NOT NULL,
  stateid character varying(3),				--new
  population bigint,
  housing bigint,
  landarea bigint,
  waterarea bigint,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  regionid character varying(1),
  regionname character varying(50),
  shape geometry(MultiPolygon),
  odotregionid character varying(2),
  odotregionname character varying(9),
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  population2010 integer,
  CONSTRAINT gtfs_countie_pkey PRIMARY KEY (countyid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_counties
  OWNER TO postgres;
  
-- Index: stateidrx

-- DROP INDEX stateidrx;

CREATE INDEX stateidrx
  ON census_counties
  USING btree
  (stateid COLLATE pg_catalog."default");
  
-- Index: countyidrx

-- DROP INDEX countyidrx;

CREATE INDEX countyidrx
  ON census_counties
  USING btree
  (countyid COLLATE pg_catalog."default");
  
------------------------------------------
-- Table: census_tracts_ref

DROP TABLE IF EXISTS census_tracts_ref;

CREATE TABLE census_tracts_ref
(
  tractid character varying(11) NOT NULL,
  countyid character varying(5),			--new
  stateid character varying(3),				--new
  tname character varying(50),
  tlongname character varying(50),
  housing bigint,
  population bigint,
  landarea bigint,
  waterarea bigint,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  shape geometry(MultiPolygon),
  population2010 integer,
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT census_tracts_ref_pkey PRIMARY KEY (tractid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_tracts_ref
  OWNER TO postgres;

INSERT INTO census_tracts_ref (tractid, countyid, stateid, tname, population,
				landarea, waterarea, lat, lon, shape)  
SELECT 	geoid10, left(geoid10,5), left(geoid10,2), namelsad10, dp0010001,
		aland10, awater10, intptlat10::double precision, intptlon10::double precision, geom
FROM tract_2010census_dp1;

update census_tracts_ref set population2010 = population;	
update census_tracts_ref set shape = st_setsrid(shape,4326);

-- Index: stateidry

-- DROP INDEX stateidry;

CREATE INDEX stateidry_r
  ON census_tracts_ref
  USING btree
  (stateid COLLATE pg_catalog."default");
  
-- Index: countyidry

-- DROP INDEX countyidry;

CREATE INDEX countyidry_r
  ON census_tracts_ref
  USING btree
  (countyid COLLATE pg_catalog."default");
  
-- Index: tractidry

-- DROP INDEX tractidry;

CREATE INDEX tractidry_r
  ON census_tracts_ref
  USING btree
  (tractid COLLATE pg_catalog."default");

------------------------------------------
-- Table: census_tracts

DROP TABLE IF EXISTS census_tracts;

CREATE TABLE census_tracts
(
  tractid character varying(11) NOT NULL,
  countyid character varying(5),			--new
  stateid character varying(3),				--new
  tname character varying(50),
  tlongname character varying(50),
  housing bigint,
  population bigint,
  landarea bigint,
  waterarea bigint,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  shape geometry(MultiPolygon),
  population2010 integer,
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT census_tract_pkey PRIMARY KEY (tractid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_tracts
  OWNER TO postgres;

-- Index: stateidry

-- DROP INDEX stateidry;

CREATE INDEX stateidry
  ON census_tracts
  USING btree
  (stateid COLLATE pg_catalog."default");
  
-- Index: countyidry

-- DROP INDEX countyidry;

CREATE INDEX countyidry
  ON census_tracts
  USING btree
  (countyid COLLATE pg_catalog."default");
  
-- Index: tractidry

-- DROP INDEX tractidry;

CREATE INDEX tractidry
  ON census_tracts
  USING btree
  (tractid COLLATE pg_catalog."default");

------------------------------------------
-- Table: census_places_ref
DROP TABLE IF EXISTS census_places_ref;

CREATE TABLE census_places_ref
(
  placeid character varying(7) NOT NULL,
  pname character varying(100) NOT NULL,
  stateid character varying(3),				--new
  population bigint,
  housing bigint,
  landarea bigint,
  waterarea bigint,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  shape geometry(MultiPolygon),
  population2010 integer,
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT gtfs_places_pkey PRIMARY KEY (placeid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_places_ref
  OWNER TO postgres;
  
INSERT INTO census_places_ref (placeid, stateid, pname, population,
				landarea, waterarea, lat, lon, shape)  
SELECT 	geoid10, left(geoid10,2), namelsad10, dp0010001,
	aland10, awater10, intptlat10::double precision, intptlon10::double precision, geom
FROM place_2010census_dp1;

update census_places_ref set population2010 = population;	
update census_places_ref set shape = st_setsrid(shape,4326);

-- Index: stateidsz

-- DROP INDEX stateidsz;

CREATE INDEX stateidsz_r
  ON census_places_ref
  USING btree
  (stateid COLLATE pg_catalog."default");
  
-- Index: placeidsz

-- DROP INDEX placeidsz;

CREATE INDEX placeidsz_r
  ON census_places_ref
  USING btree
  (placeid COLLATE pg_catalog."default");

------------------------------------------
-- Table: census_places
DROP TABLE IF EXISTS census_places;

CREATE TABLE census_places
(
  placeid character varying(7) NOT NULL,
  pname character varying(100) NOT NULL,
  stateid character varying(3),				--new
  population bigint,
  housing bigint,
  landarea bigint,
  waterarea bigint,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  shape geometry(MultiPolygon),
  population2010 integer,
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT gtfs_place_pkey PRIMARY KEY (placeid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_places
  OWNER TO postgres;
  
-- Index: stateidsz

-- DROP INDEX stateidsz;

CREATE INDEX stateidsz
  ON census_places
  USING btree
  (stateid COLLATE pg_catalog."default");
  
-- Index: placeidsz

-- DROP INDEX placeidsz;

CREATE INDEX placeidsz
  ON census_places
  USING btree
  (placeid COLLATE pg_catalog."default");

------------------------------------
--URBAN csv
------------------------------------
-- Table: "csv_2010_urban"

DROP TABLE IF EXISTS csv_2010_urban;

CREATE TABLE csv_2010_urban
(
  id serial NOT NULL,
  
  stateid character varying(3),
  geoid character varying(5),
  countyid character varying(5),
  placeid character varying(7),
  tractid character varying(11),
  blockid character varying(15),
  uaid character varying(5),
  urbantype character varying(1),					
  cdid character varying(4),
  areaname character varying(255),
  population bigint,
  CONSTRAINT csv_2010_urban_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE csv_2010_urban
  OWNER TO postgres;
  
copy csv_2010_urban(stateid,countyid,placeid,tractid,blockid,uaid,urbantype,cdid,areaname,population) 
from 'C:/Users/Administrator/Desktop/census/nhgis/nhgis0074_csv/nhgis0074_csv/nhgis0074_ds172_2010_urb_area.csv' DELIMITER ',' ENCODING 'LATIN1' CSV HEADER;

update csv_2010_urban set geoid = LPAD(uaid, 5, '0');
------------------------------------------
-- Table: census_urbans_ref

DROP TABLE IF EXISTS census_urbans_ref;

CREATE TABLE census_urbans_ref
(
  urbanid character varying(5) NOT NULL,
  uname character varying(255),
  utype character varying(1),
  poptype50k character varying(1),
  housing bigint,
  population bigint,
  landarea bigint,
  waterarea bigint,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  shape geometry(MultiPolygon),
  population2010 integer,
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT census_urbans_ref_pkey PRIMARY KEY (urbanid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_urbans_ref
  OWNER TO postgres;
  
WITH vtable AS(	SELECT geoid, urbantype, areaname, population,
		aland10 landarea, awater10 waterarea, intptlat10 lat, intptlon10 lon, geom shape
		FROM csv_2010_urban f_csv join tl_2010_us_uac10 f_shp
		ON f_csv.geoid=f_shp.geoid10) 
INSERT INTO census_urbans_ref (urbanid, utype, uname, population,
				landarea, waterarea, lat, lon, shape)  
SELECT 	geoid, urbantype, areaname, population,
	landarea, waterarea, lat::double precision, lon::double precision, shape
FROM vtable;
	
update census_urbans_ref set population2010 = population;	
update census_urbans_ref set shape = st_setsrid(shape,4326);
update census_urbans_ref set poptype50k = (CASE WHEN population>=50000 THEN 'U' ELSE 'R' END);

-- Index: urbanidrz

-- DROP INDEX urbanidrz;

CREATE INDEX urbanidrz_r
  ON census_urbans_ref
  USING btree
  (urbanid COLLATE pg_catalog."default");

------------------------------------------
-- Table: census_urbans

DROP TABLE IF EXISTS census_urbans;

CREATE TABLE census_urbans
(
  urbanid character varying(5) NOT NULL,
  uname character varying(255),
  utype character varying(1),
  poptype50k character varying(1),
  housing bigint,
  population bigint,
  landarea bigint,
  waterarea bigint,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  shape geometry(MultiPolygon),
  population2010 integer,
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT census_urban_pkey PRIMARY KEY (urbanid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_urbans
  OWNER TO postgres;
  
-- Index: urbanidrz

-- DROP INDEX urbanidrz;

CREATE INDEX urbanidrz
  ON census_urbans
  USING btree
  (urbanid COLLATE pg_catalog."default");

------------------------------------
--BLOCK csv import
------------------------------------
-- Table: "csv_2010_block_or"

DROP TABLE IF EXISTS csv_2010_block_or;

CREATE TABLE csv_2010_block_or
(
  id serial NOT NULL,
  geoid character varying(15),
  stateid character varying(3),
  countyid character varying(5),
  placeid character varying(7),
  tractid character varying(11),
  blockid character varying(15),
  uaid character varying(5),
  cdid character varying(4),
  areaname character varying(255),
  population bigint,
  CONSTRAINT csv_2010_block_or_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE csv_2010_block_or
  OWNER TO postgres;
  
copy csv_2010_block_or(stateid,countyid,placeid,tractid,blockid,uaid,cdid,areaname,population) 
from 'C:/Users/Administrator/Desktop/census/nhgis/nhgis0074_csv/nhgis0074_csv/nhgis0074_ds172_2010_block.csv' DELIMITER ',' ENCODING 'LATIN1' CSV HEADER;

update csv_2010_block_or set geoid = LPAD(stateid, 2, '0')||LPAD(countyid, 3, '0')||LPAD(tractid, 6, '0')||LPAD(blockid, 4, '0');

-------------------------------------
-- Table: census_blocks_ref

DROP TABLE IF EXISTS census_blocks_ref;

CREATE TABLE census_blocks_ref
(
  blockid character varying(15) NOT NULL,
  stateid character varying(3),				--new
  countyid character varying(5),			--new
  tractid character varying(11),			--new
  poptype character varying(5),
  landarea integer,
  waterarea integer,
  population integer NOT NULL,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  location geometry(Point,2993) NOT NULL,
  placeid character varying(7),
  congdistid character varying(4),
  regionid character varying(2),
  urbanid character varying(5),
  shape geometry(MultiPolygon),				--new
  population2010 integer,
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT gtfs_census_data_spatial_pkey PRIMARY KEY (blockid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_blocks_ref
  OWNER TO postgres;

WITH vtable AS(	SELECT geoid, stateid, cdid, countyid, tractid, population,
		aland10 landarea, awater10 waterarea, intptlat10 lat, intptlon10 lon, geom shape,
		ST_Transform(ST_SETSRID(ST_MakePoint(intptlon10::double precision, intptlat10::double precision),4326),2993) intpoint
		FROM csv_2010_block_or f_csv join tl_2010_41_tabblock10 f_shp
		ON f_csv.geoid=f_shp.geoid10) 
INSERT INTO census_blocks_ref (blockid, tractid, countyid, congdistid, stateid, population,
				landarea, waterarea, lat, lon, shape, location)  
SELECT 	geoid, tractid, countyid, cdid, stateid, population,
	landarea, waterarea, lat::double precision, lon::double precision, shape, intpoint
FROM vtable;



update census_blocks_ref set stateid = LPAD(stateid, 2, '0') where stateid=41;
update census_blocks_ref set congdistid = stateid||LPAD(congdistid, 2, '0') where stateid=41;
update census_blocks_ref set countyid = stateid||LPAD(countyid, 3, '0') where stateid=41;
update census_blocks_ref set tractid = countyid||LPAD(tractid, 6, '0') where stateid=41;
update census_blocks_ref set population2010 = population;	
update census_blocks_ref set shape = st_setsrid(shape,4326) where stateid=41;


UPDATE census_blocks_ref
SET
  urbanid     = ur.urbanid,
  poptype     = ur.poptype50k
FROM
   census_urbans_ref ur
WHERE 
    ST_Within(ST_SETSRID(st_makepoint(census_blocks_ref.lon, census_blocks_ref.lat),4326), ur.shape);
   
UPDATE census_blocks_ref SET poptype = 'R' WHERE poptype is null; 

UPDATE census_blocks_ref
SET
  placeid     = pl.placeid
FROM
   census_places_ref pl
WHERE 
   ST_Within(ST_SETSRID(st_makepoint(census_blocks_ref.lon, census_blocks_ref.lat),4326), pl.shape);
   


-- Index: idx_locations

-- DROP INDEX idx_locations;

CREATE INDEX idx_locations_r
  ON census_blocks_ref
  USING gist
  (location);
ALTER TABLE census_blocks_ref CLUSTER ON idx_locations;

-- Index: blockidsx

-- DROP INDEX blockidsx;

CREATE INDEX blockidsx_r
  ON census_blocks_ref
  USING btree
  (blockid COLLATE pg_catalog."default");

-- Index: stateidsx

-- DROP INDEX stateidsx;

CREATE INDEX stateidsx_r
  ON census_blocks_ref
  USING btree
  (stateid COLLATE pg_catalog."default");
  
-- Index: countyidsx

-- DROP INDEX countyidsx;

CREATE INDEX countyidsx_r
  ON census_blocks_ref
  USING btree
  (countyid COLLATE pg_catalog."default");
  
-- Index: tractidsx

-- DROP INDEX tractidsx;

CREATE INDEX tractidsx_r
  ON census_blocks_ref
  USING btree
  (tractid COLLATE pg_catalog."default");
  
-- Index: congdistidsx

-- DROP INDEX congdistidsx;

CREATE INDEX congdistidsx_r
  ON census_blocks_ref
  USING btree
  (congdistid COLLATE pg_catalog."default");


-- Index: placeidsx

-- DROP INDEX placeidsx;

CREATE INDEX placeidsx_r
  ON census_blocks_ref
  USING btree
  (placeid COLLATE pg_catalog."default");

-- Index: poptypesx

-- DROP INDEX poptypesx;

CREATE INDEX poptypesx
  ON census_blocks_ref
  USING btree
  (poptype COLLATE pg_catalog."default");

-- Index: regionidsx

-- DROP INDEX regionidsx;

CREATE INDEX regionidsx_r
  ON census_blocks_ref
  USING btree
  (regionid COLLATE pg_catalog."default");

-- Index: urbanidsx

-- DROP INDEX urbanidsx;

CREATE INDEX urbanidsx_r
  ON census_blocks_ref
  USING btree
  (urbanid COLLATE pg_catalog."default");
  
-------------------------------------
-- Table: census_blocks

DROP TABLE IF EXISTS census_blocks;

CREATE TABLE census_blocks
(
  blockid character varying(15) NOT NULL,
  stateid character varying(3),				--new
  countyid character varying(5),			--new
  tractid character varying(11),			--new
  poptype character varying(5),
  landarea integer,
  waterarea integer,
  population integer NOT NULL,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  location geometry(Point,2993) NOT NULL,
  placeid character varying(7),
  congdistid character varying(4),
  regionid character varying(2),
  urbanid character varying(5),
  shape geometry(MultiPolygon),				--new
  population2010 integer,
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT gtfs_census_datas_spatial_pkey PRIMARY KEY (blockid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_blocks
  OWNER TO postgres;



-- Index: idx_locations

-- DROP INDEX idx_locations;

CREATE INDEX idx_locations
  ON census_blocks
  USING gist
  (location);
ALTER TABLE census_blocks CLUSTER ON idx_locations;

-- Index: blockidsx

-- DROP INDEX blockidsx;

CREATE INDEX blockidsx
  ON census_blocks
  USING btree
  (blockid COLLATE pg_catalog."default");

-- Index: stateidsx

-- DROP INDEX stateidsx;

CREATE INDEX stateidsx
  ON census_blocks
  USING btree
  (stateid COLLATE pg_catalog."default");
  
-- Index: countyidsx

-- DROP INDEX countyidsx;

CREATE INDEX countyidsx
  ON census_blocks
  USING btree
  (countyid COLLATE pg_catalog."default");
  
-- Index: tractidsx

-- DROP INDEX tractidsx;

CREATE INDEX tractidsx
  ON census_blocks
  USING btree
  (tractid COLLATE pg_catalog."default");
  
-- Index: congdistidsx

-- DROP INDEX congdistidsx;

CREATE INDEX congdistidsx
  ON census_blocks
  USING btree
  (congdistid COLLATE pg_catalog."default");


-- Index: placeidsx

-- DROP INDEX placeidsx;

CREATE INDEX placeidsx
  ON census_blocks
  USING btree
  (placeid COLLATE pg_catalog."default");

-- Index: poptypesx

-- DROP INDEX poptypesx;

CREATE INDEX poptypesx
  ON census_blocks
  USING btree
  (poptype COLLATE pg_catalog."default");

-- Index: regionidsx

-- DROP INDEX regionidsx;

CREATE INDEX regionidsx
  ON census_blocks
  USING btree
  (regionid COLLATE pg_catalog."default");

-- Index: urbanidsx

-- DROP INDEX urbanidsx;

CREATE INDEX urbanidsx
  ON census_blocks
  USING btree
  (urbanid COLLATE pg_catalog."default");