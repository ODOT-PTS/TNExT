package com.webapp.api;

import javax.xml.bind.annotation.XmlAttribute;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Feed {
public static String a;

@XmlAttribute
@JsonSerialize
public int Len;
}
