alter table gtfs_stops add column location geometry(Point,2993);
update gtfs_stops set location = ST_transform(ST_setsrid(ST_MakePoint(lon, lat),4326), 2993);
CREATE INDEX ids_location ON gtfs_stops
  USING GIST (location);
CLUSTER ids_location ON gtfs_stops;
VACUUM ANALYZE gtfs_stops;