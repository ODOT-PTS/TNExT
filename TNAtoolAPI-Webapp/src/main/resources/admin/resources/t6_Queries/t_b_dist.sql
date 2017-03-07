DROP TABLE IF EXISTS t_b_dist;
CREATE TABLE t_b_dist
(
  blockid character varying(15) NOT NULL,
  population integer,
  agg_population integer,
  CONSTRAINT t_b_dist_pkey PRIMARY KEY (blockid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE t_b_dist
  OWNER TO postgres;


INSERT INTO t_b_dist
(blockid,population)
SELECT blockid,population
FROM census_blocks;

UPDATE t_b_dist SET agg_population = t2.pop from 
	(SELECT left(blockid,11) AS id,
             Sum(population) as pop 
      FROM t_b_dist group by left(blockid,11) 
     ) t2
where id=left(blockid,11);
     
ALTER TABLE t_b_dist
ADD ratio double precision;

UPDATE t_b_dist 
   SET ratio = (population :: double precision)/agg_population where agg_population<>0;

UPDATE t_b_dist 
   SET ratio = 0 where agg_population=0;
