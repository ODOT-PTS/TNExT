DROP TABLE IF EXISTS tract_b18101;
CREATE TABLE tract_b18101 
(GISJOIN varchar(21), tid varchar(21), STATEA varchar(11), COUNTYA varchar(11), TRACTA varchar(11), 
male_under_5_with_disability int, male_under_5_no_disability int, male_5_to_17_with_disability int, male_5_to_17_no_disability int, 
male_18_to_34_with_disability int,male_18_to_34_no_disability int,male_35_to_64_with_disability int,male_35_to_64_no_disability int,
male_65_to_74_with_disability int,male_65_to_74_no_disability int,male_over_75_with_disability int,male_over_75_no_disability int,

female_under_5_with_disability int, female_under_5_no_disability int, female_5_to_17_with_disability int, female_5_to_17_no_disability int, 
female_18_to_34_with_disability int,female_18_to_34_no_disability int,female_35_to_64_with_disability int,female_35_to_64_no_disability int,
female_65_to_74_with_disability int,female_65_to_74_no_disability int,female_over_75_with_disability int,female_over_75_no_disability int,

with_disability int, without_disability int,
CONSTRAINT t_b18101_pkey PRIMARY KEY (GISJOIN))
WITH (
  OIDS=FALSE
);
ALTER TABLE tract_b18101
  OWNER TO postgres;
