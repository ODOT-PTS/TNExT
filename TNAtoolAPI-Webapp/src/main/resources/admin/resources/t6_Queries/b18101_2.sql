
Delete from tract_b18101 where STATEA <> '41';

update tract_b18101 set tid = lpad(STATEA, 2, '00') || lpad(COUNTYA, 3, '000') || lpad(TRACTA, 6, '000000');

update tract_b18101 set with_disability 	= male_under_5_with_disability+male_5_to_17_with_disability +male_18_to_34_with_disability 
											+male_35_to_64_with_disability +male_65_to_74_with_disability +male_over_75_with_disability 
											+female_under_5_with_disability+female_5_to_17_with_disability +female_18_to_34_with_disability 
											+female_35_to_64_with_disability +female_65_to_74_with_disability +female_over_75_with_disability;
update tract_b18101 set without_disability 	= male_under_5_no_disability+male_5_to_17_no_disability +male_18_to_34_no_disability 
											+male_35_to_64_no_disability +male_65_to_74_no_disability +male_over_75_no_disability 
											+female_under_5_no_disability+female_5_to_17_no_disability +female_18_to_34_no_disability 
											+female_35_to_64_no_disability +female_65_to_74_no_disability +female_over_75_no_disability;



