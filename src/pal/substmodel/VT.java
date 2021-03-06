// VT.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


package pal.substmodel;

import pal.misc.*;
import pal.util.*;
import java.io.*;


/**
 * VT (variable time matrix) model of amino acid evolution
 * <i>Modeling Amino Acid Replacement  Mueller, T. and Vingron, M., 2000. Journal of Computational Biology, 7(6):761-776.</i>
 *
 * @version $Id: VT.java,v 1.7 2003/11/13 04:05:39 matt Exp $
 *
 * @author Korbinian Strimmer
 * @author Alexei Drummond
 */
public class VT extends AminoAcidModel implements XMLConstants
{
	/**
	 * constructor
	 *
	 * @param f amino acid frequencies
	 */
	public VT(double[] f)
	{
		super(f);

	}

	// Get numerical code describing the model type
	public int getModelID()
	{
		return 4;
	}

	public void report(PrintWriter out)
	{
		out.println("Model of substitution: VT (Mueller-Vingron 2000)");
		out.println();
		printFrequencies(out);
	}

	/**
	 * get the frequencies of the original data set that
	 * formed the basis for the estimation of the rate matrix
	 *
	 * @param f array where amino acid frequencies will be stored
	 */
	public static void getOriginalFrequencies(double[] f)
	{
		f[0]=0.078837 ;
		f[1]=0.051238 ;
		f[2]=0.042313 ;
		f[3]=0.053066 ;
		f[4]=0.015175 ;
		f[5]=0.036713 ;
		f[6]=0.061924 ;
		f[7]=0.070852 ;
		f[8]=0.023082 ;
		f[9]=0.062056 ;
		f[10]=0.096371 ;
		f[11]=0.057324 ;
		f[12]=0.023771 ;
		f[13]=0.043296 ;
		f[14]=0.043911 ;
		f[15]=0.063403 ;
		f[16]=0.055897 ;
		f[17]=0.013272 ;
		f[18]=0.034399 ;
		f[19]=0.073101 ;
	}
	/**
	 * @return the frequencies of the original data set that
	 * formed the basis for the estimation of the rate matrix
	 */
	public static double[] getOriginalFrequencies() {
		double[] f = new double[20];
		getOriginalFrequencies(f);
		return f;
	}

	public String getUniqueName() {
		return VT;
	}

	//
	// Private stuff
	//

