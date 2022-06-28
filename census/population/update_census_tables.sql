update census_states_ref 
    set population2010 = pc.population, population = pc.population 
from population_state_csv pc
where (census_states_ref.stateid = pc.state);	

update census_blocks_ref 
    set population2010 = pc.population, population = pc.population 
from population_block_csv pc
where (
    census_blocks_ref.blockid = pc.block
    and census_blocks_ref.tractid = pc.tract
    and census_blocks_ref.countyid = pc.county
);	

update census_tracts_ref 
    set population2010 = pc.population, population = pc.population 
from population_tract_csv pc
where (
    census_tracts_ref.tractid = pc.tract
    and census_tracts_ref.countyid = pc.county
);	

update census_congdists_ref 
    set population2010 = pc.population, population = pc.population 
from population_congressional_district_csv pc
where (census_congdists_ref.congdistid = pc.congressional_district);

update census_counties_ref 
    set population2010 = pc.population, population = pc.population 
from population_county_csv pc
where (census_counties_ref.countyid = pc.county);

update census_places_ref 
    set population2010 = pc.population, population = pc.population 
from population_place_csv pc
where (census_places_ref.placeid = pc.place);

update census_urbans_ref 
    set population2010 = pc.population, population = pc.population 
from population_urban_area_csv pc
where (census_urbans_ref.urbanid = pc.urban_area);