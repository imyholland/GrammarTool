package MEGrammarTool;

import MEGrammarTool.fileparsing.DataFile;
import MEGrammarTool.fileparsing.DataRow;
import MEGrammarTool.fileparsing.TabFormat;
import cern.colt.list.ObjectArrayList;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import pal.math.ConjugateGradientSearch;
import pal.math.MinimiserMonitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Vector;



public class Main extends JPanel implements ActionListener
{
    JButton tableauxSelector, constraintSelector, targetSelector, runButton;
    JLabel tableauxSourceName, constraintSourceName, targetName, statusReport;
    File targetFile;
    JFileChooser fcIn, fcOut;
	public HashMap<String,Feature> featureNameMap;
	public Vector<Feature> featureSpecs;
    public ProvisionalCRF CRF;
	public GaussianRegularizer REG;
    DataSet trainingData;
    Feature[] constraints;
	public static double TOLERANCE = 1.0e-10;

    public Main(Container thepane)
    {

        thepane.setLayout(new GridBagLayout());
        fcIn = new JFileChooser();
        fcOut = new JFileChooser();
		tableauxSourceName = new JLabel("[none]");
        constraintSourceName = new JLabel("[none]");
        targetName = new JLabel("[none]");
        tableauxSelector = new JButton("open tableaux");
        tableauxSelector.addActionListener(this);
		constraintSelector = new JButton("open constraints");
        constraintSelector.addActionListener(this);
        targetSelector = new JButton("select output file");
        targetSelector.addActionListener(this);
		runButton = new JButton("Learn and Report");
		runButton.addActionListener(this);
		statusReport = new JLabel("[news will appear here]");

		thepane.add(tableauxSelector,
			new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets( 3, 3, 3,  28 ), 0, 0 ));
		thepane.add(new JLabel("Tableaux From:"),
			new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets( 3, 3, 3,  3 ), 0, 0 ));
		thepane.add(tableauxSourceName,
			new GridBagConstraints( 0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 3, 3, 3,  3 ), 0, 0 ));
		thepane.add(constraintSelector,
			new GridBagConstraints( 3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets( 3, 3, 3,  28 ), 0, 0 ));
		thepane.add(new JLabel("Constraint Data From:"),
			new GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets( 3, 3, 3,  3 ), 0, 0 ));
		thepane.add(constraintSourceName,
			new GridBagConstraints( 2, 1, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 3, 3, 3,  3 ), 0, 0 ));
		thepane.add(targetSelector,
			new GridBagConstraints( 5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets( 3, 3, 3,  28 ), 0, 0 ));
		thepane.add(new JLabel("Save Output To:"),
			new GridBagConstraints( 4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets( 3, 3, 3,  3 ), 0, 0 ));
		thepane.add(targetName,
			new GridBagConstraints( 4, 1, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 3, 3, 3,  3 ), 0, 0 ));
		thepane.add(runButton,
			new GridBagConstraints( 0, 2, 6, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 3, 3, 3,  3 ), 0, 0 ));
		thepane.add(statusReport,
			new GridBagConstraints( 0, 3, 6, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 3, 3, 3,  3 ), 0, 0 ));
		constraintSourceName.setText("[Default Values]");
	}

    public void actionPerformed(ActionEvent e)
	{

        if (e.getSource() == tableauxSelector)
		{
			statusReport.setText("please select a tableaux source file.");
            int returnVal = fcIn.showOpenDialog(Main.this);

            if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				statusReport.setText("attempting to open selected file.");
                File file = fcIn.getSelectedFile();
				try{
					tableauxSourceName.setText(file.getName());
					REG = new GaussianRegularizer();
					CRF = new ProvisionalCRF(REG);
					featureNameMap = new HashMap<String,Feature>();
					readTableaux(file,CRF,featureNameMap);
					tableauxSourceName.setText(file.getName());
					statusReport.setText("successfully opened " + file.getName());
				}catch(Exception exc)
				{
					System.out.print("unable to read tableaux from " + file.getName() + "\n");
					tableauxSourceName.setText("[none]");
					trainingData = null;
					statusReport.setText("error occurred attempting to open file.");
				}
			}
			else
			{
				statusReport.setText("tableaux file selection canceled.");
			}
        }
		else if(e.getSource() == constraintSelector)
		{
			statusReport.setText("please select a constraint source file.");
            int returnVal = fcIn.showOpenDialog(Main.this);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				statusReport.setText("attempting to open selected file.");
                File file = fcIn.getSelectedFile();
				featureSpecs = new Vector<Feature>();
				try{
					readFeatureSpecs(file,featureSpecs);
					constraintSourceName.setText(file.getName());
					statusReport.setText("successfully opened " + file.getName());
				}catch(Exception exc)
				{
					System.out.print("unable to read constraint data from " + file.getName() + "\n");
					System.out.println(exc);
					constraintSourceName.setText("[Default Values]");
					featureSpecs = null;
					statusReport.setText("error occurred attempting to open file.");
				}
			}
			else
			{
				statusReport.setText("constraint file selection canceled.");
			}
        }
		else if(e.getSource() == targetSelector)
		{
		    statusReport.setText("please select output file.");
            int returnVal = fcOut.showSaveDialog(Main.this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = fcOut.getSelectedFile();
				targetName.setText(file.getName());
				targetFile = file;
				statusReport.setText("output file set to " + file.getName());
            }
			else
			{
				statusReport.setText("output file selection canceled.");
			}
        }
		else if (e.getSource() == runButton)
		{
		    boolean runokay = true;
		    statusReport.setText("running model...");
			if(CRF == null) {badNews("run aborted: no training data.");}
            //else if(constraints == null) {badNews("run aborted: no constraints.");}
            else
			{
				boolean readytogo = false;
				FileOutputStream fo = null;
				PrintStream textout = null;
				try{
					fo = new FileOutputStream(targetFile);
					textout = new PrintStream(fo);
					readytogo = true;
				}catch(Exception exc){badNews("run aborted: file access error."); runokay = false; statusReport.setText("run aborted: file access error");}

				if(readytogo)
				{
					CRF.printMe(textout);

					try{
						if(featureSpecs != null)
						{
							Feature thefeature, wholeconstraint;
							for(int i = 0; i < featureSpecs.size() ; i++)
							{
								thefeature = featureSpecs.get(i);
								if(featureNameMap.containsKey(thefeature.abbrev))
								{
									wholeconstraint = featureNameMap.get(thefeature.abbrev);
									wholeconstraint.setMU(thefeature.MU);
									wholeconstraint.setSIGMA2(thefeature.SIGMA2);
									textout.println("feature " + thefeature.abbrev + " had user-specified MU = " + thefeature.MU + " and SIGMA2 = " + thefeature.SIGMA2 + ".");
								}
								else
								{
									textout.println("feature " + thefeature.abbrev + " was not found in the tableaux, so its values from the feature file were ignored.");
								}
							}
							textout.println("all other features retained default MU and SIGMA2.");
							textout.println();
						}
					}catch(Exception exc){badNews("failure incorporating information from constraint file."); runokay = false; statusReport.setText("error incorporating constraint information");}

					try{
						optimize(textout);
						printPredictedProbabilities(textout);
					}catch(Exception exc){badNews("failure during main learning and output segment."); runokay = false; statusReport.setText("error encountered in learning or output.");}
				}


				if(fo != null) try{
					textout.close();
					fo.close();
				}catch(Exception exc){badNews("issues closing output file."); runokay = false; statusReport.setText("error closing output file.");}

				if(runokay) statusReport.setText("learning results written successfully.");
			}
		}
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.

		JFrame frame = new JFrame("MaxEnt Grammar Tool");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Add content to the window.
        frame.add(new Main(frame.getContentPane()));
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }


