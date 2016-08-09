@ECHO
set PGPASSWORD=%1
set p=%5
%6 -U %2 -d %3 -c "CREATE EXTENSION postgis"
%7 -U %2 -d %3 -v %4 2> %p%addCensusErr.txt
%6 -U %2 -d %3 -a -f %p%Functions.sql  1> %p%FOut.txt 2> %p%FErr.txt
exit