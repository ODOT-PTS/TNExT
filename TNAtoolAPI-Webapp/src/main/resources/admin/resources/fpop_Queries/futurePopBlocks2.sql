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
   
   
DROP TABLE IF EXISTS blocks_future_pop;
DROP TABLE IF EXISTS counties_future_pop;
