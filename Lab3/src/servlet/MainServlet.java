package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import model.Book;
import model.BookDatabase;

@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	private String errorMessage = new String();
	private BookDatabase bookDatabase;

	
	static public String SHOW_BUTTON_CLASS_NAME = "showBtn";
	static public String BOOK_TABLE_TITLE = "Book table";
	static public String ADD_BOOK_BTN = "addBookBtn";
	static public String CLEAR_BTN = "clearBtn";
    public MainServlet() throws Exception {
    	bookDatabase = new BookDatabase("dba", "goalie", "books");
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
		} catch (SQLException exception) {
			System.out.println("SQLException, doGet bookDatabase.getBooks(): "
				+ exception.getMessage()
			);
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
		} catch (ParseException e) {
			System.out.println("Publishing date incorrect. MainServlet.addBook()");
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
    		return true;
    	}
    	return false;
    }
    
    static public String getEmptyMessage(String fieldName)
    {
    	return "Field \"" + fieldName + "\" is empty!";
    }  
}
