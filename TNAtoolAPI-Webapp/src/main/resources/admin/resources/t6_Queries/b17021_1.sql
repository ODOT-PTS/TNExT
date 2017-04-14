DROP TABLE IF EXISTS blkGrp_b17021;
CREATE TABLE blkGrp_b17021 
(GISJOIN varchar(21), gbid varchar(21), STATEA varchar(11), COUNTYA varchar(11), TRACTA varchar(11), BLKGRPA varchar(11), 
below_poverty_total int, above_poverty_total int,
CONSTRAINT blkGrp_b17021_pkey PRIMARY KEY (GISJOIN))
WITH (
  OIDS=FALSE
);
ALTER TABLE blkGrp_b17021
  OWNER TO postgres;

