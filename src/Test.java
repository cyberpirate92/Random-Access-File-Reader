import java.util.Scanner;

public class Test {
	public static void main(String[] a) {
		try {
			
			RandomAccessFileReader reader = new RandomAccessFileReader("sample.txt");
			Scanner input = new Scanner(System.in);
			
			while(true) {
				System.out.println("File Operation Tester");
				System.out.println("---------------------");
				System.out.println("3 | Current Offest");
				System.out.println("4 | Cycle Forward");
				System.out.println("5 | Cycle Backward");
				System.out.println("6 | View Current Buffer");
				System.out.println("7 | Exit");
				System.out.println("8 | Search");
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
					System.out.println("\n\n########################  BUFFER CONTENTS ########################");
					for(String line : reader.getBuffer()) {
						System.out.println(line.replace("\n", "").replace("\r", ""));
					}
					System.out.println("####################################################################");
					System.out.println("Buffer Size : " + reader.getBufferSize());
					System.out.println("\n");
					break;
				case 7:
					input.close();
					reader.close();
					System.exit(0);
					break;
				case 8:
					System.out.println("Enter Search String");
					String searchString=input.nextLine();
					System.out.println("select one type of search 1.Forward 2.Backward");
					int ch = input.nextInt();
					if(ch == 1)
					{
						
						System.out.println("forward searching");
						reader.SearchStringForward(searchString);
					}
					else
					{
						
						System.out.println("backward searching");
						reader.SearchStringBackward(searchString);
					}
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
}
