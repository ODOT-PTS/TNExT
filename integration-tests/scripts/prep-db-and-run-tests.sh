#!/usr/bin/env bash

PGHOST=db PGUSER=postgres PGPASSWORD=postgres /scripts/wait-for-it.sh db:5432 --timeout=30 -- pg_restore --dbname=postgres --create /data/september17.backup

/scripts/wait-for-it.sh ${TNAST_HOST} --timeout=30 -- resttest.py http://${TNAST_HOST} /api-tests/*.yml