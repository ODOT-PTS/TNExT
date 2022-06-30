update census_blocks_ref
    set blockid = stateid||countyid||tractid||blockid,
    tractid = stateid||countyid||tractid,
    countyid = stateid||countyid;

update census_tracts_ref
    set tractid = stateid||countyid||tractid,
    countyid = stateid||countyid;

update census_places_ref
    set placeid = stateid||placeid;

update census_congdists_ref
    set congdistid = stateid||congdistid;

update census_counties_ref
    set countyid = stateid||countyid;