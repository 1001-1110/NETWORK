package packets;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String files[];
	private String serverDir;
	private int fileSizes[];
	
	public FileList(File files[], String serverDir) {
		ArrayList<File> filteredFiles = new ArrayList<>();
		for(int i = 0 ; i < files.length ; i++) {
			filteredFiles.add(files[i]);
		}		
		for(int i = 0 ; i < filteredFiles.size() ; i++) {
			if(filteredFiles.get(i).isDirectory())
				filteredFiles.remove(i);
		}				
		this.files = new String[filteredFiles.size()];
		this.fileSizes = new int[filteredFiles.size()];
		for(int i = 0 ; i < filteredFiles.size() ; i++) {
			this.files[i] = filteredFiles.get(i).getName();
			try {
				this.fileSizes[i] = (int) Files.size(filteredFiles.get(i).toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.serverDir = serverDir;
	}

	public String getServerDir() {
		return serverDir;
	}

	public int[] getFileSizes() {
		return fileSizes;
	}

	public String[] getFiles() {
		return files;
	}
	
}
