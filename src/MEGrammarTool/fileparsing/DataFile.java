package MEGrammarTool.fileparsing;

import java.io.*;

public class DataFile {

	private FileFormat format;
	private BufferedReader bufferedReader;
	private FileReader fileReader;

	public static DataFile createReader(String encoding) {
		// TODO not sure what to do with the encoding yet
		return new DataFile();
	}

	public void setDataFormat(FileFormat f) {
		this.format = f;
	}

	public void open(File f) throws FileNotFoundException {
		fileReader = new FileReader(f);
		bufferedReader = new BufferedReader(fileReader);
	}

	public DataRow next() throws IOException {
		String nextLine = bufferedReader.readLine();
		return new DataRow(nextLine, format);
	}

	public void close() throws IOException {
		bufferedReader.close();
		fileReader.close();
	}
}