	// VT model of amino acid evolution
	// Mueller, T. and Vingron, M. 2000. J. Comp. Biol.?:?-?.
	protected void rebuildRateMatrix(double[][] rate, double[] parameters)
	{
		// Q matrix
		rate[0][1] = 0.233108 ; rate[0][2] = 0.199097 ;
		rate[0][3] = 0.265145 ; rate[0][4] = 0.227333 ;
		rate[0][5] = 0.310084 ; rate[0][6] = 0.567957 ;
		rate[0][7] = 0.876213 ; rate[0][8] = 0.078692 ;
		rate[0][9] = 0.222972 ; rate[0][10] = 0.424630 ;
		rate[0][11] = 0.393245 ; rate[0][12] = 0.211550 ;
		rate[0][13] = 0.116646 ; rate[0][14] = 0.399143 ;
		rate[0][15] = 1.817198 ; rate[0][16] = 0.877877 ;
		rate[0][17] = 0.030309 ; rate[0][18] = 0.087061 ;
		rate[0][19] = 1.230985 ;

		rate[1][2] = 0.210797 ; rate[1][3] = 0.105191 ;
		rate[1][4] = 0.031726 ; rate[1][5] = 0.493763 ;
		rate[1][6] = 0.255240 ; rate[1][7] = 0.156945 ;
		rate[1][8] = 0.213164 ; rate[1][9] = 0.081510 ;
		rate[1][10] = 0.192364 ; rate[1][11] = 1.755838 ;
		rate[1][12] = 0.087930 ; rate[1][13] = 0.042569 ;
		rate[1][14] = 0.128480 ; rate[1][15] = 0.292327 ;
		rate[1][16] = 0.204109 ; rate[1][17] = 0.046417 ;
		rate[1][18] = 0.097010 ; rate[1][19] = 0.113146 ;

		rate[2][3] = 0.883422 ; rate[2][4] = 0.027495 ;
		rate[2][5] = 0.275700 ; rate[2][6] = 0.270417 ;
		rate[2][7] = 0.362028 ; rate[2][8] = 0.290006 ;
		rate[2][9] = 0.087225 ; rate[2][10] = 0.069245 ;
		rate[2][11] = 0.503060 ; rate[2][12] = 0.057420 ;
		rate[2][13] = 0.039769 ; rate[2][14] = 0.083956 ;
		rate[2][15] = 0.847049 ; rate[2][16] = 0.471268 ;
		rate[2][17] = 0.010459 ; rate[2][18] = 0.093268 ;
		rate[2][19] = 0.049824 ;

		rate[3][4] = 0.010313 ; rate[3][5] = 0.205842 ;
		rate[3][6] = 1.599461 ; rate[3][7] = 0.311718 ;
		rate[3][8] = 0.134252 ; rate[3][9] = 0.011720 ;
		rate[3][10] = 0.060863 ; rate[3][11] = 0.261101 ;
		rate[3][12] = 0.012182 ; rate[3][13] = 0.016577 ;
		rate[3][14] = 0.160063 ; rate[3][15] = 0.461519 ;
		rate[3][16] = 0.178197 ; rate[3][17] = 0.011393 ;
		rate[3][18] = 0.051664 ; rate[3][19] = 0.048769 ;

		rate[4][5] = 0.004315 ; rate[4][6] = 0.005321 ;
		rate[4][7] = 0.050876 ; rate[4][8] = 0.016695 ;
		rate[4][9] = 0.046398 ; rate[4][10] = 0.091709 ;
		rate[4][11] = 0.004067 ; rate[4][12] = 0.023690 ;
		rate[4][13] = 0.051127 ; rate[4][14] = 0.011137 ;
		rate[4][15] = 0.175270 ; rate[4][16] = 0.079511 ;
		rate[4][17] = 0.007732 ; rate[4][18] = 0.042823 ;
		rate[4][19] = 0.163831 ;

		rate[5][6] = 0.960976 ; rate[5][7] = 0.128660 ;
		rate[5][8] = 0.315521 ; rate[5][9] = 0.054602 ;
		rate[5][10] = 0.243530 ; rate[5][11] = 0.738208 ;
		rate[5][12] = 0.120801 ; rate[5][13] = 0.026235 ;
		rate[5][14] = 0.156570 ; rate[5][15] = 0.358017 ;
		rate[5][16] = 0.248992 ; rate[5][17] = 0.021248 ;
		rate[5][18] = 0.062544 ; rate[5][19] = 0.112027 ;

		rate[6][7] = 0.250447 ; rate[6][8] = 0.104458 ;
		rate[6][9] = 0.046589 ; rate[6][10] = 0.151924 ;
		rate[6][11] = 0.888630 ; rate[6][12] = 0.058643 ;
		rate[6][13] = 0.028168 ; rate[6][14] = 0.205134 ;
		rate[6][15] = 0.406035 ; rate[6][16] = 0.321028 ;
		rate[6][17] = 0.018844 ; rate[6][18] = 0.055200 ;
		rate[6][19] = 0.205868 ;

		rate[7][8] = 0.058131 ; rate[7][9] = 0.051089 ;
		rate[7][10] = 0.087056 ; rate[7][11] = 0.193243 ;
		rate[7][12] = 0.046560 ; rate[7][13] = 0.050143 ;
		rate[7][14] = 0.124492 ; rate[7][15] = 0.612843 ;
		rate[7][16] = 0.136266 ; rate[7][17] = 0.023990 ;
		rate[7][18] = 0.037568 ; rate[7][19] = 0.082579 ;

		rate[8][9] = 0.020039 ; rate[8][10] = 0.103552 ;
		rate[8][11] = 0.153323 ; rate[8][12] = 0.021157 ;
		rate[8][13] = 0.079807 ; rate[8][14] = 0.078892 ;
		rate[8][15] = 0.167406 ; rate[8][16] = 0.101117 ;
		rate[8][17] = 0.020009 ; rate[8][18] = 0.286027 ;
		rate[8][19] = 0.068575 ;

		rate[9][10] = 2.089890 ; rate[9][11] = 0.093181 ;
		rate[9][12] = 0.493845 ; rate[9][13] = 0.321020 ;
		rate[9][14] = 0.054797 ; rate[9][15] = 0.081567 ;
		rate[9][16] = 0.376588 ; rate[9][17] = 0.034954 ;
		rate[9][18] = 0.086237 ; rate[9][19] = 3.654430 ;

		rate[10][11] = 0.201204 ; rate[10][12] = 1.105667 ;
		rate[10][13] = 0.946499 ; rate[10][14] = 0.169784 ;
		rate[10][15] = 0.214977 ; rate[10][16] = 0.243227 ;
		rate[10][17] = 0.083439 ; rate[10][18] = 0.189842 ;
		rate[10][19] = 1.337571 ;

		rate[11][12] = 0.096474 ; rate[11][13] = 0.038261 ;
		rate[11][14] = 0.212302 ; rate[11][15] = 0.400072 ;
		rate[11][16] = 0.446646 ; rate[11][17] = 0.023321 ;
		rate[11][18] = 0.068689 ; rate[11][19] = 0.144587 ;

		rate[12][13] = 0.173052 ; rate[12][14] = 0.010363 ;
		rate[12][15] = 0.090515 ; rate[12][16] = 0.184609 ;
		rate[12][17] = 0.022019 ; rate[12][18] = 0.073223 ;
		rate[12][19] = 0.307309 ;

		rate[13][14] = 0.042564 ; rate[13][15] = 0.138119 ;
		rate[13][16] = 0.085870 ; rate[13][17] = 0.128050 ;
		rate[13][18] = 0.898663 ; rate[13][19] = 0.247329 ;

		rate[14][15] = 0.430431 ; rate[14][16] = 0.207143 ;
		rate[14][17] = 0.014584 ; rate[14][18] = 0.032043 ;
		rate[14][19] = 0.129315 ;

		rate[15][16] = 1.767766 ; rate[15][17] = 0.035933 ;
		rate[15][18] = 0.121979 ; rate[15][19] = 0.127700 ;

		rate[16][17] = 0.020437 ; rate[16][18] = 0.094617 ;
		rate[16][19] = 0.740372 ;

		rate[17][18] = 0.124746 ; rate[17][19] = 0.022134 ;

		rate[18][19] = 0.125733 ;
	}
}
