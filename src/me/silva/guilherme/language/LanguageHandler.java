package me.silva.guilherme.language;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class LanguageHandler {
	
	private static final String PATH = "/res/languages";
	private static final String PATH_LANG_LIST = PATH+"/config.properties";
	private Properties langConfig;
	
	private List<LanguageDependent> dependents;
	private HashMap<String, Language> languageMap;
	
	private Language current;
	private Language defL;
	
	public LanguageHandler(String def) {
		this.dependents = new ArrayList<>();
		this.languageMap = new HashMap<>();
		this.langConfig = new Properties();
		
		try {
	        InputStream stream = getClass().getResourceAsStream(PATH_LANG_LIST);
	        langConfig.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
	        
	        String[] langNames = langConfig.getProperty("languages").trim().split(",");
	        System.out.println(Arrays.toString(langNames));
	        for (String langName : langNames)
	        	languageMap.put(langName, new Language(langName, PATH+"/"+langName+".lang"));
	        
	        selectLanguage(def);
	        this.defL = current;
	        
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
	
	public Language getDefault() {
		return defL;
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
