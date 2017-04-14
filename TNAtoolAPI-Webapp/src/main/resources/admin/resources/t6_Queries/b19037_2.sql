

update blkGrp_b19037 set gbid = lpad(STATEA, 2, '00') || lpad(COUNTYA, 3, '000') || lpad(TRACTA, 6, '000000') || lpad(BLKGRPA, 1, '0') ;

ALTER TABLE blkGrp_b19037 RENAME COLUMN under_25 TO under25;
ALTER TABLE blkGrp_b19037 RENAME COLUMN from_25_to_44 TO from25to44;
ALTER TABLE blkGrp_b19037 RENAME COLUMN from_45_to_64 TO from45to64;
ALTER TABLE blkGrp_b19037 RENAME COLUMN above_65 TO above65


