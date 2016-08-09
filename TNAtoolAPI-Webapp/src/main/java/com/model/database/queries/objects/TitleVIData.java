package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "TitleVIData")
public class TitleVIData {
	@XmlAttribute
	@JsonSerialize
	public String id;

	@XmlAttribute
	@JsonSerialize
	public String name;

	@XmlAttribute
	@JsonSerialize
	public int english;

	@XmlAttribute
	@JsonSerialize
	public int spanish;
	
	@XmlAttribute
	@JsonSerialize
	public int spanishverywell;
	
	@XmlAttribute
	@JsonSerialize
	public int spanishwell;
	
	@XmlAttribute
	@JsonSerialize
	public int spanishnotwell;
	
	@XmlAttribute
	@JsonSerialize
	public int spanishnotatall;

	@XmlAttribute
	@JsonSerialize
	public int indo_european;
	
	@XmlAttribute
	@JsonSerialize
	public int indo_europeanverywell;
	
	@XmlAttribute
	@JsonSerialize
	public int indo_europeanwell;
	
	@XmlAttribute
	@JsonSerialize
	public int indo_europeannotwell;
	
	@XmlAttribute
	@JsonSerialize
	public int indo_europeannotatall;

	@XmlAttribute
	@JsonSerialize
	public int asian_and_pacific_island;
	
	@XmlAttribute
	@JsonSerialize
	public int asian_and_pacific_islandverywell;
	
	@XmlAttribute
	@JsonSerialize
	public int asian_and_pacific_islandwell;
	
	@XmlAttribute
	@JsonSerialize
	public int asian_and_pacific_islandnotwell;
	
	@XmlAttribute
	@JsonSerialize
	public int asian_and_pacific_islandnotatall;

	@XmlAttribute
	@JsonSerialize
	public int other_languages;
	
	@XmlAttribute
	@JsonSerialize
	public int other_languagesverywell;
	
	@XmlAttribute
	@JsonSerialize
	public int other_languageswell;
	
	@XmlAttribute
	@JsonSerialize
	public int other_languagesnotwell;
	
	@XmlAttribute
	@JsonSerialize
	public int other_languagesnotatall;

	@XmlAttribute
	@JsonSerialize
	public int below_poverty;

	@XmlAttribute
	@JsonSerialize
	public int above_poverty;

	@XmlAttribute
	@JsonSerialize
	public int with_disability;

	@XmlAttribute
	@JsonSerialize
	public int without_disability;

	@XmlAttribute
	@JsonSerialize
	public int from5to17;

	@XmlAttribute
	@JsonSerialize
	public int from18to64;

	@XmlAttribute
	@JsonSerialize
	public int above65;

	@XmlAttribute
	@JsonSerialize
	public int black_or_african_american;

	@XmlAttribute
	@JsonSerialize
	public int american_indian_and_alaska_native;

	@XmlAttribute
	@JsonSerialize
	public int asian;

	@XmlAttribute
	@JsonSerialize
	public int native_hawaiian_and_other_pacific_islander;

	@XmlAttribute
	@JsonSerialize
	public int other_races;

	@XmlAttribute
	@JsonSerialize
	public int two_or_more;

	@XmlAttribute
	@JsonSerialize
	public int white;

	@XmlAttribute
	@JsonSerialize
	public int hispanic_or_latino;

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
	public int english_withinx;

	@XmlAttribute
	@JsonSerialize
	public int spanish_withinx;

	@XmlAttribute
	@JsonSerialize
	public int indo_european_withinx;

	@XmlAttribute
	@JsonSerialize
	public int asian_and_pacific_island_withinx;

	@XmlAttribute
	@JsonSerialize
	public int other_languages_withinx;

	@XmlAttribute
	@JsonSerialize
	public int below_poverty_withinx;

	@XmlAttribute
	@JsonSerialize
	public int above_poverty_withinx;

	@XmlAttribute
	@JsonSerialize
	public int with_disability_withinx;

	@XmlAttribute
	@JsonSerialize
	public int without_disability_withinx;

	@XmlAttribute
	@JsonSerialize
	public int from5to17_withinx;

	@XmlAttribute
	@JsonSerialize
	public int from18to64_withinx;

	@XmlAttribute
	@JsonSerialize
	public int above65_withinx;

	@XmlAttribute
	@JsonSerialize
	public int black_or_african_american_withinx;

	@XmlAttribute
	@JsonSerialize
	public int american_indian_and_alaska_native_withinx;

	@XmlAttribute
	@JsonSerialize
	public int asian_withinx;

	@XmlAttribute
	@JsonSerialize
	public int native_hawaiian_and_other_pacific_islander_withinx;

	@XmlAttribute
	@JsonSerialize
	public int other_races_withinx;

	@XmlAttribute
	@JsonSerialize
	public int two_or_more_withinx;

	@XmlAttribute
	@JsonSerialize
	public int white_withinx;

	@XmlAttribute
	@JsonSerialize
	public int hispanic_or_latino_withinx;
	
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
