with census as (
  select 
    population2010 as population, 
    poptype, 
    block.countyid, 
    block.blockid 
  from 
    census_blocks block 
    inner join gtfs_stops stop on st_dwithin(
      block.location, stop.location, 402.335
    ) 
    inner join gtfs_stop_service_map map on map.stopid = stop.id 
    and map.agencyid_def = stop.agencyid 
  where 
    map.agencyid = 'TRIMET' 
    AND block.countyid = '41051'
  group by 
    block.blockid
), 
employment as (
  select 
    sum(c000_2010) as employment 
  from 
    census 
    left join lodes_rac_projection_block using(blockid) 
  GROUP BY 
    countyid
), 
employees as (
  select 
    sum(c000) as employees 
  from 
    census 
    left join lodes_blocks_wac using(blockid) 
  GROUP BY 
    countyid
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
    map.agencyid = 'TRIMET' 
    AND census_blocks.countyid = '41051'
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
    map.agencyid = 'TRIMET' 
    AND census_blocks.countyid = '41051'
    and poptype = 'R'
), 
routes as (
  select 
      distinct on (routeid) round(
      (trip.length + trip.estlength):: numeric, 
      2
    ) as length, 
    trip.route_id as routeid,
    id
  from 
    gtfs_trips trip 
    INNER JOIN census_counties_trip_map maps ON trip.id = maps.tripid 
  where 
    trip.agencyid = 'TRIMET' 
    AND maps.countyid = '41051'
  order by 
    routeid, 
    length DESC, 
    id    
),
fragments AS (
  SELECT
    ST_Length(ST_Transform(ST_Intersection(segs.shape, blocks.shape), 2993)) as length,
    blocks.poptype
  FROM 
    gtfs_trip_segments segs
    INNER JOIN routes ON segs.id = routes.id
    INNER JOIN census_counties census ON ST_Intersects(segs.shape, census.shape)
    INNER JOIN census_blocks blocks ON ST_Intersects(segs.shape, blocks.shape)
  where
    census.countyid = '41051'
),

rtmiles as (
  select 
    sum(length)/1609.34 as rtmiles 
  from 
    fragments
),
urtmiles as (select sum(length)/1609.34 as urtmiles FROM fragments WHERE poptype = 'U'),
rrtmiles as (select sum(length)/1609.34 as rrtmiles FROM fragments WHERE poptype = 'R')

select 
  COALESCE(urbanstopscount, 0) as urbanstopcount, 
  COALESCE(ruralstopscount, 0) as ruralstopcount, 
  COALESCE(upop, 0) as urbanpop, 
  COALESCE(rpop, 0) as ruralpop, 
  coalesce(employment, 0) as rac, 
  coalesce(employees, 0) as wac, 
  COALESCE(rtmiles, 0) as rtmiles,
  COALESCE(urtmiles, 0) as urtmiles,
  COALESCE(rrtmiles, 0) as urtmiles 
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
