# TNAST
The Transit Network Analysis Software Tool (TNAST) is a web-based software tool developed for the visualization, analysis, and reporting of regional and statewide transit networks in the state of Oregon. 
The TNA software tool has been developed using open source tools.


Following are the software and settings required to host an instance of the TNA tool on a machine: 

    1-Java Development Kit 1.8.  
    
    2-PostgreSQL 9.4: the tool makes use of the PostgreSQL database management 
     system to store data and run the queries, be it in a spatial query or not. 
     The current version of the tool is set to run on PostgreSQL 9.4.  .
     
    3-Install PostGIS extension on PostgreSQL. 
    
    4-Database dumps may be found here. Note that database dumps have to be restored with the same name. 
    
    5-For development purposes any Java IDE can be used to clone the code from the GitHub repository, 
     otherwise the code can be directly download from GitHub. 
     
    6-Once the project is copied on to the local disk, database parameters has to be set in the following 
     comma separated file. These parameters are DB Server URL, Port, Username and Password: 
     [Project Directory]\src\main\webapp\resources\admin\databaseParams.csv 
 
    7-Maven Apache Project is used to run the tool on a Tomcat7 web server. 
