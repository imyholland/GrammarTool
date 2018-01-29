/**
 *  Feature (aka constraint) of a 
 *  conditional random field.
 */

package MEGrammarTool;
import MEGrammarTool.*;

public class Feature
{

public String name = "<feature_name>";
public String abbrev = "<feature_name_abbreviation>";
public String type = "<feature_type>";  // Markedness, Faithfulness, ...
public double MU = 0.0;					// 'desired' ranking value (default is 0.0)
public double SIGMA2 = 100000.0;				// variance around 'desired' ranking value (default is 1.0)


public void setName(String n)
{ name = n; }

public void setAbbrev(String a)
{ abbrev = a; }

public void setType(String t)
{ type = t; }

public void setMU(double mu)
{ MU = mu; }

public void setSIGMA2(double s2)
{ SIGMA2 = s2; }

}