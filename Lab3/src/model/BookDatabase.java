package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.List;
import java.util.Properties;

import model.Book;
 
public class BookDatabase {
    public static final String DATABASE_URL = "jdbc:com.nuodb://localhost/";
    
    private final Connection dbConnection;
    static private String DB_NAME = "books";
    
    private ArrayList<String> columnNames;
    private ArrayList<String> columnTypes;
    public BookDatabase(
    		String user,
    		String password,
    		String dbName
    ) throws SQLException, ClassNotFoundException
    {
    	
    	
	    Properties properties = new Properties();
	    properties.put("user", user);
	    properties.put("password", password);
	    properties.put("schema", "hello");
	    dbConnection = DbConnection.getConnection(DATABASE_URL + dbName, properties);
	    
	    columnNames = new ArrayList<String>();
	    columnTypes = new ArrayList<String>();
    	columnNames.add("name");
    	columnTypes.add("string");
    	
    	columnNames.add("author");
    	columnTypes.add("string");
    	
    	columnNames.add("pageAmount");
    	columnTypes.add("int");
    	
    	columnNames.add("publishingData");
    	columnTypes.add("DATE");
    }
    
    public void close() throws SQLException {
    	dbConnection.close();
    }
    
    public void createTable() {
	    try {
	    	Statement stmt = dbConnection.createStatement();
	        stmt.execute("CREATE TABLE " + DB_NAME 
	        		+ " (id int primary key, "+  getCreateColumnList() + ")"
	        		);

	        dbConnection.commit();
	    } catch(SQLException exception) {
	        System.out.println("SQLException, skipping table creation: " + exception.getMessage());
	    } catch(Exception exception) {
	        System.out.println("Untracked exception. Skipping table creation: " + exception.getMessage());
	    }
    }
    
    private String getCreateColumnList()
    {
    	String result = new String();
    	
    	for(int i = 0; i < columnNames.size(); i++)
    	{
    		result += columnNames.get(i) + " " + columnTypes.get(i);
    		if((i + 1) < columnNames.size()) {
    			result += ", ";
			}
    	}
    	
    	return result;
    }
    
    public void addBooks() {
    	List<Book> bookList = new ArrayList<>();
    	bookList.add(new Book("n", "a", 1, new Date(2012, 12, 12)));
    	
        try {
        	String e = "insert into " + DB_NAME + 
    			" (" + generateNameColumnList() + ")" +
        		generateValuesPartStatement();
        	
        	PreparedStatement stmt = dbConnection.prepareStatement(
    			"insert into " + DB_NAME +
    			" (" + generateNameColumnList() + ")" +
                generateValuesPartStatement()
        	);
        	
            for (int i = 0; i < bookList.size(); i++) {
            	stmt.setInt(1, i + 1);
                stmt.setString(2, bookList.get(i).name);
                stmt.setString(3, bookList.get(i).author);
                stmt.setInt(4, bookList.get(i).pageAmount);
                stmt.setDate(5, bookList.get(i).publishingData);
                stmt.addBatch();             
            }
            stmt.executeBatch();
            
            dbConnection.commit();
        } catch(SQLException exception) {
	        System.out.println("SQLException, Skipping addBooks: " + exception.getMessage());
	    } catch(Exception exception) {
            System.out.println("Untracked exception. Skipping addBooks..." + exception.getMessage());
        }
    }
    
    private String generateNameColumnList()
    {
    	String listValues = new String();
    	for (int i = 0; i < columnNames.size(); i++) {
    		listValues += columnNames.get(i);
    		if((i + 1) < columnNames.size()) {
				listValues += ", ";
			}
    	}
    	
    	return "id, " + listValues;
    }
    
    private String generateValuesPartStatement()
    {
    	String listValues = new String();
		for (int i = 0; i <= columnNames.size(); i++) {
			listValues += "?";
			if((i + 1) <= columnNames.size()) {
				listValues += ", ";
			}
		}
    	return " values (" + listValues + ")";
    }
    
