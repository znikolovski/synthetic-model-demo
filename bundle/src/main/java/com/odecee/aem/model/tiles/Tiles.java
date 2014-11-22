package com.odecee.aem.model.tiles;

import io.neba.api.annotations.Children;
import io.neba.api.annotations.Path;
import io.neba.api.annotations.ResourceModel;

import java.util.List;

import com.odecee.aem.model.book.Book;

@ResourceModel(types = "synthetic-models/components/tiles")
public class Tiles {

	public String booksPath;
	
	//Fetch all book objects
	@Path("/content/book-store/books")
	@Children(resolveBelowEveryChild = "jcr:content/book")
	List<Book> books;

	public String getBooksPath() {
		return booksPath;
	}

	public void setBooksPath(String booksPath) {
		this.booksPath = booksPath;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}
}
