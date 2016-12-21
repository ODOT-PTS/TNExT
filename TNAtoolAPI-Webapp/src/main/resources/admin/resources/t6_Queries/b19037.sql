drop table IF EXISTS blkGrp_b19037;

CREATE TABLE blkGrp_b19037 
(GISJOIN varchar(21), gbid varchar(21), STATEA varchar(11), COUNTYA varchar(11), TRACTA varchar(11), BLKGRPA varchar(11), 
under_25 int, from_25_to_44 int, from_45_to_64 int, above_65 int,
CONSTRAINT blkGrp_b19037_pkey PRIMARY KEY (GISJOIN))
WITH (
  OIDS=FALSE
);
ALTER TABLE blkGrp_b19037
  OWNER TO postgres;


copy blkGrp_b19037(GISJOIN,STATEA,COUNTYA, TRACTA, BLKGRPA,
under_25,from_25_to_44,from_45_to_64,above_65) 
FROM '../../../../webapp/resources/admin/uploads/t6/b19037.csv' DELIMITER ',' CSV HEADER;

Delete from blkGrp_b19037 where STATEA <> '41';

update blkGrp_b19037 set gbid = lpad(STATEA, 2, '00') || lpad(COUNTYA, 3, '000') || lpad(TRACTA, 6, '000000') || lpad(BLKGRPA, 1, '0') ;

ALTER TABLE blkGrp_b19037 RENAME COLUMN under_25 TO under25;
ALTER TABLE blkGrp_b19037 RENAME COLUMN from_25_to_44 TO from25to44;
ALTER TABLE blkGrp_b19037 RENAME COLUMN from_45_to_64 TO from45to64;
ALTER TABLE blkGrp_b19037 RENAME COLUMN above_65 TO above65


