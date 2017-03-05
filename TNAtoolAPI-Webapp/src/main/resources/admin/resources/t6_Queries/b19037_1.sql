DROP TABLE IF EXISTS blkGrp_b19037;
CREATE TABLE blkGrp_b19037 
(GISJOIN varchar(21), gbid varchar(21), STATEA varchar(11), COUNTYA varchar(11), TRACTA varchar(11), BLKGRPA varchar(11), 
under_25 int, from_25_to_44 int, from_45_to_64 int, above_65 int,
CONSTRAINT blkGrp_b19037_pkey PRIMARY KEY (GISJOIN))
WITH (
  OIDS=FALSE
);
ALTER TABLE blkGrp_b19037
  OWNER TO postgres;

