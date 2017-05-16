
ALTER TABLE title_vi_blocks_float 
ADD COLUMN IF NOT EXISTS location geometry(Point,2993);

UPDATE title_vi_blocks_float
SET location = census_blocks.location
FROM census_blocks
WHERE title_vi_blocks_float.blockid = census_blocks.blockid;

CREATE INDEX IF NOT EXISTS t6_location
  ON title_vi_blocks_float
  USING gist
  (location);
ALTER TABLE title_vi_blocks_float CLUSTER ON t6_location;


-----------------------------------
DROP TABLE IF EXISTS bg_b_dist;
DROP TABLE IF EXISTS t_b_dist;
drop table IF EXISTS blkGrp_b19037;
drop table IF EXISTS blkGrp_b03002;
drop table IF EXISTS blkGrp_b16004;
drop table IF EXISTS blkGrp_b17021;
drop table IF EXISTS tract_b18101;
