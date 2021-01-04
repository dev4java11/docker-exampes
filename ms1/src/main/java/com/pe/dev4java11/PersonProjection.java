package com.pe.dev4java11;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "person", types = Person.class)
public interface PersonProjection {

	Long getId();
	
	String getName();
	
	String getGender();
}
