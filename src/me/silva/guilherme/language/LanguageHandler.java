package me.silva.guilherme.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LanguageHandler {
	
	private static final String PATH = "/res/languages";
	private static final String PATH_LANG_LIST = PATH+"/_list.txt";
	
	private List<LanguageDependent> dependents;
	private HashMap<String, Language> languageMap;
	private Language current;
	
	public LanguageHandler(String def) {
		this.dependents = new ArrayList<>();
		this.languageMap = new HashMap<>();
		
		try {
	        InputStream stream = getClass().getResourceAsStream(PATH_LANG_LIST);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
	       
	        String[] langFiles = reader.lines().toArray(String[]::new);
	        
	        reader.close();
	        
	        for (String langFile : langFiles) {
	        	String langName = langFile.substring(0, langFile.lastIndexOf('.'));
	        	Language langObj = new Language(langName, PATH+"/"+langFile);
	        	languageMap.put(langName, langObj);
	        }
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Set<String> getLanguageNames() {
		return languageMap.keySet();
	}
	
	public Language getCurrent() {
		return current;
	}
	
	public void addDependent(LanguageDependent dependent) {
		dependents.add(dependent);
	}
	
	public void selectLanguage(String name) {
		this.current = languageMap.get(name);
		for (LanguageDependent dependent : dependents)
			dependent.onLanguageUpdate(current);
	}
	
	public interface LanguageDependent {
		public void onLanguageUpdate(Language current);
	}
}
