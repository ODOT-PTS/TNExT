DROP TABLE IF EXISTS lodes_rac_projection_county CASCADE;
CREATE TABLE lodes_rac_projection_county 
	(countyid character varying(5), e2010 int,ecurrent int,e2015 int,e2020 int,e2025 int,e2030 int,e2035 int,e2040 int,e2045 int,e2050 int);
COPY lodes_rac_projection_county (countyid, ecurrent,e2010,e2015,e2020,e2025,e2030,e2035,e2040,e2045,e2050) 
	FROM '../../../../webapp/resources/admin/uploads/femp/future_employment.csv' DELIMITER ',' CSV HEADER;