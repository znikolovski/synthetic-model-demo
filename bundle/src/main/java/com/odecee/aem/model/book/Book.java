package com.odecee.aem.model.book;

import io.neba.api.annotations.PostMapping;
import io.neba.api.annotations.ResourceModel;
import io.neba.api.annotations.This;

import java.io.IOException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;

import com.day.cq.wcm.api.Page;
import com.odecee.aem.utils.LinkUtil;

@ResourceModel(types = "/apps/synthetic-models/components/book")
public class Book {
	
	@This
	private Resource resource;
	
	private String isbn;
	private String imagePath;
	private String title;
	private String description;
	private String genre;
	private String author;
	
	private Resource bookInfo;
	
	//Map the correct book resource based on the ISBN page property
	@PostMapping
	public void initialize() throws IOException {
		if(isbn == null) {
			isbn = PropertiesUtil.toString(resource.getParent().adaptTo(ValueMap.class).get("isbn"), null);
		}
		if(isbn == null) {
			throw new IOException("No ISBN found");
		}
		if(bookInfo==null) {
			bookInfo = resource.getResourceResolver().getResource("/content/book-store/bookinfo/"+isbn);
		}
	}
	
	public String getIsbn() {
		return isbn;
	}
	
	public String getTitle() {
		if(title != null) {
			return title;
		}
		return getProperty("title");
	}
	
	public String getDescription() {
		if(description != null) {
			return description;
		}
		return getProperty("description");
	}
	
	public String getGenre() {
		if(genre != null) {
			return genre;
		}
		return getProperty("genrename");
	}
	
	public String getAuthor() {
		if(author != null) {
			return author;
		}
		return getProperty("firstname") + " " + getProperty("lastname");
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public String getLink() {
		Page parentPage = resource.getParent().getParent().adaptTo(Page.class);
		return LinkUtil.prepareLinkPath(parentPage);
	}
	
	private String getProperty(String key) {
		return PropertiesUtil.toString(bookInfo.getResourceMetadata().get(key), null);
	}

}