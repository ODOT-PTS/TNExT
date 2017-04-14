
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

UPDATE census_congdists 
SET population2015=futurepop.pop15,population2020=futurepop.pop20,population2025=futurepop.pop25,population2030=futurepop.pop30,
    population2035=futurepop.pop35,population2040=futurepop.pop40,population2045=futurepop.pop45,population2050=futurepop.pop50
FROM (SELECT congdistid AS id,
             Sum(population2015) as pop15, Sum(population2020) as pop20, Sum(population2025) as pop25, Sum(population2030) as pop30,
             Sum(population2035) as pop35, Sum(population2040) as pop40, Sum(population2045) as pop45, Sum(population2050) as pop50
      FROM census_blocks group by congdistid
      ) futurepop
WHERE census_congdists.congdistid=futurepop.id; 

