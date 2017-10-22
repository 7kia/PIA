package model;

public class Book {
	private String name;
	private String author;
	private Integer publishingYear;
	
	public Book(String name, String author, Integer publishingYear)
	{
		this.name = name;
		this.author = author;
		this.publishingYear = publishingYear;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public Integer getPublishingYear()
	{
		return publishingYear;
	}
}
