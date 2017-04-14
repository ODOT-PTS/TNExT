
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

