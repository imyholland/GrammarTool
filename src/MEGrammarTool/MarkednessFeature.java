package MEGrammarTool; /**
 *  Markedness feature (aka constraint) of a 
 *  conditional random field.
 */

import java.util.regex.*;

public class MarkednessFeature extends Feature
{

public String name = "MarkednessFeature";
public String type = "Markedness";
public double MU = 0.0;				// 'desired' ranking value (default is 0.0)
public double SIGMA2 = 1.0;			// variance around 'desired' ranking value (default is 1.0)

public Pattern pattern;
public Matcher matcher;

/**
 *  Evaluate a single (input,output) pair with this feature. 
 *  Because this is a markedness feature, the input component 
 *  is ignored by evaluation (and could be null).
 *  @param input irrelevant input component
 *  @param output output component, the object of evaluation
 *  @return the number of 'violations' incurred by the output
 */
public double evaluate(String input, String output)
{
	double violations = 0.0;
	
	matcher.reset(output);
	while(matcher.find())
	{ violations++; }

	return violations;
}

}