// StateRemover.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)

package pal.datatype;

/**
 * A standard data type, but with characters removed
 *
 *  @version $Id: StateRemover.java,v 1.14 2003/03/23 00:04:23 matt Exp $
 *
 *  @author Matthew Goode
 */
public class StateRemover extends SimpleDataType implements java.io.Serializable {
	DataType toAdjust_;

	int[] originalToAdjusted_; /** A translation array -> originalToAdjusted_["original State"] = "adjusted state" */
	int[] adjustedToOriginal_; /** A translation array -> originalToAdjusted_["adjusted State"] = "original state" */

	public StateRemover(DataType toAdjust, int[] statesToRemove) {
		this.toAdjust_ = toAdjust;
		createTranslationTables(toAdjust.getNumStates(),statesToRemove);
	}

	private final void createTranslationTables(int numberOfOriginalStates, int[] statesToRemove) {
		originalToAdjusted_ = new int[numberOfOriginalStates];
		adjustedToOriginal_ = new int[numberOfOriginalStates-statesToRemove.length];
		int currentAdjustedState = 0;
		for(int i = 0 ; i < numberOfOriginalStates ; i++ ) {
			boolean removeState = false;
			for(int j = 0 ; j < statesToRemove.length ; j++) {
				if(statesToRemove[j] == i) {
					removeState = true;
					break;
				}
			}
			if(removeState) {
				originalToAdjusted_[i] = adjustedToOriginal_.length;
			} else {
				originalToAdjusted_[i] = currentAdjustedState;
				adjustedToOriginal_[currentAdjustedState] = i;
				currentAdjustedState++;
			}
		}
	}

	// Get number of bases
	public int getNumStates()	{
		return adjustedToOriginal_.length;
	}

	/**
		* @retrun true if this state is an unknown state
		*/
	protected final boolean isUnknownStateImpl(final int state) {
		return(state>=adjustedToOriginal_.length)||state<0;
	}


	protected int getStateImpl(char c)	{
		int unadjustedState = toAdjust_.getState(c);
		if(!toAdjust_.isUnknownState(unadjustedState)) {
			return originalToAdjusted_[unadjustedState]; /* May also return unknown, see constructor*/
		}
		return adjustedToOriginal_.length;
	}

	/**
	 * Get character corresponding to a given state
	 */
	protected char getCharImpl(final int state)
	{
		if(state>adjustedToOriginal_.length) {
			return UNKNOWN_CHARACTER;
		}
		return toAdjust_.getChar(adjustedToOriginal_[state]);
	}


	// String describing the data type
	public String getDescription()
	{
		return toAdjust_.getDescription()+" with states removed";
	}

	// Get numerical code describing the data type
	public int getTypeID()
	{
		return toAdjust_.getTypeID();
	}

}
