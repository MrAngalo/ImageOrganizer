package me.silva.guilherme.organizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

public class ImageOrganizer {
	
	private String pattern;
	private String[] extensions;

	private ResolverMode resolver;
	private TransferMode transfer;
	
	public ImageOrganizer(String pattern, String[] extensions, int resolverMode, int transferMode) {
		this.pattern = pattern;
		this.extensions = extensions;
		this.resolver = ResolverMode.values()[resolverMode];
		this.transfer = TransferMode.values()[transferMode];
	}
	
	public void organize(File source, File dest, Runnable callback) {
		for (File file : source.listFiles()) {
			
			//escapes if canceled operation
			if (Thread.currentThread().isInterrupted()) {
				return;
			}
			
			if (file.isDirectory()) {
				organize(file, dest, callback);
			} else if (Utility.hasFileExtension(file.getAbsolutePath(), extensions)) {
				String fullName = file.getName();
				int dotIndex = fullName.lastIndexOf("\\.");
				String name = fullName.substring(0, dotIndex);
				String ext = fullName.substring(dotIndex+1);
				
//				String fullName[] = file.getName().split("\\.");
//				String name = fullName[0];
//				String ext = fullName[1];
				
				try {
					BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
					
					LocalDateTime time = LocalDateTime.ofEpochSecond(attr.creationTime().to(TimeUnit.SECONDS), 0, ZoneOffset.UTC);
					//String filledPattern = date.format(DateTimeFormatter.ofPattern(pattern)).replaceAll("NAME", name) + "." + ext;
					String filledPattern = Utility.populateTemplate(pattern, time, name, ext);
					
					File newFile = resolver.validate(new File(dest, filledPattern));
					if (newFile != null) {						
						Files.createDirectories(Paths.get(newFile.getParentFile().getPath()));
						transfer.perform(file, newFile);
					}
					
					callback.run(); //runs in current thread
					Thread.sleep(3); //adds delay in case user wants to cancel operation
					
				//thrown if interrupt() is called during sleep
				//which will unset the flag
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private enum ResolverMode {
		ADD_TRAILING_INDEX {
			public File validate(File file) {
				return Utility.getUniqueFilename(file);
			}
		},
		
		SKIP_SAME_NAME {
			public File validate(File file) {
				return file.exists() ? null : file;
			}
		};

		public abstract File validate(File file);
	}
	
	private enum TransferMode {
		COPY_FILES {
			public void perform(File file1, File file2) throws IOException {
				Files.copy(file1.toPath(), file2.toPath());
			}
		},
		
		MOVE_FILES {
			public void perform(File file1, File file2) throws IOException {
				Files.move(file1.toPath(), file2.toPath());
			}
		};
		
		public abstract void perform(File file1, File file2) throws IOException;
		
	}
}
