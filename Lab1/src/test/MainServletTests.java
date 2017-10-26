package test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
    }

    @After
    public void quitDriver() {
        driver.quit();
    }
    
    @Test
    public void testAddBook() {
    	driver.navigate().refresh();
        driver.get(PATH_TO_MAIN_SERVLET);
        
        Book book = new Book("n", "a", 1);
        
        fillFields(book);
		    
        driver.findElement(By.className(MainServlet.ADD_BOOK_BTN)).click();
        driver.findElement(By.className(MainServlet.SHOW_BUTTON_CLASS_NAME)).click();
	  
        List<WebElement> rows = driver.findElements(By.tagName("td"));
        
        String s1 = rows.get(0).getText();
        String s2 = rows.get(1).getText();
        String s3 = rows.get(2).getText();
        Assert.assertFalse(rows.get(0).getText() == book.name);
        Assert.assertFalse(rows.get(1).getText() == book.author);
        Assert.assertFalse(rows.get(2).getText() == book.publishingYear.toString());
    }
    
    @Test
    public void testEmptyField() {
    	driver.navigate().refresh();
        driver.get(PATH_TO_MAIN_SERVLET);
        
        driver.findElement(By.className(MainServlet.ADD_BOOK_BTN)).click();
        String errorMessage = driver.findElement(By.tagName("h1")).getText();
        Assert.assertEquals(errorMessage, MainServlet.getErrorMessage("name"));
        
        driver.findElement(By.className("nameField")).sendKeys("a");
        driver.findElement(By.className(MainServlet.ADD_BOOK_BTN)).click();
        errorMessage = driver.findElement(By.tagName("h1")).getText();
        Assert.assertEquals(errorMessage, MainServlet.getErrorMessage("author"));
        
        driver.findElement(By.className("nameField")).sendKeys("a");
        driver.findElement(By.className("authorField")).sendKeys("a1");
        driver.findElement(By.className(MainServlet.ADD_BOOK_BTN)).click();
        errorMessage = driver.findElement(By.tagName("h1")).getText();
        Assert.assertEquals(errorMessage, MainServlet.getErrorMessage("publishingYear"));
    }

    @Test
    public void testPublishingData() {
    	driver.navigate().refresh();
        driver.get(PATH_TO_MAIN_SERVLET);
        
        testFields("n", "a", "-9999999999", "publishingYear");
        testBook(new Book("n", "a", Integer.MIN_VALUE), "publishingYear");
        testBook(new Book("n", "a", -1), "publishingYear");
        testBook(new Book("n", "a", 0), null);
        testBook(new Book("n", "a", 2017), null);
        testBook(new Book("n", "a", Integer.MAX_VALUE), null);
        testFields("n", "a", "9999999999", "publishingYear");
    }
  
    private void testBook(Book book, String incorrectField)
    {
    	fillFields(book);
    	sumbmitData(incorrectField);
    }
    
    private void testFields(
    		String name,
    		String author,
    		String publishingYear,
    		String incorrectField
    ) {
    	fillFields(name, author, publishingYear);
    	sumbmitData(incorrectField);
    }
    
    private void sumbmitData(String incorrectField)
    {
    	driver.findElement(By.className(MainServlet.ADD_BOOK_BTN)).click();
    	
    	if(incorrectField != null)
    	{
    		String errorMessage = driver.findElement(By.tagName("h1")).getText();
    		Assert.assertEquals(errorMessage, MainServlet.getErrorMessage(incorrectField));
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
