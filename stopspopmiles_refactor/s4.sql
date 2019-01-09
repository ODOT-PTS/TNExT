with census as (
  select 
    population2010 as population, 
    poptype, 
    block.blockid, 
    block.regionid 
  from 
    census_blocks block 
    inner join gtfs_stops stop on st_dwithin(
      block.location, stop.location, 404.233
    ) 
    inner join gtfs_stop_service_map map on map.stopid = stop.id 
    and map.agencyid_def = stop.agencyid 
  where 
    map.agencyid = 'AGENCYID' 
    And block.regionid = 'AREAID' 
  group by 
    block.blockid
), 
employment as (
  select 
    sum(c000_2010) as employment 
  from 
    census 
    left join lodes_rac_projection_block using(blockid) 
  group by 
    census.regionid
), 
employees as (
  select 
    sum(c000) as employees 
  from 
    census 
    left join lodes_blocks_wac using(blockid) 
  group by 
    census.regionid
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
    and stop.regionid = 'AREAID' 
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
    and stop.regionid = 'AREAID' 
    and poptype = 'R'
), 
routes as (
  select 
    max(
      round(
        (maps.length):: numeric, 
        2
      )
    ) as length, 
    trip.route_id as routeid 
  from 
    gtfs_trips trip 
    inner join census_counties_trip_map maps on trip.id = maps.tripid 
  where 
    trip.agencyid = 'AGENCYID' 
    AND maps.regionid = 'AREAID' 
  group by 
    trip.route_id
), 
rmiles as (
  select 
    sum(length) as rtmiles 
  from 
    routes
) 
select 
  COALESCE(urbanstopscount, 0) as urbanstopcount, 
  COALESCE(ruralstopscount, 0) as ruralstopcount, 
  COALESCE(upop, 0) as urbanpop, 
  COALESCE(rpop, 0) as ruralpop, 
  coalesce(employment, 0) as rac, 
  coalesce(employees, 0) as wac, 
  COALESCE(rtmiles, 0) as rtmiles 
from 
  urbanpop 
  inner join ruralpop on true 
  inner join rmiles on true 
  inner join employment on true 
  inner join employees on true 
  inner join urbanstopcount on true 
  inner join ruralstopcount on true;
