import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class RandomAccessFileReader {
	
	private RandomAccessFile fileHandle;
	private boolean isForwardFacing = true;
	private ArrayList<String> buffer;
	private static final int DEFAULT_BUFFER_SIZE = 5;
	private int bufferSize;
	private boolean firstReverse = true; 
	
	public RandomAccessFileReader(String filename) throws IOException, FileNotFoundException {
		try {
			fileHandle = new RandomAccessFile(new File(filename), "r");
			fileHandle.seek(0);
			this.buffer = new ArrayList<String>();
			this.bufferSize = RandomAccessFileReader.DEFAULT_BUFFER_SIZE;
			this.cycleForward();	// initial buffer fill
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
	
	public RandomAccessFileReader(String filename, int bufferSize) throws IOException, FileNotFoundException {
		this(filename);
		if(bufferSize > 0) {
			this.bufferSize = bufferSize;
		}
	}
	
	public void moveCursorToStartOfBuffer() throws IOException {
		this.fileHandle.seek(this.fileHandle.getFilePointer() - (getBufferLength()+1));
		this.isForwardFacing = false;
	}
	
	public void moveCursorToEndOfBuffer() throws IOException {
		this.fileHandle.seek(this.fileHandle.getFilePointer() + (getBufferLength()+1));
		this.isForwardFacing = true;
	}
	
	public long getBufferLength() {
		long length = 0;
		for(String s : buffer)
			length += s.length();
		return length;
	}
	
	public String readNextLine() throws IOException {
		if(!isForwardFacing) {
			moveCursorToEndOfBuffer();
		}
		if(fileHandle.getFilePointer() < fileHandle.length()) {
			
			StringBuilder stringBuilder = new StringBuilder();
			byte[] tempBuffer = new byte[1];
			
			for(; fileHandle.getFilePointer() < fileHandle.length() ;) {
				fileHandle.read(tempBuffer);
				String s = new String(tempBuffer);
				stringBuilder.append(s);
				tempBuffer = new byte[1];
				if(s.contains("\n")) {
					break;
				}
			}
			System.out.println("Next Line: <"+ stringBuilder.toString() +">");
			return stringBuilder.toString();
		}
		else {
			// currently at end of file, cannot read next line
			return null;
		}
	}
	
	public String readPreviousLine() throws IOException {
		if(isForwardFacing) {
			moveCursorToStartOfBuffer();
		}
		if(getCurrentOffset() <= 0) {
			// currently at beginning of file, cannot read previous line
			return null;
		}
		else {
			
			fileHandle.seek(fileHandle.getFilePointer()-1);
			
			StringBuilder stringBuilder = new StringBuilder();
			byte[] tempBuffer = new byte[1];
			int lineCounter = 0;
			for(; fileHandle.getFilePointer() >= 0; fileHandle.seek(fileHandle.getFilePointer()-2)) {
				fileHandle.read(tempBuffer);
				String s = new String(tempBuffer);
				stringBuilder.append(s);
				tempBuffer = new byte[1];
				if(s.contains("\n")) {
					if(firstReverse) {
						firstReverse = false;
						break;
					}
					else if( ++lineCounter == 2) {
						break;
					}
				}
				if(fileHandle.getFilePointer() < 2) {
					break;
				}
			}
			String line = stringBuilder.reverse().toString();
			System.out.println("Previous line: <" + line + ">");
			return line;
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
	
	public void cycleForward() throws IOException {
		if(fileHandle != null) {
			if(buffer.size() == 0) {
				// fill the buffer
				for(;buffer.size() < this.bufferSize;) {
					String line = this.readNextLine();
					if(line != null) {
						buffer.add(line);
					}
					else {
						break;
					}
				}
			}
			else  {
				String line = this.readNextLine();
				if(line != null) {
					buffer.remove(0);
					buffer.add(line);
				}
			}
		}
	}
	
	public void cycleBackward() throws IOException {
		if(fileHandle != null) {
			if(buffer.size() == 0) {
				// fill the buffer
				for(;buffer.size() < this.bufferSize;) {
					String line = this.readNextLine();
					if(line != null) {
						buffer.add(line);
					}
					else {
						break;
					}
				}
			}
			else  {
				String line = this.readPreviousLine();
				if(line != null) {
					buffer.remove(buffer.size()-1);
					buffer.add(0, line);
				}
			}
		}
	}
	
	public int getBufferSize() {
		return this.buffer.size();
	}
	
	public ArrayList<String> getBuffer() {
		return this.buffer;
	}
}