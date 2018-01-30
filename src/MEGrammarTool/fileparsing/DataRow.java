package MEGrammarTool.fileparsing;

public class DataRow {

	private String[] split;

	public DataRow(String line, FileFormat f) {
		split = line.split(f.getSeparator());
	}

	public String getString(int i) {
		return split[i];
	}

	public int size() {
		return split.length;
	}
}
