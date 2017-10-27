package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.javafx.collections.MappingChange.Map;

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
    	
    	printHtmlPage(response, getAddBookPage());
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(books.isEmpty()) {
	        request.setAttribute("showAddBookPage", true);
	        doPost(request, response);
	        return;
	    }
	    
	    printHtmlPage(response, getBooksListPage(books));
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
			books.add(new Book(name, author, pageAmount, format.parse(dateStr)));
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
    
    private void printHtmlPage(HttpServletResponse response, String page) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print(page);
        out.flush();
    }
    
    private String getAddBookPage()
    {
    	String addFormHtmlPage = getHeader();
    	if(!errorMessage.isEmpty())
    	{
    		addFormHtmlPage += getErrorMessageHtml();
    	}
    	addFormHtmlPage += getAddBookForm();               
    	addFormHtmlPage += getFooter();
        return addFormHtmlPage;
    }
       
    private String getErrorMessageHtml()
    {
    	return "<h1>" + errorMessage + "</h1>\n";	
    }
    
    private String getAddBookForm()
    {
    	return "<form action = \"MainServlet\" method = \"POST\">\n" +
    			getInputHtml("Book Name", "text", "name", "nameField") +
    			getInputHtml("Author", "text", "author", "authorField") +
    			getInputHtml("Page amount", "number", "pageAmount", "pageAmountField") +
    			getInputHtml("Publishing date", "date", "publishingDate", "publishingDateField") +
            "<input type = \"submit\" value = \"Add book\" class =\"" + ADD_BOOK_BTN + "\"/>\n" +
            "</form>\n" +  
            getHtmlButton("GET", "Show my books", SHOW_BUTTON_CLASS_NAME);
    }
    
    private String getInputHtml(String title, String type, String name, String className)
    {
    	return title + ": <input type = \"" + type + "\" name = \"" + name 
    			+ "\" class =\"" + className + "\"/>\n" +
                "<br />\n" ;
    }
    
    private String getBooksListPage(List<Book> Books) {
    	String bookListHtmlPage = getHeader();
    	bookListHtmlPage += "<h1>" + BOOK_TABLE_TITLE + "</h1>";
    	bookListHtmlPage +=getBookTable(Books);               
        bookListHtmlPage += getFooter();
        return bookListHtmlPage;
    }
    
    private String getHtmlButton(String method, String title, String className)
    {
    	return "<form action = \"MainServlet\" method=\"" + method +"\">\n" +
    			"<input type = \"submit\" class=\"" + className + 
    			"\" value = \"" + title + "\" />\n" +
    			"</form>\n";
    }
    
    private String getBookTable(List<Book> Books)
    {
    	String htmlTable = "<table>\n";
    	htmlTable += "<tr>\n" +
                "<th>Name</th>\n" +
                "<th>Author</th>\n" +
                "<th>Page amount</th>\n" +
                "<th>Publishing date</th>\n" +
                "</tr>\n";
    	
    	for(Book book : books)
        {
    		htmlTable += "<tr>\n" +
                            "<td>" + book.name + "</td>\n" +
                            "<td>" + book.author + "</td>\n" +
                            "<td>" + book.pageAmount + "</td>\n" +
                            "<td>" + book.publishingData + "</td>\n" +
                            "</tr>\n";
        }
    	
    	htmlTable += "</table>\n";
    	return htmlTable;
    }

    public String getHeader() {
        return "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<body>\n";
    }

    private String getFooter() {
        return "</body>\n" +
        		"</html>";
    }
}
