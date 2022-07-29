#!/bin/bash

# Perform all steps of census geography import
# This script is meant to be ran inside of the tnext container in a local development environment.
# Shapefiles for census geographies are expected to be in the census/shapefiles/ directory when this script when ran.
# This is provided for convenience and documentation -- for better troubleshooting, it is recommended to run each line in this
# script one at a time.

# IMPORTANT! These scripts were written for *2010* Oregon census import, some manual changes are necessary if importing data for
# a different year. If importing non-2010 census data, please do the following:
#   - Update table and column names in import_census_geographies.sql to match the appropriate file name and property names (respectively) 
#       of geography shapefiles
#   - Change the column name in update_census_tables_with_population.sql to match the year you are importing population data for (e.g. population2020)
#   - Change the year used in the Census API requests in create_population_csvs.sh to match the year you are importing population data for
#       (e.g. https://api.census.gov/data/2020/dec/sf1... )

# Usage:
#   . census.sh census_reference

dbname=${arg:-census_reference}

psql -U postgres -h db -c "CREATE DATABASE $dbname"
psql -U postgres -h db -d $dbname -c "CREATE EXTENSION IF NOT EXISTS postgis"

. copy_shapefiles_to_db.sh $dbname
psql -U postgres -h db -d $dbname -f create_census_tables.sql
psql -U postgres -h db -d $dbname -f import_census_geographies.sql

. create_population_csvs.sh
psql -U postgres -h db -d $dbname -f create_population_tables.sql
psql -U postgres -h db -d $dbname -f update_census_tables_with_population.sql
psql -U postgres -h db -d $dbname -f postprocess_geography_ids.sql