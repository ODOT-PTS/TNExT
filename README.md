[![CircleCI](https://circleci.com/gh/ODOT-PTS/TNAST_MAVEN.svg?style=svg)](https://circleci.com/gh/ODOT-PTS/TNAST_MAVEN)

# TNAST
The Transit Network Analysis Software Tool (TNAST) is a web-based software tool developed for the visualization, analysis, and reporting of regional and statewide transit networks in the state of Oregon. 
The TNA software tool has been developed using open source tools.

The following software packages must be installed on a computer to be able to host an instance of the Transit Network Analysis Software Tool (TNAST):

1.	**Java Development Kit 1.8**. 
2.	**PostgreSQL 9.4**. The TNAST utilizes the PostgreSQL database management system to store 
    data and to run both standard and spatial queries. The current version of the TNAST is 
    set to run on PostgreSQL 9.4. 
3.	**[PostGIS](http://postgis.net/install/) extension** to PostgreSQL to enable spatial analysis.
4.	**Database dumps**. Database dumps can be found [here](https://drive.google.com/open?id=0Bx4Zxars8NaNOWNxTlctME92OGc). Note that database dumps have to be 
    restored with the same name. In other words, `winter16.backup` should be restored to a postgreql database named `winter16`.
5.	**Java IDE**. For development purposes, any Java IDE can be used to clone the source code 
    available at the GitHub repository. Otherwise, the source code can be downloaded 
    directly from GitHub. 
6.	**Maven Apache Project**. Maven is used to run the TNAST on a Tomcat7 web server.
7.  **Tomcat Config** The war file should be installed as ROOT.war on the Tomcat7 server, it will be installed to '/' on the server.
            
# Setting Database Parameters
Once the source code of the TNAST is downloaded from the GitHub repository and copied onto a local computer disk, the comma separated value (CSV) file named “databaseParams.csv” must be opened. The path to access the file “databaseParams.csv” is shown below. [Project Directory] indicates the location where the source code of the TNAST is saved on the local computer disk.
          
                      <Your_Project_Directory>\src\main\webapp\resources\admin\databaseParams.csv

Once the file “databaseParams.csv” has been accessed, the database parameters DB Server **URL**, **Port**, **Username**, and **Password** must be set by the user.
