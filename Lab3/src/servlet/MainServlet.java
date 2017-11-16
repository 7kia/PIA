package servlet;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import model.Book;
import model.BookDatabase;

@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	private String errorMessage = new String();
	private BookDatabase bookDatabase;
	private static final Logger log = Logger.getLogger(MainServlet.class);
	
	static public String SHOW_BUTTON_CLASS_NAME = "showBtn";
	static public String BOOK_TABLE_TITLE = "Book table";
	static public String ADD_BOOK_BTN = "addBookBtn";
	static public String CLEAR_BTN = "clearBtn";
	
    public MainServlet() throws Exception {
    	bookDatabase = new BookDatabase("dba", "goalie", "books");
    	

		log.info("MainServlet start");
		
    }
    
    
    public void init(ServletConfig config) throws ServletException {
		System.out.println("Log4JInitServlet is initializing log4j");
		String log4jLocation = config.getInitParameter("log4j-properties-location");

		ServletContext sc = config.getServletContext();

		if (log4jLocation == null) {
			System.err.println("*** No log4j-properties-location init param, so initializing log4j with BasicConfigurator");
			BasicConfigurator.configure();
		} else {
			String webAppPath = sc.getRealPath("/");
			String log4jProp = webAppPath + log4jLocation;
			File yoMamaYesThisSaysYoMama = new File(log4jProp);
			if (yoMamaYesThisSaysYoMama.exists()) {
				System.out.println("Initializing log4j with: " + log4jProp);
				PropertyConfigurator.configure(log4jProp);
			} else {
				System.err.println("*** " + log4jProp + " file not found, so initializing log4j with BasicConfigurator");
				BasicConfigurator.configure();
			}
		}
		super.init(config);
	}
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {    
    	if(request.getAttribute("showAddBookPage") == null) {
    		if(!(fieldIsEmpty(request, "name")
    				|| fieldIsEmpty(request, "author")
    				|| fieldIsEmpty(request, "pageAmount")
    				|| fieldIsEmpty(request, "publishingDate")
    				)
    		) {
    			addBook(request);
    		}
        }
    	
		request.setAttribute("errorMessage", errorMessage);

		
		
    	request.getRequestDispatcher("AddBooks.jsp").forward(request, response);
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(bookDatabase.isEmpty()) {
	        request.setAttribute("showAddBookPage", true);
	        doPost(request, response);
	        return;
	    }
	    
		List<Book> books;
		try {
			books = bookDatabase.getBooks();
			
			request.setAttribute("books", books);
			request.setAttribute("bookDatabase", bookDatabase);
			request.getRequestDispatcher("BookTable.jsp").forward(request, response);
	    	log.info("Open book table");
		} catch (SQLException exception) {
			String exceptionMessage = "SQLException, doGet bookDatabase.getBooks(): "
				+ exception.getMessage();
			log.error(exceptionMessage);
			System.out.println(exceptionMessage);
			exception.printStackTrace();
		}
		
	}

	private void addBook(HttpServletRequest request)
    {
    	String name = request.getParameter("name");
		String author = request.getParameter("author");
		   
		if(!checkPublishingYear(request)) {
			return;
		}
		String yearString = request.getParameter("pageAmount");
		Integer pageAmount = Integer.parseUnsignedInt(yearString);
		
		if(!checkPublishingData(request))
		{
			return;
		}
		
		String dateStr = request.getParameter("publishingDate");
    	DateFormat format = getDateFormat();
		try {
			java.sql.Date sqlDate = new java.sql.Date(format.parse(dateStr).getTime());
			bookDatabase.insertName(new Book(name, author, pageAmount, sqlDate), 1);
			errorMessage = new String();
			log.info("Add book");
		} catch (ParseException e) {
			String exceptionMessage = "Publishing date incorrect. MainServlet.addBook()";
			System.out.println(exceptionMessage);
			log.error(exceptionMessage);
		}
    }
    
	private void clearBookDatabase()
	{
		bookDatabase.clear();
	}
	
    public static SimpleDateFormat getDateFormat()
    {
    	return new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    }
    
    public static String getStringDatePresentation(Date date)
    {
    	return getDateFormat().format(date);
    }
    
    private boolean checkPublishingData(HttpServletRequest request) {
    	String dateStr = request.getParameter("publishingDate");
    	DateFormat format = getDateFormat();
    	
    	try {
			Date date = format.parse(dateStr);
		} catch (ParseException e) {
			errorMessage = getIncorrectDateMesage();
			log.error(errorMessage);
			return false;
		}
		return true;
	}

	private boolean checkPublishingYear(HttpServletRequest request)
    {
    	try
    	{
    		String yearString = request.getParameter("pageAmount");
    		Integer number = Integer.parseUnsignedInt(yearString);
    	} catch(NumberFormatException e) {
    		errorMessage = getOutOfRangeMessage();
    		log.error(errorMessage);
    		return false;
    	}
    	return true;		
    }
    
    static public String getOutOfRangeMessage() {
    	return "Field \"Page amount\" must be unsigned number in range[" 
				+ 0 + "; " + Integer.MAX_VALUE + "]";
    }
    
    static public String getIncorrectDateMesage()
    {
    	return "Field \"Publishing date\" must contain date";
    }
    
    private boolean fieldIsEmpty(HttpServletRequest request, String fieldName)
    {
    	if(request.getParameter(fieldName).isEmpty()) {
    		errorMessage = getEmptyMessage(fieldName);
    		log.error(errorMessage);
    		return true;
    	}
    	return false;
    }
    
    static public String getEmptyMessage(String fieldName)
    {
    	return "Field \"" + fieldName + "\" is empty!";
    }  
}
