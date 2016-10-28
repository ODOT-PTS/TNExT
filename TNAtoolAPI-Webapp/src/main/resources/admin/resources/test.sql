CREATE TABLE gtfs_pg_users
(
  username text NOT NULL,
  email text NOT NULL,
  firstname text,
  quota integer NOT NULL,
  usedspace integer NOT NULL,
  lastname text,
  password text NOT NULL,
  active boolean NOT NULL,
  key integer,
  CONSTRAINT users_pkey PRIMARY KEY (username)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gtfs_pg_users
  OWNER TO postgres;
