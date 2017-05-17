
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

