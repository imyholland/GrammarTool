/**
 *  Container class for a list of tableaux.
 *  Conventions:
 *  Each tableau T is represented as a String[], 
 *  where T[0] is the input, and T[k], 1 <= k < T.length
 *  are the candidates.
 */

package MEGrammarTool;
import MEGrammarTool.*;
import cern.colt.list.*;

public class DataSet
{
public ObjectArrayList tableaux;	// list of String[]s
public IntArrayList winners;		// indicates which candidate (row) is the desired winner in each tableau
public DoubleArrayList counts;		// number of occurrences of each tableau in the data set

DataSet()
{
	tableaux = new ObjectArrayList();
	winners = new IntArrayList();
	counts = new DoubleArrayList();
}

public void addTableau(String[] T, double N, int W)
{
	tableaux.add(T);
	winners.add(W);
	counts.add(N);
}

}