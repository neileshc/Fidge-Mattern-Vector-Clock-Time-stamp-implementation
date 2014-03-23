import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;

public class SctpServer extends Thread {
	static Integer SERVER_PORT;
	public static int mynodeno;
	public static final int MESSAGE_SIZE = 10000;
	public int msgsent = 0;
	public int[] servervectorclock = new int[Configfilereader.totalnodes];
	public ArrayList<SctpChannel> sc = new ArrayList<>();
	public boolean isfinal = false;
	
	
	public SctpServer(int nodeno) {
		// Initializing Server Port details
		SERVER_PORT = Configfilereader.Machineport[(nodeno - 1)];
		mynodeno = nodeno;

		for (int i = 0; i < Configfilereader.totalnodes; i++) {
			servervectorclock[i] = 0;
		}
	}

	public void run() {

		try {
			// Opening server Channel
			SctpServerChannel ssc = SctpServerChannel.open();

			// Create socket address
			InetSocketAddress serverAddr = new InetSocketAddress(SERVER_PORT);

			// Bind the channel's socket to the server
			ssc.bind(serverAddr);
			System.out.println("Setting up the distributed network .....");
			System.out.println("Server UP for node : " + mynodeno
					+ " at Port number: " + SERVER_PORT);

			// Server runs in loop for accepting connections from clients
			do {
				// Returns a new SCTPChannel between the server and client
				sc.add(ssc.accept());
				System.out.println("connection accepted from another node");

				// Break the loop once you have all the clients connected
				if (sc.size() == (Configfilereader.totalnodes - 1))
					break;

			} while (true);

			// Allow some time to server to accept connections
			System.out
			.println("Node Setup Completed , Preparing network to send messages.....");
			this.sleep(3000);
			

			int i = 0;
			for (i = 0; i < Configfilereader.numberofmessages; i++) // Loop
																	// decides
																	// how many
																	// messages
																	// to send.
			{
				SctpMain.sv.incrementVectorClock(mynodeno); // increment the
															// clock
				servervectorclock = SctpMain.sv.getCurrentvectorClock();
				SctpMain.sm.setmsgid(1); // increment message id no

				// code for internal events
				if(i>0 & (i%Configfilereader.timeforinternalmsg)==0)
				{
					System.out.println("\n\n Internal event (self stock correction) occured ");
					for (int k = 0; k < Configfilereader.totalnodes; k++) {
						System.out.print(SctpMain.sv.currentvectorClock[k] + "\t");
						//System.out.println();
					}
				}
								
				for (int j = 0; j < sc.size(); j++) {
					// call for send message method
					isfinal = false;
					
					SendMsg(sc.get(j), isfinal, SctpMain.sa.sellStock());
					// this.sleep(mynodeno*10);
				}
				this.sleep(mynodeno * 50); // sleep time to keep delay between
											// the send messages
			}

			this.sleep(3000); // sleep time so that client gets enough time to
								// receive updates from other server and update
								// clock value

			// Code for termination message and clock updates
			{
				SctpMain.sv.incrementVectorClock(mynodeno); // increment the
															// clock
				servervectorclock = SctpMain.sv.getCurrentvectorClock();
				SctpMain.sm.setmsgid(1); // increment message id no
				isfinal = true;
				for (int j = 0; j < sc.size(); j++) {
					// call for send message method
					// SctpMain.sv.incrementVectorClock(mynodeno); // increment
					// the clock
					SendMsg(sc.get(j), isfinal, 0);
					// this.sleep(mynodeno*2);
				}
			}
			this.sleep(3000);
			SctpMain.msgexch = SctpMain.msgexch + ((i + 1));
			// this.sleep(20000);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void SendMsg(SctpChannel sc, boolean isfinal, int stockexchange) {
		// Buffer to hold messages in byte format
		ByteBuffer buf = ByteBuffer.allocate(MESSAGE_SIZE);
		try {
			if (isfinal == false) {
				SctpMain.sm.setContent("\n\nTransaction initiated from Machine : "+Configfilereader.Machinename[(mynodeno - 1)] +"(Port : "
						+ SERVER_PORT + ")\n Message ID : ");

			} else {
				SctpMain.sm.setContent("\n\nTermination message from : "
						+ SERVER_PORT + "\n Message ID : ");
			}
			SctpMain.sm.setStockexchange(stockexchange);
			SctpMain.sm.setVectorClock(servervectorclock);

			// Before sending messages add additional information about the
			// message
			MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);

			buf.clear();

			// convert the string message into bytes and put it in the byte
			// buffer
			buf.put(SctpMessage.serialize(SctpMain.sm));

			// Reset a pointer to point to the start of buffer
			buf.flip();

			// Send a message in the channel (byte format)
			sc.send(buf, messageInfo);
			// System.out.println("Server" + newmsg.getContent());

			buf.clear();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}