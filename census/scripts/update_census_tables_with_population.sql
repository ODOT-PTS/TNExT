update census_states_ref 
    set population2010 = pc.population, population = pc.population, housing = pc.housing 
from population_state_csv pc
where (census_states_ref.stateid = pc.state);	

update census_blocks_ref 
    set population2010 = pc.population, population = pc.population, housing = pc.housing 
from population_block_csv pc
where (
    census_blocks_ref.blockid = pc.block
    and census_blocks_ref.tractid = pc.tract
    and census_blocks_ref.countyid = pc.county
);	

update census_tracts_ref 
    set population2010 = pc.population, population = pc.population, housing = pc.housing 
from population_tract_csv pc
where (
    census_tracts_ref.tractid = pc.tract
    and census_tracts_ref.countyid = pc.county
);	

update census_congdists_ref 
    set population2010 = pc.population, population = pc.population, housing = pc.housing 
from population_congressional_district_csv pc
where (census_congdists_ref.congdistid = pc.congressional_district);

update census_counties_ref 
    set population2010 = pc.population, population = pc.population, housing = pc.housing 
from population_county_csv pc
where (census_counties_ref.countyid = pc.county);

update census_places_ref 
    set population2010 = pc.population, population = pc.population, housing = pc.housing 
from population_place_csv pc
where (census_places_ref.placeid = pc.place);

update census_urbans_ref 
    set population2010 = pc.population, population = pc.population, housing = pc.housing 
from population_urban_area_csv pc
where (census_urbans_ref.urbanid = pc.urban_area);

update census_urbans_ref set poptype50k = (CASE WHEN population>=50000 THEN 'U' ELSE 'R' END);

UPDATE census_blocks_ref set poptype = ur.poptype50k FROM census_urbans_ref ur WHERE (census_blocks_ref.urbanid = ur.urbanid);
   
UPDATE census_blocks_ref SET poptype = 'R' WHERE poptype is null; 