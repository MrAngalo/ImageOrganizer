package me.silva.guilherme.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Language {

	//this maintain the same order as the lines in the language files
	public enum PromptKey {
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
	private HashMap<PromptKey, String> promptMap;
	
	public Language(String langName, String path) {
		this.langName = langName;
		this.promptMap = new HashMap<>();
		
		try {
			InputStream stream = getClass().getResourceAsStream(path);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
	        
	        String[] prompts = reader.lines().filter(line -> line.length() > 0 && line.charAt(0) != '#').toArray(String[]::new);
	        PromptKey[] keys = PromptKey.values();
	        
	        reader.close();
			
			if (prompts.length != keys.length)
				throw new IllegalStateException("The language "+langName+" is missing prompt lines!");
			
			for (int i = 0; i < keys.length; i++)
				this.promptMap.put(keys[i], prompts[i]);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getLangName() {
		return langName;
	}
	
	public String getPrompt(PromptKey key) {
		return promptMap.get(key);
	}
}
