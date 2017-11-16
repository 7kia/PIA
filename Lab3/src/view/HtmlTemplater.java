package view;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import model.Book;
import model.TemplateVariable;

public class HtmlTemplater {
	
	private static String VARIABLE_TEMPLATE = "[\\w]+";
	private static String VARIABLE_AND_FIELD = VARIABLE_TEMPLATE +
			"(." + VARIABLE_TEMPLATE + ")*";
	
	private static Pattern variablePattern = Pattern.compile(VARIABLE_TEMPLATE);
	
	private Map<String, TemplateVariable> variables = new HashMap<String, TemplateVariable>();

	static public void render(String fileName, HttpServletRequest request)
	{
		BufferedReader br = null;
		FileReader fr = null;

		try {

			//br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				
				System.out.println(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
					
				if (fr != null) {
					fr.close();
				}				

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	
	// The method is public because need to test it
	static public List<String> extractTemplateVariables(String string)
	{
		List<String> variables = new ArrayList<>();
		
		Pattern p = Pattern.compile("\\$\\(" + VARIABLE_AND_FIELD +"\\)");  
        Matcher matcher = p.matcher(string); 
		
        while(matcher.find()) {
            variables.add(string.substring(matcher.start(), matcher.end()));
        }
        
        return variables;
	}
	
	static public List<List<String>> extractVariables(List<String> templates)
	{
		List<List<String>> variables = new ArrayList<>();
				
		for(int i = 0; i < templates.size(); i++)
		{
			List<String> callPart = extractVariablePart(templates.get(i));
			
			variables.add(callPart);
		}
       
        return variables;
	}
	
	
	static public List<String> extractVariablePart(String string)
	{
		Matcher matcher = variablePattern.matcher(string);
		
		List<String> callPart = new ArrayList<>();
		while(matcher.find()) {
			callPart.add(
				string.substring(matcher.start(), matcher.end())
            );
        }
		
		return callPart;
	}
	
	// TODO : do private
	public String convertToTemplateString(String string)
	{
		List<String> templates = extractTemplateVariables(string);
		List<List<String>> variableParts = extractVariables(templates);
		
		List<String> values = new ArrayList();
		for(int i = 0; i < variableParts.size(); i++) {
			String value = getValue(variableParts.get(i));
			values.add(value);
		}
		
		String convertString = string;
		for(int i = 0; i < templates.size(); i++) {
			convertString = convertString.replace(templates.get(i), values.get(i));
		}

		return convertString;
	}
	
	// The method is public because need to test it
	public String getValue(List<String> variableParts)
	{
		TemplateVariable variable = variables.get(variableParts.get(0));
		if(variable != null) {
			return extractValue(variable, variableParts);
		}
		return "";
	}
	
	private String extractValue(TemplateVariable variable, List<String> variableParts)
	{
		switch (variable.type) {
		case "String":
			return variable.object.toString();
		case "Integer":
			return ((Integer) variable.object).toString();
		case "Date":
			return ((Date) variable.object).toString();
		case "Book":
			return extractValueToBook(variable, variableParts);
		default:
			break;
		}
		return "";
	}
	
	private String extractValueToBook(TemplateVariable variable, List<String> variableParts)
	{
		if(variableParts.size() > 1) {
			switch (variableParts.get(1)) {
			case "name":
				return ((Book) variable.object).name;
			case "author":
				return ((Book) variable.object).author;
			case "pageAmount":
				return ((Book) variable.object).pageAmount.toString();
			case "publishingData":
				return ((Book) variable.object).publishingData.toString();
			default:
				break;
			}
		}
		
		return "";
	}
	
	public void setAttribute(String name, String type, Object object)
	{
		variables.put(name, new TemplateVariable(type, object));
	}
	
}
