INSERT INTO lodes_rac_projection_county SELECT *
FROM temp_01
ON CONFLICT DO NOTHING; 

DROP TABLE temp_01;