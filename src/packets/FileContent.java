package packets;

import java.io.File;
import java.io.Serializable;

public class FileContent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private byte content[];
	private File sourceFile;
	private File destFile;
	private String sender;
	private String receiver;
	private int progress;

	public FileContent(File sourceFile, File destFile, String sender, String receiver, int progress, byte content[]) {
		this.content = content;
		this.sender = sender;
		this.receiver = receiver;
		this.sourceFile = sourceFile;
		this.destFile = destFile;
		this.progress = progress;
	}
	
	public int getProgress() {
		return progress;
	}

	public String getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiver;
	}	
	
	public byte[] getContent() {
		return content;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public File getDestFile() {
		return destFile;
	}

}
