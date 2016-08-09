drop table if exists gtfs_stop_route_map;

CREATE TABLE gtfs_stop_route_map
(
  gid serial NOT NULL,
  agencyid character varying(255),
  routeid character varying(255),
  agencyid_def character varying(50),
  stopid character varying(255),
  CONSTRAINT gtfs_stop_route_map_pkey PRIMARY KEY (gid),
  CONSTRAINT fk57a2facec6b68b22 FOREIGN KEY (agencyid_def, stopid)
      REFERENCES gtfs_stops (agencyid, id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gtfs_stop_route_map
  OWNER TO postgres;


insert into gtfs_stop_route_map(stopId, agencyId, agencyId_def, routeId) 
select distinct stimes.stop_id, routes.agency, stimes.stop_agencyId, routes.id 
from gtfs_routes routes
inner join gtfs_trips trips on routes.id = trips.route_id and routes.agencyId = trips.agencyId
inner join gtfs_stop_times stimes on trips.id = stimes.trip_id and trips.agencyId = stimes.trip_agencyId;
/*inner join gtfs_stops stops on stimes.stop_id = stops.id ; */
