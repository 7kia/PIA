<!DOCTYPE html>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Books</title>
	</head>
	<body>
		<h1>${errorMessage}</h1>
		<form action = "MainServlet" method = "POST">
		    Book Name: <input type = "text" name = "name"
		    	class="nameField">
		    <br/>
		    Author: <input type = "text" name = "author"
		    	class="authorField">
		    <br/>
		    Page amount: <input type = "number" name = "pageAmount"
		    	class="pageAmountField">
		    <br/>
		    Publishing date: <input type = "date" name = "publishingDate"
		    	class="publishingDateField">
		    <br/>
		    
		    <input type = "submit" value = "Add book" class="addBookBtn" />
		  </form>
		  <form action = "MainServlet" method="GET">
		    <input type = "submit" value = "Show my books" 
		    class="showBtn"/>
		  </form>
	</body>
</html>
