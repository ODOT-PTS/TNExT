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


