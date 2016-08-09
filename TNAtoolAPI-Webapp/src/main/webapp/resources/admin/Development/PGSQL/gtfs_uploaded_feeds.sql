CREATE TABLE gtfs_uploaded_feeds
(
  feedname text NOT NULL,
  username text,
  ispublic boolean NOT NULL,
  feedsize integer,
  CONSTRAINT feeds_pkey PRIMARY KEY (feedname),
  CONSTRAINT gtfs_uploaded_feeds_username_fkey FOREIGN KEY (username)
      REFERENCES gtfs_pg_users (username) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gtfs_uploaded_feeds
  OWNER TO postgres;