import java.util.ArrayList;
import java.util.Scanner;

public class Test {
	public static void main(String[] a) {
		try {
			
			ArrayList<String> result;
			RandomAccessFileReader reader = new RandomAccessFileReader("sample2.txt");
			Scanner input = new Scanner(System.in);
			String searchTerm;
			
			while(true) {
				System.out.println("File Operation Tester");
				System.out.println("---------------------");
				System.out.println("3 | Current Offest");
				System.out.println("4 | Cycle Forward");
				System.out.println("5 | Cycle Backward");
				System.out.println("6 | View Current Buffer");
				System.out.println("7 | Search Forward");
				System.out.println("8 | Search Backward");
				System.out.println("9 | Exit");
				System.out.print("Your choice: ");
				int choice = input.nextInt();
				switch(choice) {
				case 3:
					System.out.println(reader.getCurrentOffset());
					break;
				case 4:
					reader.cycleForward();
					break;
				case 5:
					reader.cycleBackward();
					break;
				case 6:
					displayBuffer(reader.getBuffer());
					break;
				case 7:
					System.out.print("Search term: ");
					input.nextLine();
					searchTerm = input.nextLine();
					result = reader.searchForward(searchTerm); 
					if(result == null) {
						System.out.println("Search term not found");
					}
					else {
						displayBuffer(result);
					}
					break;
				case 8:
					System.out.print("Search term: ");
					input.nextLine();
					searchTerm = input.nextLine();
					result = reader.searchBackward(searchTerm);
					if(result == null) {
						System.out.println("Not Found");
					}
					else {
						displayBuffer(result);
					}
					break;
				case 9:
					input.close();
					reader.close();
					System.exit(0);
					break;
				
				default:
					System.out.println("Choose a valid choice");	
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void displayBuffer(ArrayList<String> buffer) {
		System.out.println("\n\n########################  BUFFER CONTENTS ########################");
		for(String line : buffer) {
			System.out.println(line.replace("\n", "").replace("\r", ""));
		}
		System.out.println("####################################################################");
		System.out.println("Buffer Size : " + buffer.size());
		System.out.println("\n");
	}
}
