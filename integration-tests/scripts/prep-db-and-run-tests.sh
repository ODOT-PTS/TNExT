#!/usr/bin/env bash
export PGHOST=db
export PGUSER=postgres
export PGPASSWORD=postgres

/scripts/wait-for-it.sh db:5432 --timeout=30 -- echo "db ready"

if [ "$(createdb test || echo exists)" == "exists" ]; then 
    echo "exists"
else
    pg_restore -v --dbname=test /data/may2018.backup
fi

/scripts/wait-for-it.sh ${TNAST_HOST} --timeout=30 -- echo "tnast ready"

echo "running tests"
/scripts/wait-for-it.sh ${TNAST_HOST}:8080 --timeout=30 -- resttest.py http://${TNAST_HOST} /api-tests/*.yaml
echo "running done"
