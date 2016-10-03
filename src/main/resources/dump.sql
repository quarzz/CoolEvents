--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.4
-- Dumped by pg_dump version 9.5.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: Access; Type: TABLE; Schema: public; Owner: stud
--

CREATE TABLE "Access" (
    id integer NOT NULL,
    user_id integer,
    event_id integer,
    access integer
);


ALTER TABLE "Access" OWNER TO stud;

--
-- Name: Access_id_seq; Type: SEQUENCE; Schema: public; Owner: stud
--

CREATE SEQUENCE "Access_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "Access_id_seq" OWNER TO stud;

--
-- Name: Access_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: stud
--

ALTER SEQUENCE "Access_id_seq" OWNED BY "Access".id;


--
-- Name: Events; Type: TABLE; Schema: public; Owner: stud
--

CREATE TABLE "Events" (
    id integer NOT NULL,
    title character varying(50),
    description text,
    date timestamp without time zone
);


ALTER TABLE "Events" OWNER TO stud;

--
-- Name: Events_id_seq; Type: SEQUENCE; Schema: public; Owner: stud
--

CREATE SEQUENCE "Events_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "Events_id_seq" OWNER TO stud;

--
-- Name: Events_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: stud
--

ALTER SEQUENCE "Events_id_seq" OWNED BY "Events".id;


--
-- Name: Users; Type: TABLE; Schema: public; Owner: stud
--

CREATE TABLE "Users" (
    id integer NOT NULL,
    "firstName" character varying(50),
    "lastName" character varying(50),
    login character varying(50) NOT NULL,
    password character varying(256)
);


ALTER TABLE "Users" OWNER TO stud;

--
-- Name: Users_id_seq; Type: SEQUENCE; Schema: public; Owner: stud
--

CREATE SEQUENCE "Users_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "Users_id_seq" OWNER TO stud;

--
-- Name: Users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: stud
--

ALTER SEQUENCE "Users_id_seq" OWNED BY "Users".id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: stud
--

ALTER TABLE ONLY "Access" ALTER COLUMN id SET DEFAULT nextval('"Access_id_seq"'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: stud
--

ALTER TABLE ONLY "Events" ALTER COLUMN id SET DEFAULT nextval('"Events_id_seq"'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: stud
--

ALTER TABLE ONLY "Users" ALTER COLUMN id SET DEFAULT nextval('"Users_id_seq"'::regclass);


--
-- Data for Name: Access; Type: TABLE DATA; Schema: public; Owner: stud
--

COPY "Access" (id, user_id, event_id, access) FROM stdin;
2	2	1	1
1	1	1	2
3	2	2	2
4	1	2	1
5	1	3	2
6	2	3	1
7	1	4	2
8	1	5	2
9	1	6	2
10	2	7	2
11	2	8	2
12	2	9	2
13	2	10	2
\.


--
-- Name: Access_id_seq; Type: SEQUENCE SET; Schema: public; Owner: stud
--

SELECT pg_catalog.setval('"Access_id_seq"', 8, true);


--
-- Data for Name: Events; Type: TABLE DATA; Schema: public; Owner: stud
--

COPY "Events" (id, title, description, date) FROM stdin;
3	New Year	Description of 3nd event  ccccccccccccccccccccccccccccc	2016-01-01 00:00:00
1	Mihail's birthday	Description of 1st event aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa	1995-10-02 00:00:00
2	Vladislav's birthday	Description of 2nd event  bbbbbbbbbbbbbbbbbbbbbbb	1996-02-20 00:00:00
4	Mother's day	Description of 4th event	2016-10-14 23:49:48.498
5	Christmas	Description of 5th event	2016-01-07 00:00:00.724
6	Victory Day	Description of 6th event	2016-05-05 00:00:00
7	Kalyady	Description of 7th event	2016-12-25 00:00:00
8	Kupalle	Description of 7th event	2016-06-21 00:00:00
9	Diploma	Red Diploma	2017-06-06 00:00:00
10	University Graduation	I've been waiting for it so long	2017-07-31 00:00:00
\.


--
-- Name: Events_id_seq; Type: SEQUENCE SET; Schema: public; Owner: stud
--

SELECT pg_catalog.setval('"Events_id_seq"', 9, true);


--
-- Data for Name: Users; Type: TABLE DATA; Schema: public; Owner: stud
--

COPY "Users" (id, "firstName", "lastName", login, password) FROM stdin;
1	Mihail	Kukuev	misha.kukuev@gmail.com	admin
2	Vladislav	Matulis	quarzzap@gmail.com	admin
3	Dmitriy	Sipeiko	Mihail.Kukuev@yandex.ru	12345
4	Ivan	Ivanov	aaaa	12345
5	Pit	Orliv	bbbb	12345
\.


--
-- Name: Users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: stud
--

SELECT pg_catalog.setval('"Users_id_seq"', 8, true);


--
-- Name: Access_pkey; Type: CONSTRAINT; Schema: public; Owner: stud
--

ALTER TABLE ONLY "Access"
    ADD CONSTRAINT "Access_pkey" PRIMARY KEY (id);


--
-- Name: Events_pkey; Type: CONSTRAINT; Schema: public; Owner: stud
--

ALTER TABLE ONLY "Events"
    ADD CONSTRAINT "Events_pkey" PRIMARY KEY (id);


--
-- Name: Users_pkey; Type: CONSTRAINT; Schema: public; Owner: stud
--

ALTER TABLE ONLY "Users"
    ADD CONSTRAINT "Users_pkey" PRIMARY KEY (id);


--
-- Name: Users_login_uindex; Type: INDEX; Schema: public; Owner: stud
--

CREATE UNIQUE INDEX "Users_login_uindex" ON "Users" USING btree (login);


--
-- Name: Access_event_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: stud
--

ALTER TABLE ONLY "Access"
    ADD CONSTRAINT "Access_event_id_fkey" FOREIGN KEY (event_id) REFERENCES "Events"(id);


--
-- Name: Access_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: stud
--

ALTER TABLE ONLY "Access"
    ADD CONSTRAINT "Access_user_id_fkey" FOREIGN KEY (user_id) REFERENCES "Users"(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

