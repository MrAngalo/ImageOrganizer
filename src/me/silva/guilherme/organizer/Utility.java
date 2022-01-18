package me.silva.guilherme.organizer;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public final class Utility {
	
	private static JFileChooser folderChooser;
	
	static {
		folderChooser = new JFileChooser();
		folderChooser.setCurrentDirectory(new File("."));
		folderChooser.setDialogTitle("choosertitle");
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		folderChooser.setAcceptAllFileFilterUsed(true);
	}
	
	public static boolean openWebpage(String url) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(new URL(url).toURI());
	            return true;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return false;
	}
	
	public static boolean requestConfirmation(String prompt) {
		return JOptionPane.showConfirmDialog(null, prompt, "Confirm Action",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
	}
	
	public static File chooseDirectory() {
		return (folderChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) ?
				folderChooser.getSelectedFile() : null;
	}
	
	// source: https://www.geeksforgeeks.org/simplify-directory-path-unix-like/
	// This code is contributed by decode2207.
	// function to simplify a Unix - styled absolute path
	public static String simplifyPath(String path) {
		path = path.replace("/", "\\");
		
		// using vector in place of stack
		Vector<String> v = new Vector<String>();
		int n = path.length();
		String ans = "";
		for (int i = 0; i < n; i++) {
			String dir = "";

			// forming the current directory.
			while (i < n && path.charAt(i) != '\\') {
				dir += path.charAt(i);
				i++;
			}

			// if ".." , we pop.
			if (dir.equals("..")) {
				if (v.size() != 0) {
					v.remove(v.size() - 1);
				}
			} else if (dir.equals(".") || dir.equals("")) {
				// do nothing (added for better understanding.)
			} else {
				// push the current directory into the vector.
				v.add(dir);
			}
		}
		
		// forming the ans
		for (String i : v) {
			ans += "\\" + i;
		}
		
		// vector is empty
		if (ans == "")
			return "\\";
		return ans;
	}

	public static int countFiles(File dir, String[] exts) {
		int counter = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			
			if (file.isDirectory()) {
				counter += countFiles(file, exts);
			} else if (hasFileExtension(file.getAbsolutePath(), exts)) {
				counter++;
			}
			
		}
		return counter;
	}
	
	public static boolean hasFileExtension(String file, String... exts) {
		int lastIndex = file.lastIndexOf('.');
		if (lastIndex > 0) {
			String fileExt = file.substring(lastIndex+1);
			for (String ext : exts) {
				if (ext.equalsIgnoreCase(fileExt)) {
					return true;
				}		
			}
		}
		return false;
	}
	
	public static String getFileExtension(String file) {
		int lastIndex = file.lastIndexOf('.');
		return (lastIndex >= 0) ? file.substring(lastIndex+1) : "";
	}
	
	public static File getUniqueFilename( File file )
	{
		String fullName = file.getName();
		int dotIndex = fullName.lastIndexOf(".");
		String name = fullName.substring(0, dotIndex);
		String ext = fullName.substring(dotIndex+1);
		
//	    String fullName[] = file.getName().split("\\.");
//		String name = fullName[0];
//		String ext = fullName[1];
	    
	    int counter = 1;
	    while(file.exists())
	        file = new File( file.getParent(), name+" ("+(counter++)+")." + ext );
	    
	    return file;
	}
	
	public static String populateTemplate(String template, LocalDateTime time, String name, String ext) {
		try {
			return time.format(DateTimeFormatter.ofPattern(template))
					.replaceAll("NAME", name)
					.replaceAll("EXT", ext) + "." + ext;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static <T> boolean contains(T[] array, T object) {
		for (T item : array)
			if (item.equals(object))
				return true;
		return false;
	}
}
