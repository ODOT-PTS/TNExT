DROP TABLE IF EXISTS title_vi_blocks_float;

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
FROM bg_b_dist;



ALTER TABLE title_vi_blocks_float add HH_under25 double precision;
ALTER TABLE title_vi_blocks_float add HH_from25to44 double precision;
ALTER TABLE title_vi_blocks_float add HH_from45to64 double precision;
ALTER TABLE title_vi_blocks_float add HH_above65 double precision;

with b19037 as(
	select * from blkGrp_b19037
)
UPDATE title_vi_blocks_float 
SET HH_under25 = ((SELECT under25 FROM b19037 WHERE gbid=left(blockid,12))*ratio),
	HH_from25to44 = ((SELECT from25to44 FROM b19037 WHERE gbid=left(blockid,12))*ratio),
	HH_from45to64 = ((SELECT from45to64 FROM b19037 WHERE gbid=left(blockid,12))*ratio),
	HH_above65 = ((SELECT above65 FROM b19037 WHERE gbid=left(blockid,12))*ratio);


-------------------
ALTER TABLE title_vi_blocks_float add black_or_african_american double precision;
ALTER TABLE title_vi_blocks_float add american_indian_and_alaska_native double precision;
ALTER TABLE title_vi_blocks_float add asian double precision;
ALTER TABLE title_vi_blocks_float add native_hawaiian_and_other_pacific_islander double precision;
ALTER TABLE title_vi_blocks_float add other_races double precision;
ALTER TABLE title_vi_blocks_float add two_or_more double precision;
ALTER TABLE title_vi_blocks_float add white double precision;
ALTER TABLE title_vi_blocks_float add hispanic_or_latino double precision;

with b03002 as(
	select * from blkgrp_b03002
)
UPDATE title_vi_blocks_float 
   SET black_or_african_american = ((SELECT black_or_african_american FROM b03002 WHERE gbid=left(blockid,12))*ratio),
	american_indian_and_alaska_native = ((SELECT american_indian_and_alaska_native FROM b03002 WHERE gbid=left(blockid,12))*ratio),
	asian = ((SELECT asian FROM b03002 WHERE gbid=left(blockid,12))*ratio),
	native_hawaiian_and_other_pacific_islander = ((SELECT native_hawaiian_and_other_pacific_islander FROM b03002 WHERE gbid=left(blockid,12))*ratio),
	other_races = ((SELECT other FROM b03002 WHERE gbid=left(blockid,12))*ratio),
	two_or_more = ((SELECT two_or_more FROM b03002 WHERE gbid=left(blockid,12))*ratio),
	white = ((SELECT white FROM b03002 WHERE gbid=left(blockid,12))*ratio),
	hispanic_or_latino = ((SELECT hispanic_or_latino FROM b03002 WHERE gbid=left(blockid,12))*ratio);
-------------------


ALTER TABLE title_vi_blocks_float add english double precision;
ALTER TABLE title_vi_blocks_float add spanish double precision;
ALTER TABLE title_vi_blocks_float add indo_european double precision;
ALTER TABLE title_vi_blocks_float add asian_and_pacific_island double precision;
ALTER TABLE title_vi_blocks_float add other_languages double precision;
ALTER TABLE title_vi_blocks_float add from5to17 double precision;
ALTER TABLE title_vi_blocks_float add from18to64 double precision;
ALTER TABLE title_vi_blocks_float add above65 double precision;

ALTER TABLE title_vi_blocks_float add spanishverywell double precision;
ALTER TABLE title_vi_blocks_float add spanishwell double precision;
ALTER TABLE title_vi_blocks_float add spanishnotwell double precision;
ALTER TABLE title_vi_blocks_float add spanishnotatall double precision;

ALTER TABLE title_vi_blocks_float add indo_europeanverywell double precision;
ALTER TABLE title_vi_blocks_float add indo_europeanwell double precision;
ALTER TABLE title_vi_blocks_float add indo_europeannotwell double precision;
ALTER TABLE title_vi_blocks_float add indo_europeannotatall double precision;

ALTER TABLE title_vi_blocks_float add asian_and_pacific_islandverywell double precision;
ALTER TABLE title_vi_blocks_float add asian_and_pacific_islandwell double precision;
ALTER TABLE title_vi_blocks_float add asian_and_pacific_islandnotwell double precision;
ALTER TABLE title_vi_blocks_float add asian_and_pacific_islandnotatall double precision;

