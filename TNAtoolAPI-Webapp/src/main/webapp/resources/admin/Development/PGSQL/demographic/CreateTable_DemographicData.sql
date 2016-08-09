/* Create tables for title VI data */
DROP TABLE IF EXISTS oregon_TitleVI CASCADE;
CREATE TABLE oregon_TitleVI
        ( county_id character varying(5) NOT NULL PRIMARY KEY UNIQUE
        , County varchar 
        , Pop int
        , Pop_Disabled int
        , Tot_Pop_BP int
        , Tot_Pop_AP int
        , ENG_SPK int
        , ESP_SPK int
        , OTHER_INDO_SPK int
        , ASIAN_SPK int
        , OTHER_LNG_SPK int
        , HH_Tot int
        , HH_White int
        , HH_Hispanic int
        , HH_Black int
        , HH_American_Indian int
        , HH_Asian int
        , HH_Pacific_Islander int
        , HH_Pacific_Other int
        , HH_White_Not_Hisp int
        , HH_Over_65 int
        , HH_Under_65 int
        ); 
		
COPY oregon_TitleVI FROM 'C:/Users/Administrator/git/TNAsoftware/TNAtoolAPI-Webapp/WebContent/admin/Development/PGSQL/demographic/or_demographics.csv' DELIMITER ',' CSV HEADER;