    public void insertName(Book book, int id) {
	    try {
	    	PreparedStatement stmt = dbConnection.prepareStatement(
        		"insert into " + DB_NAME + 
        		" (" + generateNameColumnList() + ")" +
        		generateValuesPartStatement()
        	);
	    	
	    	stmt.setInt(1, id);
            stmt.setString(2, book.name);
            stmt.setString(3, book.author);
            stmt.setInt(4, book.pageAmount);
            stmt.setDate(5, book.publishingData);
             
	        stmt.addBatch();
	        stmt.executeBatch();
	        dbConnection.commit();
	    
	    } catch(SQLException exception) {
	        System.out.println("SQLException, Skipping insert: " + exception.getMessage());
	    } catch(Exception exception) {
	        System.out.println("Untracked exception. Skipping insert..." + exception.getMessage());
	    }
    }
    
    public String getName(int id) throws SQLException {
    	PreparedStatement pst = dbConnection.prepareStatement(
    		getSelectStatement("name", DB_NAME, "id=?")
    	);

	    pst.setInt(1, id);
	        
	    ResultSet rs = pst.executeQuery();
        if (rs.next()) {
        	return rs.getString(1);
        } 
        return null;
    }
    
    public boolean isEmpty()
    {
    	try {
    		PreparedStatement stmt = dbConnection.prepareStatement(
	    		getSelectStatement("count(*)", DB_NAME, "id=?")
	    	);
    		
    		ResultSet rs = stmt.executeQuery();
    		rs.next();
            return rs.getInt(1) == 0;  
    	} catch(SQLException exception) {
    		return true;
 	    } catch(Exception exception) {
 	        System.out.println("Untracked exception. Skipping insert..." + exception.getMessage());
 	    }
    	
    	return false;
    }
    
    public void clear()
    {
    	try {
			PreparedStatement stmt = dbConnection.prepareStatement(
					getDeleteStatement(DB_NAME)
				);
			
			stmt.addBatch();
	        stmt.executeBatch();
	        dbConnection.commit();
		} catch (SQLException exception) {
			System.out.println("SQLException, Skipping clear: " + exception.getMessage());
		}
    }
    
    private String getDeleteStatement(String tableName)
    {
    	return "DELETE FROM " + DB_NAME + " where id > 0 ";
    }
    
    public String getPublishingData(int id) throws SQLException {
        PreparedStatement pst = dbConnection.prepareStatement(
    		getSelectStatement("publishingData", DB_NAME, "id=?")
    	);

        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
        	return rs.getString(1);
        }    
        return null;
    }
    
    public Book getBook(int id) throws SQLException {
    	PreparedStatement pst = dbConnection.prepareStatement(
    		getSelectStatement("*", DB_NAME, "id=" + id)
    	);
    	
    	Book book = new Book();
    	
    	ResultSet rs = pst.executeQuery();
    	rs.next();
    	book.name = rs.getString(2);
    	book.author = rs.getString(3);
    	book.pageAmount = rs.getInt(4);
    	book.publishingData = rs.getDate(5);
    	
    	return book;
    }
    
    public List<Book> getBooks() throws SQLException {
    	PreparedStatement pst = dbConnection.prepareStatement(
			"SELECT MAX(id) FROM " + DB_NAME
    	);
    	
    	List<Book> books = new ArrayList<Book>();
    	
    	ResultSet rs = pst.executeQuery();
    	rs.next();
    	int lastId = rs.getInt(1);
    	
    	for(int i = 1; i <= lastId; i++) {
    		books.add(getBook(i));
    	}
    	return books;
    }
    
    
    private String getSelectStatement(
    		String columns,
    		String tableName,
    		String condition
    ) {
    	return "select " + columns + " from " + tableName + " where " + condition;
    }
    
    
}