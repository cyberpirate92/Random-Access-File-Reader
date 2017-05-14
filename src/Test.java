import java.util.Scanner;

public class Test {
	public static void main(String[] a) {
		try {
			
			RandomAccessFileReader reader = new RandomAccessFileReader("TraceFile.log");
			Scanner input = new Scanner(System.in);
			
			while(true) {
				long offset;
				
				System.out.println("File Operation Tester");
				System.out.println("---------------------");
				System.out.println("1 | Read Next Line");
				System.out.println("2 | Previous Line");
				System.out.println("3 | Current Offest");
				System.out.println("4 | Goto offset");
				System.out.println("5 | Exit");
				System.out.print("Your choice: ");
				int choice = input.nextInt();
				switch(choice) {
				case 1:
					System.out.println(reader.readNextLine());
					break;
				case 2:
					System.out.println(reader.readPreviousLine());
					break;
				case 3:
					System.out.println(reader.getCurrentOffset());
					break;
				case 4:
					System.out.print("Enter Offset [ Max: " + reader.getMaxOffset() + "] : ");
					offset = input.nextLong();
					reader.setOffset(offset);
					break;
				case 5:
					input.close();
					reader.close();
					System.exit(0);
				default:
					System.out.println("Choose a valid choice");	
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
