--DROP TABLE IF EXISTS lodes_rac_projection_county CASCADE;
CREATE TABLE  IF NOT EXISTS lodes_rac_projection_county 
	(countyid character varying(5), e2010 int,ecurrent int,e2015 int,e2020 int,e2025 int,e2030 int,e2035 int,e2040 int,e2045 int,e2050 int);
	
DROP TABLE IF EXISTS temp_01;
CREATE TABLE temp_01 as
(SELECT * FROM lodes_rac_projection_county LIMIT 1);

TRUNCATE TABLE temp_01;

	
