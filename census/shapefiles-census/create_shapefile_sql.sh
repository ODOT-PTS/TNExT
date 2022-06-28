#!/bin/bash
# This script will create tables from all shapefiles in the current directory.
# This must be ran in the tnext docker container in a local dev environemnt
# Arguments: name of database to create tables in (required). Example: 
#     . create_shapefile_sql.sh census2010

# for local dev use only
PGPASSWORD=postgres
arg=$1
dbname=${arg:-census_reference}
echo $1
echo $dbname
for f in */*.shp; do
  fn=$(basename -s .shp $f)
  shp2pgsql -s 4326 -d -I $f > $fn.sql
  psql -U postgres -h db -d $dbname -f $fn.sql
  rm $fn.sql
done
