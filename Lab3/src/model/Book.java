package model;

import java.sql.Date;

public class Book {
	public String name;
	public String author;
	public Integer pageAmount;
	public Date publishingData;
	
	public Book()
	{
		
	}
	
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
	
	public String getName() {
        return name;
	}
	
	public String getAuthor() {
        return author;
	}
	
	public Integer getPageAmount() {
        return pageAmount;
	}
	
	public Date getPublishingData() {
        return publishingData;
	}
	
	
}
