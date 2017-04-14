DROP TABLE IF EXISTS bg_b_dist;
CREATE TABLE bg_b_dist
(
  blockid character varying(15) NOT NULL,
  population integer,
  agg_population integer,
  CONSTRAINT bg_b_dist_pkey PRIMARY KEY (blockid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE bg_b_dist
  OWNER TO postgres;


INSERT INTO bg_b_dist
(blockid,population)
SELECT blockid,population
FROM census_blocks;

UPDATE bg_b_dist SET agg_population = t2.pop from 
	(SELECT left(blockid,12) AS id,
             Sum(population) as pop 
      FROM bg_b_dist group by left(blockid,12) 
     ) t2
where id=left(blockid,12);

ALTER TABLE bg_b_dist
ADD ratio double precision;

UPDATE bg_b_dist 
   SET ratio = (population :: double precision)/agg_population where agg_population<>0;

UPDATE bg_b_dist 
   SET ratio = 0 where agg_population=0;

