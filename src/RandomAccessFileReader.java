import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileReader {
	
	private RandomAccessFile fileHandle;
	
	public RandomAccessFileReader(String filename) throws IOException, FileNotFoundException {
		try {
			fileHandle = new RandomAccessFile(new File(filename), "r");
			fileHandle.seek(0);
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
		if(getCurrentOffset() <= 0) {
			// currently at beginning of file, cannot read previous line
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
}
