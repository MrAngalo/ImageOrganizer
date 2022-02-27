package me.silva.guilherme.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

public class Language {

	//this maintain the same order as the lines in the language files
	
	public enum PromptKey {
		//Font
		FontFamily, FontStyle, FontSize,
		//Main GUI
		SourceLabel, SourceToolTip, SourceButton,
		DestLabel, DestToolTip, DestButton,
		PatternTemplateLabel, PatternTemplateToolTip,
		PatternCustomLabel, PatternCustomToolTip, PatternCustomButton,
		OptionsLabel,
		TransferModeToolTip, TransferModeOption1, TransferModeOption2,
		ResolverModeToolTip, ResolverModeOption1, ResolverModeOption2,
		ExtensionsLabel, ExtensionsToolTip,
		StartButton, CancelButton,
		//Status Code
		StatusCode1, StatusCode2, StatusCode3,
		StatusCode4, StatusCode5, StatusCode6,
		StatusCode7, StatusCode8, StatusCode9,
		StatusCode10, StatusCode11, StatusCode12,
		StatusCode13,
		//Select Folder GUI
		ConfirmationPopup1, ConfirmationPopup2, ConfirmationPopup3
	}
	
	private String langName;
	private Properties properties;
//	private HashMap<PromptKey, String> promptMap;
	
	public Language(String langName, String path) {
		this.langName = langName;
//		this.promptMap = new HashMap<>();
		this.properties = new Properties();
		
		try {
			InputStream stream = getClass().getResourceAsStream(path);
//	        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
	        
			properties.load(new InputStreamReader(stream, Charset.forName("UTF-8")));
//	        String[] prompts = reader.lines().filter(line -> line.length() > 0 && line.charAt(0) != '#').toArray(String[]::new);
//	        PromptKey[] keys = PromptKey.values();
	        
	        if (!proptsContainKeys())
				throw new IllegalStateException("The language "+langName+" has unnecessary or missing keys!");
	        
//	        reader.close();
//			this.promptMap.put(keys[i], prompts[i]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getLangName() {
		return langName;
	}
	
	
	public String getPrompt(PromptKey key) {
		return properties.getProperty(key.toString());
//		return promptMap.get(key);
	}
	
	public boolean proptsContainKeys() {
		PromptKey[] keys = PromptKey.values();
		if (keys.length > properties.keySet().size())
			return false;
		
        for (PromptKey key : keys)
        	if (properties.getProperty(key.toString()) == null)
        		return false;

        return true;
	}
}
