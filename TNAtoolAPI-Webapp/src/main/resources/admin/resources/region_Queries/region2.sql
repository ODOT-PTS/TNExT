CREATE INDEX IF NOT EXISTS counties_regions_id
  ON counties_regions
  USING btree
  (countyid COLLATE pg_catalog."default");

-------------------------------------------------------------

UPDATE census_counties 
   SET regionid=counties_regions.regionid,regionname=counties_regions.regionname
   FROM counties_regions 
   WHERE census_counties.countyid=counties_regions.countyid; 
   
UPDATE census_counties SET odotregionid = regionid;
UPDATE census_counties SET odotregionname = regionname;

-------------------------------------------------------------
UPDATE census_blocks 
   SET regionid=counties_regions.regionid
   FROM counties_regions 
   WHERE census_blocks.countyid=counties_regions.countyid; 

   

DROP TABLE IF EXISTS counties_regions;