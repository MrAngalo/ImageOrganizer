package me.silva.guilherme.organizer;

import static me.silva.guilherme.language.Language.PromptKey.ConfirmationPopup1;
import static me.silva.guilherme.language.Language.PromptKey.ConfirmationPopup2;
import static me.silva.guilherme.language.Language.PromptKey.ConfirmationPopup3;
import static me.silva.guilherme.language.Language.PromptKey.CancelButton;
import static me.silva.guilherme.language.Language.PromptKey.DestButton;
import static me.silva.guilherme.language.Language.PromptKey.DestLabel;
import static me.silva.guilherme.language.Language.PromptKey.DestToolTip;
import static me.silva.guilherme.language.Language.PromptKey.ExtensionsLabel;
import static me.silva.guilherme.language.Language.PromptKey.ExtensionsToolTip;
import static me.silva.guilherme.language.Language.PromptKey.OptionsLabel;
import static me.silva.guilherme.language.Language.PromptKey.PatternCustomButton;
import static me.silva.guilherme.language.Language.PromptKey.PatternCustomLabel;
import static me.silva.guilherme.language.Language.PromptKey.PatternCustomToolTip;
import static me.silva.guilherme.language.Language.PromptKey.PatternTemplateLabel;
import static me.silva.guilherme.language.Language.PromptKey.PatternTemplateToolTip;
import static me.silva.guilherme.language.Language.PromptKey.ResolverModeOption1;
import static me.silva.guilherme.language.Language.PromptKey.ResolverModeOption2;
import static me.silva.guilherme.language.Language.PromptKey.ResolverModeToolTip;
import static me.silva.guilherme.language.Language.PromptKey.SourceButton;
import static me.silva.guilherme.language.Language.PromptKey.SourceLabel;
import static me.silva.guilherme.language.Language.PromptKey.SourceToolTip;
import static me.silva.guilherme.language.Language.PromptKey.StartButton;
import static me.silva.guilherme.language.Language.PromptKey.TransferModeOption1;
import static me.silva.guilherme.language.Language.PromptKey.TransferModeOption2;
import static me.silva.guilherme.language.Language.PromptKey.TransferModeToolTip;
import static me.silva.guilherme.language.Language.PromptKey.StatusCode1;
import static me.silva.guilherme.language.Language.PromptKey.values;

import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import me.silva.guilherme.language.LanguageHandler;

