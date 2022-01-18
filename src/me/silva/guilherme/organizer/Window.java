package me.silva.guilherme.organizer;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;

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

public class Window extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField sorceField;
	private JTextField destField;
	private JTextField patternField;
	private JTextField extensionField;
	
	//https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
	private static final String[] PATTERN_TEMPLATES = {
		"y/MM MMMM/dd MMM y H'h_'m'm_'s's'", /* 2020/08 August/15 Aug 2020 22h_43m_46s */
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
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Window() {
		setResizable(false);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel logo = new JLabel(new ImageIcon(getClass().getResource("/logo.png")));
		logo.setBounds(10, 10, 466, 64);
		contentPane.add(logo);
		
		JLabel sourceLabel = new JLabel("Source Directory:");
		sourceLabel.setBounds(10, 84, 140, 20);
		contentPane.add(sourceLabel);

		sorceField = new JTextField();
		sorceField.setToolTipText("the directory including all images to be organized (recursive)");
		sorceField.setBounds(160, 84, 241, 22);
		contentPane.add(sorceField);
		sorceField.setColumns(10);
		
		JButton sourceOpen = new JButton("Open");
		sourceOpen.setBounds(411, 84, 65, 21);
		contentPane.add(sourceOpen);
		
		sourceOpen.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	File directory = Utility.chooseDirectory();
		    	if (directory != null)
		    		sorceField.setText(directory.toString());
		    }
		});
		
		JLabel destLabel = new JLabel("Destination Directory:");
		destLabel.setBounds(10, 114, 140, 20);
		contentPane.add(destLabel);
		
		destField = new JTextField();
		destField.setToolTipText("the directory to deposit sorted images");
		destField.setColumns(10);
		destField.setBounds(160, 114, 241, 22);
		contentPane.add(destField);
		
		JButton destOpen = new JButton("Open");
		destOpen.setBounds(411, 114, 65, 21);
		contentPane.add(destOpen);
		
		destOpen.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	File directory = Utility.chooseDirectory();
		    	if (directory != null)
		    		destField.setText(directory.toString());
		    }
		});

		JLabel patternTemplateLabel = new JLabel("Folder Pattern Template:");
		patternTemplateLabel.setBounds(10, 144, 140, 20);
		contentPane.add(patternTemplateLabel);
		
		JComboBox patternTemplateSelect = new JComboBox();
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

		JLabel patternLabel = new JLabel("Folder Pattern Used:");
		patternLabel.setBounds(10, 174, 140, 20);
		contentPane.add(patternLabel);
		
		patternField = new JTextField();
		patternField.setToolTipText("the format used inside the destination directory");
		patternField.setColumns(10);
		patternField.setBounds(160, 175, 241, 22);
		contentPane.add(patternField);
		patternField.setText(PATTERN_TEMPLATES[0]);

		JButton patternHelp = new JButton("?");
		patternHelp.setBounds(411, 176, 65, 21);
		contentPane.add(patternHelp);
		
		patternHelp.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	Utility.openWebpage("https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns");
		    }
		});
		
		JLabel optionsLabel = new JLabel("Options:");
		optionsLabel.setBounds(10, 204, 100, 20);
		contentPane.add(optionsLabel);
		
		JComboBox transferModeSelect = new JComboBox();
		transferModeSelect.setToolTipText("select how source images will be treated");
		transferModeSelect.setModel(new DefaultComboBoxModel(new String[] {"Copy files from source", "Move files from source (caution)"}));
		transferModeSelect.setSelectedIndex(0);
		transferModeSelect.setBounds(160, 207, 316, 22);
		contentPane.add(transferModeSelect);
		
		JComboBox resolverModeSelect = new JComboBox();
		resolverModeSelect.setToolTipText("select how repeating images will be treated");
		resolverModeSelect.setModel(new DefaultComboBoxModel(new String[] {"Add trailing index to files of same name ex. (1)", "Skip files with the same name"}));
		resolverModeSelect.setSelectedIndex(0);
		resolverModeSelect.setBounds(160, 239, 316, 22);
		contentPane.add(resolverModeSelect);
		
		JLabel extensionLabel = new JLabel("Extensions:");
		extensionLabel.setBounds(10, 269, 140, 20);
		contentPane.add(extensionLabel);
		
		extensionField = new JTextField();
		extensionField.setToolTipText("extensions that will be analyzed inside the source directory");
		extensionField.setText("png,jpg,gif,mp4,mov,mpg,wmv");
		extensionField.setColumns(10);
		extensionField.setBounds(160, 270, 241, 22);
		contentPane.add(extensionField);
		
		JLabel status = new JLabel("");
		status.setBounds(10, 300, 286, 20);
		contentPane.add(status);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setBounds(306, 325, 170, 21);
		contentPane.add(progressBar);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setEnabled(false);
		cancelButton.setBounds(396, 299, 80, 21);
		contentPane.add(cancelButton);
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imgOrgThrd.interrupt();
		    	status.setText("Status: Pending Cancelation!");
			}
		});
		
		JButton startButton = new JButton("Start");
		startButton.setBounds(306, 299, 80, 21);
		contentPane.add(startButton);
		
		startButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	
		    	String sourceVal = sorceField.getText();
				String destVal = destField.getText();
				String patternVal = patternField.getText();
				String extensionsVal = extensionField.getText();
				
				int transferMode = transferModeSelect.getSelectedIndex();
				int resolverMode = resolverModeSelect.getSelectedIndex();
				
				String error = validateFields(sourceVal, destVal, patternVal, extensionsVal);
		    	if (error != null) {
		    		status.setText(error);
		    		return;
		    	}
		    	
		    	File source = new File(Utility.simplifyPath(sourceVal));
		    	File dest = new File(Utility.simplifyPath(destVal));
		    	error = validatePaths(source, dest);
		    	if (error != null) {
		    		status.setText(error);
		    		return;
		    	}
		    	
		    	String[] extensions = extensionsVal.split(",");
		    	error = validateExtensions(extensions);
		    	if (error != null) {
		    		status.setText(error);
		    		return;
		    	}
		    	
		    	int totalFileCount = Utility.countFiles(source, extensions);
		    	if (totalFileCount == 0) {
		    		status.setText("Error: There are no files to organize!");
		    		return;
		    	}
		    	
		    	status.setText(""); //no warnings, errors
		    	
		    	if (!Utility.contains(PATTERN_TEMPLATES, patternVal)) {
		    		LocalDateTime time = LocalDateTime.now();
		    		String filledPattern = Utility.populateTemplate(patternVal, time, "Beach", "png");
		    		
		    		if (filledPattern == null) {
		    			status.setText("Error: The custom pattern is invalid!");
		    			return;
		    		}
		    		
		    		if (!Utility.requestConfirmation("You are using a CUSTOM PATTERN! Are you sure you want to continue?\n\n"+filledPattern)) {
				    	return;
			    	}
		    	}
		    	
		    	if (!Utility.requestConfirmation("There are "+totalFileCount+
		    			" files in source that will be affected! Are you sure you want to continue?")) {
		    		return;	
		    	}
		    	
		    	if (dest.listFiles().length > 0 && !Utility.requestConfirmation(
		    			"The destination folder is not empty! Are you sure you want to continue?")) {
			    	return;
		    	}
		    	
		    	status.setText("Sucess: Processing images!");
		    	
		    	progressBar.setMinimum(0);
		    	progressBar.setMaximum(totalFileCount);
		    	
		    	imgOrgThrd = new Thread(new Runnable() {
					public void run() {
						
						ImageOrganizer imgOrg = new ImageOrganizer(patternVal, extensions, resolverMode, transferMode);
				    	imgOrg.organize(source, dest, new Runnable() {
								private int counter = 0;
								public void run() {
									progressBar.setValue(++counter);
								}
							});
				    	
				    	status.setText(!imgOrgThrd.isInterrupted() ? "Sucess: Finished operation!" : "Sucess: Canceled operation!");
				    	startButton.setEnabled(true);
				    	cancelButton.setEnabled(false);
				    	imgOrgThrd = null;
					}
					
				});
		    	imgOrgThrd.start();

		    	startButton.setEnabled(false);
		    	cancelButton.setEnabled(true);
		    }
		});
		
		JLabel credits = new JLabel("Guilherme Silva | https://binarygamers.com/");
		credits.setBounds(10, 329, 286, 20);
		contentPane.add(credits);
	}

	private String[] populateTemplates() {
		LocalDateTime time = LocalDateTime.now();
		
		String[] populated = new String[PATTERN_TEMPLATES.length];
		for (int i = 0; i < populated.length; i++) {
			//populated[i] = time.format(DateTimeFormatter.ofPattern(TEMPLATES[i])).replaceAll("NAME", "Beach") + ".png";
			populated[i] = Utility.populateTemplate(PATTERN_TEMPLATES[i], time, "Beach", "png");
		}
		
		return populated;
	}
	
	private String validateFields(String... values) {
		for (String value: values)
			if (value.isEmpty())
				return "Error: a field is empty!";
		return null;
	}
	
	private String validatePaths(File source, File dest) {
		
		//escapes if one of the paths does not exist
    	if (!source.exists() || !dest.exists())
    		return "Error: a path does not exist!";
    	
    	if (!source.isAbsolute() || !dest.isAbsolute())
    		return "Error: a path cannot be relative!";
    	
    	//escapes if the paths are the same
    	if (source.equals(dest))
    		return "Error: the paths cannot be the same!";
    	
    	//escapes if one of the paths is not a directory
    	if (!source.isDirectory() || !dest.isDirectory())
    		return "Error: a path is not a directory!";
    	
    	Path sourcePath = source.toPath();
    	Path destPath = dest.toPath();
    	
    	//escapes if one of the paths contain another
    	if (sourcePath.startsWith(destPath) || destPath.startsWith(sourcePath))
    		return "Error: a path cannot contain another!";
    	
    	return null;
	}
	
	private String validateExtensions(String[] extensions) {
		for (String extension : extensions)
			if (extension.contains(" "))
				return "Error: an extension cannot contain spaces!";
		return null;
	}
}
