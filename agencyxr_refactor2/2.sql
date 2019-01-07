with svcids as (
  (
    select
      serviceid_agencyid,
      serviceid_id,
      'Thu 08 Nov 2018' as day
    from
      gtfs_calendars gc
    where
      startdate :: int <= 20180101
      and enddate :: int >= 20180101
      and MONDAY = 1
      and serviceid_agencyid || serviceid_id not in (
        select
          serviceid_agencyid || serviceid_id
        from
          gtfs_calendar_dates
        where
          date = 20180101
          and exceptiontype = 2
      )
    union
    select
      serviceid_agencyid,
      serviceid_id,
      'Thu 08 Nov 2018'
    from
      gtfs_calendar_dates gcd
    where
      date = 20180101
      and exceptiontype = 1
  )
),
trips as (
  select
    trip.agencyid as aid,
    trip.id as tripid,
    trip.route_id as routeid,
    round(
      (map.length):: numeric,
      2
    ) as length,
    map.tlength as tlength,
    map.stopscount as stops,
    trip.stopscount as ss
  from
    svcids
    inner join gtfs_trips trip using(
      serviceid_agencyid, serviceid_id
    )
    inner join census_counties_trip_map map on trip.id = map.tripid and trip.agencyid = map.agencyid
  where
    trip.agencyid = 'AGENCYID'
    and map.countyid = 'AREAID'
),
service as (
  select
    COALESCE(
      sum(length),
      0
    ) as svcmiles,
    COALESCE(
      sum(tlength),
      0
    ) as svchours,
    COALESCE(
      sum(stops),
      0
    ) as svcstops
  from
    trips
),
stops as (
  select
    stop.blockid,
    trips.aid as aid,
    stime.stop_id as stopid,
    min(stime.arrivaltime) as arrival,
    max(stime.departuretime) as departure,
    stop.location,
    count(trips.aid) as service
  from
    gtfs_stops stop
    inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid
    and stime.stop_id = stop.id
    inner join trips on stime.trip_agencyid = trips.aid
    and stime.trip_id = trips.tripid
  group by
    trips.aid,
    stime.stop_id,
    stop.location,
    stop.blockid
),
svcstops_urban AS (
  SELECT
    SUM(service) AS svcstops_urban
  FROM
    stops
    INNER JOIN census_blocks USING(blockid)
  WHERE
    poptype = 'U'
),
svcstops_rural AS (
  SELECT
    SUM(service) AS svcstops_rural
  FROM
    stops
    INNER JOIN census_blocks USING(blockid)
  WHERE
    poptype = 'R'
),
stops_with_arrivals as (
  select
    trips.aid as aid,
    stime.stop_id as stopid,
    min(stime.arrivaltime) as arrival,
    max(stime.departuretime) as departure,
    stop.location,
    count(trips.aid) as service
  from
    gtfs_stops stop
    inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid
    and stime.stop_id = stop.id
    inner join trips on stime.trip_agencyid = trips.aid
    and stime.trip_id = trips.tripid
  where
    stime.arrivaltime > 0
    and stime.departuretime > 0
  group by
    trips.aid,
    stime.stop_id,
    stop.location
),
undupblocks as (
  select
    block.populationPOPYEAR as population,
    block.poptype,
    block.blockid,
    sum(stops.service) as service
  from
    census_blocks block
    inner join stops on st_dwithin(
      block.location, stops.location, 0.1234
    )
  where
    countyid = 'AREAID'
  group by
    block.blockid
),
svchrs as (
  select
    COALESCE(
      min(arrival),
      -1
    ) as fromtime,
    COALESCE(
      max(departure),
      -1
    ) as totime
  from
    stops_with_arrivals
),
racserved as (
  select
    COALESCE(
      sum(c000_POPYEAR * service),
      0
    ) AS srac
  from
    undupblocks
    inner join lodes_rac_projection_block using(blockid)
),
wacserved as (
  select
    COALESCE(
      sum(c000 * service),
      0
    ) AS swac
  from
    undupblocks
    inner join lodes_blocks_wac using(blockid)
),
upopserved as (
  select
    COALESCE(
      sum(population * service),
      0
    ) as uspop
  from
    undupblocks
  where
    poptype = 'U'
),
rpopserved as (
  select
    COALESCE(
      sum(population * service),
      0
    ) as rspop
  from
    undupblocks
  where
    poptype = 'R'
),
upop_los as (
  select
    COALESCE(
      sum(population),
      0
    ) as upop_los
  from
    undupblocks
  where
    poptype = 'U'
    AND service >= 1234
),
rpop_los as (
  select
    COALESCE(
      sum(population),
      0
    ) as rpop_los
  from
    undupblocks
  where
    poptype = 'R'
    AND service >= 1234
),
svcdays as (
  select
    COALESCE(
      array_agg(distinct day):: text,
      '-'
    ) as svdays
  from
    svcids
)
select
  svcmiles,
  svchours,
  svcstops_urban,
  svcstops_rural,
  uspop,
  rspop,
  upop_los,
  rpop_los,
  swac,
  srac,
  svdays,
  fromtime,
  totime
from
  service
  inner join upopserved on true
  inner join rpopserved on true
  inner join svcdays on true
  inner join racserved on true
  inner join svchrs on true
  inner join wacserved on true
  inner join svcstops_urban on true
  inner join svcstops_rural on true
  inner join upop_los on true
  inner join rpop_los on true ";