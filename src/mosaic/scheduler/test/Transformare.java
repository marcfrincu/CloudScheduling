package mosaic.scheduler.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;

public class Transformare {

	public static void transform(FisServer input) {
		try {
			LineNumberReader in = new LineNumberReader(new FileReader(
					input.getSourceFileName()));
			PrintWriter out = new PrintWriter(new FileWriter(
					input.getResultFileName()));
			String line = "";
			long val = 0;
			int serverSize = input.getServerSize();
			long noServers = 0;
			while ((line = in.readLine()) != null) {
				val = Long.parseLong(line) * 100;
				noServers = val / serverSize;
				if (val % serverSize == 0) {
					out.write(noServers + "\n");
				} else {
					out.write((noServers + 1) + "\n");
				}
			}
			in.close();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		/*FisServer medium = new FisServer("fisEx/medium.txt", 125, "fisRez/medium.txt");
		transform(medium);
		FisServer medium2 = new FisServer("fisEx/medium2.txt", 261, "fisRez/medium2.txt");
		transform(medium2);
		FisServer large = new FisServer("fisEx/large.txt", 143, "fisRez/large.txt");
		transform(large);
		FisServer small4 = new FisServer("fisEx/small.txt", 4, "fisRez/small4.txt");
		transform(small4);
		*/
		FisServer small42 = new FisServer("fisEx/small.txt", 42, "fisEx/small42.txt");
		transform(small42);
	}
}

class FisServer {
	private String sourceFileName;
	private int serverSize;
	private String resultFileName;

	
	public FisServer(String sourceFileName, int serverSize,
			String resultFileName) {
		super();
		this.sourceFileName = sourceFileName;
		this.serverSize = serverSize;
		this.resultFileName = resultFileName;
	}
	public int getServerSize() {
		return serverSize;
	}

	public void setServerSize(int serverSize) {
		this.serverSize = serverSize;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public String getResultFileName() {
		return resultFileName;
	}

	public void setResultFileName(String resultFileName) {
		this.resultFileName = resultFileName;
	}

	@Override
	public String toString() {
		return "FisServer [sourceFileName=" + sourceFileName + ", serverSize="
				+ serverSize + ", resultFileName=" + resultFileName + "]";
	}
}
