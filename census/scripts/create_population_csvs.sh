echo "FETCHING STATE"
> population.state.csv
curl "https://api.census.gov/data/2010/dec/sf1?get=P001001,H001001,NAME&for=state:41&key=$CENSUS_API_KEY" | jq -c '.[]' | while read i; do
    echo $i | sed -e 's/[][]//g' >> population.state.csv
    done
echo "FETCHING COUNTY"
> population.county.csv
curl "https://api.census.gov/data/2010/dec/sf1?get=P001001,H001001,NAME&for=county:*&in=state:41&key=$CENSUS_API_KEY" | jq -c '.[]' | while read i; do
    echo $i | sed -e 's/[][]//g' >> population.county.csv
    done
echo "FETCHING PLACE"
> population.place.csv
curl "https://api.census.gov/data/2010/dec/sf1?get=P001001,H001001,NAME&for=place:*&in=state:41&key=$CENSUS_API_KEY" | jq -c '.[]' | while read i; do
    echo $i | sed -e 's/[][]//g' >> population.place.csv
    done
echo "FETCHING URBAN AREA"
> population.urban_area.csv
curl "https://api.census.gov/data/2010/dec/sf1?get=P001001,H001001,NAME&for=urban%20area%20(or%20part):*&in=state:41&key=$CENSUS_API_KEY" | jq -c '.[]' | while read i; do
    echo $i | sed -e 's/[][]//g' >> population.urban_area.csv
    done
echo "FETCHING TRACT"
> population.tract.csv
curl "https://api.census.gov/data/2010/dec/sf1?get=P001001,H001001,NAME&for=tract:*&in=state:41&key=$CENSUS_API_KEY" | jq -c '.[]' | while read i; do
    echo $i | sed -e 's/[][]//g' >> population.tract.csv
    done
echo "FETCHING CONGRESSIONAL DISTRICT"
> population.congressional_district.csv
curl "https://api.census.gov/data/2010/dec/sf1?get=P001001,H001001,NAME&for=congressional%20district:*&in=state:41&key=$CENSUS_API_KEY" | jq -c '.[]' | while read i; do
    echo $i | sed -e 's/[][]//g' >> population.congressional_district.csv
    done
echo "FETCHING BLOCK"
> population.block.csv
curl "https://api.census.gov/data/2010/dec/sf1?get=P001001,H001001,NAME&for=block:*&in=state:41&in=county:*&in=tract:*&key=$CENSUS_API_KEY" | jq -c '.[]' | while read i; do
    echo $i | sed -e 's/[][]//g' >> population.block.csv
    done