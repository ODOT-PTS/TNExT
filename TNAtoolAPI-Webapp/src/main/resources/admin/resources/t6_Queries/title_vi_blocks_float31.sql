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
