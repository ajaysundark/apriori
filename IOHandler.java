import java.io.*;
import java.util.*;

public class IOHandler {
	public static void main(String[] args) {
		if (args.length!=2) {
			System.err.println("Incorrect Usage");
		}

		
		try {
			String input = args[0];
			String output = args[1];
			File in = new File(input);
			Scanner scanner = new Scanner(in);
			File of = new File(output);
			PrintWriter pw = new PrintWriter(of);
			int prevTransId, transId, itemId;
			prevTransId= 0;
			StringBuilder sb = new StringBuilder();
			while(scanner.hasNext()) {
				if (!scanner.hasNext()) {
	                break;
	            }
	            transId = scanner.nextInt();
	            itemId = scanner.nextInt();

	            if (prevTransId==transId) {
	            	sb.append(itemId);
	            	sb.append(',');
	            } else {
	            	sb.setLength(sb.length()-1);
	            	pw.println(sb.toString());
	            	sb.setLength(0);
	            	prevTransId=transId;

	            	sb.append(itemId);
	            	sb.append(',');
	            }
			}

			sb.setLength(sb.length()-1);
	        pw.println(sb.toString());

			scanner.close();
			pw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}