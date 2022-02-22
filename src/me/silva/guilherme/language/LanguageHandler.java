package me.silva.guilherme.language;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LanguageHandler {
	
	private static final String PATH = "res/languages";
	
	private List<LanguageDependent> dependents;
	private HashMap<String, Language> languageMap;
	private Language current;
	
	public LanguageHandler(String def) {
		this.dependents = new ArrayList<>();
		this.languageMap = new HashMap<>();
		
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(PATH);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        
        for (String file : reader.lines().toArray(String[]::new)) {
        	String langName = file.substring(0, file.lastIndexOf('.'));
        	
        	Language lang = new Language(langName,
        			ClassLoader.getSystemClassLoader().getResource(PATH+"/"+file));
        	
        	languageMap.put(langName, lang);
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
