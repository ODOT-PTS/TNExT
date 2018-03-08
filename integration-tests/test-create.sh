set -ex

alias curl=echo
# Delete existing
curl -s 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteDB?&index=1'

# Create new database
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/readDBinfo'  
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'  
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedGTFS'  
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedPNR'  
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedT6'  
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedEmp'  
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedfEmp'  
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedfPop'  
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedRegion'  
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkForDeactivated?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/getIndex'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkInput?&dbname=april18&cURL=jdbc:postgresql://db:5432/&user=postgres&pass=postgres&db=april18&oldURL=&olddbname='
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/addDB?&db=1,april18,com/model/database/connections/spatial/april18.cfg.xml,com/model/database/connections/transit/april18.cfg.xml,jdbc:postgresql://db:5432/april18,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'

# Refresh
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/readDBinfo'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'

# UPLOAD FEEDS
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteProcessGTFS'

# Actually upload
for gtfsfile in "$@"
do
    gtfsbase=`basename $gtfsfile`
    # curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDuplicateFeeds?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&feed=southlanewheels-or-us.zip'
    # http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteProcessGTFS
    curl -u admin:tnast -X POST 'http://localhost:8080/TNAtoolAPI-Webapp/admin' -F "files[]=@${gtfsfile}" -F "name=gtfs"
    curl -s --fail "http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/addfeed?&feedname=${gtfsbase}&feedsize=11511&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,"
done

# Set uploaded status
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkGTFSstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=gtfs_feeds&b=true'

# Refresh
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkUpdatestatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedGTFS'

# Copy census
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/copyCensus?&dbFrom=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml&dbTo=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&section=census'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkCensusstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=census&b=true'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkUpdatestatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkFpopstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=future_pop&b=true'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkRegionstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=region&b=true'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkfEmpstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkEmpstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkT6status?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkPNRstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/copyCensus?&dbFrom=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml&dbTo=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&section=employment'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkEmpstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=employment&b=true'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/copyCensus?&dbFrom=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml&dbTo=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&section=parknride'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkPNRstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=parknride&b=true'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/copyCensus?&dbFrom=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml&dbTo=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&section=title6'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkT6status?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=title6&b=true'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/copyCensus?&dbFrom=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml&dbTo=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&section=femployment'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkfEmpstatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=future_emp&b=true'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'

# Run update queries
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/updateFeeds?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&username=admin'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/updateNext?&feed=southlanewheels-or-us&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&agency=130&username=admin'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/updateNext?&feed=rhodyexpress-or-us&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&agency=185&username=admin'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/updateNext?&feed=woodburn-or-us&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&agency=129&username=admin'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/updateNext?&feed=swanisland-or-us&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&agency=162&username=admin'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkUpdatestatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/changeDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,&field=update_process&b=true'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'

# Activate
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/activateDBs?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/readDBinfo'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=0,september17,com/model/database/connections/spatial/september17.cfg.xml,com/model/database/connections/transit/september17.cfg.xml,jdbc:postgresql://db:5432/september17,postgres,postgres,com/model/database/connections/spatial/mapping/mapping.hbm.xml,com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml,com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db=1,april18,,,jdbc:postgresql://db:5432/april18,postgres,postgres,,,'

# Cleanup
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedGTFS'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedPNR'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedT6'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedEmp'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedfEmp'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedfPop'
curl -s --fail 'http://localhost:8080/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedRegion'
