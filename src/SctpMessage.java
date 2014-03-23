import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SctpMessage implements Serializable {
	private String content;
	private int msgid;
	private int[] vectorClock = new int[Configfilereader.totalnodes];
	private int stockexchange = 0;

	public SctpMessage(int nodeno) {
		content = "Initialized message";
		msgid = (nodeno * 1000); // Generates message ID
		stockexchange = 0;

		// Initialize Vector clock to 0
		for (int i = 0; i < Configfilereader.totalnodes; i++) {
			vectorClock[i] = 0;
		}
	}

	public int[] getVectorClock() {
		return vectorClock;
	}

	public int getStockexchange() {
		return stockexchange;
	}

	public void setStockexchange(int stockexchange) {
		this.stockexchange = stockexchange;
	}

	public void setVectorClock(int[] vectorClock) {
		this.vectorClock = vectorClock;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getmsgid() {
		return msgid;
	}

	// You call this method from server when new message is sent
	public void setmsgid(int x) {
		this.msgid = this.msgid + x;
	}

	public static byte[] serialize(Object obj) throws IOException {
		ObjectOutputStream out;// = new ObjectOutputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		out = new ObjectOutputStream(bos);
		out.writeObject(obj);
		return bos.toByteArray();
	}

	public static Object deserialize(byte[] obj) throws IOException,
			ClassNotFoundException {
		ObjectInputStream in;// = new ObjectOutputStream();
		ByteArrayInputStream bos = new ByteArrayInputStream(obj);
		in = new ObjectInputStream(bos);
		return in.readObject();
	}

}