private void printPredictedProbabilities( PrintStream outputTarget)
{
	ObjectArrayList outputProbs = CRF.outputProbabilities(CRF.lambda);
	outputTarget.println("Input:\tCandidate:\tObserved:\tPredicted:");
	for(int j = 0; j < CRF.mydata.size(); j++)
	{
		String[] cands_j = ((OTData) CRF.mydata.get(j)).candidateNames;
		DoubleMatrix1D outputProbs_j = (DoubleMatrix1D) outputProbs.get(j);

		for(int i = 0; i < cands_j.length; i++)
		{
			outputTarget.println(((OTData) CRF.mydata.get(j)).inputForm +
								"\t" + cands_j[i] +
								"\t" + ((OTData) CRF.mydata.get(j)).frequencies[i] +
								"\t" + outputProbs_j.get(i));
		}
	}
}


/*private void printTableaux(PrintStream outputTarget)
{
	/*
	ObjectArrayList outputViolns = CRF.evaluateTableaux(CRF.features, D);
	ObjectArrayList outputProbs = CRF.outputProbabilities(CRF.features, CRF.lambda, D);
	for(int j = 0; j < D.tableaux.size(); j++)
	{
		String[] tableau_j = (String[]) D.tableaux.get(j);
		DoubleMatrix2D  violations_j = (DoubleMatrix2D) outputViolns.get(j);
		DoubleMatrix1D outputProbs_j = (DoubleMatrix1D) outputProbs.get(j);

		outputTarget.println();
		outputTarget.print("Input: " + tableau_j[0]);
		for(int k = 0; k < CRF.features.length; k++)
		{ outputTarget.print("\t" + ((MarkednessFeature) CRF.features[k]).pattern.pattern()); }
		outputTarget.println();
		for(int i = 1; i < tableau_j.length; i++)
		{
			outputTarget.print(tableau_j[i] + "\t");
			for(int k = 0; k < CRF.features.length; k++)
			{ outputTarget.print(violations_j.get(i-1,k) + "\t"); }
			outputTarget.println();
		}
	}

}*/


