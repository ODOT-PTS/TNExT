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
