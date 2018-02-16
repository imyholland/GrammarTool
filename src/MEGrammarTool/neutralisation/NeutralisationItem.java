package MEGrammarTool.neutralisation;

import java.util.ArrayList;
import java.util.List;

public class NeutralisationItem {
	//needs to be a list so you can have things like p;v and b;v in the same NeutralisationItem, but you can also have just p;0
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
