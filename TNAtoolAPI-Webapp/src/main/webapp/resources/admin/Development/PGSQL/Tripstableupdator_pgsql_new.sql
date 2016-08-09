/*creating this index makes a huge difference*/
create INDEX trips_shapeids on gtfs_trips (shapeid_agencyid,shapeid_id);
create unique index tripids on gtfs_trips (agencyid,id);

/*Eficient update queries for trips table:*/

update gtfs_trips trip set shape = tss.shape, epshape=GoogleEncodeLine(tss.shape), length = (tss.length)/1609.34, estlength=0 FROM 
(select ST_MakeLine(ST_setsrid(ST_MakePoint(shppoint.lon, shppoint.lat),4326)) as shape, ST_Length(st_transform(ST_MakeLine(ST_setsrid(ST_MakePoint(shppoint.lon, shppoint.lat),4326)),2993)) as length, 
shppoint.shapeid_agencyid as agencyid, shppoint.shapeid_id as shapeid from (select * from gtfs_shape_points where shapeid_agencyid='200' order by shapeid_agencyid, shapeid_id, sequence) as shppoint 
group by agencyid, shapeid) 
as tss where tss.agencyid = trip.shapeid_agencyid and tss.shapeid = trip.shapeid_id;

update gtfs_trips trip set shape = tes.shape, epshape=GoogleEncodeLine(tes.shape), estlength = (tes.estlength)/1609.34, length=0 FROM (select est.shape, est.estlength, est.agencyid, est.tripid from 
(select ST_MakeLine(ST_setsrid(ST_MakePoint(point.lon, point.lat),4326)) as shape, ST_Length(st_transform(ST_MakeLine(ST_setsrid(ST_MakePoint(point.lon, point.lat),4326)),2993)) as estlength, 
point.agencyid as agencyid, point.tripid as tripid from (select stop.lat as lat, stop.lon as lon, stime.gid as gid, stime.trip_id as tripid, stime.trip_agencyid as agencyid from gtfs_stops stop 
inner join gtfs_stop_times stime on stop.agencyid = stime.stop_agencyid and stop.id = stime.stop_id where stop.agencyid='200' order by stime.trip_agencyid, stime.trip_id, stime.stopsequence) as point 
group by point.agencyid, point.tripid) as est inner join gtfs_trips trip on est.agencyid = trip.agencyid and est.tripid = trip.id where trip.shapeid_id isnull) as tes 
where tes.agencyid = trip.agencyid and tes.tripid = trip.id;

update gtfs_trips set stopscount = stpt.cnt from (select count(gid) as cnt, trip_id, trip_agencyid from gtfs_stop_times where stop_agencyid='200' group by trip_id, trip_agencyid) as stpt 
where stpt.trip_agencyid = agencyid and stpt.trip_id=id ;

with tempstopcodes as (select stop.id as stopid, stop.agencyid as agencyid , makeuid(rank() over (partition by stop.agencyid order by stop.id)) as uid from gtfs_stops stop where stop.agencyid='200')
update gtfs_trips set uid = stpt.uid from (select string_agg(tmp.uid,'!' order by tmp.uid) as uid, stime.trip_id, stime.trip_agencyid from 
gtfs_stop_times stime inner join tempstopcodes tmp on tmp.agencyid=stime.stop_agencyid and tmp.stopid=stime.stop_id
group by stime.trip_agencyid, stime.trip_id)as stpt where stpt.trip_agencyid = agencyid and stpt.trip_id=id;

with tempetriptimes as (select trip_id as tripid, trip_agencyid as agencyid, coalesce(max(departuretime),0) as tripfinish, coalesce(min(arrivaltime),0) as tripstart from gtfs_stop_times where arrivaltime>0 and departuretime>0  
and stop_agencyid='200' group by trip_id, trip_agencyid)
update gtfs_trips trips set tlength=tripfinish-tripstart from tempetriptimes result where result.tripid = trips.id and result.agencyid = trips.agencyid;

update gtfs_trips set tlength=0 where tlength isnull or tlength<0;
