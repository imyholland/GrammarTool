package MEGrammarTool.fileparsing;

public class DataRow {

	private String[] split;

	public DataRow(String line, FileFormat f) {
		if (line == null || f == null) {
			System.out.println("one param was null");
		}
		split = line.split(f.getSeparator(), -1);
	}

	public String getString(int i) {
		return split[i];
	}

	public int size() {
		return split.length;
	}
}
