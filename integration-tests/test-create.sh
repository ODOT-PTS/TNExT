set -ex

export ENDPOINT="http://localhost:8080/TNAtoolAPI-Webapp"

# Delete existing
curl -s "${ENDPOINT}/modifiers/dbupdate/deleteDB?&index=1"

# Create new database
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/readDBinfo"  
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"  
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedGTFS"  
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedPNR"  
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedT6"  
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedEmp"  
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedfEmp"  
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedfPop"  
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedRegion"  
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkForDeactivated?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/getIndex"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkInput?&dbname=april18&cURL=jdbc:postgresql://db:5432/&user=postgres&pass=postgres&db=april18&oldURL=&olddbname="
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/addDB?&db=1,april18,com/model/database/connections/spatial/april18.cfg.xml,com/model/database/connections/transit/april18.cfg.xml,jdbc:postgresql://db:5432/april18,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"

# Refresh
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/readDBinfo"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"

# UPLOAD FEEDS
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteProcessGTFS"

# Actually upload
for gtfsfile in "$@"
do
    gtfsbase=`basename $gtfsfile`
    curl -u admin:tnast -X POST "${ENDPOINT}/admin" -F "files[]=@${gtfsfile}" -F "name=gtfs"
    curl -s --fail "${ENDPOINT}/modifiers/dbupdate/addfeed?&feedname=${gtfsbase}&feedsize=11511&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
done

# Set uploaded status
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkGTFSstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=gtfs_feeds&b=true"

# Refresh
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkUpdatestatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedGTFS"

# Copy census
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/copyCensus?&dbFrom=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml&dbTo=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&section=census"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkCensusstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=census&b=true"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkUpdatestatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkFpopstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=future_pop&b=true"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkRegionstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=region&b=true"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkfEmpstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkEmpstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkT6status?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkPNRstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/copyCensus?&dbFrom=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml&dbTo=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&section=employment"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkEmpstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=employment&b=true"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/copyCensus?&dbFrom=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml&dbTo=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&section=parknride"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkPNRstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=parknride&b=true"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/copyCensus?&dbFrom=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml&dbTo=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&section=title6"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkT6status?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=title6&b=true"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/copyCensus?&dbFrom=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml&dbTo=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&section=femployment"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkfEmpstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=future_emp&b=true"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"

# Run update queries
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/updateFeeds?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&username=admin"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/updateNext?&feed=southlanewheels-or-us&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&agency=130&username=admin"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/updateNext?&feed=rhodyexpress-or-us&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&agency=185&username=admin"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/updateNext?&feed=woodburn-or-us&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&agency=129&username=admin"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/updateNext?&feed=swanisland-or-us&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&agency=162&username=admin"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkUpdatestatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=update_process&b=true"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"

# Activate
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/activateDBs?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/readDBinfo"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"

# Cleanup
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedGTFS"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedPNR"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedT6"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedEmp"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedfEmp"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedfPop"
curl -s --fail "${ENDPOINT}/modifiers/dbupdate/deleteUploadedRegion"

# Acceptance test on database creation