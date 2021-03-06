// CoalescentIntervals.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)

 
package pal.coalescent;

import pal.misc.*;
import pal.tree.*;
import pal.util.*;
import pal.io.*;

import java.util.*;
import java.io.*;


/**
 * A series of coalescent intervals representing the time
 * order information contained in a (serial) clock-constrained
 * tree. Can model both n-coalescents and s-coalescents.
 *
 * @version $Id: CoalescentIntervals.java,v 1.14 2001/08/04 19:57:31 alexi Exp $
 *
 * @author Alexei Drummond
 * @author Korbinian Strimmer
 */
public class CoalescentIntervals implements Units, Report, Serializable
{

	// PUBLIC STUFF

	/** Denotes and interval after which a coalescent event is observed
	  * (i.e. the number of lineages is smaller in the next interval) */
	public static final int COALESCENT = 0;

	/** 
	 * Denotes an interval at the end of which a new sample addition is 
	 * observed (i.e. the number of lineages is larger in the next interval).
	 */
	public static final int NEW_SAMPLE = 1;
	
	/** 
	 * Denotes an interval at the end of which nothing is
	 * observed (i.e. the number of lineages is the same in the next interval).
	 */
	public static final int NOTHING = 2;
	
	

	/** The widths of the intervals. */
	private double[] intervals;

	/** The number of uncoalesced lineages within a particular interval. */
	private int[] numLineages;

	/**
	 * Parameterless constructor.
	 */
	public CoalescentIntervals()
	{
		units = GENERATIONS;
		
		fo = FormattedOutput.getInstance();
	}

	/**
	 * Constructor taking a number of intervals.
	 */
	public CoalescentIntervals(int size)
	{
		this();
		
		intervals = new double[size];
		numLineages = new int[size];
	}

	/** The units in which the intervals are expressed. */
	private int units;

	/**
	 * Sets the units these coalescent intervals are 
	 * measured in.
	 */
	public void setUnits(int u)
	{
		units = u;
	}

	/**
	 * Returns the units these coalescent intervals are 
	 * measured in.
	 */
	public int getUnits()
	{
		return units;
	}
	
	/**
	 * Returns the number of uncoalesced lineages within this interval.
	 * Required for s-coalescents, where new lineages are added as
	 * earlier samples are come across.
	 */
	public int getNumLineages(int i) {
		return numLineages[i];
	}

	/**
	 * set the number lineages for this particular interval.
	 */
	public void setNumLineages(int i, int numLines) {
		numLineages[i] = numLines;
	}


	/**
	 * Returns the number coalescent events in an interval
	 */
	public int getCoalescentEvents(int i)
	{
		if (i < intervals.length-1)
		{
			return numLineages[i]-numLineages[i+1];
		}
		else
		{
			return numLineages[i]-1;
		}
	}

	/**
	 * Returns the type of interval observed.
	 */
	public int getIntervalType(int i)
	{
		int numEvents = getCoalescentEvents(i);
		
		if (numEvents > 0) return COALESCENT;
		else if (numEvents < 0) return NEW_SAMPLE;
		else return NOTHING;
	}

	/**
	 * Gets an interval.
	 */
	public double getInterval(int i) {
		return intervals[i];
	}

	/**
	 * Sets interval.
	 */
	public void setInterval(int i, double value) {
		intervals[i] = value;
	}

	/**
	 * get the total height of the genealogy represented by these
	 * intervals. 
	 */
	public double getTotalHeight() {
		
		double height=0.0;
		for (int j=0; j < intervals.length; j++) {
			height += intervals[j];
		}
		return height;
	}


	/**
	 * get number of intervals
	 */
	public int getIntervalCount()
	{
		return intervals.length;
	}


	/**
	 * Checks whether this set of coalescent intervals is fully resolved
	 * (i.e. whether is has exactly one coalescent event in each
	 * subsequent interval)
	 */
	public boolean isBinaryCoalescent()
	{
		for (int i = 0; i < intervals.length; i++)
		{
			if (getCoalescentEvents(i) != 1) return false; 
		}
		
		return true;
	}

	/**
	 * Checks whether this set of coalescent intervals coalescent only
	 * (i.e. whether is has exactly one or more coalescent event in each
	 * subsequent interval)
	 */
	public boolean isCoalescentOnly()
	{
		for (int i = 0; i < intervals.length; i++)
		{
			if (getCoalescentEvents(i) < 1) return false; 
		}
		
		return true;
	}


