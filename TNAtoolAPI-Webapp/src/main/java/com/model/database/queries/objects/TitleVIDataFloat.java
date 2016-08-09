package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "TitleVIDataFloat")
public class TitleVIDataFloat {
	@XmlAttribute
	@JsonSerialize
	public String id;

	@XmlAttribute
	@JsonSerialize
	public String name;

	@XmlAttribute
	@JsonSerialize
	public float english = 0;

	@XmlAttribute
	@JsonSerialize
	public float spanish = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float spanishverywell = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float spanishwell = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float spanishnotwell = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float spanishnotatall = 0;

	@XmlAttribute
	@JsonSerialize
	public float indo_european = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float indo_europeanverywell = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float indo_europeanwell = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float indo_europeannotwell = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float indo_europeannotatall = 0;

	@XmlAttribute
	@JsonSerialize
	public float asian_and_pacific_island = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float asian_and_pacific_islandverywell = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float asian_and_pacific_islandwell = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float asian_and_pacific_islandnotwell = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float asian_and_pacific_islandnotatall = 0;

	@XmlAttribute
	@JsonSerialize
	public float other_languages = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float other_languagesverywell = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float other_languageswell = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float other_languagesnotwell = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float other_languagesnotatall = 0;

	@XmlAttribute
	@JsonSerialize
	public float below_poverty = 0;

	@XmlAttribute
	@JsonSerialize
	public float above_poverty = 0;

	@XmlAttribute
	@JsonSerialize
	public float with_disability = 0;

	@XmlAttribute
	@JsonSerialize
	public float without_disability = 0;

	@XmlAttribute
	@JsonSerialize
	public float from5to17 = 0;

	@XmlAttribute
	@JsonSerialize
	public float from18to64 = 0;

	@XmlAttribute
	@JsonSerialize
	public float above65 = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float black_or_african_american = 0;
	
	@XmlAttribute
	@JsonSerialize
	public float american_indian_and_alaska_native = 0;

	@XmlAttribute
	@JsonSerialize
	public float asian = 0;

	@XmlAttribute
	@JsonSerialize
	public float native_hawaiian_and_other_pacific_islander = 0;

	@XmlAttribute
	@JsonSerialize
	public float other_races = 0;

	@XmlAttribute
	@JsonSerialize
	public float two_or_more = 0;

	@XmlAttribute
	@JsonSerialize
	public float white = 0;

	@XmlAttribute
	@JsonSerialize
	public float hispanic_or_latino = 0;

	@XmlAttribute
	@JsonSerialize
	public long english_served;

	@XmlAttribute
	@JsonSerialize
	public long spanish_served;

	@XmlAttribute
	@JsonSerialize
	public long indo_european_served;

	@XmlAttribute
	@JsonSerialize
	public long asian_and_pacific_island_served;

	@XmlAttribute
	@JsonSerialize
	public long other_languages_served;

	@XmlAttribute
	@JsonSerialize
	public long below_poverty_served;

	@XmlAttribute
	@JsonSerialize
	public long above_poverty_served;

	@XmlAttribute
	@JsonSerialize
	public long with_disability_served;

	@XmlAttribute
	@JsonSerialize
	public long without_disability_served;

	@XmlAttribute
	@JsonSerialize
	public long from5to17_served;

	@XmlAttribute
	@JsonSerialize
	public long from18to64_served;

	@XmlAttribute
	@JsonSerialize
	public long above65_served;

	@XmlAttribute
	@JsonSerialize
	public long black_or_african_american_served;

	@XmlAttribute
	@JsonSerialize
	public long american_indian_and_alaska_native_served;

	@XmlAttribute
	@JsonSerialize
	public long asian_served;

	@XmlAttribute
	@JsonSerialize
	public long native_hawaiian_and_other_pacific_islander_served;

	@XmlAttribute
	@JsonSerialize
	public long other_races_served;

	@XmlAttribute
	@JsonSerialize
	public long two_or_more_served;

	@XmlAttribute
	@JsonSerialize
	public long white_served;

	@XmlAttribute
	@JsonSerialize
	public long hispanic_or_latino_served;
	
	@XmlAttribute
	@JsonSerialize
	public float english_withinx;

	@XmlAttribute
	@JsonSerialize
	public float spanish_withinx;

	@XmlAttribute
	@JsonSerialize
	public float indo_european_withinx;

	@XmlAttribute
	@JsonSerialize
	public float asian_and_pacific_island_withinx;

	@XmlAttribute
	@JsonSerialize
	public float other_languages_withinx;

	@XmlAttribute
	@JsonSerialize
	public float below_poverty_withinx;

	@XmlAttribute
	@JsonSerialize
	public float above_poverty_withinx;

	@XmlAttribute
	@JsonSerialize
	public float with_disability_withinx;

	@XmlAttribute
	@JsonSerialize
	public float without_disability_withinx;

	@XmlAttribute
	@JsonSerialize
	public float from5to17_withinx;

	@XmlAttribute
	@JsonSerialize
	public float from18to64_withinx;

	@XmlAttribute
	@JsonSerialize
	public float above65_withinx;

	@XmlAttribute
	@JsonSerialize
	public float black_or_african_american_withinx;

	@XmlAttribute
	@JsonSerialize
	public float american_indian_and_alaska_native_withinx;

	@XmlAttribute
	@JsonSerialize
	public float asian_withinx;

	@XmlAttribute
	@JsonSerialize
	public float native_hawaiian_and_other_pacific_islander_withinx;

	@XmlAttribute
	@JsonSerialize
	public float other_races_withinx;

	@XmlAttribute
	@JsonSerialize
	public float two_or_more_withinx;

	@XmlAttribute
	@JsonSerialize
	public float white_withinx;

	@XmlAttribute
	@JsonSerialize
	public float hispanic_or_latino_withinx;
	
	@XmlAttribute
	@JsonSerialize
	public long english_atlos;

	@XmlAttribute
	@JsonSerialize
	public long spanish_atlos;

	@XmlAttribute
	@JsonSerialize
	public long indo_european_atlos;

	@XmlAttribute
	@JsonSerialize
	public long asian_and_pacific_island_atlos;

	@XmlAttribute
	@JsonSerialize
	public long other_languages_atlos;

	@XmlAttribute
	@JsonSerialize
	public long below_poverty_atlos;

	@XmlAttribute
	@JsonSerialize
	public long above_poverty_atlos;

	@XmlAttribute
	@JsonSerialize
	public long with_disability_atlos;

	@XmlAttribute
	@JsonSerialize
	public long without_disability_atlos;

	@XmlAttribute
	@JsonSerialize
	public long from5to17_atlos;

	@XmlAttribute
	@JsonSerialize
	public long from18to64_atlos;

	@XmlAttribute
	@JsonSerialize
	public long above65_atlos;

	@XmlAttribute
	@JsonSerialize
	public long black_or_african_american_atlos;

	@XmlAttribute
	@JsonSerialize
	public long american_indian_and_alaska_native_atlos;

	@XmlAttribute
	@JsonSerialize
	public long asian_atlos;

	@XmlAttribute
	@JsonSerialize
	public long native_hawaiian_and_other_pacific_islander_atlos;

	@XmlAttribute
	@JsonSerialize
	public long other_races_atlos;

	@XmlAttribute
	@JsonSerialize
	public long two_or_more_atlos;

	@XmlAttribute
	@JsonSerialize
	public long white_atlos;

	@XmlAttribute
	@JsonSerialize
	public long hispanic_or_latino_atlos;
}
