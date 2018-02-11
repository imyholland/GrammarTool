package MEGrammarTool.neutralisation;

import java.util.ArrayList;
import java.util.List;

public class NeutralisationItem {

	public List<Outcome> outcomes = new ArrayList<>();
	public double neutPenalty;

	@Override
	public String toString() {
		return "NeutralisationItem{" +
				"outcomes=" + outcomes +
				", neutPenalty=" + neutPenalty +
				'}';
	}
}
