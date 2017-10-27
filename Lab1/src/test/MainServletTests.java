package test;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import model.Book;
import servlet.MainServlet;

public class MainServletTests {
    static private String PATH_TO_CHROME_DRIVER = "f:\\SDK\\GeckoDriver\\chromedriver.exe";
    static private String PATH_TO_MAIN_SERVLET = "http://localhost:8080/Lab1/MainServlet";
    static private ChromeDriverService service;
    static private WebDriver driver;
    
    private Date rightDate = new Date(2012, 12, 12);
    
    @BeforeClass
    public static void createAndStartService() throws IOException{
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(PATH_TO_CHROME_DRIVER))
                .usingAnyFreePort()
                .build();
        service.start();
        createDriver();
    }

    @AfterClass
    public static void createAndStopService() {
    	driver.quit();
        service.stop();
    }

    static public void createDriver() {
        driver = new RemoteWebDriver(service.getUrl(),
                DesiredCapabilities.chrome());
        
        driver.navigate().refresh();
        driver.get(PATH_TO_MAIN_SERVLET);
    }
    
    @Test
    public void testEmptyField() {
    	testFields("", "a", "5", "12.12.2012",  MainServlet.getEmptyMessage("name"));
    	testFields("n", "", "2", "12.12.2012", MainServlet.getEmptyMessage("author"));
    	testFields("c", "a", "", "12.12.2012", MainServlet.getEmptyMessage("pageAmount"));
    	testFields("c", "a", "3", "", MainServlet.getEmptyMessage("publishingDate"));
    }

    @Test
    public void testPageAmountField() {
        testFields(
        		"n",
        		"a",
        		"-9999999999",
        		"12.12.2012",
        		MainServlet.getOutOfRangeMessage()
        );
        testBook(
    		new Book("n", "a", Integer.MIN_VALUE, rightDate),
    		MainServlet.getOutOfRangeMessage()
        );
        testBook(
    		new Book("n", "a", -1, rightDate),
    		MainServlet.getOutOfRangeMessage()
        );
        testFields(
        	"n", 
        	"a", 
        	"9999999999", 
        	"12.12.2012",
        	MainServlet.getOutOfRangeMessage()
        );
        
        testBook(new Book("n", "a", 0, rightDate), null);
        testBook(new Book("n", "a", 2017, rightDate), null);
        testBook(new Book("n", "a", Integer.MAX_VALUE, rightDate), null);
    }
  
    @Test
    public void testPublishingDateFields() {
    	testFields(
            	"n", 
            	"a", 
            	"100", 
            	"12.12.2012",
            	null
            );
    	testFields(
            	"n",
            	"a",
            	"100",
            	"120.12.2012",
            	MainServlet.getIncorrectDateMesage()
            );
    	testFields(
            	"n", 
            	"a", 
            	"100", 
            	"2012",
            	MainServlet.getIncorrectDateMesage()
            );
    	testFields(
            	"n", 
            	"a", 
            	"100", 
            	"d",
            	MainServlet.getIncorrectDateMesage()
            );
    	testFields(
            	"n", 
            	"a", 
            	"100", 
            	"",
            	MainServlet.getIncorrectDateMesage()
            );
    }
    
    @Test
    public void testNameAndAuthorFields() {
    	testFields("a", "b", "1", "12.12.2012", null);
    }
    private void testBook(Book book, String servletMessage)
    {
    	fillFields(book);
    	sumbmitData(servletMessage);
    }
    
    private void testFields(
    		String name,
    		String author,
    		String pageAmount,
    		String publishingDate,
    		String servletMessage
    ) {
    	fillFields(name, author, pageAmount, publishingDate);
    	sumbmitData(servletMessage);
    }
    
    private void sumbmitData(String servletMessage)
    {
    	driver.findElement(By.className(MainServlet.ADD_BOOK_BTN)).click();
    	
    	if(servletMessage != null)
    	{
    		String errorMessage = driver.findElement(By.tagName("h1")).getText();
    		Assert.assertEquals(errorMessage, servletMessage);
    		clearFields();
    	}
    }
    
    @Before
    public void clearFields()
    {
    	driver.findElement(By.className("nameField")).clear();
    	driver.findElement(By.className("authorField")).clear();
    	driver.findElement(By.className("pageAmountField")).clear();
    	driver.findElement(By.className("publishingDateField")).sendKeys(Keys.DELETE);
    }
    
    private void fillFields(Book book)
    {
        driver.findElement(By.className("nameField")).sendKeys(book.name);
        driver.findElement(By.className("authorField")).sendKeys(book.author);
        
        String integerStr = Integer.toString(book.pageAmount);
        driver.findElement(By.className("pageAmountField")).sendKeys(integerStr);
        
        String dateStr = MainServlet.getDateFormat().format(book.publishingData);
        driver.findElement(By.className("publishingDateField")).sendKeys(dateStr);
    }
    
    private void fillFields(
    		String name,
    		String author,
    		String pageAmount,
    		String publishingDate
    ) {
        driver.findElement(By.className("nameField")).sendKeys(name);
        driver.findElement(By.className("authorField")).sendKeys(author);
        driver.findElement(By.className("pageAmountField")).sendKeys(pageAmount);
        driver.findElement(By.className("publishingDateField")).sendKeys(publishingDate);
    }
}
