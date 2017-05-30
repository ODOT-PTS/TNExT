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