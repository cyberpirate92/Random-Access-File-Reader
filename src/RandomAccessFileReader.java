import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

public class RandomAccessFileReader {
	
	private RandomAccessFile top, bottom;
	private ArrayList<String> buffer;
	private boolean found;
	Scanner input = new Scanner(System.in);
	private static final long DEFAULT_BUFFER_SIZE = 4;
	
	public RandomAccessFileReader(String filename) throws IOException, FileNotFoundException {
		try {
			top = new RandomAccessFile(new File(filename), "r");
			bottom = new RandomAccessFile(new File(filename), "r");
			buffer = new ArrayList<String>();
			
			top.seek(0);
			bottom.seek(0);
			buffer.ensureCapacity(4);
			
			initializeBuffer();
			
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
	
	public String readNextLine(RandomAccessFile pointer) throws IOException {
		return pointer.readLine();
	}
	
	public String readPreviousLine(RandomAccessFile pointer) throws IOException {
		
		pointer.seek(pointer.getFilePointer()-1);
		
		StringBuilder stringBuilder = new StringBuilder();
		int newLineCounter = 0;
		byte[] tempBuffer = new byte[1];
		
		for(; pointer.getFilePointer() > 0; pointer.seek(pointer.getFilePointer()-2)) {
			//printPointers();
			pointer.read(tempBuffer);
			String s = new String(tempBuffer);
			stringBuilder.append(s);
			tempBuffer = new byte[1];
			if(s.contains("\n")) {
				newLineCounter++;
				if(newLineCounter == 2) {
					break;
				}
			}
		}
		
		// corner case
		if(pointer.getFilePointer() == 0) {
			pointer.read(tempBuffer);
			stringBuilder.append(new String(tempBuffer));
			pointer.seek(0);
		}
		
		return stringBuilder.reverse().toString().replace("\n", "").replace("\r", "");
	}
	
	public long getMaxOffset() throws IOException {
		return bottom.length();
	}
	
	public void setOffset(long position) throws IOException{
		if(position >= 0 && position <= getMaxOffset()) {
			this.bottom.seek(position);
		}
	}
	
	public long getCurrentOffset() throws IOException {
		if(bottom != null) {
			return bottom.getFilePointer();
		}
		else {
			throw new NullPointerException("File pointer is null");
		}
	}
	
	public void close() throws IOException {
		if(bottom != null) {
			bottom.close();
		}
	}
	
	public void initializeBuffer() throws IOException {
		while(buffer.size() < RandomAccessFileReader.DEFAULT_BUFFER_SIZE) {
			buffer.add(bottom.readLine());
		}
	}
	
	public void cycleForward() throws IOException {
		if(bottom.getFilePointer() < bottom.length()) {
			readNextLine(top);
			String nextLine = readNextLine(bottom);
			buffer.remove(0);
			buffer.add(nextLine);
		}
		else {
			System.out.println("Reached end of file, cannot read further");
		}
	}
	
	public void cycleBackward() throws IOException {
		if(top.getFilePointer() > 0) {
			readPreviousLine(bottom);
			String previousLine = readPreviousLine(top);
			buffer.remove(buffer.size()-1);		// remove last element
			buffer.add(0, previousLine);		// add previous line to start of buffer
		}
		else {
			System.out.println("Reached start of file, cannot read backward");
		}
	}
	
	public ArrayList<String> getBuffer() {
		return this.buffer;
	}
	
	public long getBufferSize() {
		return this.buffer.size();
	}
	
	// debug method
	public void printPointers() throws IOException {
		System.out.println("TOP			: " + top.getFilePointer());
		System.out.println("BOTTOM		: " + bottom.getFilePointer());
		System.out.println("DIFFERENCE	: " + (bottom.getFilePointer() - top.getFilePointer()));
	}
	
	public void SearchStringForward(String searchString) throws IOException {
		if(!found){
			SearchInBuffer(searchString);
			for(int i=0;i<DEFAULT_BUFFER_SIZE;i++){
				cycleForward();
			}
		}
	}
	
    public void SearchStringBackward(String searchString) throws IOException {
    	if(!found){
			SearchInBuffer(searchString);
			for(int i=0;i<DEFAULT_BUFFER_SIZE;i++){
				cycleBackward();
			}
		}
		
	}
	
    public void SearchInBuffer(String searchString)
    {
    	for(String S:buffer){
			if(S.contains(searchString)){
				System.out.println("String found at line : " +S);
				found=true;
				break;
			}
		}
    }
}