	/**
	 * Group intervals following a given (compatible) reference.
	 * The reference must have the same number of lineages at
	 * the start of the first interval, and the present
	 * CoalsecentIntervals must be fully resolved. 
	 */
	public void groupIntervals(CoalescentIntervals reference)
	{
		if (!isBinaryCoalescent())
		{
			throw new IllegalArgumentException("CoalescentIntervals must purely consist of only single coalescents");
		}
		
		if (getNumLineages(0) != reference.getNumLineages(0))
		{
			throw new IllegalArgumentException("Incompatible reference CoalescentIntervals");
		}
		
		int refSize = reference.getIntervalCount();
		
		double[] newIntervals = new double[refSize];
		int[] newNumLineages = new int[refSize];
		
		int count = 0;
		for (int i = 0; i < refSize; i++)
		{
			newNumLineages[i] = reference.getNumLineages(i);
			
			int numEvents = reference.getCoalescentEvents(i);
			for (int j = 0; j < numEvents; j++)
			{
				newIntervals[i] += intervals[count];
				count++;
			}
		}
		
		intervals = newIntervals;
		numLineages = newNumLineages;
	}

	/**
	 * Returns a list stating which of the intervals are <= minSize
	 * (and thus should be pooled).
	 */
	public void getSmallIntervals(double minSize, boolean[] smallInterval)
	{
		if (intervals.length != smallInterval.length)
			throw new IllegalArgumentException("Array length incompatible");
		
		for (int i = 0; i < intervals.length; i++)
		{
			if (intervals[i] > minSize)
			{
				smallInterval[i] = false;
			}
			else
			{
				smallInterval[i] = true;
			}
		}
	}


	/**
	 * Starting at time zero (i.e. with the interval with largest number of lineages),
	 * the specified small intervals are pooled with the next non-small interval
	 * (if this does not exist then with the previous non-small interval)
	 */
	public void poolIntervals(boolean[] smallInterval)
	{
		int uniqueIntervals = 0;
		for (int i = 0; i < intervals.length; i++)
		{
			if (smallInterval[i] == false) uniqueIntervals++;
		}
		if (uniqueIntervals == 0) uniqueIntervals = 1;
		
		double[] newIntervals = new double[uniqueIntervals];
		int[] newNumLineages = new int[uniqueIntervals];
		
		int count = 0;
		int coalescences = 0;
		int numLines = numLineages[0];
		for (int i = 0; i < intervals.length; i++)
		{
			if (i < intervals.length-1)
			{
				coalescences += numLineages[i]-numLineages[i+1];
			}
			else
			{
				coalescences += numLineages[i]-1;
			}
						
			newIntervals[count] = intervals[i] + newIntervals[count];
			newNumLineages[count] = numLines;
			
			if (smallInterval[i] == false)
			{
				count++;
				if (count == uniqueIntervals) count--;
				numLines = numLines - coalescences;
		
				coalescences = 0;
			}
		}
		
		intervals = newIntervals;
		numLineages = newNumLineages;
	}


	/**
	 * Starting at time zero (i.e. with the interval with largest number of lineages),
	 * small intervals (<= minSize) are pooled with the next non-small interval
	 * (if this does not exist then with the previous non-small interval)
	 */
	public void poolSmallIntervals(double minSize)
	{
		boolean[] smallInterval = new boolean[intervals.length];
		getSmallIntervals(minSize, smallInterval);
		poolIntervals(smallInterval);
	}	


	/** 
	 * Returns the log likelihood of this set of coalescent intervals, 
	 * given a demographic model.
	 */
	public double computeLogLikelihood(DemographicModel model) {
	
		double total=0.0;
		double currentTime = 0.0;
		double intervalVal = 0.0;
	
		try {
	
			for (int j = 0; j < intervals.length; j++)
			{		
				total += model.computeLogLikelihood(intervals[j], currentTime, numLineages[j],
				getIntervalType(j));
			
				// insert zero-length coalescent intervals
				int diff = getCoalescentEvents(j)-1;
				for (int k = 0; k < diff; k++)
				{
					total += model.computeLogLikelihood(0.0, currentTime, numLineages[j]-k-1, COALESCENT);
				}
			
				currentTime += intervals[j]; 
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			System.out.println(e);
			System.out.println(this);
		}
		return total;
	}

	public String toString()
	{
		OutputTarget out = OutputTarget.openString();

		out.println("Lin.\tCoal.\tSize\tTotal");
		double total = 0.0;
		
		for (int i = 0; i < intervals.length; i++)
		{		
			total += intervals[i];
			
			out.print(numLineages[i] + "\t");
			out.print(getCoalescentEvents(i) + "\t");
			
			fo.displayDecimal(out, intervals[i], 5);
			out.print("\t");
			fo.displayDecimal(out, total, 5);
			out.println();
		}
		out.close();
		
		return out.getString();
	}
	
	public void report(PrintWriter out)
	{
		out.println(this);
	}
	
	//
	// private stuff
	//
	
	private FormattedOutput fo;
}
