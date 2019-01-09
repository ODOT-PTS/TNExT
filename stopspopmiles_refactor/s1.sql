with census as (
  select 
    population2010 as population, 
    poptype, 
    block.blockid 
  from 
    census_blocks block 
    inner join gtfs_stops stop on st_dwithin(
      block.location, stop.location, 404.233
    ) 
    inner join gtfs_stop_service_map map on map.stopid = stop.id 
    and map.agencyid_def = stop.agencyid 
  where 
    map.agencyid = 'AGENCYID' 
  group by 
    block.blockid
), 
employment as (
  select 
    sum(c000_2010) as employment 
  from 
    census 
    left join lodes_rac_projection_block using(blockid)
), 
employees as (
  select 
    sum(c000) as employees 
  from 
    census 
    left join lodes_blocks_wac using(blockid)
), 
urbanpop as (
  select 
    COALESCE(
      sum(population), 
      0
    ) upop 
  from 
    census 
  where 
    poptype = 'U'
), 
ruralpop as (
  select 
    COALESCE(
      sum(population), 
      0
    ) rpop 
  from 
    census 
  where 
    poptype = 'R'
), 
urbanstopcount as (
  select 
    count(stop.id) as urbanstopscount 
  from 
    gtfs_stops stop 
    inner join gtfs_stop_service_map map on map.stopid = stop.id 
    and map.agencyid_def = stop.agencyid 
    inner join census_blocks using(blockid) 
  where 
    map.agencyid = 'AGENCYID' 
    and poptype = 'U'
), 
ruralstopcount as (
  select 
    count(stop.id) as ruralstopscount 
  from 
    gtfs_stops stop 
    inner join gtfs_stop_service_map map on map.stopid = stop.id 
    and map.agencyid_def = stop.agencyid 
    inner join census_blocks using(blockid) 
  where 
    map.agencyid = 'AGENCYID' 
    and poptype = 'R'
), 
routes as (
  select 
    distinct on (routeid) round(
      (trip.length + trip.estlength):: numeric, 
      2
    ) as length, 
    trip.route_id as routeid, 
    id, 
    shape as tripshape 
  from 
    gtfs_trips trip 
  where 
    trip.agencyid = 'AGENCYID' 
  order by 
    routeid, 
    length DESC, 
    shape, 
    id
), 
rrtmiles AS (
  SELECT 
    SUM(
      ST_Length(
        ST_Transform(
          ST_Intersection(
            gtfs_trip_segments.shape, census_blocks.shape
          ), 
          2993
        )
      )
    ) / 1609.34 as rrtmiles 
  FROM 
    routes 
    INNER JOIN gtfs_trip_segments ON routes.id = gtfs_trip_segments.id 
    INNER JOIN census_blocks ON ST_Intersects(
      gtfs_trip_segments.shape, census_blocks.shape
    ) 
  WHERE 
    census_blocks.poptype = 'R'
), 
urtmiles AS (
  SELECT 
    SUM(
      ST_Length(
        ST_Transform(
          ST_Intersection(
            gtfs_trip_segments.shape, census_blocks.shape
          ), 
          2993
        )
      )
    ) / 1609.34 as urtmiles 
  FROM 
    routes 
    INNER JOIN gtfs_trip_segments ON routes.id = gtfs_trip_segments.id 
    INNER JOIN census_blocks ON ST_Intersects(
      gtfs_trip_segments.shape, census_blocks.shape
    ) 
  WHERE 
    census_blocks.poptype = 'U'
), 
rtmiles AS (
  SELECT 
    SUM(
      ST_Length(
        ST_Transform(gtfs_trip_segments.shape, 2993)
      )
    ) / 1609.34 as rtmiles 
  FROM 
    routes 
    INNER JOIN gtfs_trip_segments ON routes.id = gtfs_trip_segments.id
) 
select 
  COALESCE(urbanstopscount, 0) as urbanstopcount, 
  COALESCE(ruralstopscount, 0) as ruralstopcount, 
  COALESCE(upop, 0) as urbanpop, 
  COALESCE(rpop, 0) as ruralpop, 
  coalesce(employment, 0) as rac, 
  coalesce(employees, 0) as wac, 
  COALESCE(rtmiles, 0) as rtmiles, 
  COALESCE(urtmiles, 0) as urtmiles, 
  COALESCE(rrtmiles, 0) as rrtmiles 
from 
  urbanpop 
  inner join ruralpop on true 
  inner join rtmiles on true 
  inner join urtmiles on true 
  inner join rrtmiles on true 
  inner join employment on true 
  inner join employees on true 
  inner join urbanstopcount on true 
  inner join ruralstopcount on true;