private void optimize(PrintStream outputTarget)
{
	for(int k = 0; k < CRF.lambda.length; k++)
		{ CRF.lambda[k] = -5.0; }
	ConjugateGradientSearch cg = new ConjugateGradientSearch(2);
	cg.prin = 0;
	cg.optimize(CRF,
				CRF.lambda,
				TOLERANCE,
				TOLERANCE,
				new MinimiserMonitor.Utils().createNullMonitor());

	outputTarget.println();
	outputTarget.println("|weights| after optimization:");
	for(int k = 0; k < CRF.features.length; k++)
	{ outputTarget.println(CRF.features[k].name
		+ " (mu=" + CRF.features[k].MU
		+ ", sigma^2=" + REG.sigma2(CRF.features[k])
		+ ")\t" + Math.abs(CRF.lambda[k])); }
}

private static void readFeatureSpecs(File f, Vector<Feature> fs) throws Exception
{
	DataFile read = DataFile.createReader("8859_1");
	read.setDataFormat(new TabFormat());
	read.open(f);
	DataRow filerow = read.next();
	int i = 0;
	while(filerow != null)
	{
		Feature freshfeature = new Feature();
		freshfeature.setAbbrev(filerow.getString(0));
		freshfeature.setMU( (new Double(filerow.getString(1))).doubleValue() );
		freshfeature.setSIGMA2( (new Double(filerow.getString(2))).doubleValue() );
		fs.add(freshfeature);
		filerow = read.next();
		i = i+1;
		if(i == 4) filerow = null;
	}
	read.close();
}

private static void readTableaux(File f, ProvisionalCRF CRF, HashMap<String,Feature> featureNameMap) throws Exception
{
	DataFile read = DataFile.createReader("8859_1");
	read.setDataFormat(new TabFormat());
    int numconstraints;
	ObjectArrayList tableaux = new ObjectArrayList();
	read.open( f );
	DataRow longnames = read.next();
	DataRow shortnames = read.next();
	numconstraints = longnames.size()-3;
	Feature[] featureArray = new Feature[numconstraints];
	for(int i = 0; i < numconstraints; i++)
	{
		featureArray[i] = new Feature();
		featureArray[i].setAbbrev(shortnames.getString(i+3));
		featureArray[i].setName(longnames.getString(i+3));
		try{
			featureNameMap.put( featureArray[i].abbrev , featureArray[i]);
		}
		catch( Exception exc )
		{
			System.out.println("fuck this");// what
		}
	}
	DataRow filerow;
	filerow = read.next();
	String firstcolval = filerow.getString(0);
	if(firstcolval.length() == 0) throw new Exception("no input for first candidate batch");
	while(filerow != null)
	{
		OTData freshdata = new OTData();
		freshdata.inputForm = firstcolval;
		Vector<String> candidatebatch = new Vector<String>();
		Vector<double[]> violationbatch = new Vector<double[]>();
		Vector<Double> frequencybatch = new Vector<Double>();
		do
		{
			String candidatename = filerow.getString(1);
			String freqstring = filerow.getString(2);
                    //get frequency
			String violationsbuffer;
			double[] violations = new double[numconstraints];
			for(int i = 0; i < numconstraints; i++)
			{
				violationsbuffer = filerow.getString(i+3);
				violations[i] = (violationsbuffer.length()==0)?0:( new Double(violationsbuffer)).doubleValue();
			}
			filerow = read.next();
			violationbatch.add(violations);
			candidatebatch.add(candidatename);
			Double freqval = (freqstring.length()==0)?(new Double(0)):(new Double(freqstring));
			frequencybatch.add(freqval);
		}
		while(filerow!=null && (firstcolval = filerow.getString(0)).length()==0);
		freshdata.candidateNames = new String[candidatebatch.size()];
		freshdata.frequencies = new double[candidatebatch.size()];
		double[][] violationtable = new double[candidatebatch.size()][];
		for(int j = 0; j < candidatebatch.size(); j++)
		{
			freshdata.frequencies[j] = (frequencybatch.elementAt(j)).doubleValue();
			freshdata.candidateNames[j] = candidatebatch.elementAt(j);
			violationtable[j] = violationbatch.elementAt(j);
		}
		DoubleMatrix2D vmatrix = DoubleFactory2D.dense.make(violationtable);
		freshdata.violations = vmatrix;
		tableaux.add(freshdata);
	}


	CRF.setMyData(tableaux);
	CRF.features = featureArray;
	CRF.lambda = new double[featureArray.length];

	read.close();
/*
	for(int j = 0; j < tableaux.size(); j++)
	{
		OTData OTData_j = (OTData) tableaux.get(j);
		String input_j = OTData_j.inputForm;
		String[] outputs_j = OTData_j.candidateNames;
		double[] freqs_j = OTData_j.frequencies;
		DoubleMatrix2D violations_j = OTData_j.violations;
		PrintStream outputTarget = System.out;
		outputTarget.println();
		outputTarget.print("Input: " + input_j);
		for(int k = 0; k < numconstraints; k++)
		{ outputTarget.print("\t" + featureArray[k].abbrev); }
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
*/

}

private void badNews(String s)
{
	System.out.println(s);
}


    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }
}
