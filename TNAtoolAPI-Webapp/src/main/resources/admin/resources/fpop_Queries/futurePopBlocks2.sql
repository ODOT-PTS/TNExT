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

