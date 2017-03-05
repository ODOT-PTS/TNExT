DROP TABLE IF EXISTS blocks_future_pop;
CREATE TABLE blocks_future_pop
(
  blockid character varying(15) NOT NULL,
  countyid character varying(5),
  ratio double precision,
  countypop double precision,
  population2010 double precision,
  population2015 double precision,
  population2020 double precision,
  population2025 double precision,
  population2030 double precision,
  population2035 double precision,
  population2040 double precision,
  population2045 double precision,
  population2050 double precision,
  CONSTRAINT blocks_future_pop_pkey PRIMARY KEY (blockid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE blocks_future_pop
  OWNER TO postgres;

INSERT INTO blocks_future_pop
(blockid,countyid,population2010)
SELECT blockid,left(blockid,5),population
FROM census_blocks;

CREATE INDEX IF NOT EXISTS blocks_future_pop_id
  ON blocks_future_pop
  USING btree
  (blockid COLLATE pg_catalog."default");


WITH blocks_future_pop_temp AS
     (SELECT countyid AS id,
             Sum(population2010) as pop 
      FROM blocks_future_pop group by countyid
     ) 
UPDATE blocks_future_pop 
   SET countypop = (SELECT pop FROM blocks_future_pop_temp WHERE id=countyid) ; 

update blocks_future_pop set ratio = population2010/countypop;

-------------------------------------------------------------
DROP TABLE if exists counties_future_pop;

CREATE TABLE counties_future_pop
(
  countyid character varying(5),
  population2015 integer,
  population2020 integer,
  population2025 integer,
  population2030 integer,
  population2035 integer,
  population2040 integer,
  population2045 integer,
  population2050 integer,
  CONSTRAINT counties_future_pop_pkey PRIMARY KEY (countyid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE counties_future_pop
  OWNER TO postgres;


