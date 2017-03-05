INSERT INTO lodes_blocks_wac SELECT *
FROM temp_01
ON CONFLICT DO NOTHING; 

DROP TABLE temp_01;

ALTER TABLE lodes_blocks_wac
ADD COLUMN IF NOT EXISTS location geometry(Point,2993);

UPDATE lodes_blocks_wac
SET location = (SELECT location FROM census_blocks WHERE lodes_blocks_wac.blockid = census_blocks.blockid);

CREATE INDEX IF NOT EXISTS wac_blockid
  ON lodes_blocks_wac
  USING btree
  (blockid COLLATE pg_catalog."default");

CREATE INDEX IF NOT EXISTS wac_location
  ON lodes_blocks_wac
  USING gist
  (location);
ALTER TABLE lodes_blocks_wac CLUSTER ON wac_location;
