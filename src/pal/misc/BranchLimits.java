// BranchLimits.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


package pal.misc;


/**
 * limits for branch lengths
 *
 * @version $Id: BranchLimits.java,v 1.11 2002/04/27 23:58:28 matt Exp $
 *
 * @author Korbinian Strimmer
 */
public interface BranchLimits
{
	//
	// Public stuff
	//

	/** minimum branch length */
	double MINARC = 1.0e-9;

	/** maximum branch length */
	double MAXARC = 1.0;

	/** default branch length */
	double DEFAULT_LENGTH = 0.04;

	/** maximum tolerated error when determining branch lengths */
	double ABSTOL = 5.0e-07;

	/** desired fractional digits when determining branch lengths */
	int FRACDIGITS = 6;
}
