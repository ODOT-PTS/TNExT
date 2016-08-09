/*************************************************************
 * First of two methods. Pass in a geometry (LINESTRING only).
 * Returns ASCII-encoded point array for use in Google Maps.
 ************************************************************/
CREATE OR REPLACE FUNCTION GoogleEncodeLine
(
  g GEOMETRY
)
RETURNS TEXT AS $$
DECLARE
  pt1 GEOMETRY;
  pt2 GEOMETRY;
  p INT; np INT;
  deltaX INT;
  deltaY INT;
  enX VARCHAR(255);
  enY VARCHAR(255);
  gEncoded TEXT;
BEGIN
  gEncoded = '';
  np = ST_NPoints(g);

  IF np > 3 THEN
    g = ST_SimplifyPreserveTopology(g, 0.00001);
    np = ST_NPoints(g);
  END IF;

  pt1 = ST_SetSRID(ST_MakePoint(0, 0),4326);

  FOR p IN 1..np BY 1 LOOP
    pt2 = ST_PointN(g, p);
    deltaX = (floor(ST_X(pt2)*1e5)-floor(ST_X(pt1)*1e5))::INT;
    deltaY = (floor(ST_Y(pt2)*1e5)-floor(ST_Y(pt1)*1e5))::INT;
    enX = GoogleEncodeSignedInteger(deltaX);
    enY = GoogleEncodeSignedInteger(deltaY);
    gEncoded = gEncoded || enY || enX;

    pt1 = ST_SetSRID(ST_MakePoint(ST_X(pt2), ST_Y(pt2)),4326);
  END LOOP;
RETURN gEncoded;
End
$$ LANGUAGE plpgsql;

/**************************************************************
 * Second of two methods. Accepts a signed integer (LON or LAT
 * by 1e5) and returns an ASCII-encoded coordinate expression.
 *************************************************************/
CREATE OR REPLACE FUNCTION GoogleEncodeSignedInteger(c INT)
RETURNS VARCHAR(255) AS $$
DECLARE
  e VARCHAR(255);
  s BIT(32);
  b BIT(6);
  n INT;
BEGIN
 e = '';
 s = (c::BIT(32))<<1;

 IF s::INT < 0 THEN
   s = ~s;
   END IF;

 WHILE s::INT >= B'100000'::INT LOOP
   b = B'100000' | (('0'||substring(s, 28, 5))::BIT(6));
   n = b::INT + 63;
   e = e || chr(n);
   s = s >> 5;
 END LOOP;
 e = e || chr(s::INT+63);

RETURN e;
End
$$ LANGUAGE plpgsql;

/*create table tempshapes and fill it with route shapes*/
drop table if exists tempshapes;
create table tempshapes(
shapeid varchar(50),
agencyid varchar(50),
shape geometry(linestring),
length float,
primary key (agencyid, shapeid));
CREATE UNIQUE INDEX id ON tempshapes (agencyid,shapeid);

insert into tempshapes (shape, length, agencyid, shapeid)
select ST_MakeLine(ST_setsrid(ST_MakePoint(point.lon, point.lat),4326)) as shape, 
	ST_Length(st_transform(ST_MakeLine(ST_setsrid(ST_MakePoint(point.lon, point.lat),4326)),2993)) as length,
	point.shapeid_agencyid as agencyid,
	point.shapeid_id as shapeid
	from ( select * from gtfs_shape_points order by  shapeid_agencyid, shapeid_id, sequence) as point		
	group by agencyid, shapeid;
	
/*create a table named tempestshapes for trips without a route shape and fill it with estimated route shapes */
drop table if exists tempestshapes;
create table tempestshapes(
tripid varchar(255),
agencyid varchar(50),
shape geometry(linestring),
estlength float,
primary key (agencyid, tripid));
CREATE UNIQUE INDEX idd ON tempestshapes (agencyid,tripid);

