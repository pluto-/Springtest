-- Table: building_costs

-- DROP TABLE building_costs;

CREATE TABLE building_costs
(
  building_id integer NOT NULL,
  resource_id integer NOT NULL,
  amount integer NOT NULL,
  id serial NOT NULL,
  CONSTRAINT building_costs_pkey PRIMARY KEY (id),
  CONSTRAINT building_costs_building_id_fkey FOREIGN KEY (building_id)
  REFERENCES buildings (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT building_costs_resource_id_fkey FOREIGN KEY (resource_id)
  REFERENCES resources (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT building_costs_unique_cost UNIQUE (building_id, resource_id)
)
WITH (
OIDS=FALSE
);
ALTER TABLE building_costs
OWNER TO gamecontent;


-- Table: buildings

-- DROP TABLE buildings;

CREATE TABLE buildings
(
  id serial NOT NULL,
  name character varying NOT NULL,
  buildtime integer NOT NULL,
  generated_id integer,
  generated_amount real,
  CONSTRAINT buildings_pkey PRIMARY KEY (id),
  CONSTRAINT buildings_generated_id_fkey FOREIGN KEY (generated_id)
  REFERENCES resources (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT unique_name UNIQUE (name)
)
WITH (
OIDS=FALSE
);
ALTER TABLE buildings
OWNER TO gamecontent;


-- Table: resources

-- DROP TABLE resources;

CREATE TABLE resources
(
  id serial NOT NULL,
  name character varying NOT NULL,
  CONSTRAINT resources_pkey PRIMARY KEY (id)
)
WITH (
OIDS=FALSE
);
ALTER TABLE resources
OWNER TO gamecontent;
