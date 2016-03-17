CREATE TABLE public.items
(
  id                INTEGER                     NOT NULL,
  created_by_user   CHARACTER VARYING(255)      NOT NULL,
  creation_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  modification_time TIMESTAMP WITHOUT TIME ZONE,
  modified_by_user  CHARACTER VARYING(255)      NOT NULL,
  item_name         CHARACTER VARYING(255),
  description       CHARACTER VARYING(255),
  currency          CHARACTER VARYING(255),
  amount            NUMERIC(19, 2),
  CONSTRAINT items_pkey PRIMARY KEY (id)
)
WITH (
OIDS =FALSE
);
ALTER TABLE public.items
OWNER TO postgres;

CREATE TABLE public.services
(
  id                INTEGER                     NOT NULL,
  created_by_user   CHARACTER VARYING(255)      NOT NULL,
  creation_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  modification_time TIMESTAMP WITHOUT TIME ZONE,
  modified_by_user  CHARACTER VARYING(255)      NOT NULL,
  service_name      CHARACTER VARYING(255),
  description       CHARACTER VARYING(255),
  is_continuous     BOOLEAN,
  is_auto           BOOLEAN,
  length            CHARACTER VARYING(255),
  currency          CHARACTER VARYING(255),
  amount            NUMERIC(19, 2),
  CONSTRAINT services_pkey PRIMARY KEY (id)
)
WITH (
OIDS =FALSE
);
ALTER TABLE public.services
OWNER TO postgres;

CREATE TABLE public.purchases
(
  id                INTEGER                     NOT NULL,
  created_by_user   CHARACTER VARYING(255)      NOT NULL,
  creation_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  modification_time TIMESTAMP WITHOUT TIME ZONE,
  modified_by_user  CHARACTER VARYING(255)      NOT NULL,
  client            CHARACTER VARYING(255),
  state             CHARACTER VARYING(255),
  item_id           INTEGER,
  CONSTRAINT purchases_pkey PRIMARY KEY (id),
  CONSTRAINT fk_purchase_item FOREIGN KEY (item_id)
  REFERENCES public.items (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS =FALSE
);
ALTER TABLE public.purchases
OWNER TO postgres;

CREATE TABLE public.subscriptions
(
  id                INTEGER                     NOT NULL,
  created_by_user   CHARACTER VARYING(255)      NOT NULL,
  creation_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  modification_time TIMESTAMP WITHOUT TIME ZONE,
  modified_by_user  CHARACTER VARYING(255)      NOT NULL,
  client            CHARACTER VARYING(255),
  end_date          TIMESTAMP WITHOUT TIME ZONE,
  start_date        TIMESTAMP WITHOUT TIME ZONE,
  state             CHARACTER VARYING(255),
  service_id        INTEGER,
  CONSTRAINT subscriptions_pkey PRIMARY KEY (id),
  CONSTRAINT fk_subscription_service FOREIGN KEY (service_id)
  REFERENCES public.services (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS =FALSE
);
ALTER TABLE public.subscriptions
OWNER TO postgres;

