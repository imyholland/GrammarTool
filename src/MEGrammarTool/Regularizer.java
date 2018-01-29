/**
 *  Interface for regularizers (priors) 
 *  on Conditional Random Fields.
 */

package MEGrammarTool;
import MEGrammarTool.*;
import MEGrammarTool.*;

public interface Regularizer
{
	public double MU = 0.0;			//  default weight
	public double SIGMA2 = 1.0;		//  variance

	public double value(Feature[] features, double[] weights);
	
	public double[] gradient(Feature[] features, double[] weights);
	
	public double sigma2(Feature feature);

}