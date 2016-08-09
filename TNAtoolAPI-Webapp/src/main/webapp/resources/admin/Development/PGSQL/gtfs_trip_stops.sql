drop table if exists gtfs_trip_stops;

CREATE TABLE gtfs_trip_stops
(
  trip_agencyid character varying(50),
  trip_id character varying(255),
  stop_agencyid_origin character varying(50),
  stop_id_origin character varying(255),
  stop_name_origin character varying(255),
  stop_agencyid_destination character varying(50),
  stop_id_destination character varying(255),
  stop_name_destination character varying(255),
  CONSTRAINT gtfs_trip_stops_pkey PRIMARY KEY (trip_agencyid, trip_id),
  CONSTRAINT gtfs_trip_stops_fkey1 FOREIGN KEY (stop_agencyid_origin, stop_id_origin)
      REFERENCES gtfs_stops (agencyid, id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
   CONSTRAINT gtfs_trip_stops_fkey2 FOREIGN KEY (stop_agencyid_destination, stop_id_destination)
      REFERENCES gtfs_stops (agencyid, id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION    
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gtfs_trip_stops
  OWNER TO postgres;

with seq as (select trip_agencyid as taid, trip_id as tripid, min(stopsequence) as mins, max(stopsequence) as maxs from gtfs_stop_times stime group by trip_agencyid, trip_id),
tpoints as (select stime1.stop_agencyid as osaid, stime1.stop_id as ostopid, stime2.stop_agencyid as dsaid, stime2.stop_id as dstopid, seq.taid as taid, seq.tripid as tripid from gtfs_stop_times stime1 inner join seq  
on seq.taid = stime1.trip_agencyid and seq.tripid=stime1.trip_id and seq.mins= stime1.stopsequence inner join gtfs_stop_times stime2 
on seq.taid = stime2.trip_agencyid and seq.tripid=stime2.trip_id and seq.mins= stime2.stopsequence),
tripstops as (select tpoints.taid as trip_agencyid, tpoints.tripid as trip_id, stop1.name as stop_name_origin, tpoints.osaid as stop_agencyid_origin, tpoints.ostopid as stop_id_origin, 
stop2.name as stop_name_destination, tpoints.dsaid as stop_agencyid_destination, tpoints.dstopid as stop_id_destination from gtfs_stops stop1 inner join tpoints 
on stop1.agencyid = tpoints.osaid and stop1.id = tpoints.ostopid inner join gtfs_stops stop2 on stop2.agencyid = tpoints.dsaid and stop2.id = tpoints.dstopid)
insert into gtfs_trip_stops (trip_agencyid, trip_id, stop_name_origin, stop_agencyid_origin, stop_id_origin, stop_name_destination, stop_agencyid_destination, stop_id_destination) select * from tripstops;