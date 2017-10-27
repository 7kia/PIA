package model;

import java.util.Date;

public class Book {
	public String name;
	public String author;
	public Integer pageAmount;
	public Date publishingData;
	
	public Book(
		String name,
		String author,
		Integer pageAmount,
		Date publishingData
	) {
		this.name = name;
		this.author = author;
		this.pageAmount = pageAmount;
		this.publishingData = publishingData;
	}
	
}
