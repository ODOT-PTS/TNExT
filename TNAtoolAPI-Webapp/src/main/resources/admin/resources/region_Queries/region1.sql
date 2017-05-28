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

alter table census_counties alter regionname type character varying (255);
alter table census_counties alter regionid type character varying (10);

alter table census_counties alter odotregionname type character varying (255);
alter table census_counties alter odotregionid type character varying (10);