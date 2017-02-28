DROP TABLE if exists blocks_future_pop;

CREATE TABLE blocks_future_pop
(
  blockid character varying(15) NOT NULL,
  countyid character varying(5),
  ratio double precision,
  countypop double precision,
  population2010 double precision,
  population2015 double precision,
  population2020 double precision,
  population2025 double precision,
  population2030 double precision,
  population2035 double precision,
  population2040 double precision,
  population2045 double precision,
  population2050 double precision,
  CONSTRAINT blocks_future_pop_pkey PRIMARY KEY (blockid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE blocks_future_pop
  OWNER TO postgres;

INSERT INTO blocks_future_pop
(blockid,countyid,population2010)
SELECT blockid,left(blockid,5),population
FROM census_blocks;

CREATE INDEX IF NOT EXISTS blocks_future_pop_id
  ON blocks_future_pop
  USING btree
  (blockid COLLATE pg_catalog."default");


WITH blocks_future_pop_temp AS
     (SELECT countyid AS id,
             Sum(population2010) as pop 
      FROM blocks_future_pop group by countyid
     ) 
UPDATE blocks_future_pop 
   SET countypop = (SELECT pop FROM blocks_future_pop_temp WHERE id=countyid) ; 

update blocks_future_pop set ratio = population2010/countypop;

-------------------------------------------------------------
DROP TABLE if exists counties_future_pop;

CREATE TABLE counties_future_pop
(
  countyid character varying(5),
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT counties_future_pop_pkey PRIMARY KEY (countyid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE counties_future_pop
  OWNER TO postgres;

copy counties_future_pop(countyid,population2015,population2020,population2025, 
population2030, population2035,population2040,population2045,population2050) 
FROM '../../../../webapp/resources/admin/uploads/fpop/future_population.csv' DELIMITER ',' CSV HEADER;

CREATE INDEX IF NOT EXISTS counties_future_pop_id
  ON counties_future_pop
  USING btree
  (countyid COLLATE pg_catalog."default");

-------------------------------------------------------------
ALTER TABLE census_counties add COLUMN IF NOT EXISTS population2010 integer;
ALTER TABLE census_counties add COLUMN IF NOT EXISTS population2015 integer;
ALTER TABLE census_counties add COLUMN IF NOT EXISTS population2015 integer;
ALTER TABLE census_counties add COLUMN IF NOT EXISTS population2020 integer;
ALTER TABLE census_counties add COLUMN IF NOT EXISTS population2025 integer;
ALTER TABLE census_counties add COLUMN IF NOT EXISTS population2030 integer;
ALTER TABLE census_counties add COLUMN IF NOT EXISTS population2035 integer;
ALTER TABLE census_counties add COLUMN IF NOT EXISTS population2040 integer;
ALTER TABLE census_counties add COLUMN IF NOT EXISTS population2045 integer;
ALTER TABLE census_counties add COLUMN IF NOT EXISTS population2050 integer;

update census_counties set population2010 = population;

UPDATE census_counties 
   SET population2015=countypop.population2015,population2020=countypop.population2020,population2025=countypop.population2025,population2030=countypop.population2030,
       population2035=countypop.population2035,population2040=countypop.population2040,population2045=countypop.population2045,population2050=countypop.population2050
   FROM counties_future_pop countypop
   WHERE census_counties.countyid=countypop.countyid; 

-------------------------------------------------------------
UPDATE blocks_future_pop 
   SET 	population2015=countypop.population2015*blocks_future_pop.ratio,population2020=countypop.population2020*blocks_future_pop.ratio,
	population2025=countypop.population2025*blocks_future_pop.ratio,population2030=countypop.population2030*blocks_future_pop.ratio,
	population2035=countypop.population2035*blocks_future_pop.ratio,population2040=countypop.population2040*blocks_future_pop.ratio,
	population2045=countypop.population2045*blocks_future_pop.ratio,population2050=countypop.population2050*blocks_future_pop.ratio
   FROM census_counties countypop
   WHERE blocks_future_pop.countyid=countypop.countyid; 

-------------------------------------------------------------
ALTER TABLE census_blocks add COLUMN IF NOT EXISTS population2010 integer;
ALTER TABLE census_blocks add COLUMN IF NOT EXISTS population2015 integer;
ALTER TABLE census_blocks add COLUMN IF NOT EXISTS population2020 integer;
ALTER TABLE census_blocks add COLUMN IF NOT EXISTS population2025 integer;
ALTER TABLE census_blocks add COLUMN IF NOT EXISTS population2030 integer;
ALTER TABLE census_blocks add COLUMN IF NOT EXISTS population2035 integer;
ALTER TABLE census_blocks add COLUMN IF NOT EXISTS population2040 integer;
ALTER TABLE census_blocks add COLUMN IF NOT EXISTS population2045 integer;
ALTER TABLE census_blocks add COLUMN IF NOT EXISTS population2050 integer;

update census_blocks set population2010 = population;


UPDATE census_blocks 
   SET population2015=futurepop.population2015,population2020=futurepop.population2020,population2025=futurepop.population2025,population2030=futurepop.population2030,
       population2035=futurepop.population2035,population2040=futurepop.population2040,population2045=futurepop.population2045,population2050=futurepop.population2050
   FROM blocks_future_pop futurepop
   WHERE census_blocks.blockid=futurepop.blockid; 

-------------------------------------------------------------

ALTER TABLE census_states add COLUMN IF NOT EXISTS population2010 integer;
ALTER TABLE census_states add COLUMN IF NOT EXISTS population2015 integer;
ALTER TABLE census_states add COLUMN IF NOT EXISTS population2020 integer;
ALTER TABLE census_states add COLUMN IF NOT EXISTS population2025 integer;
ALTER TABLE census_states add COLUMN IF NOT EXISTS population2030 integer;
ALTER TABLE census_states add COLUMN IF NOT EXISTS population2035 integer;
ALTER TABLE census_states add COLUMN IF NOT EXISTS population2040 integer;
ALTER TABLE census_states add COLUMN IF NOT EXISTS population2045 integer;
ALTER TABLE census_states add COLUMN IF NOT EXISTS population2050 integer;

update census_states set population2010 = population;

WITH census_blocks_temp AS
     (SELECT stateid AS id,
             Sum(population2015) as pop15, Sum(population2020) as pop20, Sum(population2025) as pop25, Sum(population2030) as pop30,
             Sum(population2035) as pop35, Sum(population2040) as pop40, Sum(population2045) as pop45, Sum(population2050) as pop50
      FROM census_blocks group by stateid
     ) 
UPDATE census_states 
   SET population2015=futurepop.pop15,population2020=futurepop.pop20,population2025=futurepop.pop25,population2030=futurepop.pop30,
       population2035=futurepop.pop35,population2040=futurepop.pop40,population2045=futurepop.pop45,population2050=futurepop.pop50
   FROM census_blocks_temp futurepop
   WHERE census_states.stateid=futurepop.id; 
-------------------------------------------------------------

ALTER TABLE census_congdists add COLUMN IF NOT EXISTS population2010 integer;
ALTER TABLE census_congdists add COLUMN IF NOT EXISTS population2015 integer;
ALTER TABLE census_congdists add COLUMN IF NOT EXISTS population2020 integer;
ALTER TABLE census_congdists add COLUMN IF NOT EXISTS population2025 integer;
ALTER TABLE census_congdists add COLUMN IF NOT EXISTS population2030 integer;
ALTER TABLE census_congdists add COLUMN IF NOT EXISTS population2035 integer;
ALTER TABLE census_congdists add COLUMN IF NOT EXISTS population2040 integer;
ALTER TABLE census_congdists add COLUMN IF NOT EXISTS population2045 integer;
ALTER TABLE census_congdists add COLUMN IF NOT EXISTS population2050 integer;

update census_congdists set population2010 = population;

WITH census_blocks_temp AS
     (SELECT congdistid AS id,
             Sum(population2015) as pop15, Sum(population2020) as pop20, Sum(population2025) as pop25, Sum(population2030) as pop30,
             Sum(population2035) as pop35, Sum(population2040) as pop40, Sum(population2045) as pop45, Sum(population2050) as pop50
      FROM census_blocks group by congdistid
     ) 
UPDATE census_congdists 
   SET population2015=futurepop.pop15,population2020=futurepop.pop20,population2025=futurepop.pop25,population2030=futurepop.pop30,
       population2035=futurepop.pop35,population2040=futurepop.pop40,population2045=futurepop.pop45,population2050=futurepop.pop50
   FROM census_blocks_temp futurepop
   WHERE census_congdists.congdistid=futurepop.id; 

-------------------------------------------------------------
ALTER TABLE census_places add COLUMN IF NOT EXISTS population2010 integer;
ALTER TABLE census_places add COLUMN IF NOT EXISTS population2015 integer;
ALTER TABLE census_places add COLUMN IF NOT EXISTS population2020 integer;
ALTER TABLE census_places add COLUMN IF NOT EXISTS population2025 integer;
ALTER TABLE census_places add COLUMN IF NOT EXISTS population2030 integer;
ALTER TABLE census_places add COLUMN IF NOT EXISTS population2035 integer;
ALTER TABLE census_places add COLUMN IF NOT EXISTS population2040 integer;
ALTER TABLE census_places add COLUMN IF NOT EXISTS population2045 integer;
ALTER TABLE census_places add COLUMN IF NOT EXISTS population2050 integer;

update census_places set population2010 = population;

WITH census_blocks_temp AS
     (SELECT placeid AS id,
             Sum(population2015) as pop15, Sum(population2020) as pop20, Sum(population2025) as pop25, Sum(population2030) as pop30,
             Sum(population2035) as pop35, Sum(population2040) as pop40, Sum(population2045) as pop45, Sum(population2050) as pop50
      FROM census_blocks group by placeid
     ) 
UPDATE census_places 
   SET population2015=futurepop.pop15,population2020=futurepop.pop20,population2025=futurepop.pop25,population2030=futurepop.pop30,
       population2035=futurepop.pop35,population2040=futurepop.pop40,population2045=futurepop.pop45,population2050=futurepop.pop50
   FROM census_blocks_temp futurepop
   WHERE census_places.placeid=futurepop.id; 
-------------------------------------------------------------
ALTER TABLE census_tracts add COLUMN IF NOT EXISTS population2010 integer;
ALTER TABLE census_tracts add COLUMN IF NOT EXISTS population2015 integer;
ALTER TABLE census_tracts add COLUMN IF NOT EXISTS population2020 integer;
ALTER TABLE census_tracts add COLUMN IF NOT EXISTS population2025 integer;
ALTER TABLE census_tracts add COLUMN IF NOT EXISTS population2030 integer;
ALTER TABLE census_tracts add COLUMN IF NOT EXISTS population2035 integer;
ALTER TABLE census_tracts add COLUMN IF NOT EXISTS population2040 integer;
ALTER TABLE census_tracts add COLUMN IF NOT EXISTS population2045 integer;
ALTER TABLE census_tracts add COLUMN IF NOT EXISTS population2050 integer;

update census_tracts set population2010 = population;

WITH census_blocks_temp AS
     (SELECT left(blockid,11) AS id,
             Sum(population2015) as pop15, Sum(population2020) as pop20, Sum(population2025) as pop25, Sum(population2030) as pop30,
             Sum(population2035) as pop35, Sum(population2040) as pop40, Sum(population2045) as pop45, Sum(population2050) as pop50
      FROM census_blocks group by left(blockid,11)
     ) 
UPDATE census_tracts 
   SET population2015=futurepop.pop15,population2020=futurepop.pop20,population2025=futurepop.pop25,population2030=futurepop.pop30,
       population2035=futurepop.pop35,population2040=futurepop.pop40,population2045=futurepop.pop45,population2050=futurepop.pop50
   FROM census_blocks_temp futurepop
   WHERE census_tracts.tractid=futurepop.id; 
-------------------------------------------------------------
ALTER TABLE census_urbans add COLUMN IF NOT EXISTS population2010 integer;
ALTER TABLE census_urbans add COLUMN IF NOT EXISTS population2015 integer;
ALTER TABLE census_urbans add COLUMN IF NOT EXISTS population2020 integer;
ALTER TABLE census_urbans add COLUMN IF NOT EXISTS population2025 integer;
ALTER TABLE census_urbans add COLUMN IF NOT EXISTS population2030 integer;
ALTER TABLE census_urbans add COLUMN IF NOT EXISTS population2035 integer;
ALTER TABLE census_urbans add COLUMN IF NOT EXISTS population2040 integer;
ALTER TABLE census_urbans add COLUMN IF NOT EXISTS population2045 integer;
ALTER TABLE census_urbans add COLUMN IF NOT EXISTS population2050 integer;

update census_urbans set population2010 = population;

WITH census_blocks_temp AS
     (SELECT urbanid AS id,
             Sum(population2015) as pop15, Sum(population2020) as pop20, Sum(population2025) as pop25, Sum(population2030) as pop30,
             Sum(population2035) as pop35, Sum(population2040) as pop40, Sum(population2045) as pop45, Sum(population2050) as pop50
      FROM census_blocks group by urbanid
     ) 
UPDATE census_urbans 
   SET population2015=futurepop.pop15,population2020=futurepop.pop20,population2025=futurepop.pop25,population2030=futurepop.pop30,
       population2035=futurepop.pop35,population2040=futurepop.pop40,population2045=futurepop.pop45,population2050=futurepop.pop50
   FROM census_blocks_temp futurepop
   WHERE census_urbans.urbanid=futurepop.id; 
   
   
﻿DROP TABLE if exists blocks_future_pop;
﻿DROP TABLE if exists counties_future_pop;

