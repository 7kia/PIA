package model;

import java.sql.Date;

/**
 * Book
 *  
 */
public class Book {
    /**
     * name
     */
	public String name;
	/**
     * author
     */
	public String author;
	/**
     * pageAmount
     */
	public Integer pageAmount;
	/**
     * publishingData
     */
	public Date publishingData;
	
	/**
	 * The constructor need if not need set values for creation 
	 */
	public Book()
	{
		
	}
	
	/**
     * The constructor set all values 
     */
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
	
	/**
     *  getName()
     */
	public String getName() {
        return name;
	}
	
	/**
     *  getAuthor()
     */
	public String getAuthor() {
        return author;
	}
	
	/**
     *  pageAmount()
     */
	public Integer getPageAmount() {
        return pageAmount;
	}
	
	/**
     *  getPublishingData()
     */
	public Date getPublishingData() {
        return publishingData;
	}
	
	
}
