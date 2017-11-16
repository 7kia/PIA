package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriverService;

import model.Book;
import view.HtmlTemplater;

class VariableExtractTests {

	private HtmlTemplater templater = new HtmlTemplater();
	
	@Test
	void checkExtractTemplateVariables() {
		
		List<String> emptyList = new ArrayList<>();
		testExtractTemplateVariables("a", emptyList);
        testExtractTemplateVariables("aaa", emptyList);
        testExtractTemplateVariables("12", emptyList);
        testExtractTemplateVariables("-12", emptyList);
        testExtractTemplateVariables("-0", emptyList);
        testExtractTemplateVariables("0", emptyList);
        
        testExtractTemplateVariables("name", emptyList);
        testExtractTemplateVariables("book.name", emptyList);
        
        testExtractTemplateVariables("$name", emptyList);
        testExtractTemplateVariables("$book.name", emptyList);
        
        testExtractTemplateVariables("$", emptyList);
        testExtractTemplateVariables("$(", emptyList);
        testExtractTemplateVariables("$()", emptyList);
        
        
        List<String> resultList = new ArrayList<>();
        resultList.add("$(name)");
        testExtractTemplateVariables("$(name)", resultList);
        
        resultList.clear();
        resultList.add("$(book.name)");
        testExtractTemplateVariables("$(book.name)", resultList);
        
        resultList.clear();
        resultList.add("$(book.name)");
        resultList.add("$(var)");
        testExtractTemplateVariables("<t>$(book.name)</t>\n$(var)", resultList);
	}
	
	@Test
	void checkExtractVariables() {
        List<String> resultList = new ArrayList<>();
        resultList.add("name");
        testExtractVariables("$(name)", resultList);
        
        resultList.clear();
        resultList.add("book");
        resultList.add("name");
        testExtractVariables("$(book.name)", resultList);

	}
	
	@Test
	void checkGetValueForSimpleType() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2012, 12, 12);
		Date date = new Date(calendar.getTimeInMillis());
		
		templater.setAttribute("strVar", "String", "theString");
		templater.setAttribute("intVar", "Integer", new Integer(4));
		templater.setAttribute("dateVar", "Date", date);
		
		testGetValueForSimpleTemplate("strVar", "theString");
		testGetValueForSimpleTemplate("intVar", "4");
		//testGetValueForSimpleTemplate("dateVar", "2012-12-12");// TODO : might incorrect convert to string
	}
	
	@Test
	void checkGetValueForBook() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2012, 12, 12);
		Date date = new Date(calendar.getTimeInMillis());
		
		Book testBook = new Book("n", "a", 9, date);
		templater.setAttribute("bookVar", "Book", testBook);
		
		List<String> resultList = new ArrayList<>();
		
		resultList.add("bookVar");
		testGetValue(resultList, "");
		
		resultList.add("name");
		testGetValue(resultList, testBook.name);
		
		resultList.remove(1);
		resultList.add("author");
		testGetValue(resultList, testBook.author);
		
		resultList.remove(1);
		resultList.add("pageAmount");
		testGetValue(resultList, testBook.pageAmount.toString());
		
		resultList.remove(1);
		resultList.add("publishingData");
		testGetValue(resultList, testBook.publishingData.toString());
	}
	
	@Test
	void checkConvertString() {
		templater.setAttribute("strVar", "String", "theString");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2012, 12, 12);
		Date date = new Date(calendar.getTimeInMillis());
		
		Book testBook = new Book("n", "a", 9, date);
		templater.setAttribute("book", "Book", testBook);
		
		testConvertString(
			"<t>$(book.name)</t>\n$(strVar)",
			"<t>n</t>\ntheString"
		);
		
		testConvertString(
				"<tr>"
		           + "<td>$(book.name)</td>"
		           + "<td>$(book.author) </td>"
		           + "<td>$(book.pageAmount)</td>"
		           + "<td>$(book.publishingData)</td>"
		           + "</tr>",
		        "<tr>"
		           + "<td>n</td>"
		           + "<td>a </td>"
		           + "<td>9</td>"
		           + "<td>2013-01-12</td>"
		           + "</tr>"
			);
	}
	
	private void testConvertString(String string, String convertString)
	{
		String result = templater.convertToTemplateString(string);
		
		Assert.assertEquals(result, convertString);
	}
	
	private void testGetValue(List<String> variableParts, String rightResult) {
		String result = templater.getValue(variableParts);
		
		Assert.assertEquals(result, rightResult);
	}

	private void testGetValueForSimpleTemplate(String name, String value)
	{
		List<String> resultList = new ArrayList<>();
		
		resultList.add(name);
		testGetValue(resultList, value);
	}
	
	public void testExtractVariables(String string, List<String> rightParts)
	{
		List<String> result = HtmlTemplater.extractVariablePart(string);
		
		Assert.assertEquals(result, rightParts);
	}
	
	public void testExtractTemplateVariables(String string, List<String> rightResult)
	{
		List<String> result = HtmlTemplater.extractTemplateVariables(string);
		
		Assert.assertEquals(result, rightResult);
	}

}
