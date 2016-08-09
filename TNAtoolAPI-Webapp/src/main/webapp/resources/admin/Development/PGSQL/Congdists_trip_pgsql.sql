drop table if exists census_congdists_trip_map;

CREATE TABLE census_congdists_trip_map
(
  gid serial NOT NULL,
  agencyid character varying(255),
  agencyid_def character varying(255),
  routeid character varying(255),
  congdistid character varying(4),
  tripid character varying(255),
  serviceid character varying(255),
  stopscount integer,  
  length float,
  tlength int,
  shape geometry(multilinestring),
  uid varchar(512),
  CONSTRAINT census_congdists_trip_map_pkey PRIMARY KEY (gid),
  CONSTRAINT census_congdists_trip_map_fkey FOREIGN KEY (agencyid, tripid)
      REFERENCES gtfs_trips (agencyid, id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE census_congdists_trip_map
  OWNER TO postgres;

with groutes as(SELECT routeid, agencyid_def, ST_multi(ST_Collect(ST_SetSRID(ST_MakePoint(stops.lon, stops.lat), 4326))) multi
		FROM gtfs_stop_route_map AS map INNER JOIN gtfs_stops AS stops
		ON map.agencyid_def = stops.agencyid
		AND map.stopid = stops.id
		GROUP BY routeid, agencyid_def
)
insert into census_congdists_trip_map(tripid, agencyid, agencyid_def, serviceid, routeid,  congdistid, shape, length, uid) 
select trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, congdist.congdistid, st_multi(ST_CollectionExtract(st_union(ST_Intersection(trip.shape,congdist.shape)),2)), (ST_Length(st_transform(ST_Intersection(trip.shape,congdist.shape),2993))/1609.34), trip.uid
from gtfs_trips trip
inner join groutes on trip.route_id=groutes.routeid AND trip.serviceid_agencyid=groutes.agencyid_def
inner join census_congdists congdist on st_intersects(congdist.shape,trip.shape)=true and st_intersects(congdist.shape,multi)=true group by trip.id, trip.agencyid, congdist.congdistid;

update census_congdists_trip_map set stopscount = res.cnt+0 from 
(select count(stop.id) as cnt, stop.congdistid as cid, stime.trip_agencyid as aid, stime.trip_id as tid 
from gtfs_stops stop inner join gtfs_stop_times stime 
on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id group by stime.trip_agencyid, stime.trip_id, stop.congdistid) as res
where congdistid =  res.cid and agencyid = res.aid and tripid=res.tid;

update census_congdists_trip_map set stopscount=0 where stopscount IS NULL;
update census_congdists_trip_map map set tlength=res.time from (select max(departuretime)-min(arrivaltime) as time, trip_agencyid as agencyid, trip_id as id from gtfs_stop_times where arrivaltime>0 and departuretime>0 group by trip_agencyid, trip_id) as res 
where res.agencyid = map.agencyid and res.id=map.tripid;

update census_congdists_trip_map map set tlength=res.ttime from (
select max(stimes.departuretime)-min(stimes.arrivaltime) as ttime, 
stimes.agencyid, stimes.tripid, stimes.geoid from (
select stime.arrivaltime, stime.departuretime, stime.trip_agencyid as agencyid, stime.trip_id as tripid, stop.congdistid as geoid
from gtfs_stop_times stime inner join gtfs_stops stop on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id) as stimes
where stimes.arrivaltime>0 and stimes.departuretime>0 group by stimes.agencyid, stimes.tripid, stimes.geoid) as res 
where res.agencyid = map.agencyid and res.tripid=map.tripid and res.geoid=map.congdistid;

update census_congdists_trip_map set tlength=0 where tlength isnull;
