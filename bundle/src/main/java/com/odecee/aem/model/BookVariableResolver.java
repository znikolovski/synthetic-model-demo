package com.odecee.aem.model;

import io.neba.api.configuration.PlaceholderVariableResolver;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.odecee.aem.model.book.Book;

@Component(
		name="BookVariableResolver",
		label="Book Variable Resolver")
@Service
public class BookVariableResolver implements PlaceholderVariableResolver {
	
	private final static Logger log = LoggerFactory.getLogger(Book.class);
	
	private static final String ISBN_PLACEHOLDER = "isbn";
	private static final String LANGUAGE_PLACEHOLDER = "language";

	public String resolve(String variableName) {
		log.info("Looking for variable: " + variableName);
		
		if(ISBN_PLACEHOLDER.equals(variableName)) {
			return "0747532745";
		}
		if(LANGUAGE_PLACEHOLDER.equals(variableName)) {
			return "en";
		}
		
		return null;
	}
	
}
