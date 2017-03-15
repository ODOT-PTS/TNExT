--DROP TABLE IF EXISTS title_vi_blocks_float;
CREATE TABLE IF NOT EXISTS title_vi_blocks_float
(
  blockid character varying(15) NOT NULL,
  ratio double precision,
  CONSTRAINT title_vi_blocks_float_pkey PRIMARY KEY (blockid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE title_vi_blocks_float
  OWNER TO postgres;


INSERT INTO title_vi_blocks_float
(blockid,ratio)
SELECT blockid,ratio
FROM bg_b_dist
ON CONFLICT DO NOTHING;

CREATE INDEX IF NOT EXISTS t6_blockid
  ON title_vi_blocks_float
  USING btree
  (blockid COLLATE pg_catalog."default");



ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS HH_under25 double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS HH_from25to44 double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS HH_from45to64 double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS HH_above65 double precision;

UPDATE title_vi_blocks_float 
SET HH_under25 = blkGrp_b19037.under25*ratio,
	HH_from25to44 = blkGrp_b19037.from25to44*ratio,
	HH_from45to64 = blkGrp_b19037.from45to64*ratio,
	HH_above65 = blkGrp_b19037.above65*ratio
FROM blkGrp_b19037 
WHERE left(blockid,12) = blkGrp_b19037.gbid;

-------------------
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
-------------------

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
