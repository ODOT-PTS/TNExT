DROP TABLE IF EXISTS blkGrp_b03002;
CREATE TABLE blkGrp_b03002 
(GISJOIN varchar(21), gbid varchar(21), STATEA varchar(11), COUNTYA varchar(11), TRACTA varchar(11), BLKGRPA varchar(11), 
not_hispanic_or_latino_white_alone int, not_hispanic_or_latino_black_or_african_american_alone int, 
not_hispanic_or_latino_american_indian_and_alaska_native_alone int, not_hispanic_or_latino_asian_alone int, 
not_hispanic_or_latino_native_hawaiian_and_other_pacific_islander_alone int, not_hispanic_or_latino_some_other_race_alone int, 
not_hispanic_or_latino_two_or_more_races int,hispanic__or__latino int,
black_or_african_american int, american_indian_and_alaska_native int, asian int, native_hawaiian_and_other_pacific_islander int, other int, two_or_more int, white int, hispanic_or_latino int,
CONSTRAINT blkGrp_b03002_pkey PRIMARY KEY (GISJOIN))
WITH (
  OIDS=FALSE
);
ALTER TABLE blkGrp_b03002
  OWNER TO postgres;


copy blkGrp_b03002(GISJOIN,STATEA,COUNTYA, TRACTA, BLKGRPA,
not_hispanic_or_latino_white_alone,not_hispanic_or_latino_black_or_african_american_alone,
not_hispanic_or_latino_american_indian_and_alaska_native_alone,not_hispanic_or_latino_asian_alone,
not_hispanic_or_latino_native_hawaiian_and_other_pacific_islander_alone,not_hispanic_or_latino_some_other_race_alone, 
not_hispanic_or_latino_two_or_more_races,hispanic__or__latino) 
FROM '../../../../webapp/resources/admin/uploads/t6/b03002.csv' DELIMITER ',' CSV HEADER;

Delete from blkGrp_b03002 where STATEA <> '41';

update blkGrp_b03002 set gbid = lpad(STATEA, 2, '00') || lpad(COUNTYA, 3, '000') || lpad(TRACTA, 6, '000000') || lpad(BLKGRPA, 1, '0') ;

update blkGrp_b03002 set black_or_african_american = not_hispanic_or_latino_black_or_african_american_alone;
update blkGrp_b03002 set american_indian_and_alaska_native = not_hispanic_or_latino_american_indian_and_alaska_native_alone;UEYE005+UEYE015;
update blkGrp_b03002 set asian = not_hispanic_or_latino_asian_alone;
update blkGrp_b03002 set native_hawaiian_and_other_pacific_islander = not_hispanic_or_latino_native_hawaiian_and_other_pacific_islander_alone;
update blkGrp_b03002 set other = not_hispanic_or_latino_some_other_race_alone;
update blkGrp_b03002 set two_or_more = not_hispanic_or_latino_two_or_more_races;
update blkGrp_b03002 set white = not_hispanic_or_latino_white_alone;
update blkGrp_b03002 set hispanic_or_latino = hispanic__or__latino;


