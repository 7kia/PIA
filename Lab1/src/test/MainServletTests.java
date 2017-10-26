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
        driver.get(PATH_TO_MAIN_SERVLET);
        
        Book book = new Book("n", "a", 1);
        
        driver.findElement(By.className("nameBtn")).sendKeys(book.getName());
        driver.findElement(By.className("authorBtn")).sendKeys(book.getAuthor());
        
        String integerStr = Integer.toString(book.getPublishingYear());
        driver.findElement(By.className("publishingYearBtn")).sendKeys(integerStr);
		    
        driver.findElement(By.className(MainServlet.ADD_BOOK_BTN)).click();
        driver.findElement(By.className(MainServlet.SHOW_BUTTON_CLASS_NAME)).click();
	  
        List<WebElement> rows = driver.findElements(By.tagName("td"));
        Assert.assertTrue(rows.get(0).getText() == book.getName());
        Assert.assertTrue(rows.get(1).getText() == book.getAuthor());
        Assert.assertTrue(rows.get(2).getText() == book.getPublishingYear().toString());

    }

   
}
