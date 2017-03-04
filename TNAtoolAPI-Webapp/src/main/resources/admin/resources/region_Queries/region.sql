DROP TABLE if exists counties_regions;

CREATE TABLE counties_regions
(
  countyid character varying(5),
  regionid character varying(10),
  regionname character varying(255),
  CONSTRAINT counties_region_pkey PRIMARY KEY (countyid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE counties_regions
  OWNER TO postgres;

copy counties_regions(countyid,regionid,regionname) 
FROM '../../../../webapp/resources/admin/uploads/region/regions.csv' DELIMITER ',' CSV HEADER;

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