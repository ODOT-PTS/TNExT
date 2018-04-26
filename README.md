[![CircleCI](https://circleci.com/gh/ODOT-PTS/TNExT.svg?style=svg)](https://circleci.com/gh/ODOT-PTS/TNExT)

# TNExT
The Transit Network Explorer Tool (TNExT) is a web-based software tool developed for the visualization, analysis, and reporting of regional and statewide transit networks in the state of Oregon. 

The TNExT software tool has been developed using open source tools.

## Running using Docker

This repository is provides a set of [Docker containers](https://www.docker.com/what-container) to compile and run TNExT, together with a PostGIS database. This is the simplest approach to running TNExT locally, since you will not have to install all project dependencies.

1. Install [Docker for Mac](https://www.docker.com/docker-mac), [Docker for Windows](https://www.docker.com/docker-windows), or [Docker CE for your Linux distribution](https://www.docker.com/community-edition).
2. Clone a local copy of this repository: `git clone https://github.com/ODOT-PTS/TNExT.git`
3. Start a Docker Compose right: `docker-compose up`

## Running using local system dependencies

The following software packages must be installed on a computer to be able to host an instance of TNExT:

1.	**Java Development Kit 1.8**. 
2.	**PostgreSQL 9.4**. The TNExT utilizes the PostgreSQL database management system to store 
    data and to run both standard and spatial queries. The current version of the TNExT is 
    set to run on PostgreSQL 9.4. 
3.	**[PostGIS](http://postgis.net/install/) extension** to PostgreSQL to enable spatial analysis.
4.	**Database dumps**. Database dumps can be found [here](https://drive.google.com/open?id=0Bx4Zxars8NaNOWNxTlctME92OGc). Note that database dumps have to be 
    restored with the same name. In other words, `winter16.backup` should be restored to a postgreql database named `winter16`.
5.	**Java IDE**. For development purposes, any Java IDE can be used to clone the source code 
    available at the GitHub repository. Otherwise, the source code can be downloaded 
    directly from GitHub. 
6.	**Maven Apache Project**. Maven is used to run the TNExT on a Tomcat7 web server.
7.  **Tomcat Config** The war file should be installed as ROOT.war on the Tomcat7 server, it will be installed to '/' on the server.

## Testing

See the [integration test readme](integration-tests/README.md).