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
 
public class HelloDB {
    public static final String DRIVER_CLASS = "com.nuodb.jdbc.Driver";
    public static final String DATABASE_URL = "jdbc:com.nuodb://localhost/";
    
    private final Connection dbConnection;
    static private String DB_NAME = "books";
    
    private ArrayList<String> columnNames;
    private ArrayList<String> columnTypes;
    public HelloDB(String user, String password, String dbName)
    	throws SQLException
    {
	    Properties properties = new Properties();
	    properties.put("user", user);
	    properties.put("password", password);
	    properties.put("schema", "hello");
	    dbConnection = DriverManager.getConnection(DATABASE_URL + dbName, properties);
	    
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
    
    public void createTable() throws SQLException {
	    try (Statement stmt = dbConnection.createStatement()) {
	        stmt.execute("CREATE TABLE " + DB_NAME 
	        		+ " (id int primary key, "+  getCreateColumnList() + ")"
	        		);

	        dbConnection.commit();
	    } catch(Exception exception) {
	        System.out.println("Skipping table creation: " + exception.getMessage());
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
    
    public void addBooks() throws SQLException {
    	List<Book> bookList = new ArrayList<>();
    	bookList.add(new Book("n", "a", 1, new Date(2012, 12, 12)));
    	
        try {
        	
        	String e = "insert into " + DB_NAME + " (" + generateNameColumnList() + ")" +
            		generateValuesPartStatement();
        	
        	PreparedStatement stmt = dbConnection.
                    prepareStatement("insert into " + DB_NAME + " (" + generateNameColumnList() + ")" +
                    		generateValuesPartStatement());
        	
            for (int i = 0; i < bookList.size(); i++) {
            	stmt.setInt(1, i + 1);
                stmt.setString(2, bookList.get(i).name);
                stmt.setString(3, bookList.get(i).author);
                stmt.setInt(4, bookList.get(i).pageAmount);
                stmt.setDate(5, bookList.get(i).publishingData);
                stmt.addBatch();             
            }
            stmt.executeBatch();
        } catch(Exception exception) {
            System.out.println("Skipping populateDemo..." + exception.getMessage());
        }
        dbConnection.commit();
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
    
    /**
     * Inserts a new name into the table with a 0 balance. The id must be unique.
     *
     * @param id a unique numeric identifier
     * @param name a name associated with the given id
     */
    public void insertName(Book book, int id) throws SQLException {
    	
    	
    	
	    try (PreparedStatement stmt = dbConnection.
	    		prepareStatement(
	        		"insert into " + DB_NAME + 
	        		" (" + generateNameColumnList() + ")" +
	        		generateValuesPartStatement()
	        	)
	        ) {
	    	stmt.setInt(1, id);
            stmt.setString(2, book.name);
            stmt.setString(3, book.author);
            stmt.setInt(4, book.pageAmount);
            stmt.setDate(5, book.publishingData);
             
	        stmt.addBatch();
	        stmt.executeBatch();
	        dbConnection.commit();
	    } catch(Exception exception) {
	        System.out.println("Skipping insert..." + exception.getMessage());
	    }
    }
    
    /**
     * Gets the name for the given id, or null if no name exists.
     *
     * @param id an identifier
     * @return the name associated with the identifier, or null
     */
    public String getName(int id) throws SQLException {
	    try (PreparedStatement pst = dbConnection.
	        prepareStatement(getSelectStatement("name", DB_NAME, "id=?"))) {
	        pst.setInt(1, id);
	        try (ResultSet rs = pst.executeQuery()){
	        if (rs.next())
	            return rs.getString(1);
	        return null;
	        }  
	    }
    }
     
    /**
     * Gets the balance for the given id, or null if no name exists.
     *
     * @param id an identifier
     * @return the balance associated with the identifier, or null
     */
    public String getPublishingData(int id) throws SQLException {
        try (PreparedStatement pst = dbConnection.
            prepareStatement(getSelectStatement("publishingData", DB_NAME, "id=?"))
            ) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()){
                if (rs.next())
                    return rs.getString(1);
                return null;
            }
        }
    }
    
    private String getSelectStatement(String columns, String tableName, String condition)
    {
    	return "select " + columns + " from " + tableName + " where " + condition;
    }
    
    /** Main-line for this example. */
    public static void main(String [] args) throws Exception {
    	Class.forName(DRIVER_CLASS);
      
        HelloDB helloDB = new HelloDB("dba", "goalie", "books");
        helloDB.createTable();
        helloDB.addBooks();
        String name, balance;
  
        for(int i=1; i<=1; i++) {
            name = helloDB.getName(i);
            balance = helloDB.getPublishingData(i);
            if (name != null || balance != null) {
	            System.out.println(name + ":\n Account ID = " + i +
	                "\n Balance = $" + helloDB.getPublishingData(i) + "\n");
            } else {
            	System.out.println("Account ID "+i+": NOT FOUND");
            }
        }
        helloDB.close();
    }
}