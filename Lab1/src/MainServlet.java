import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

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
            "Book Name: <input type = \"text\" name = \"name\">\n" +
            "<br />\n" +
            "Author: <input type = \"text\" name = \"author\" />\n" +
            "<br />\n" +
            "Publishing year: <input type = \"number\" name = \"publishingYear\" />\n" +
            "<br />\n" +
            "<input type = \"submit\" value = \"Add book\" />\n" +
            "</form>\n" +  
            getHtmlButton("GET", "Show my books");
    }
    
    private String getBooksListPage(List<Book> Books) {
    	String bookListHtmlPage = getHeader();
    	bookListHtmlPage +=getBookTable(Books);               
        bookListHtmlPage += getFooter();
        return bookListHtmlPage;
    }
    
    private String getHtmlButton(String method, String title)
    {
    	return "<form action = \"MainServlet\" method=\"" + method +"\">\n" +
    			"<input type = \"submit\" value = \"" + title + "\" />\n" +
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
