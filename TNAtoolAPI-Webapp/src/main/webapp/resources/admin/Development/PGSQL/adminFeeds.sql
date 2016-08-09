delete from gtfs_pg_users;
delete from gtfs_uploaded_feeds;
delete from gtfs_selected_feeds;

insert into gtfs_pg_users (username,email,firstname,lastname,quota,usedspace,password,active,key)
VALUES ('admin','admin','','',1,0,'1234',false,1234);

insert into gtfs_uploaded_feeds (username,feedname,ispublic,feedsize)
select 'admin', feedname, false, 0 
from gtfs_feed_info;

insert into gtfs_selected_feeds (username,feedname,agency_id)
select 'admin', feedname, defaultid
from gtfs_feed_info;
