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
