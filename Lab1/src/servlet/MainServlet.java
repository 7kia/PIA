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
    
	static public String SHOW_BUTTON_CLASS_NAME = "showBtn";
	static public String BOOK_TABLE_TITLE = "Book table";
	static public String ADD_BOOK_BTN = "addBookBtn";
    public MainServlet() {
        books = new ArrayList<Book>();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {    

    	if(request.getAttribute("showAddBookPage") == null)
        {
		   String name = request.getParameter("name");
		   String author = request.getParameter("author");
		
		   String yearString = request.getParameter("publishingYear");
		   Integer publishingYear = yearString.isEmpty() 
				? 0 
				: Integer.parseUnsignedInt(yearString);
		   
		   books.add(new Book(name, author, publishingYear));
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

    private void printHtmlPage(HttpServletResponse response, String page) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print(page);
        out.flush();
    }
    
    private String getAddBookPage()
    {
    	String addFormHtmlPage = getHeader();
    	addFormHtmlPage += getAddBookForm();               
    	addFormHtmlPage += getFooter();
        return addFormHtmlPage;
    }
       
    private String getAddBookForm()
    {
    	return "<form action = \"MainServlet\" method = \"POST\">\n" +
    			getInputHtml("Book Name", "text", "name", "nameBtn") +
    			getInputHtml("Author", "text", "author", "authorBtn") +
    			getInputHtml("Publishing year", "number", "publishingYear", "publishingYearBtn") +
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
                            "<td>" + book.getName() + "</td>\n" +
                            "<td>" + book.getAuthor() + "</td>\n" +
                            "<td>" + book.getPublishingYear() + "</td>\n" +
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
