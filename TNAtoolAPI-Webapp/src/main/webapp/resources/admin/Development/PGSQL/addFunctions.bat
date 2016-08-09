set PGPASSWORD=%1
set p=%5
%4 -U %2 -d %3 -a -f %p%Functions.sql  1> %p%FOut.txt 2> %p%FErr.txt
exit