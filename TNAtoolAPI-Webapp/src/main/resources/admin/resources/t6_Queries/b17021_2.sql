
Delete from blkGrp_b17021 where STATEA <> '41';

update blkGrp_b17021 set gbid = lpad(STATEA, 2, '00') || lpad(COUNTYA, 3, '000') || lpad(TRACTA, 6, '000000') || lpad(BLKGRPA, 1, '0') ;

ALTER TABLE blkGrp_b17021 RENAME COLUMN below_poverty_total TO below_poverty;
ALTER TABLE blkGrp_b17021 RENAME COLUMN above_poverty_total TO above_poverty;


