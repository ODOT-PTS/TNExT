ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS english double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS spanish double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS indo_european double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS asian_and_pacific_island double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS other_languages double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS from5to17 double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS from18to64 double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS above65 double precision;

ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS spanishverywell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS spanishwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS spanishnotwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS spanishnotatall double precision;

ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS indo_europeanverywell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS indo_europeanwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS indo_europeannotwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS indo_europeannotatall double precision;

ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS asian_and_pacific_islandverywell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS asian_and_pacific_islandwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS asian_and_pacific_islandnotwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS asian_and_pacific_islandnotatall double precision;

ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS otherverywell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS otherwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS othernotwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS othernotatall double precision;


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
