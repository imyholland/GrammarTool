/**
 *  Class for conditional random fields
 *  (see, for example, Goldwater \& Johnson 2003)
 */

package MEGrammarTool;
import MEGrammarTool.*;
import pal.math.*;
import cern.colt.list.*;
import cern.colt.matrix.*;
import cern.colt.matrix.linalg.*;
import cern.colt.function.*;
import cern.jet.math.*;
import java.io.*;

public class ProvisionalCRF implements MFWithGradient
//public class ProvisionalCRF implements MultivariateFunction   // approx. of gradient
{
Regularizer regularizer;	// regularizer over features, weights
Feature[] features;			// features (aka constraints)
double[] lambda;			// weights

ObjectArrayList mydata;	// list of tableaux

// matrix methods and functions for convenience
Algebra alg;				// cern.col.linalg.Algebra
DoubleFunction exp = cern.jet.math.Functions.bindArg1(
					cern.jet.math.Functions.pow, Math.E);
DoubleDoubleFunction mult = cern.jet.math.Functions.mult;
DoubleDoubleFunction div = cern.jet.math.Functions.div;
DoubleDoubleFunction plus = cern.jet.math.Functions.plus;
DoubleDoubleFunction minus = cern.jet.math.Functions.minus;
DoubleFunction neg = cern.jet.math.Functions.neg; // f(x) == -x
	
/**
 * Default constructor
 */
ProvisionalCRF(Regularizer r)
{
	regularizer = r;
	features = null;
	lambda = null;
	mydata = null;
	alg = new cern.colt.matrix.linalg.Algebra();
}


public void setMyData(ObjectArrayList L)
{
	mydata = L;
}


/**
 *  Creates a tableaux (list of DoubleMatrix2D objects) 
 *  by evaluating each tableaux in data set D with each
 *  of the features in this field.
 */
/*public ObjectArrayList evaluateTableaux(Feature[] features, DataSet D)
*{
*	int numberOfTableaux = D.tableaux.size();
*	String[] tableau_j = null;
*	double[][] violations_j = null;
*	
*	ObjectArrayList tblx = new ObjectArrayList();
*
*	for(int j = 0; j < numberOfTableaux; j++)
*	{
*		tableau_j = (String[]) D.tableaux.get(j);
*
*		// tableau columns are initially rows (one per constraint)
*		violations_j = new double[features.length][tableau_j.length-1];
*		for(int k = 0; k < features.length; k++)
*		{ violations_j[k] = features[k].evaluate(tableau_j); }
*	
*		// place tableau in a DoubleMatrix2D container, and transpose
*		DoubleMatrix2D tbl_j = DoubleFactory2D.dense.make(violations_j);
*		tbl_j = alg.transpose(tbl_j);
*		tblx.add(tbl_j);
*	}
*	return tblx;
*}
*/


/**
 *  Given a list of Features, a weight vector, and a 
 *  tableaux (list of DoubleMatrix2D objects), returns 
 *  a list of DoubleMatrix1D objects: the output 
 *  probabilities for the tableaux given the Features
 *  and their weights.
 */
public ObjectArrayList outputProbabilities(double[] weights)
{
	ObjectArrayList outputProbs = new ObjectArrayList();
	DoubleMatrix1D W = DoubleFactory1D.dense.make(weights);
	DoubleMatrix2D tableau_j;
	DoubleMatrix1D cands_j;
	double Z_j = 0.0;

	for(int j = 0; j < mydata.size(); j++)
	{
		tableau_j = ( (OTData) mydata.get(j) ).violations;
		
		// unnormalized probabilities
		cands_j = alg.mult(tableau_j, W);
		cands_j.assign(exp);
		
		// partition function
		Z_j = cands_j.zSum();
		
		// normalized probabilities
		cands_j.assign(cern.jet.math.Functions.bindArg2(div,Z_j));
		
		outputProbs.add(cands_j);
	}

	return outputProbs;
}


/************************************************/
/* Methods required to implement MFWithGradient */
/************************************************/

public int getNumArguments()
{ return features.length; }

public double getLowerBound(int arg)
{ return Double.NEGATIVE_INFINITY; }

public double getUpperBound(int arg)
{ return 0.0; }


/**
 *  Compute the negative log conditional likelihood 
 *  (i.e., neg log pseudo-likelihood) with regularizer.
 *  @param weights vector of feature weights
 *  @return neg log conditional likelihood + regularizer
 *  <p>
 *  Presupposes:
 *  o DoubleMatrix3D tableaux,
 *
 *  o int[] winners, where winners[j] is the 
 *  row in tableaux.viewSlice(j) that contains
 *  the observed winner for tableau j, and
 *
 *  o int[] count, where count[j] is the
 *  number of times tableau j occurs in 
 *  the data set.
 */
public double evaluate(double[] weights)
{
	double[] freqs_j;
	double logPL = 0.0;
	DoubleMatrix2D tableau_j;   // individual tableau (one input,
								// multiple outputs)
	double logPL_j = 0.0;		// contribution of tableau_j
								// to the total pseudo-likelihood

	DoubleMatrix1D W = DoubleFactory1D.dense.make(weights);

	for(int j = 0; j < mydata.size(); j++)
	{
		tableau_j = ( (OTData) mydata.get(j) ).violations;
		freqs_j = ( (OTData) mydata.get(j) ).frequencies;
		logPL_j = 0.0;
		
		for(int k = 0; k < freqs_j.length; k ++)
		{
			if( freqs_j[k] !=0)
			{
				logPL_j = 0.0;
	
				// add (W . features(y_j, x_j))
				logPL_j += alg.mult(W, tableau_j.viewRow(k)); //do weights instead of winners here add loop BEN FIX THIS like below with fuck loop on k and multiply by freq
		
		
		
				// subtract log(sum_y exp(W . features(y,x_j)))
				DoubleMatrix1D Z = alg.mult(tableau_j, W);
				Z.assign(exp);
				logPL_j -= Math.log(Z.zSum());
		
				// add logPL_j once for every 
				// occurrences of tableau_j
				logPL += (freqs_j[k] * logPL_j);
			}
		}
	}
	
	double negLogPL = -logPL;

	// add the value of the regularizer
	double objective = negLogPL + regularizer.value(features, weights);
	
//	System.out.println("objective= " + objective);
	return objective;
}

/**
 *  Compute the negative log conditional likelihood
 *  (i.e., neg log pseudo-likelihood) with regularizer,
 *  and compute the gradient.
 *  @param weights vector of feature weights
 *  @param gradient gradient vector
 *  @return neg log conditional likelihood + regularizer
 */
public double evaluate(double[] weights, double[] gradient)
{
	computeGradient(weights, gradient);
	return evaluate(weights);
}

public void printMe(PrintStream outputTarget)
{
	for(int j = 0; j < mydata.size(); j++)
	{
		int numconstraints = features.length;
		OTData OTData_j = (OTData) mydata.get(j);
		String input_j = OTData_j.inputForm;
		String[] outputs_j = OTData_j.candidateNames;
		double[] freqs_j = OTData_j.frequencies;
		DoubleMatrix2D violations_j = OTData_j.violations;
		outputTarget.println();
		outputTarget.print("Input: " + input_j);
		for(int k = 0; k < numconstraints; k++)
		{ outputTarget.print("\t" + features[k].abbrev); }
		outputTarget.println();
		for(int i = 0; i < outputs_j.length; i++)
		{
			outputTarget.print(outputs_j[i] + "\t");
			outputTarget.print("" + freqs_j[i] + "\t");
			for(int k = 0; k < numconstraints; k++)
			{ outputTarget.print(violations_j.get(i,k) + "\t"); }
			outputTarget.println();
		}
	}
}

/**
 *  Compute the gradient of the neg log conditional 
 *  likelihood (i.e., of the neg log pseudo-likelihood)
 *  with regularizer.
 *  @param weights vector of feature weights
 *  @param gradient gradient vector
 *  @return void
 */
public void computeGradient(double[] weights, double[] gradient)
{
	DoubleMatrix1D W = DoubleFactory1D.dense.make(weights);
	DoubleMatrix1D grad = DoubleFactory1D.dense.make(gradient.length);
	DoubleMatrix1D grad_j = DoubleFactory1D.dense.make(gradient.length);
	DoubleMatrix2D tableau_j;
	double[] freqs_j;
	double Z_j;
	
	for(int j = 0; j < mydata.size(); j++)
	{
		tableau_j = ( (OTData) mydata.get(j) ).violations;
		freqs_j = ( (OTData) mydata.get(j) ).frequencies;
		for(int k = 0; k < freqs_j.length; k ++)
		{
			if(freqs_j[k] !=0)
			{
				// add f_k(y_j,x) for each feature k
				// this is *log(observed)* violations of f_k
				grad_j = tableau_j.viewRow(k).copy(); //BEN FUCK THIS IS HARD UM ... ooh! just add 'em together. should work. another loop um... view each row and multiply
	
				// partition function: Z_j <- sum_y exp(W . features(y,x_j))
				DoubleMatrix1D cands = alg.mult(tableau_j, W);
				cands.assign(exp);
				Z_j = cands.zSum();
		
				// sum_y [ exp(W . features(y,x_j)) * f_k(y,x_j)] for each k 
				cands = alg.mult(alg.transpose(tableau_j), cands);
				// divide each term by the partition function Z_j
				// this is *log(expected)* violations of f_k
				cands.assign(cern.jet.math.Functions.bindArg2(div,Z_j));

				// log(observed) - log(expected) (i.e., log(O/E))
				grad_j.assign(cands, minus);

				// multiply grad_j by the number 
				// of occurences of tableau_j
				grad_j.assign(cern.jet.math.Functions.bindArg1(mult,(double) freqs_j[k]));

				grad.assign(grad_j, plus);
			}
		}
	}
	
	grad.assign(neg);

	// subtract the gradient of the regularizer 
	grad.assign(
		DoubleFactory1D.dense.make(
			regularizer.gradient(features, weights)),
		plus);

	System.arraycopy(grad.toArray(), 0, gradient, 0, gradient.length);
}

}//ProvisionalCRF