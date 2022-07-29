
/* ------------- STATE -------------- */

INSERT INTO census_states_ref (stateid, sname,
				landarea, waterarea, lat, lon, shape)  
SELECT 	statefp10, name10,
		aland10, awater10, intptlat10::double precision, intptlon10::double precision, geom
FROM tl_2010_41_state10
ON CONFLICT DO NOTHING;
	
-- update census_states_ref set population2010 = population;	
update census_states_ref set shape = st_setsrid(shape,4326);

/* ------------- CONGRESSIONAL DISTRICTS -------------- */

INSERT INTO census_congdists_ref (congdistid, stateid, cname,
				landarea, waterarea, lat, lon, shape)  
SELECT 	cd111fp, statefp10, namelsad10,
	aland10, awater10, intptlat10::double precision, intptlon10::double precision, geom
FROM tl_2010_41_cd111
ON CONFLICT DO NOTHING;

-- update census_congdists_ref set population2010 = population;	
update census_congdists_ref set shape = st_setsrid(shape,4326);

/* ------------- COUNTIES ----------------- */

INSERT INTO census_counties_ref (countyid, stateid, cname,
				landarea, waterarea, lat, lon, shape)  
SELECT 	countyfp10, statefp10, namelsad10,
		aland10, awater10, intptlat10::double precision, intptlon10::double precision, geom
FROM tl_2010_41_county10
ON CONFLICT DO NOTHING;

-- update census_counties_ref set population2010 = population;	
update census_counties_ref set shape = st_setsrid(shape,4326);


/* --------------- TRACTS ------------------ */

INSERT INTO census_tracts_ref (tractid, countyid, stateid, tname,
				landarea, waterarea, lat, lon, shape)  
SELECT 	tractce10, countyfp10, left(geoid10,2), namelsad10,
		aland10, awater10, intptlat10::double precision, intptlon10::double precision, geom
FROM tl_2010_41_tract10
ON CONFLICT DO NOTHING;

-- update census_tracts_ref set population2010 = population;	
update census_tracts_ref set shape = st_setsrid(shape,4326);

/* -------------- PLACES -------------------- */

INSERT INTO census_places_ref (placeid, stateid, pname,
				landarea, waterarea, lat, lon, shape)  
SELECT 	placefp10, left(geoid10,2), namelsad10,
	aland10, awater10, intptlat10::double precision, intptlon10::double precision, geom
FROM tl_2010_41_place10
ON CONFLICT DO NOTHING;

-- update census_places_ref set population2010 = population;	
update census_places_ref set shape = st_setsrid(shape,4326);

/* --------------- URBAN ---------------------- */

-- copy csv_2010_urban(stateid,countyid,placeid,tractid,blockid,uaid,urbantype,cdid,areaname,population) 
-- from 'C:/Users/Administrator/Desktop/census/nhgis/nhgis0074_csv/nhgis0074_csv/nhgis0074_ds172_2010_urb_area.csv' DELIMITER ',' ENCODING 'LATIN1' CSV HEADER;

-- update csv_2010_urban set geoid = LPAD(uaid, 5, '0');

-- WITH vtable AS(	SELECT geoid, urbantype, areaname,
-- 		aland10 landarea, awater10 waterarea, intptlat10 lat, intptlon10 lon, geom shape
-- 		FROM csv_2010_urban f_csv join tl_2010_us_uac10 f_shp
-- 		ON f_csv.geoid=f_shp.geoid10) 
INSERT INTO census_urbans_ref (urbanid, utype, uname,
				landarea, waterarea, lat, lon, shape)  
SELECT 	uace10, uatyp10, name10,
	aland10, awater10, intptlat10::double precision, intptlon10::double precision, geom
FROM tl_2010_us_uac10
-- urban geographies are only available nationally, so here we only import areas within state
JOIN census_states_ref on (
	ST_Within(ST_SETSRID(
		st_makepoint(tl_2010_us_uac10.intptlon10::double precision, tl_2010_us_uac10.intptlat10::double precision),4326), 
		census_states_ref.shape
	)
	AND stateid = '41' -- Oregon state ID
)
ON CONFLICT DO NOTHING;
	
-- update census_urbans_ref set population2010 = population;	
update census_urbans_ref set shape = st_setsrid(shape,4326);
-- update census_urbans_ref set poptype50k = (CASE WHEN population>=50000 THEN 'U' ELSE 'R' END);

/* -------------- BLOCK -------------- */

-- copy csv_2010_block_or(stateid,countyid,placeid,tractid,blockid,uaid,cdid,areaname,population) 
-- from 'C:/Users/Administrator/Desktop/census/nhgis/nhgis0074_csv/nhgis0074_csv/nhgis0074_ds172_2010_block.csv' DELIMITER ',' ENCODING 'LATIN1' CSV HEADER;

-- update csv_2010_block_or set geoid = LPAD(stateid, 2, '0')||LPAD(countyid, 3, '0')||LPAD(tractid, 6, '0')||LPAD(blockid, 4, '0');


-- WITH vtable AS(	SELECT geoid, stateid, cdid, countyid, tractid, population,
-- 		aland10 landarea, awater10 waterarea, intptlat10 lat, intptlon10 lon, geom shape,
-- 		ST_Transform(ST_SETSRID(ST_MakePoint(intptlon10::double precision, intptlat10::double precision),4326),2993) intpoint
-- 		FROM csv_2010_block_or f_csv join tl_2010_41_tabblock10 f_shp
-- 		ON f_csv.geoid=f_shp.geoid10) 
INSERT INTO census_blocks_ref (blockid, tractid, countyid, stateid,
				landarea, waterarea, lat, lon, shape, location)  
SELECT 	blockce10, tractce10, countyfp10, statefp10,
	aland10, awater10, intptlat10::double precision, intptlon10::double precision, geom,
	ST_Transform(ST_SETSRID(ST_MakePoint(intptlon10::double precision, intptlat10::double precision),4326),2993)
FROM tl_2010_41_tabblock10
ON CONFLICT DO NOTHING;

-- update census_blocks_ref set stateid = LPAD(stateid, 2, '0');
-- -- update census_blocks_ref set congdistid = stateid||LPAD(congdistid, 2, '0');
-- update census_blocks_ref set countyid = stateid||LPAD(countyid, 3, '0');
-- update census_blocks_ref set tractid = countyid||LPAD(tractid, 6, '0');
-- update census_blocks_ref set population2010 = population;	
update census_blocks_ref set shape = st_setsrid(shape,4326);

UPDATE census_blocks_ref
SET
  urbanid     = ur.urbanid
--   poptype     = ur.poptype50k
FROM
   census_urbans_ref ur
WHERE 
    ST_Within(ST_SETSRID(st_makepoint(census_blocks_ref.lon, census_blocks_ref.lat),4326), ur.shape);
   
-- UPDATE census_blocks_ref SET poptype = 'R' WHERE poptype is null; 

UPDATE census_blocks_ref
SET
  placeid     = pl.placeid
FROM
   census_places_ref pl
WHERE 
   ST_Within(ST_SETSRID(st_makepoint(census_blocks_ref.lon, census_blocks_ref.lat),4326), pl.shape);

UPDATE census_blocks_ref
SET
  congdistid     = cd.congdistid
FROM
   census_congdists_ref cd
WHERE 
   ST_Within(ST_SETSRID(st_makepoint(census_blocks_ref.lon, census_blocks_ref.lat),4326), cd.shape);
   