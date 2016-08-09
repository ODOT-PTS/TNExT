CREATE TABLE gtfs_selected_feeds
(
  username text NOT NULL,
  feedname text NOT NULL,
  agency_id text,
  CONSTRAINT selected_feeds_pkey PRIMARY KEY (username, feedname),
  CONSTRAINT gtfs_selected_feeds_username_fkey FOREIGN KEY (username)
      REFERENCES gtfs_pg_users (username) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gtfs_selected_feeds
  OWNER TO postgres;
