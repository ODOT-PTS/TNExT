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

logfile=/var/log/tnext/test.log
if [ -f "${logfile}" ]; then
    rm ${logfile}
fi
mkdir -p `dirname ${logfile}`

echo "running tests"
for i in `ls api-tests/*.yaml`; do
    echo $i
    resttest.py --log debug http://${TNAST_HOST} $i | tee -a ${logfile}
done
echo "running done"
