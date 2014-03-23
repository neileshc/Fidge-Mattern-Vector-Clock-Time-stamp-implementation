public class SctpStockapp {
	public static int Currentstock;
	public int stocksold;
	public int stockreceived;
	boolean isfinal = false;

	public SctpStockapp() {
		Currentstock = (SctpMain.nodeno) * 10000;
	}

	public int sellStock() {
		stocksold = (SctpMain.nodeno * 10 * (Configfilereader.totalnodes - 1));
		Currentstock = Currentstock - stocksold;

		// returns the stocks that need to sell to another node
		return (stocksold / (Configfilereader.totalnodes - 1));
	}

	public void buyStock(int receivedstock) {
		Currentstock = Currentstock + receivedstock;
	}

}
