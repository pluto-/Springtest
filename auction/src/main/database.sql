-- Table: auctions

-- DROP TABLE auctions;

CREATE TABLE auctions
(
  id serial NOT NULL,
  seller_id integer NOT NULL,
  created_at timestamp without time zone NOT NULL DEFAULT now(),
  completed boolean NOT NULL DEFAULT false,
  enabled boolean NOT NULL DEFAULT true,
  buyer_id integer,
  demand_resource_id integer NOT NULL,
  demand_amount integer NOT NULL,
  offer_resource_id integer NOT NULL,
  offer_amount integer NOT NULL,
  CONSTRAINT auctions_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE auctions
  OWNER TO auction;


-- Table: completed_auctions

-- DROP TABLE completed_auctions;

CREATE TABLE completed_auctions
(
  id serial NOT NULL,
  auction_id integer NOT NULL,
  processed boolean NOT NULL DEFAULT false,
  CONSTRAINT completed_auctions_id_fkey FOREIGN KEY (id)
      REFERENCES auctions (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE completed_auctions
  OWNER TO auction;
