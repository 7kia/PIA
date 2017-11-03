<!DOCTYPE html>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Book table</title>
	</head>
	<body>
		<input type="button" value="clear table" onclick="${bookDatabase.clear()}" />
		<table>
   			<tr>
                <th>Name</th>
                <th>Author</th>
                <th>Page amount</th>
                <th>Publishing date</th>
           </tr>
			<c:forEach items="${books}" var="book">
				<tr>
		           <td>${book.name}</td>
		           <td>${book.author} </td>
		           <td>${book.pageAmount}</td>
		           <td>${book.publishingData}</td>
		        </tr>
			</c:forEach>
		</table>
	</body>
</html>
