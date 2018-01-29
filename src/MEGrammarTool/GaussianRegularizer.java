/**
 *  Gaussian regularizer (Chen \& Rosenfeld) 
 *  that uses the (possibly unique) MUs and SIGMA2s
 *  that are specified within each feature.
 */

package MEGrammarTool;
import MEGrammarTool.*;

public class GaussianRegularizer implements Regularizer
{

public double value(Feature[] features, double[] weights)
{
	double val = 0.0;
	for(int k = weights.length-1; k >= 0; k--)
	{ val += (Math.pow((weights[k]-features[k].MU),2)/features[k].SIGMA2); }
	return val;
}

public double[] gradient(Feature[] features, double[] weights)
{
	double[] grad = new double[weights.length];
	for(int k = (weights.length-1); k >= 0; k--)
	{ grad[k] = ((2 * (weights[k]-features[k].MU))/features[k].SIGMA2); }
	return grad;
}

public double sigma2(Feature feature)
{ return feature.SIGMA2; }

}//GaussianRegularizer