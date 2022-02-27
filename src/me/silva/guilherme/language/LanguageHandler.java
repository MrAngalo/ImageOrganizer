package me.silva.guilherme.language;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class LanguageHandler {
	
	private static final String PATH = "/res/languagesfutureformat";
	private static final String PATH_LANG_LIST = PATH+"/config.properties";
	private Properties langConfig;
	
	private List<LanguageDependent> dependents;
	private HashMap<String, Language> languageMap;
	private Language current;
	
	public LanguageHandler(String def) {
		this.dependents = new ArrayList<>();
		this.languageMap = new HashMap<>();
		this.langConfig = new Properties();
		
		try {
	        InputStream stream = getClass().getResourceAsStream(PATH_LANG_LIST);
//	        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
	       
	        langConfig = new Properties();
	        langConfig.load(new InputStreamReader(stream, Charset.forName("UTF-8")));
	        
	        String[] langNames = langConfig.getProperty("languages").split(",");
//	        reader.lines().toArray(String[]::new);
//	        reader.close();
	        
	        for (String langName : langNames)
//	        	String langName = langFile.substring(0, langFile.lastIndexOf('.'));
	        	languageMap.put(langName, new Language(langName, PATH+"/"+langName+".lang"));
	        
	        selectLanguage(def);
	        
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
