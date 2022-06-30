## Census Data Import

Census geographies, population data and related datasets need to be imported using a semi-manual process. This document was created as a result of performing a test import of Census 2010 data. 

The steps listed here may need alterations for future imports, but this document attempts to highlight steps and files that are expected to need updating for future Census data updates.

### A. Gather Geographic Data

#### Geographic Levels
Geography and population data must be gathered for each geographic level below:

- State
- County
- Congressional District
- Place
- Block
- Census Tract
- Urban Area

#### Steps
1. Go to https://www.census.gov/cgi-bin/geo/shapefiles/index.php
2. For each geographic level:
    - Download the shapefile for Oregon for the desired year. For urban areas, download the national file (state-level file is not available).
 
### B. Prepare For Data Import

These steps should each be done locally; there is no need to commit file changes for the actual import. 

#### Steps
1. Confirm shapefiles have been gathered for all geographic levels. Unzip each shapefile zipfile and place the unzipped folder in `census/shapefiles`
2. Make any necessary corrections to table names and column names in `census/scripts/import_census_geographies.sql`. The table names should match the shapefile folder name for its respective geographic level, and column names should match the appropriate shapefile attribute names. Shapefile attributes can be viewed in GIS software, or may be found in the shapefile's xml file. 
3. Make any necessary corrections to the year in the API request urls in `census/scripts/create_population_csvs.sh`. 
4. Make any necessary corrections to the name of the updated column in `census/scripts/update_census_tables_with_population.sql`. The `population{YEAR}` column being updated should use the closest 5-increment year you are importing population data for.

### C. Import Geographic Data

You will need to build a new `census_reference` database that new snapshots can import data from. The easiest way to do this is to build it locally and copy it onto staging TNExT when ready.

#### Steps
1. Build and run TNExT locally
2. Attach to the `tnext` docker container: `docker exec -it tnext_tnext_1 /bin/bash`
3. Either run `. census.sh census_reference`, or run each step in `census_reference.sh` manually. Running manually is strongly recommended for better error catching and troubleshooting. 
4. If all steps complete successfully, the `census_reference` has been built. At this point, you can start the process of copying to staging (see Section E), but if you are planning on importing other new datasets (i.e. employment, Title VI, etc.) it's recommended that you continue doing so locally (see Section D) and copy to staging after all new datasets are imported as a workaround to broken file upload/import (pending issue to be created).

### D. Import Additional Datasets

This section documents a workaround for the broken upload/import file functionality (see https://github.com/ODOT-PTS/TNExT/issues/276), as well as helper notes for importing specific datasets.

#### Dataset-Specific Notes
- **Title VI** 
    - The `b18101` dataset could not be downloaded for one state only. The national file must be downloaded and the other states must be removed manually.
    - There's a helper script `census/scripts/process_titlevi_csvs.sh` that may be able to automate some of the column selection/renaming that's necessary before upload. The current version of the script at the time of writing this document was used for processing the 2006-2010 Title VI dataset, but column names and ordering should be confirmed before using on any other dataset. 
    
#### Steps
1. Gather the dataset following the instructions described in TNExT's admin UI for the dataset. The instructions are shown when clicking the "Import ..." button inside the "Notes" drawer.
2. If not currently building a snapshot (for instance, if you want to create a dummy snapshot solely for storing data for copying to future datasets), create a new snapshot using the TNExT Admin UI and import or copy census data.
3. *[Workaround]* Place the file(s) with the correct name and format in `census/uploads/` folder
4. "Upload" the file(s) in TNExT:
    - Click the "Import ..." button for the dataset
    - *[Workaround]* Select one of the files to upload -- it doesn't matter which file. The file will be uploaded, but it won't be used to import data (the file(s) placed in `census/uploads` will be used. Selecting a file however is necessary for TNExT to allow you to proceed with import.
    - Click "Start Upload" and proceed with importing the dataset. Confirm that there is a green checkmark next to the dataset after importing and closing the import panel. 

### E. Copy Database

For when you are building a `census_reference` database or snapshot locally that you want to move to a remote host.

#### Requirements
- FTP tool or `sftp` commandline tool

#### Steps
1. Attach to the `tnext` docker container: `docker exec -it tnext_tnext_1 /bin/bash`
2. Create a backup file of the database you want to copy in `data/`. Example:
    - `pg_dump -h db -U postgres -Fc -v -f data/census_reference.backup census_reference`
3. Transfer the backup file to the host using `sftp` or an FTP tool of your choice
4. If *replacing* an existing database, rename the existing database and optionally take a backup using `pg_dump`.
5. On the host, in the TNExT project root directory execute these commands in the tnext container (Examples for importing a new `census_reference` database):
    - `createdb -h "db" -U postgres census_reference`
    - `pg_restore -h "db" -U postgres -Fc -v -d census_reference /data/census_reference.backup`
