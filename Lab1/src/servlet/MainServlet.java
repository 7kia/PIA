package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

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
	private String errorMessage = new String();;
	
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
    				|| fieldIsEmpty(request, "publishingYear"))
    		) {
    			addBook(request);
    		}
        }
    	
    	printHtmlPage(response, getAddBookPage());
    }
    
    private void addBook(HttpServletRequest request)
    {
    	String name = request.getParameter("name");
		String author = request.getParameter("author");
		   
		if(!checkPublishingYear(request)) {
			return;
		}
		String yearString = request.getParameter("publishingYear");
		Integer publishingYear = Integer.parseUnsignedInt(yearString);
		   
		books.add(new Book(name, author, publishingYear));
    }
    
    private boolean checkPublishingYear(HttpServletRequest request)
    {
    	try
    	{
    		String yearString = request.getParameter("publishingYear");
    		Integer.parseUnsignedInt(yearString);
    	} catch(NumberFormatException e) {
    		errorMessage = "Field \"Publishing Year\" must be unsigned number";
    		return false;
    	}
    	return true;		
    }
    
    private boolean fieldIsEmpty(HttpServletRequest request, String fieldName)
    {
    	if(request.getParameter(fieldName).isEmpty()) {
    		errorMessage = getErrorMessage(fieldName);
    		return true;
    	}
    	return false;
    }
    
    static public String getErrorMessage(String fieldName)
    {
    	return "Field \"" + fieldName + "\" is empty!";
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(books.isEmpty()) {
            request.setAttribute("showAddBookPage", true);
            doPost(request, response);
            return;
        }
        
        printHtmlPage(response, getBooksListPage(books));
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
    		addFormHtmlPage += getErrorMessage();
    		errorMessage = new String();
    	}
    	addFormHtmlPage += getAddBookForm();               
    	addFormHtmlPage += getFooter();
        return addFormHtmlPage;
    }
       
    private String getErrorMessage()
    {
    	return "<h1>" + errorMessage + "</h1>\n";
    }
    
    private String getAddBookForm()
    {
    	return "<form action = \"MainServlet\" method = \"POST\">\n" +
    			getInputHtml("Book Name", "text", "name", "nameField") +
    			getInputHtml("Author", "text", "author", "authorField") +
    			getInputHtml("Publishing year", "number", "publishingYear", "publishingYearField") +
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
                "<th>Publishing year</th>\n" +
                "</tr>\n";
    	
    	for(Book book : books)
        {
    		htmlTable += "<tr>\n" +
                            "<td>" + book.name + "</td>\n" +
                            "<td>" + book.author + "</td>\n" +
                            "<td>" + book.publishingYear + "</td>\n" +
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
