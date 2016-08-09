/*uncomment if the shapes table has its default wierd name*/
/*alter table tl_2010_41_tabblock10 rename to census_blocks_reference;*/
/*uncomment if the table does not already have the blockId column*/
/*alter table gtfs_stops add column blockid varchar(15);*/
update gtfs_stops stop set blockid=shape.geoid10 from census_blocks_reference shape where st_within(ST_MakePoint(stop.lon, stop.lat),shape.geom)=true ;
/*select stop.agencyId, stop.name, shape.geoid10 from gtfs_stops stop join census_blocks_reference shape on st_within(ST_MakePoint(stop.lon, stop.lat),shape.geom)=true ;*/
/*select stop.agencyId, stop.name, right(shape.geoid10,5)  from gtfs_stops stop join census_places_reference shape on st_within(ST_MakePoint(stop.lon, stop.lat),shape.geom)=true ;*/
/*alter table gtfs_stops add column placeid varchar(7);*/
update gtfs_stops stop set placeid=shape.placeid from census_places shape where st_within(ST_SetSRID(ST_MakePoint(stop.lon, stop.lat),4326),shape.shape)=true ;
/*alter table gtfs_stops add column congdistid varchar(4);*/
update gtfs_stops stop set congdistid=shape.congdistid from census_congdists shape where st_within(ST_SetSRID(ST_MakePoint(stop.lon, stop.lat),4326),shape.shape)=true; 
/*alter table gtfs_stops add column regionid varchar(2);*/
update gtfs_stops stop set regionid = county.odotregionid from census_counties county where left(stop.blockid,5)= county.countyid::varchar(5);
/*uncomment to delete urban areas not in Oregon state*/
/*delete from census_urban where name10 not like '%OR%';*/
/*vacuum analyze urban;*/
/*alter table gtfs_stops add column urbanid varchar(5);*/
update gtfs_stops stop set urbanid=shape.urbanid from census_urbans shape where st_within(ST_SetSRID(ST_MakePoint(stop.lon, stop.lat),4326),shape.shape)=true;