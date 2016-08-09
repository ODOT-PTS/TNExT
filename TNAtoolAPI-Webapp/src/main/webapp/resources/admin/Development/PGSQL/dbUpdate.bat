set PGPASSWORD=%1
set p=%5
%4 -U %2 -d %3 -a -f %p%Functions.sql  1> %p%FOut.txt 2> %p%FErr.txt
%4 -U %2 -d %3 -a -f %p%Tripstableupdator_pgsql.sql  1> %p%cmdOut.txt 2> %p%cmdErr.txt
%4 -U %2 -d %3 -a -f %p%Stops_AddGeolocation.sql  1>> %p%cmdOut.txt 2>> %p%cmdErr.txt
%4 -U %2 -d %3 -a -f %p%StopsGeoCoder_PGSQL.sql  1>> %p%cmdOut.txt 2>> %p%cmdErr.txt
%4 -U %2 -d %3 -a -f %p%Stops-For-Route-query_pgsql.sql  1>> %p%cmdOut.txt 2>> %p%cmdErr.txt
%4 -U %2 -d %3 -a -f %p%servedStopsSelector-2.sql  1>> %p%cmdOut.txt 2>> %p%cmdErr.txt
%4 -U %2 -d %3 -a -f %p%gtfs_trip_stops.sql  1>> %p%cmdOut.txt 2>> %p%cmdErr.txt
%4 -U %2 -d %3 -a -f %p%Counties_trip_pgsql.sql  1>> %p%cmdOut.txt 2>> %p%cmdErr.txt
%4 -U %2 -d %3 -a -f %p%Tracts_trip_pgsql.sql  1>> %p%cmdOut.txt 2>> %p%cmdErr.txt
%4 -U %2 -d %3 -a -f %p%Urbans_trip_pgsql.sql  1>> %p%cmdOut.txt 2>> %p%cmdErr.txt
%4 -U %2 -d %3 -a -f %p%Places_trip_pgsql.sql  1>> %p%cmdOut.txt 2>> %p%cmdErr.txt
%4 -U %2 -d %3 -a -f %p%Congdists_trip_pgsql.sql  1>> %p%cmdOut.txt 2>> %p%cmdErr.txt
%4 -U %2 -d %3 -a -f %p%calendar_range_finder.sql  1>> %p%cmdOut.txt 2>> %p%cmdErr.txt
%4 -U %2 -d %3 -a -f %p%adminFeeds.sql  1>> %p%cmdOut.txt 2>> %p%cmdErr.txt
exit