package view;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import model.TemplateVariable;

public class HtmlTemplater {
	
	private static String VARIABLE_TEMPLATE = "[\\w]+";
	private static String VARIABLE_AND_FIELD = VARIABLE_TEMPLATE +
			"(." + VARIABLE_TEMPLATE + ")*";
	
	private static Pattern variablePattern = Pattern.compile(VARIABLE_TEMPLATE);
	
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
	
	private Map<String, TemplateVariable> variables = new HashMap<String, TemplateVariable>();
	
	
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
		List<String> variables = extractTemplateVariables(string);
		
		
		
		return "";
	}
	
	public void setAttribute(String name, String type, Object object)
	{
		variables.put(name, new TemplateVariable(type, object));
	}
	
}
