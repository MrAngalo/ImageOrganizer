package me.silva.guilherme.language;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Language {

	//this maintain the same order as the lines in the language files
	
	public enum ProptKey {
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
	private Properties propts;
	private Font font;
//	private HashMap<PromptKey, String> promptMap;
	
	public Language(String langName, String path) {
		this.langName = langName;
		this.propts = new Properties();
		
		try {
			InputStream stream = getClass().getResourceAsStream(path);
			propts.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
	        
	        if (!proptsContainKeys())
				throw new IllegalStateException("The language "+langName+" has unnecessary or missing keys!");
	        
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String family = getPropt(ProptKey.FontFamily);
		int style = Font.PLAIN;
		int size = Integer.valueOf(getPropt(ProptKey.FontSize));
		switch (getPropt(ProptKey.FontStyle)) {
		case "Bold":
			style = Font.BOLD;
			break;
		case "Italic":
			style = Font.ITALIC;
			break;
		case "Bold Italic":
			style = Font.BOLD | Font.ITALIC;
			break;
		}
		this.font = new Font(family, style, size);
	}
	
	public String getLangName() {
		return langName;
	}
	
	
	public String getPropt(ProptKey key) {
		return propts.getProperty(key.toString());
	}
	
	public Font getFont() {
		return font;
	}
	
	private boolean proptsContainKeys() {
		ProptKey[] keys = ProptKey.values();
		if (keys.length > propts.keySet().size())
			return false;
		
        for (ProptKey key : keys)
        	if (propts.getProperty(key.toString()) == null)
        		return false;

        return true;
	}
}
