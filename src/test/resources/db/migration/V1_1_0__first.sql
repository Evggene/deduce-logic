CREATE SCHEMA public;

CREATE TABLE public.model (
	id int4 NOT NULL GENERATED ALWAYS AS IDENTITY,
	"name" varchar NOT NULL,
	CONSTRAINT model_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX model_name_idx ON public.model USING btree (name);

CREATE TABLE public.knownfacts (
	id int4 NOT NULL GENERATED ALWAYS AS IDENTITY,
	model_id int4 NOT NULL,
	fact varchar NOT NULL,
	CONSTRAINT knownfacts_pk PRIMARY KEY (id),
	CONSTRAINT knownfacts_fk FOREIGN KEY (model_id) REFERENCES model(id)
);


CREATE TABLE public.type_of_expression (
	id int4 NOT NULL GENERATED ALWAYS AS IDENTITY,
	type_expression varchar NOT NULL,
	CONSTRAINT type_of_expression_pk PRIMARY KEY (id)
);
CREATE UNIQUE INDEX type_of_expression_idx ON public.type_of_expression USING btree (type_expression);

CREATE TABLE public.expressions (
	parent_id int4 NULL,
	id int4 NOT NULL GENERATED ALWAYS AS IDENTITY,
	fact varchar NULL,
	type_id int4 NOT NULL,
	CONSTRAINT rule_pk PRIMARY KEY (id),
	CONSTRAINT expression_fk2 FOREIGN KEY (type_id) REFERENCES type_of_expression(id)
);

CREATE TABLE public.rules (
	id int4 NOT NULL GENERATED ALWAYS AS IDENTITY,
	result_fact varchar(255) NOT NULL,
	model_id int4 NOT NULL,
	expression_id int4 NOT NULL,
	CONSTRAINT rules_pk PRIMARY KEY (id),
	CONSTRAINT rules_fk FOREIGN KEY (model_id) REFERENCES model(id),
	CONSTRAINT rules_fkk FOREIGN KEY (expression_id) REFERENCES expressions(id)
);
INSERT INTO public.type_of_expression
(type_expression)
VALUES('or'), ('and'), ('fact')