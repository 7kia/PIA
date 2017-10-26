package test;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import model.Book;
import servlet.MainServlet;

public class MainServletTests {
    static private String PATH_TO_CHROME_DRIVER = "f:\\SDK\\GeckoDriver\\chromedriver.exe";
    static private String PATH_TO_MAIN_SERVLET = "http://localhost:8080/Lab1/MainServlet";
    private static ChromeDriverService service;
    private WebDriver driver;
     
    @BeforeClass
    public static void createAndStartService() throws IOException{
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(PATH_TO_CHROME_DRIVER))
                .usingAnyFreePort()
                .build();
        service.start();
    }

    @AfterClass
    public static void createAndStopService() {
        service.stop();
    }

    @Before
    public void createDriver() {
        driver = new RemoteWebDriver(service.getUrl(),
                DesiredCapabilities.chrome());
        
        driver.navigate().refresh();
        driver.get(PATH_TO_MAIN_SERVLET);
    }

    @After
    public void quitDriver() {
        driver.quit();
    }
    
    @Test
    public void testEmptyField() {
    	testFields("", "a", "5",  MainServlet.getEmptyMessage("name"));
    	testFields("n", "", "2",  MainServlet.getEmptyMessage("author"));
    	testFields("c", "a", "",  MainServlet.getEmptyMessage("publishingYear"));
    }

    @Test
    public void testPublishingDataField() {
        testFields("n", "a", "-9999999999", MainServlet.getOutOfRangeMessage());
        testBook(new Book("n", "a", Integer.MIN_VALUE), MainServlet.getEmptyMessage("publishingYear"));
        testBook(new Book("n", "a", -1), MainServlet.getEmptyMessage("publishingYear"));
        testFields("n", "a", "9999999999", MainServlet.getOutOfRangeMessage());
        
        testBook(new Book("n", "a", 0), null);
        testBook(new Book("n", "a", 2017), null);
        testBook(new Book("n", "a", Integer.MAX_VALUE), null);
    }
  
    @Test
    public void testNameAndAuthorFields() {
        testBook(new Book("a", "b", 0), null);
    }
    
    private void testBook(Book book, String servletMessage)
    {
    	fillFields(book);
    	sumbmitData(servletMessage);
    }
    
    private void testFields(
    		String name,
    		String author,
    		String publishingYear,
    		String servletMessage
    ) {
    	fillFields(name, author, publishingYear);
    	sumbmitData(servletMessage);
    }
    
    private void sumbmitData(String servletMessage)
    {
    	driver.findElement(By.className(MainServlet.ADD_BOOK_BTN)).click();
    	
    	if(servletMessage != null)
    	{
    		String errorMessage = driver.findElement(By.tagName("h1")).getText();
    		Assert.assertEquals(errorMessage, servletMessage);
    	}
    }
    
    private void fillFields(Book book)
    {
        driver.findElement(By.className("nameField")).sendKeys(book.name);
        driver.findElement(By.className("authorField")).sendKeys(book.author);
        
        String integerStr = Integer.toString(book.publishingYear);
        driver.findElement(By.className("publishingYearField")).sendKeys(integerStr);
    }
    
    private void fillFields(String name, String author, String publishingYear)
    {
        driver.findElement(By.className("nameField")).sendKeys(name);
        driver.findElement(By.className("authorField")).sendKeys(author);
        driver.findElement(By.className("publishingYearField")).sendKeys(publishingYear);
    }
}
