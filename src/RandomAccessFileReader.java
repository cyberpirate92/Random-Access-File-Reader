import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class RandomAccessFileReader {
	
	private static final int DEFAULT_BUFFER_SIZE = 5;
	
	private int maxBufferSize;
	private RandomAccessFile fileHandle;
	private ArrayList<String> buffer;
	private long bufferStartPointer;	// pointer indicating cursor position for the starting line of the buffer 
	
	public RandomAccessFileReader(String filename) throws IOException, FileNotFoundException {
		try {
			fileHandle = new RandomAccessFile(new File(filename), "r");
			fileHandle.seek(0);
			this.maxBufferSize = DEFAULT_BUFFER_SIZE;
			buffer = new ArrayList<String>();
			buffer.ensureCapacity(this.maxBufferSize);
			this.cycleForward();	// calling initially to populate the buffer
		}
		catch(FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
			throw fileNotFoundException;
		}
		catch(IOException ioException) {
			ioException.printStackTrace();
			throw ioException;
		}
	}
	
	public String readNextLine() throws IOException {
		return fileHandle.readLine();
	}
	
	public String readPreviousLine() throws IOException {
		
		long temp = fileHandle.getFilePointer();
		fileHandle.seek(bufferStartPointer);
		
		if(getCurrentOffset() <= 0) {
			// currently at beginning of file, cannot read previous line
			fileHandle.seek(temp);
			return null;
		}
		else {
			
			fileHandle.seek(fileHandle.getFilePointer()-1);
			
			StringBuilder stringBuilder = new StringBuilder();
			int newLineCounter = 0;
			byte[] tempBuffer = new byte[1];
			
			for(; fileHandle.getFilePointer() > 0; fileHandle.seek(fileHandle.getFilePointer()-2)) {
				fileHandle.read(tempBuffer);
				String s = new String(tempBuffer);
				if(s.contains("\n")) {
					newLineCounter++;
					if(newLineCounter == 2) {
						break;
					}
				}
				stringBuilder.append(s);
				tempBuffer = new byte[1];
			}
			
			// swapping pointers
			this.bufferStartPointer = fileHandle.getFilePointer();
			fileHandle.seek(temp);
			
			return stringBuilder.reverse().toString().replace("\n", "").replace("\r", "");
		}
	}
	
	public long getMaxOffset() throws IOException {
		return fileHandle.length();
	}
	
	public void setOffset(long position) throws IOException{
		if(position >= 0 && position <= getMaxOffset()) {
			this.fileHandle.seek(position);
		}
	}
	
	public long getCurrentOffset() throws IOException {
		if(fileHandle != null) {
			return fileHandle.getFilePointer();
		}
		else {
			throw new NullPointerException("File pointer is null");
		}
	}
	
	public void close() throws IOException {
		if(fileHandle != null) {
			fileHandle.close();
		}
	}
	
	public void setBlockSize(int size) {
		this.maxBufferSize = size;
	}
	
	public int getBlockSize() {
		return this.maxBufferSize;
	}
	
	public ArrayList<String> getBuffer() {
		return this.buffer;
	}
	
	public void cycleForward() throws IOException {
		if(fileHandle != null) {
			if(fileHandle.getFilePointer() < fileHandle.length()) {
				if(buffer.size() == 0) {
					// buffer is empty, fill it
					bufferStartPointer = 0;
					while(buffer.size() < this.maxBufferSize && fileHandle.getFilePointer() < fileHandle.length()) {
						buffer.add(this.readNextLine());
					}
				}
				else if(buffer.size() == this.maxBufferSize) {
					String line = this.readNextLine();
					if(line != null) {
						buffer.remove(0);
						buffer.add(line);
					}
					else {
						System.out.println("Cannot cycle forward, reached EOF");
					}
				}
				else {
					System.out.println("[DEBUG] cycleForward(), bufferSize=" + buffer.size() + ", blockSize=" + this.maxBufferSize);
					System.out.println("[DEBUG] filePointer="+fileHandle.getFilePointer() + ", Length=" + this.getMaxOffset());
				}
			}
		}
	}
	
	public void cycleBackward() throws IOException {
		if(fileHandle != null) {
			if(fileHandle.getFilePointer() < fileHandle.length()) {
				if(buffer.size() == 0) {
					// buffer is empty, fill it
					bufferStartPointer = 0;
					while(buffer.size() < this.maxBufferSize && fileHandle.getFilePointer() < fileHandle.length()) {
						buffer.add(this.readNextLine());
					}
				}
				else if(buffer.size() == this.maxBufferSize) {
					String line = this.readPreviousLine();
					if(line != null) {
						buffer.remove(buffer.size()-1);
						buffer.add(0, line);
					}
					else {
						System.out.println("Cannot cycle backward, reached BOF");
					}
				}
				else {
					System.out.println("[DEBUG] cycleBackward(), bufferSize=" + buffer.size() + ", blockSize=" + this.maxBufferSize);
					System.out.println("[DEBUG] filePointer="+fileHandle.getFilePointer() + ", Length=" + this.getMaxOffset());
				}
			}
		}
	}
	
	public int getCurrentBufferSize() {
		return this.buffer.size();
	}
}