public class Window extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private LanguageHandler langHand;
	private int statusCode;
	
	private JPanel contentPane;
	private JTextField sorceField;
	private JTextField destField;
	private JTextField patternField;
	private JTextField extensionField;
	
	private JLabel status;
	
	//https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
	private static final String[] PATTERN_TEMPLATES = {
		"y/MM MMMM/dd MMM y H'h_'m''s's'", /* 2020/08 August/15 Aug 2020 22h_4346s */
		"y/MM MMMM/dd MMM y 'NAME'", /* 2020/08 August/15 Aug 2020 Clouds */
		"y/MM MMMM/'NAME'", /* 2020/08 August/Clouds */
		"y/'EXT'/'NAME'", /* 2020/png/Clouds*/
		"'EXT'/'NAME'" /* pngg/Clouds*/
	};
	
	private Thread imgOrgThrd;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window frame = new Window();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws MalformedURLException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Window() throws MalformedURLException {
		langHand = new LanguageHandler("en-US");
		
		setResizable(false);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel logo = new JLabel(new ImageIcon(getClass().getResource("/res/images/logo.png")));

		logo.setBounds(10, 10, 466, 64);
		contentPane.add(logo);
		
		//text is functionally unecessary, but shows up in window builder
		JLabel sourceLabel = new JLabel("Source Directory:");
		langHand.addDependent(lang -> sourceLabel.setText(lang.getPrompt(SourceLabel)));
		sourceLabel.setBounds(10, 84, 140, 20);
		contentPane.add(sourceLabel);

		sorceField = new JTextField();
		//text is functionally unecessary, but shows up in window builder
		sorceField.setToolTipText("the directory including all images to be organized (recursive)");
		langHand.addDependent(lang -> sorceField.setToolTipText(lang.getPrompt(SourceToolTip)));
		sorceField.setBounds(160, 84, 241, 22);
		contentPane.add(sorceField);
		sorceField.setColumns(10);

		//text is functionally unecessary, but shows up in window builder
		JButton sourceOpen = new JButton("Open");
		langHand.addDependent(lang -> sourceOpen.setText(lang.getPrompt(SourceButton)));
		sourceOpen.setBounds(411, 84, 65, 21);
		contentPane.add(sourceOpen);
		
		sourceOpen.addActionListener( e -> {
	    	File directory = Utility.chooseDirectory();
	    	if (directory != null)
	    		sorceField.setText(directory.toString());
		});
		
		//text is functionally unecessary, but shows up in window builder
		JLabel destLabel = new JLabel("Destination Directory:");
		langHand.addDependent(lang -> destLabel.setText(lang.getPrompt(DestLabel)));
		destLabel.setBounds(10, 114, 140, 20);
		contentPane.add(destLabel);
		
		destField = new JTextField();
		//text is functionally unecessary, but shows up in window builder
		destField.setToolTipText("the directory to deposit sorted images");
		langHand.addDependent(lang -> destField.setToolTipText(lang.getPrompt(DestToolTip)));
		destField.setColumns(10);
		destField.setBounds(160, 114, 241, 22);
		contentPane.add(destField);

		//text is functionally unecessary, but shows up in window builder
		JButton destOpen = new JButton("Open");
		langHand.addDependent(lang -> destOpen.setText(lang.getPrompt(DestButton)));
		destOpen.setBounds(411, 114, 65, 21);
		contentPane.add(destOpen);
		
		destOpen.addActionListener(e -> {
	    	File directory = Utility.chooseDirectory();
	    	if (directory != null)
	    		destField.setText(directory.toString());
		});

		//text is functionally unecessary, but shows up in window builder
		JLabel patternTemplateLabel = new JLabel("Folder Pattern Template:");
		langHand.addDependent(lang -> patternTemplateLabel.setText(lang.getPrompt(PatternTemplateLabel)));
		patternTemplateLabel.setBounds(10, 144, 140, 20);
		contentPane.add(patternTemplateLabel);
		
		JComboBox patternTemplateSelect = new JComboBox();
		//text is functionally unecessary, but shows up in window builder
		patternTemplateSelect.setToolTipText("The folder structure used to organize files");
		langHand.addDependent(lang -> patternTemplateSelect.setToolTipText(lang.getPrompt(PatternTemplateToolTip)));
		patternTemplateSelect.setModel(new DefaultComboBoxModel(populateTemplates()));
		patternTemplateSelect.setSelectedIndex(0);
		patternTemplateSelect.setBounds(160, 144, 316, 22);
		contentPane.add(patternTemplateSelect);
		
		patternTemplateSelect.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					patternField.setText(PATTERN_TEMPLATES[patternTemplateSelect.getSelectedIndex()]);
			}
		});

		//text is functionally unecessary, but shows up in window builder
		JLabel patternLabel = new JLabel("Folder Pattern Used:");
		langHand.addDependent(lang -> patternLabel.setText(lang.getPrompt(PatternCustomLabel)));
		patternLabel.setBounds(10, 174, 140, 20);
		contentPane.add(patternLabel);
		
		patternField = new JTextField();
		//text is functionally unecessary, but shows up in window builder
		patternField.setToolTipText("the format used inside the destination directory");
		langHand.addDependent(lang -> patternField.setToolTipText(lang.getPrompt(PatternCustomToolTip)));
		patternField.setColumns(10);
		patternField.setBounds(160, 175, 241, 22);
		contentPane.add(patternField);
		patternField.setText(PATTERN_TEMPLATES[0]);

		//text is functionally unecessary, but shows up in window builder
		JButton patternHelp = new JButton("?");
		langHand.addDependent(lang -> patternHelp.setText(lang.getPrompt(PatternCustomButton)));
		patternHelp.setBounds(411, 176, 65, 21);
		contentPane.add(patternHelp);
		
		patternHelp.addActionListener( e -> {
	    	Utility.openWebpage("https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns");
		});
		
		//text is functionally unecessary, but shows up in window builder
		JLabel optionsLabel = new JLabel("Options:");
		langHand.addDependent(lang -> optionsLabel.setText(lang.getPrompt(OptionsLabel)));
		optionsLabel.setBounds(10, 204, 100, 20);
		contentPane.add(optionsLabel);
		
		JComboBox transferModeSelect = new JComboBox();
		//text is functionally unecessary, but shows up in window builder
		transferModeSelect.setToolTipText("select how source images will be treated");
		transferModeSelect.setModel(new DefaultComboBoxModel(new String[] {
				"Copy files from source", "Move files from source (caution)"}));
		langHand.addDependent(lang -> {
			transferModeSelect.setToolTipText(lang.getPrompt(TransferModeToolTip));
			transferModeSelect.setModel(new DefaultComboBoxModel(new String[] {
					lang.getPrompt(TransferModeOption1), lang.getPrompt(TransferModeOption2)}));
		});
		transferModeSelect.setSelectedIndex(0);
		transferModeSelect.setBounds(160, 207, 316, 22);
		contentPane.add(transferModeSelect);
		
		JComboBox resolverModeSelect = new JComboBox();
		//text is functionally unecessary, but shows up in window builder
		resolverModeSelect.setToolTipText("select how repeating images will be treated");
		resolverModeSelect.setModel(new DefaultComboBoxModel(new String[] {
				"Add trailing index to files of same name ex. (1)", "Skip files with the same name"}));
		langHand.addDependent(lang -> {
			resolverModeSelect.setToolTipText(lang.getPrompt(ResolverModeToolTip));
			resolverModeSelect.setModel(new DefaultComboBoxModel(new String[] {
					lang.getPrompt(ResolverModeOption1), lang.getPrompt(ResolverModeOption2)}));
		});
		resolverModeSelect.setSelectedIndex(0);
		resolverModeSelect.setBounds(160, 239, 316, 22);
		contentPane.add(resolverModeSelect);

		//text is functionally unecessary, but shows up in window builder
		JLabel extensionLabel = new JLabel("Extensions:");
		langHand.addDependent(lang -> extensionLabel.setText(lang.getPrompt(ExtensionsLabel)));
		extensionLabel.setBounds(10, 269, 140, 20);
		contentPane.add(extensionLabel);
		
		extensionField = new JTextField();
		//text is functionally unecessary, but shows up in window builder
		extensionField.setToolTipText("extensions that will be analyzed inside the source directory");
		langHand.addDependent(lang -> extensionField.setToolTipText(lang.getPrompt(ExtensionsToolTip)));
		extensionField.setText("png,jpg,jpeg,gif,mp4,mov,mpg,wmv,3gp,avi,ogg,mp3,wma");
		extensionField.setColumns(10);
		extensionField.setBounds(160, 270, 241, 22);
		contentPane.add(extensionField);
		
		statusCode = 0;
		status = new JLabel("");
		langHand.addDependent(lang -> updateStatus(statusCode));
		status.setBounds(10, 300, 286, 20);
		contentPane.add(status);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setBounds(306, 325, 170, 21);
		contentPane.add(progressBar);

		//text is functionally unecessary, but shows up in window builder
		JButton cancelButton = new JButton("Cancel");
		langHand.addDependent(lang -> cancelButton.setText(lang.getPrompt(CancelButton)));
		cancelButton.setEnabled(false);
		cancelButton.setBounds(396, 299, 80, 21);
		contentPane.add(cancelButton);
		
		cancelButton.addActionListener( e -> {
			imgOrgThrd.interrupt();
			updateStatus(statusCode = 13);
		});

		//text is functionally unecessary, but shows up in window builder
		JButton startButton = new JButton("Start");
		langHand.addDependent(lang -> startButton.setText(lang.getPrompt(StartButton)));
		startButton.setBounds(306, 299, 80, 21);
		contentPane.add(startButton);
		
		startButton.addActionListener( e -> {
		    	
	    	String sourceVal = sorceField.getText();
			String destVal = destField.getText();
			String patternVal = patternField.getText();
			String extensionsVal = extensionField.getText();
				
			int transferMode = transferModeSelect.getSelectedIndex();
			int resolverMode = resolverModeSelect.getSelectedIndex();
				
			statusCode = 0;
			
			for (String value : new String[]{sourceVal, destVal, patternVal, extensionsVal}) {
				if (value.isEmpty()) {
					//Error: a field is empty!
					updateStatus(statusCode = 1);
					return;
				}
			}
			
	    	File source = new File(Utility.simplifyPath(sourceVal));
	    	File dest = new File(Utility.simplifyPath(destVal));
	    	
	    	//escapes if one of the paths does not exist
	    	if (!source.exists() || !dest.exists()) {
	    		//Error: a path does not exist!
	    		updateStatus(statusCode = 2);
	    		return;
	    	}
	    	
	    	//espaces if one of the paths is relative ex "./folder"
	    	if (!source.isAbsolute() || !dest.isAbsolute()) {
	    		//Error: a path cannot be relative!
	    		updateStatus(statusCode = 3);
	    		return;
	    	}
	    	
	    	//escapes if the paths are the same
	    	if (source.equals(dest)) {
	    		//Error: the paths cannot be the same!
	    		updateStatus(statusCode = 4);
	    		return;
	    	}
	    	
	    	//escapes if one of the paths is not a directory
	    	if (!source.isDirectory() || !dest.isDirectory()) {
	    		//Error: a path is not a directory!
	    		updateStatus(statusCode = 5);
	    		return;
	    	}
	    	
	    	Path sourcePath = source.toPath();
	    	Path destPath = dest.toPath();
	    	
	    	//escapes if one of the paths contain another
	    	if (sourcePath.startsWith(destPath) || destPath.startsWith(sourcePath)) {
	    		//Error: a path cannot contain another!
	    		updateStatus(statusCode = 6);
	    		return;
	    	}
		    	
	    	String[] extensions = extensionsVal.split(",");
	    	for (String extension : extensions) {
				if (extension.contains(" ")) {
					//Error: an extension cannot contain spaces!
		    		updateStatus(statusCode = 7);
		    		return;
		    	}
	    	}
	  
	    	int totalFileCount = Utility.countFiles(source, extensions);
	    	if (totalFileCount == 0) {
	    		//Error: There are no files to organize!
	    		updateStatus(statusCode = 8);
	    		return;
	    	}
	    	
	    	//Empty Message
	    	updateStatus(statusCode = 0); //no warnings, errors
	    	
	    	if (!Utility.contains(PATTERN_TEMPLATES, patternVal)) {
	    		LocalDateTime time = LocalDateTime.now();
	    		String filledPattern = Utility.populateTemplate(patternVal, time, "Beach", "png");
	    		
	    		if (filledPattern == null) {
	    			//Error: The custom pattern is invalid!
	    			updateStatus(statusCode = 9);
	    			return;
	    		}
	    		
	    		if (!Utility.requestConfirmation(
	    				langHand.getCurrent().getPrompt(ConfirmationPopup1).replace("{pattern}", filledPattern))) {
			    	return;
		    	}
	    	}
		    
	    	if (!Utility.requestConfirmation(
	    			langHand.getCurrent().getPrompt(ConfirmationPopup2).replace("{total}", totalFileCount+""))) {
	    		return;	
	    	}
		    	
	    	if (dest.listFiles().length > 0 && !Utility.requestConfirmation(
	    			langHand.getCurrent().getPrompt(ConfirmationPopup3))) {
		    	return;
	    	}
	    	
	    	//Sucess: Processing images!
	    	updateStatus(statusCode = 10);
	    	
	    	progressBar.setMinimum(0);
	    	progressBar.setMaximum(totalFileCount);
	    	
	    	imgOrgThrd = new Thread(() -> {
					
				ImageOrganizer imgOrg = new ImageOrganizer(patternVal, extensions, resolverMode, transferMode);
		    	imgOrg.organize(source, dest, new Runnable() {
						private int counter = 0;
						public void run() {
							progressBar.setValue(++counter);
						}
					});
		    	
		    	//if not interrupted -> Sucess: Finished operation!
		    	//if interrupted -> Sucess: Canceled operation!
		    	updateStatus(!imgOrgThrd.isInterrupted() ? (statusCode = 11) : (statusCode = 12));
		    	
		    	startButton.setEnabled(true);
		    	cancelButton.setEnabled(false);
		    	imgOrgThrd = null;
				
			});
	    	imgOrgThrd.start();
	    	startButton.setEnabled(false);
	    	cancelButton.setEnabled(true);
		});
		
		JLabel credits = new JLabel("Guilherme Silva | https://binarygamers.com/");
		credits.setBounds(10, 329, 286, 20);
		contentPane.add(credits);
		
		JComboBox languageSelect = new JComboBox();
		languageSelect.setModel(new DefaultComboBoxModel(new String[] {"en-US"}));
		languageSelect.setModel(new DefaultComboBoxModel(langHand.getLanguageNames().toArray(String[]::new)));
		languageSelect.setSelectedIndex(0);
		languageSelect.setBounds(411, 10, 65, 22);
		contentPane.add(languageSelect);
		
		languageSelect.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED)
				langHand.selectLanguage(languageSelect.getSelectedItem().toString());
		});
		
        langHand.selectLanguage("en-US");
	}

	private String[] populateTemplates() {
		LocalDateTime time = LocalDateTime.now();
		
		String[] populated = new String[PATTERN_TEMPLATES.length];
		for (int i = 0; i < populated.length; i++) {
			//populated[i] = time.format(DateTimeFormatter.ofPattern(TEMPLATES[i])).replaceAll("NAME", "Beach") + ".png";
			populated[i] = Utility.populateTemplate(PATTERN_TEMPLATES[i], time, "Banana", "png");
		}
		
		return populated;
	}
	
	private void updateStatus(int code) {
		status.setText(code == 0 ? "" :
			langHand.getCurrent().getPrompt(values()[StatusCode1.ordinal() + code -1]));
	}
}
