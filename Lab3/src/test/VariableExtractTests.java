package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import view.HtmlTemplater;

class VariableExtractTests {

	private HtmlTemplater tempalter = new HtmlTemplater();
	
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
		List<String> emptyList = new ArrayList<>();

        List<String> resultList = new ArrayList<>();
        resultList.add("name");
        testExtractVariables("$(name)", resultList);
        
        resultList.clear();
        resultList.add("book");
        resultList.add("name");
        testExtractVariables("$(book.name)", resultList);

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
