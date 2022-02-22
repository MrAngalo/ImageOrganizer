package me.silva.guilherme.language;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class Language {

	//this maintain the same order as the lines in the language files
	public enum PromptKey {
		//Main GUI
		M_SourceLabel, M_SourceToolTip, M_SourceButton,
		M_DestLabel, M_DestToolTip, M_DestButton,
		M_PatternTemplateLabel, M_PatternTemplateToolTip,
		M_PatternCustomLabel, M_PatternCustomToolTip, M_PatternCustomButton,
		M_OptionsLabel,
		M_TransferModeToolTip, M_TransferModeOption1, M_TransferModeOption2,
		M_ResolverModeToolTip, M_ResolverModeOption1, M_ResolverModeOption2,
		M_ExtensionsLabel, M_ExtensionsToolTip,
		M_StartButton, M_CancelButton,
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
	
	public Language(String langName, URL resource) {

		try {
			File file = new File(resource.toURI()); // creates a new file instance
			FileReader fr = new FileReader(file); // reads the file
			BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
			
			String[] prompts = br.lines().filter(line -> line.length() > 0 && line.charAt(0) != '#').toArray(String[]::new);
			PromptKey[] keys = PromptKey.values();
			
			fr.close(); // closes the stream and release the resources
			
			if (prompts.length != keys.length)
				throw new IllegalStateException("The language "+langName+" is missing prompt lines!");
			
			this.langName = langName;
			this.promptMap = new HashMap<>();
			for (int i = 0; i < keys.length; i++)
				this.promptMap.put(keys[i], prompts[i]);
			
		} catch (IOException | URISyntaxException e) {
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
