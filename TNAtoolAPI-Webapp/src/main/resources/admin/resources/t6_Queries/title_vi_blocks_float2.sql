
UPDATE title_vi_blocks_float 
SET english = blkGrp_b16004.english*ratio,
	spanish = blkGrp_b16004.spanish*ratio,
	spanishverywell = blkGrp_b16004.spanishverywell*ratio,
	spanishwell = blkGrp_b16004.spanishwell*ratio,
	spanishnotwell = blkGrp_b16004.spanishnotwell*ratio,
	spanishnotatall = blkGrp_b16004.spanishnotatall*ratio,
	
	indo_european = blkGrp_b16004.indo_european*ratio,
	indo_europeanverywell = blkGrp_b16004.indo_europeanverywell*ratio,
	indo_europeanwell = blkGrp_b16004.indo_europeanwell*ratio,
	indo_europeannotwell = blkGrp_b16004.indo_europeannotwell*ratio,
	indo_europeannotatall = blkGrp_b16004.indo_europeannotatall*ratio,

	asian_and_pacific_island = blkGrp_b16004.asian_and_pacific_island*ratio,
	asian_and_pacific_islandverywell = blkGrp_b16004.asian_and_pacific_islandverywell*ratio,
	asian_and_pacific_islandwell = blkGrp_b16004.asian_and_pacific_islandwell*ratio,
	asian_and_pacific_islandnotwell = blkGrp_b16004.asian_and_pacific_islandnotwell*ratio,
	asian_and_pacific_islandnotatall = blkGrp_b16004.asian_and_pacific_islandnotatall*ratio,

	other_languages = blkGrp_b16004.other*ratio,
	otherverywell = blkGrp_b16004.otherverywell*ratio,
	otherwell = blkGrp_b16004.otherwell*ratio,
	othernotwell = blkGrp_b16004.othernotwell*ratio,
	othernotatall = blkGrp_b16004.othernotatall*ratio,

	from5to17 = blkGrp_b16004.from5to17*ratio,
	from18to64 = blkGrp_b16004.from18to64*ratio,
	above65 = blkGrp_b16004.above65*ratio
FROM blkGrp_b16004 
WHERE left(blockid,12) = blkGrp_b16004.gbid;
----------------------------------------------
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS below_poverty double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS above_poverty double precision;


UPDATE title_vi_blocks_float 
SET below_poverty = blkGrp_b17021.below_poverty*ratio,
	above_poverty = blkGrp_b17021.above_poverty*ratio
FROM blkGrp_b17021 
WHERE left(blockid,12) = blkGrp_b17021.gbid;

--------------------
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS tract_ratio double precision;

UPDATE title_vi_blocks_float 
SET tract_ratio = t_b_dist.ratio
From t_b_dist
WHERE title_vi_blocks_float.blockid = t_b_dist.blockid;


ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS with_disability double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS without_disability double precision;

UPDATE title_vi_blocks_float 
SET with_disability = tract_b18101.with_disability*tract_ratio,
	without_disability = tract_b18101.without_disability*tract_ratio
FROM tract_b18101
WHERE left(blockid,11) = tract_b18101.tid;

------------------------------
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
