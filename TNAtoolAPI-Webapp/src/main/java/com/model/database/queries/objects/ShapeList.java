package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "shapeList")
public class ShapeList {
	
	@XmlElement(name="shapes")
	public List<Rshape> shapelist = new ArrayList<Rshape>();	

}