insert into tempestshapes (shape, estlength, agencyid, tripid) 
		select est.shape, est.estlength, est.agencyid, est.tripid from
		(select ST_MakeLine(ST_setsrid(ST_MakePoint(point.lon, point.lat),4326)) as shape,
		ST_Length(st_transform(ST_MakeLine(ST_setsrid(ST_MakePoint(point.lon, point.lat),4326)),2993)) as estlength,
		point.agencyid as agencyid,
		point.tripid as tripid 
		from (
			select stop.lat as lat, stop.lon as lon, stime.gid as gid, stime.trip_id as tripid, stime.trip_agencyid as agencyid
			from gtfs_stops stop inner join gtfs_stop_times stime 
			on stop.agencyid = stime.stop_agencyid and stop.id = stime.stop_id		 
			order by stime.trip_agencyid, stime.trip_id, stime.stopsequence) as point
			group by point.agencyid, point.tripid) 
		as est inner join gtfs_trips trip
		on est.agencyid = trip.agencyid and est.tripid = trip.id 
		where trip.shapeid_id isnull;
/*Adding geometry column to trips table*/
SELECT AddGeometryColumn( 'public', 'gtfs_trips', 'shape', 4326, 'linestring', 2 );
/*updating the trips table with real shapes and lengths*/
update gtfs_trips trip set shape = tss.shape, epshape=GoogleEncodeLine(tss.shape), length = (tss.length)/1609.34, estlength=0 FROM tempshapes tss 
	where tss.agencyid = trip.shapeid_agencyid  and tss.shapeid = trip.shapeid_id;
/*updating the trips table with estimated shapes and lengths*/
update gtfs_trips trip set shape = tes.shape, epshape=GoogleEncodeLine(tes.shape), estlength = (tes.estlength)/1609.34, length=0 FROM tempestshapes tes 
	where tes.agencyid = trip.agencyid  and tes.tripid = trip.id;
/*Update the stopscount column in trips table*/
update gtfs_trips set stopscount = stpt.cnt from (select count(distinct(stop_id)) as cnt, trip_id, trip_agencyid from gtfs_stop_times group by trip_id, trip_agencyid)as stpt where stpt.trip_agencyid = agencyid and stpt.trip_id=id ;

CREATE OR REPLACE FUNCTION makeuid
(
  r int
)
RETURNS TEXT AS $$
DECLARE
  buff TEXT;
  rem int;
  div int;
  BEGIN
  buff = '';
  div = r;
  rem = 0;
while div>0 Loop
if div >92 then
	rem = div%93;
	buff = chr(rem+34)||buff;
	div = div / 93;
else 
	buff = chr(div+34)||buff;
	div = 0;
end if;
end loop;  
RETURN buff;
End
$$ LANGUAGE plpgsql;

drop table if exists tempstopcodes;
create table tempstopcodes(
stopid varchar(255),
agencyid varchar(50),
rank int,
uid varchar(255));

insert into tempstopcodes(stopid, agencyid,rank) select stop.id, stop.agencyid, rank() over (partition by stop.agencyid order by stop.id) from gtfs_stops stop;
update tempstopcodes set uid= makeuid(rank);

/*alter table gtfs_trips add column uid varchar(512);*/
alter table gtfs_trips alter column uid type varchar(512);
update gtfs_trips set uid = stpt.uid from 
(select string_agg(tmp.uid,'!' order by tmp.uid) as uid, stime.trip_id, stime.trip_agencyid from 
gtfs_stop_times stime inner join tempstopcodes tmp on tmp.agencyid=stime.stop_agencyid and tmp.stopid=stime.stop_id
group by stime.trip_agencyid, stime.trip_id)as stpt where stpt.trip_agencyid = agencyid and stpt.trip_id=id;

/*Creating tempetriptimes table to store trip start and finish times*/
drop table if exists tempetriptimes;
create table tempetriptimes(
tripid varchar(255),
agencyid varchar(50),
tripstart int,
tripfinish int,
tlength int,
primary key (agencyid, tripid));
CREATE UNIQUE INDEX temptriptimesid ON tempetriptimes (agencyid,tripid);

/*adding the trip start and finish times to the table*/
insert into tempetriptimes(tripid,agencyid, tripstart, tripfinish)
	select trip_id, trip_agencyid, min(arrivaltime), max(departuretime) from gtfs_stop_times where arrivaltime>0 and departuretime>0 group by trip_id, trip_agencyid;

/*calculating trip time lengths*/
update tempetriptimes set tlength= tripfinish-tripstart;

/*transferring trip time lengths to the trips table*/
update gtfs_trips trips set tlength=result.tlength from tempetriptimes result where result.tripid = trips.id and result.agencyid = trips.agencyid;
update gtfs_trips set tlength=0 where tlength isnull;
