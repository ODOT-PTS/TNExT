DROP TABLE IF EXISTS parknride;

CREATE TABLE parknride(
pnrid integer PRIMARY KEY NOT NULL,
lat double precision NOT NULL,
lon double precision NOT NULL,
lotName text,
location text,
city character varying (30),
zipcode integer,
countyID character varying(5) NOT NULL,
county character varying(50) NOT NULL,
spaces integer,
accessibleSpaces integer,
bikeRackSpaces integer,
bikeLockerSpaces integer,
electricVehicleSpaces integer,
carSharing text,
transitService text, 
availability text,
timeLimit text,
restroom text,
benches text,
shelter text,
indoorWaitingArea text,
trashCan text,
lighting text,
securityCameras text,
sidewalks text,
pnrSignage text,
lotSurface text,
propertyOwner text,
localExpert text,
FOREIGN KEY (countyid)
      REFERENCES census_counties (countyid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE parknride
  OWNER TO postgres;

COPY parknride
FROM 'C:/Users/Administrator/git/TNAsoftware/TNAtoolAPI-Webapp/WebContent/admin/Development/PGSQL/pnr.csv' DELIMITER ',' CSV HEADER;

ALTER TABLE parknride
ADD geom geometry(Point, 2993);

UPDATE parknride 
  SET geom = ST_transform(ST_setsrid(ST_MakePoint(lon, lat),4326), 2993);
  
UPDATE parknride SET lotName='N/A' WHERE lotName IS NULL;
UPDATE parknride SET location='N/A' WHERE location IS NULL;
UPDATE parknride SET city='N/A' WHERE city IS NULL;
UPDATE parknride SET county='N/A' WHERE county IS NULL;
UPDATE parknride SET spaces=0 WHERE spaces IS NULL;
UPDATE parknride SET accessiblespaces=0 WHERE accessiblespaces IS NULL;
UPDATE parknride SET bikerackspaces=0 WHERE bikerackspaces IS NULL;
UPDATE parknride SET bikelockerspaces=0 WHERE bikelockerspaces IS NULL;
UPDATE parknride SET electricvehiclespaces=0 WHERE electricvehiclespaces IS NULL;
UPDATE parknride SET carsharing='N/A' WHERE carsharing IS NULL;
UPDATE parknride SET transitservice='N/A' WHERE transitservice IS NULL;
UPDATE parknride SET availability='N/A' WHERE availability IS NULL;
UPDATE parknride SET timelimit='N/A' WHERE timelimit IS NULL;
UPDATE parknride SET restroom='N/A' WHERE restroom IS NULL;
UPDATE parknride SET benches='N/A' WHERE benches IS NULL;
UPDATE parknride SET shelter='N/A' WHERE shelter IS NULL;
UPDATE parknride SET trashcan='N/A' WHERE trashcan IS NULL;
UPDATE parknride SET lighting='N/A' WHERE lighting IS NULL;
UPDATE parknride SET securitycameras='N/A' WHERE securitycameras IS NULL;
UPDATE parknride SET sidewalks='N/A' WHERE sidewalks IS NULL;
UPDATE parknride SET pnrsignage='N/A' WHERE pnrsignage IS NULL;
UPDATE parknride SET lotsurface='N/A' WHERE lotsurface IS NULL;
UPDATE parknride SET propertyowner='N/A' WHERE propertyowner IS NULL;
UPDATE parknride SET localexpert='N/A' WHERE localexpert IS NULL;