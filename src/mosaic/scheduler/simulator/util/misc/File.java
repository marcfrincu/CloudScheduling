package mosaic.scheduler.simulator.util.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Class for offering simple read/write functionality
 * @author Marc Frincu
 *
 */
public class File {

	/**
	 * Reads the contents of a text file and returns an ArrayList where each element is a line in the file 
	 * @param fileName
	 * @return the ArrayList of lines found in the file
	 * @throws Exception
	 */
	public static ArrayList<String> readFile(String fileName) throws Exception {
		FileInputStream fstream = new FileInputStream(fileName);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		ArrayList<String> output = new ArrayList<String>();

		while ((strLine = br.readLine()) != null) {
			output.add(strLine);
		}

		in.close();

		return output;
	}

	/**
	 * Writes the <i>content</i> to the specified <i>fileName</i>
	 * @param fileName
	 * @param content
	 * @throws Exception
	 */
	public static void writeFile(String fileName, StringBuffer content)
			throws Exception {
		FileOutputStream fstream = new FileOutputStream(fileName);
		DataOutputStream out = new DataOutputStream(fstream);
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(out));
		output.write(content.toString());
		output.close();
	}

}
