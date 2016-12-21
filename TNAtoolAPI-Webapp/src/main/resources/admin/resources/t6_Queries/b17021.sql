drop table IF EXISTS blkGrp_b17021;

CREATE TABLE blkGrp_b17021 
(GISJOIN varchar(21), gbid varchar(21), STATEA varchar(11), COUNTYA varchar(11), TRACTA varchar(11), BLKGRPA varchar(11), 
below_poverty_total int, above_poverty_total int,
CONSTRAINT blkGrp_b17021_pkey PRIMARY KEY (GISJOIN))
WITH (
  OIDS=FALSE
);
ALTER TABLE blkGrp_b17021
  OWNER TO postgres;


copy blkGrp_b17021(GISJOIN,STATEA,COUNTYA, TRACTA, BLKGRPA,
below_poverty_total,above_poverty_total) 
FROM '../../../../webapp/resources/admin/uploads/t6/b17021.csv' DELIMITER ',' CSV HEADER;

Delete from blkGrp_b17021 where STATEA <> '41';

update blkGrp_b17021 set gbid = lpad(STATEA, 2, '00') || lpad(COUNTYA, 3, '000') || lpad(TRACTA, 6, '000000') || lpad(BLKGRPA, 1, '0') ;

ALTER TABLE blkGrp_b17021 RENAME COLUMN below_poverty_total TO below_poverty;
ALTER TABLE blkGrp_b17021 RENAME COLUMN above_poverty_total TO above_poverty;


