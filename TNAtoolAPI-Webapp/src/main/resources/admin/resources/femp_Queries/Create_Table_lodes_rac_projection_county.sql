--DROP TABLE IF EXISTS lodes_rac_projection_county CASCADE;
CREATE TABLE  IF NOT EXISTS lodes_rac_projection_county 
	(countyid character varying(5), e2010 int,ecurrent int,e2015 int,e2020 int,e2025 int,e2030 int,e2035 int,e2040 int,e2045 int,e2050 int);
	
DROP TABLE IF EXISTS temp_01;
CREATE TABLE temp_01 as
(SELECT * FROM lodes_rac_projection_county LIMIT 1);

TRUNCATE TABLE temp_01;

COPY temp_01 (countyid, ecurrent,e2010,e2015,e2020,e2025,e2030,e2035,e2040,e2045,e2050) 
	FROM '../../../../webapp/resources/admin/uploads/femp/future_employment.csv' DELIMITER ',' CSV HEADER;
	
INSERT INTO lodes_rac_projection_county SELECT *
FROM temp_01
ON CONFLICT DO NOTHING; 

DROP TABLE temp_01;