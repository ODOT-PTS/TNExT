ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS black_or_african_american double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS american_indian_and_alaska_native double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS asian double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS native_hawaiian_and_other_pacific_islander double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS other_races double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS two_or_more double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS white double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS hispanic_or_latino double precision;

UPDATE title_vi_blocks_float 
SET	black_or_african_american = blkgrp_b03002.black_or_african_american*ratio,
  	american_indian_and_alaska_native = blkgrp_b03002.american_indian_and_alaska_native*ratio,
	asian = blkgrp_b03002.asian*ratio,
	native_hawaiian_and_other_pacific_islander = blkgrp_b03002.native_hawaiian_and_other_pacific_islander*ratio,
	other_races = blkgrp_b03002.other*ratio,
	two_or_more = blkgrp_b03002.two_or_more*ratio,
	white = blkgrp_b03002.white*ratio,
	hispanic_or_latino = blkgrp_b03002.hispanic_or_latino*ratio
FROM blkgrp_b03002 
WHERE left(blockid,12) = blkgrp_b03002.gbid;