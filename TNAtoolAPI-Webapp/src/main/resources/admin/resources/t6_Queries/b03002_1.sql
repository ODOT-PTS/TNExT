DROP TABLE IF EXISTS blkGrp_b03002;
CREATE TABLE blkGrp_b03002 
(GISJOIN varchar(21), gbid varchar(21), STATEA varchar(11), COUNTYA varchar(11), TRACTA varchar(11), BLKGRPA varchar(11), 
not_hispanic_or_latino_white_alone int, not_hispanic_or_latino_black_or_african_american_alone int, 
not_hispanic_or_latino_american_indian_and_alaska_native_alone int, not_hispanic_or_latino_asian_alone int, 
not_hispanic_or_latino_native_hawaiian_and_other_pacific int, not_hispanic_or_latino_some_other_race_alone int, 
not_hispanic_or_latino_two_or_more_races int,hispanic__or__latino int,
black_or_african_american int, american_indian_and_alaska_native int, asian int, native_hawaiian_and_other_pacific_islander int, other int, two_or_more int, white int, hispanic_or_latino int,
CONSTRAINT blkGrp_b03002_pkey PRIMARY KEY (GISJOIN))
WITH (
  OIDS=FALSE
);
ALTER TABLE blkGrp_b03002
  OWNER TO postgres;

