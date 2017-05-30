UPDATE title_vi_blocks_float 
SET english = blkGrp_b16004.english*ratio,
	spanish = blkGrp_b16004.spanish*ratio,
	spanishverywell = blkGrp_b16004.spanishverywell*ratio,
	spanishwell = blkGrp_b16004.spanishwell*ratio,
	spanishnotwell = blkGrp_b16004.spanishnotwell*ratio,
	spanishnotatall = blkGrp_b16004.spanishnotatall*ratio,
	
	indo_european = blkGrp_b16004.indo_european*ratio,
	indo_europeanverywell = blkGrp_b16004.indo_europeanverywell*ratio,
	indo_europeanwell = blkGrp_b16004.indo_europeanwell*ratio,
	indo_europeannotwell = blkGrp_b16004.indo_europeannotwell*ratio,
	indo_europeannotatall = blkGrp_b16004.indo_europeannotatall*ratio,

	asian_and_pacific_island = blkGrp_b16004.asian_and_pacific_island*ratio,
	asian_and_pacific_islandverywell = blkGrp_b16004.asian_and_pacific_islandverywell*ratio,
	asian_and_pacific_islandwell = blkGrp_b16004.asian_and_pacific_islandwell*ratio,
	asian_and_pacific_islandnotwell = blkGrp_b16004.asian_and_pacific_islandnotwell*ratio,
	asian_and_pacific_islandnotatall = blkGrp_b16004.asian_and_pacific_islandnotatall*ratio,

	other_languages = blkGrp_b16004.other*ratio,
	other_languagesverywell = blkGrp_b16004.otherverywell*ratio,
	other_languageswell = blkGrp_b16004.otherwell*ratio,
	other_languagesnotwell = blkGrp_b16004.othernotwell*ratio,
	other_languagesnotatall = blkGrp_b16004.othernotatall*ratio,

	from5to17 = blkGrp_b16004.from5to17*ratio,
	from18to64 = blkGrp_b16004.from18to64*ratio,
	above65 = blkGrp_b16004.above65*ratio
FROM blkGrp_b16004 
WHERE left(blockid,12) = blkGrp_b16004.gbid;
