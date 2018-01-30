package MEGrammarTool.fileparsing;

import java.util.Arrays;

public class DataRow {

	private String[] split;

	public DataRow(String line, FileFormat f) {
		if (line == null || f == null) {
			System.out.println("one param was null");
		}
		split = new String[17];
		String[] lineParts = line.split(f.getSeparator());
		for (int i=0; i < split.length; i++) {
			if (i < lineParts.length)
				split[i] = lineParts[i];
			else
				split[i] = "";
		}
	}

	public String getString(int i) {
		return split[i];
	}

	public int size() {
		return split.length;
	}
}
