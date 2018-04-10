package packets;

import java.io.File;
import java.io.Serializable;

public class FileHeader implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private File sourceFile;
	private File destFile;
	private String request;
	private String sender;
	private String receiver;
	private int maxBytes;
	private boolean accepted;
	
	public FileHeader(File sourceFile, File destFile, String sender, String receiver, String request, int maxBytes, boolean accepted) {
		this.sourceFile = sourceFile;
		this.destFile = destFile;
		this.sender = sender;
		this.receiver = receiver;
		this.request = request;
		this.maxBytes = maxBytes;
		this.accepted = accepted;
	}

	public int getMaxBytes() {
		return maxBytes;
	}

	public String getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiver;
	}	
	
	public File getSourceFile() {
		return sourceFile;
	}

	public File getDestFile() {
		return destFile;
	}

	public String getRequest() {
		return request;
	}

	public boolean isAccepted() {
		return accepted;
	}
	
}
