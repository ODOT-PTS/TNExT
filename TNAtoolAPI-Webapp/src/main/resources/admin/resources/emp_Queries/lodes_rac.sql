--DROP TABLE IF EXISTS lodes_blocks_rac;
CREATE TABLE IF NOT EXISTS lodes_blocks_rac(
blockid character varying(15),
C000 int,
CA01 int,
CA02 int,
CA03 int,
CE01 int,
CE02 int,
CE03 int,
CNS01 int,
CNS02 int,
CNS03 int,
CNS04 int,
CNS05 int,
CNS06 int,
CNS07 int,
CNS08 int,
CNS09 int,
CNS10 int,
CNS11 int,
CNS12 int,
CNS13 int,
CNS14 int,
CNS15 int,
CNS16 int,
CNS17 int,
CNS18 int,
CNS19 int,
CNS20 int,
CR01 int,
CR02 int,
CR03 int,
CR04 int,
CR05 int,
CR07 int,
CT01 int,
CT02 int,
CD01 int,
CD02 int,
CD03 int,
CD04 int,
CS01 int,
CS02 int,
createdate character varying(8),
PRIMARY KEY(blockid)
);
ALTER TABLE lodes_blocks_rac DROP COLUMN IF EXISTS location;

DROP TABLE IF EXISTS temp_01;
CREATE TABLE temp_01 as
(SELECT * FROM lodes_blocks_rac LIMIT 1);

TRUNCATE TABLE temp_01;

COPY temp_01 
FROM '../../../../webapp/resources/admin/uploads/emp/rac.csv' DELIMITER ',' CSV HEADER;

INSERT INTO lodes_blocks_rac SELECT *
FROM temp_01
ON CONFLICT DO NOTHING; 

DROP TABLE temp_01;

ALTER TABLE lodes_blocks_rac
ADD COLUMN IF NOT EXISTS location geometry(Point,2993);

UPDATE lodes_blocks_rac
SET location = (SELECT location FROM census_blocks WHERE lodes_blocks_rac.blockid = census_blocks.blockid);

CREATE INDEX IF NOT EXISTS rac_blockid
  ON lodes_blocks_rac
  USING btree
  (blockid COLLATE pg_catalog."default");

CREATE INDEX IF NOT EXISTS rac_location
  ON lodes_blocks_rac
  USING gist
  (location);
ALTER TABLE lodes_blocks_rac CLUSTER ON rac_location;
