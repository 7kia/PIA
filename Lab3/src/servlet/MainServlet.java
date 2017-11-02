package servlet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Book;

@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private List<Book> books;   
	private String errorMessage = new String();
	
	static public String SHOW_BUTTON_CLASS_NAME = "showBtn";
	static public String BOOK_TABLE_TITLE = "Book table";
	static public String ADD_BOOK_BTN = "addBookBtn";
    public MainServlet() {
        books = new ArrayList<Book>();
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
		if(books.isEmpty()) {
	        request.setAttribute("showAddBookPage", true);
	        doPost(request, response);
	        return;
	    }
	    
		request.setAttribute("books", books);
		request.getRequestDispatcher("BookTable.jsp").forward(request, response);
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
			books.add(new Book(name, author, pageAmount, sqlDate));
			errorMessage = new String();
		} catch (ParseException e) {
			System.out.println("Publishing date incorrect. MainServlet.addBook()");
		}
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