ALTER TABLE title_vi_blocks_float add otherverywell double precision;
ALTER TABLE title_vi_blocks_float add otherwell double precision;
ALTER TABLE title_vi_blocks_float add othernotwell double precision;
ALTER TABLE title_vi_blocks_float add othernotatall double precision;


with b16004 as(
	select * from blkGrp_b16004
)
UPDATE title_vi_blocks_float 
   SET english = ((SELECT english FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	spanish = ((SELECT spanish FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	spanishverywell = ((SELECT spanishverywell FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	spanishwell = ((SELECT spanishwell FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	spanishnotwell = ((SELECT spanishnotwell FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	spanishnotatall = ((SELECT spanishnotatall FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	
	indo_european = ((SELECT indo_european FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	indo_europeanverywell = ((SELECT indo_europeanverywell FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	indo_europeanwell = ((SELECT indo_europeanwell FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	indo_europeannotwell = ((SELECT indo_europeannotwell FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	indo_europeannotatall = ((SELECT indo_europeannotatall FROM b16004 WHERE gbid=left(blockid,12))*ratio),

	asian_and_pacific_island = ((SELECT asian_and_pacific_island FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	asian_and_pacific_islandverywell = ((SELECT asian_and_pacific_islandverywell FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	asian_and_pacific_islandwell = ((SELECT asian_and_pacific_islandwell FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	asian_and_pacific_islandnotwell = ((SELECT asian_and_pacific_islandnotwell FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	asian_and_pacific_islandnotatall = ((SELECT asian_and_pacific_islandnotatall FROM b16004 WHERE gbid=left(blockid,12))*ratio),

	other_languages = ((SELECT other FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	otherverywell = ((SELECT otherverywell FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	otherwell = ((SELECT otherwell FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	othernotwell = ((SELECT othernotwell FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	othernotatall = ((SELECT othernotatall FROM b16004 WHERE gbid=left(blockid,12))*ratio),

	from5to17 = ((SELECT from5to17 FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	from18to64 = ((SELECT from18to64 FROM b16004 WHERE gbid=left(blockid,12))*ratio),
	above65 = ((SELECT above65 FROM b16004 WHERE gbid=left(blockid,12))*ratio);

----------------------------------------------
ALTER TABLE title_vi_blocks_float add below_poverty double precision;
ALTER TABLE title_vi_blocks_float add above_poverty double precision;

with b17021 as(
	select * from blkGrp_b17021
)
UPDATE title_vi_blocks_float 
   SET below_poverty = ((SELECT below_poverty FROM b17021 WHERE gbid=left(blockid,12))*ratio),
	above_poverty = ((SELECT above_poverty FROM b17021 WHERE gbid=left(blockid,12))*ratio);

--------------------
ALTER TABLE title_vi_blocks_float add tract_ratio double precision;

with tractRatio as(
	select blockid as id, ratio as r from t_b_dist 
)
UPDATE title_vi_blocks_float 
   SET tract_ratio = (select r from tractRatio where id=blockid);



ALTER TABLE title_vi_blocks_float add with_disability double precision;
ALTER TABLE title_vi_blocks_float add without_disability double precision;

with b18101 as(
	select * from tract_b18101
)
UPDATE title_vi_blocks_float 
   SET with_disability = ((SELECT with_disability FROM b18101 WHERE tid=left(blockid,11))*tract_ratio),
	without_disability = ((SELECT without_disability FROM b18101 WHERE tid=left(blockid,11))*tract_ratio);

------------------------------
ALTER TABLE title_vi_blocks_float 
ADD location geometry(Point,2993);

UPDATE title_vi_blocks_float
SET location = (SELECT location FROM census_blocks WHERE title_vi_blocks_float.blockid = census_blocks.blockid);

CREATE INDEX t6_location
  ON title_vi_blocks_float
  USING gist
  (location);
ALTER TABLE title_vi_blocks_float CLUSTER ON t6_location;

CREATE INDEX t6_blockid
  ON title_vi_blocks_float
  USING btree
  (blockid COLLATE pg_catalog."default");

-----------------------------------
DROP TABLE IF EXISTS bg_b_dist;
DROP TABLE IF EXISTS t_b_dist;
drop table IF EXISTS blkGrp_b19037;
drop table IF EXISTS blkGrp_b03002;
drop table IF EXISTS blkGrp_b16004;
drop table IF EXISTS blkGrp_b17021;
drop table IF EXISTS tract_b18101;
