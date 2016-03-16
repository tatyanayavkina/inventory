CREATE TABLE public.items
(
  id integer NOT NULL,
  created_by_user character varying(255) NOT NULL,
  creation_time timestamp without time zone NOT NULL,
  modification_time timestamp without time zone,
  modified_by_user character varying(255) NOT NULL,
  item_name character varying(255),
  description character varying(255),
  currency character varying(255),
  amount numeric(19,2),
  CONSTRAINT items_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.items
  OWNER TO postgres;

CREATE TABLE public.services
(
  id integer NOT NULL,
  created_by_user character varying(255) NOT NULL,
  creation_time timestamp without time zone NOT NULL,
  modification_time timestamp without time zone,
  modified_by_user character varying(255) NOT NULL,
  service_name character varying(255),
  description character varying(255),
  is_continuous boolean,
  is_auto boolean,
  length character varying(255),
  currency character varying(255),
  amount numeric(19,2),
  CONSTRAINT services_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.services
  OWNER TO postgres;

CREATE TABLE public.purchases
(
  id integer NOT NULL,
  created_by_user character varying(255) NOT NULL,
  creation_time timestamp without time zone NOT NULL,
  modification_time timestamp without time zone,
  modified_by_user character varying(255) NOT NULL,
  client character varying(255),
  state character varying(255),
  item_id integer,
  CONSTRAINT purchases_pkey PRIMARY KEY (id),
  CONSTRAINT fk_purchase_item FOREIGN KEY (item_id)
      REFERENCES public.items (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.purchases
  OWNER TO postgres;

  CREATE TABLE public.subscriptions
(
  id integer NOT NULL,
  created_by_user character varying(255) NOT NULL,
  creation_time timestamp without time zone NOT NULL,
  modification_time timestamp without time zone,
  modified_by_user character varying(255) NOT NULL,
  client character varying(255),
  end_date timestamp without time zone,
  start_date timestamp without time zone,
  state character varying(255),
  service_id integer,
  CONSTRAINT subscriptions_pkey PRIMARY KEY (id),
  CONSTRAINT fk_subscription_service FOREIGN KEY (service_id)
      REFERENCES public.services (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.subscriptions
  OWNER TO postgres;

