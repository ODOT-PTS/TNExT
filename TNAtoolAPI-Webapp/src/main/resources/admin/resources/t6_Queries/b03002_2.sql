

update blkGrp_b03002 set gbid = lpad(STATEA, 2, '00') || lpad(COUNTYA, 3, '000') || lpad(TRACTA, 6, '000000') || lpad(BLKGRPA, 1, '0') ;

update blkGrp_b03002 set black_or_african_american = not_hispanic_or_latino_black_or_african_american_alone;
update blkGrp_b03002 set american_indian_and_alaska_native = not_hispanic_or_latino_american_indian_and_alaska_native_alone;
update blkGrp_b03002 set asian = not_hispanic_or_latino_asian_alone;
update blkGrp_b03002 set native_hawaiian_and_other_pacific_islander = not_hispanic_or_latino_native_hawaiian_and_other_pacific;
update blkGrp_b03002 set other = not_hispanic_or_latino_some_other_race_alone;
update blkGrp_b03002 set two_or_more = not_hispanic_or_latino_two_or_more_races;
update blkGrp_b03002 set white = not_hispanic_or_latino_white_alone;
update blkGrp_b03002 set hispanic_or_latino = hispanic__or__latino;


