ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS indo_europeanverywell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS indo_europeanwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS indo_europeannotwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS indo_europeannotatall double precision;

ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS asian_and_pacific_islandverywell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS asian_and_pacific_islandwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS asian_and_pacific_islandnotwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS asian_and_pacific_islandnotatall double precision;

ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS other_languagesverywell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS other_languageswell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS other_languagesnotwell double precision;
ALTER TABLE title_vi_blocks_float ADD COLUMN IF NOT EXISTS other_languagesnotatall double precision;