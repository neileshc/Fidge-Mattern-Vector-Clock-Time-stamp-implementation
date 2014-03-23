import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SctpMain {
	public static int msgexch = 0;
	public static SctpVectorClock sv;
	public static SctpMessage sm;
	public static SctpStockapp sa;
	public static SctpServer s1;
	public static int nodeno;
	public static BufferedReader br = new BufferedReader(new InputStreamReader(
			System.in));
	
	public static void main(String args[]) throws InterruptedException,
			IOException {

		nodeno = Integer.parseInt(args[0]);
		Configfilereader r = new Configfilereader();
		r.readfile();
		sa = new SctpStockapp();

		System.out
				.println("\n\n***** Stock trading network.....*****\n This application runs on differnt nodes treating \n them as different stock accounts \n Every node sells some amount of stocks \n to other nodes and in also receives \n stocks from other nodes \n");
		System.out.println("Initial Stock options node " + nodeno + " has :"
				+ sa.Currentstock);

		sm = new SctpMessage(nodeno);
		sv = new SctpVectorClock();

		SctpServer s1 = new SctpServer(nodeno);
		Thread t1 = new Thread(s1);
		t1.start();
		
		Thread.sleep(1000);

		System.out
		.println("Press enter to start Stock Application...Make sure your all node servers are UP");
		br.readLine();
		
		SctpClient c1 = new SctpClient(nodeno);
		Thread t2 = new Thread(c1);
		t2.start();

		t1.join();
		t2.join();

		System.out.println("---------------------------------");
		System.out.println("Total Message exchanged by node " + nodeno + " : "
				+ msgexch);
		System.out.println("Current Stocks node :" + nodeno + " has "
				+ sa.Currentstock);
		
			
		System.out.println("Vector Clock state after termination");
		
		for (int i = 0; i < Configfilereader.totalnodes; i++) {
			System.out.print(sv.currentvectorClock[i] + "\t");
			
		}
		
		System.out.println();
	}
}
