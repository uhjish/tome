/*
 *      ControlsDialog.java
 *      
 *      Copyright 2008 Ajish D. George <ajish@hocuslocus.com>
 *      
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 2 of the License, or
 *      (at your option) any later version.
 *      
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *      
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *      MA 02110-1301, USA.
 */

package org.omelogic.tome;

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import edu.uci.ics.jung.algorithms.cluster.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.decorators.*;
import edu.uci.ics.jung.graph.filters.*;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.statistics.*;
import edu.uci.ics.jung.utils.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.control.*;
import prefuse.data.Table;
import org.omelogic.tome.histogram.*;
public class ControlsDialog extends JDialog implements ActionListener{


	private static final String title = "_.controls._";

	private JPanel ctrlsPanel;
	private JTextField numCtrlsField;
	private UndirectedSparseGraph tomeGraph;
	private UndirectedSparseGraph backingGraph;
	private int numCtrls;
	private int numBins;
	
	public ControlsDialog(UndirectedSparseGraph atomeGraph, UndirectedSparseGraph abackingGraph, int anumCtrls, int anumBins)
	{
		//this.setModal(true);
		this.setResizable(false);
		ctrlsPanel = new JPanel(new BorderLayout());
		ctrlsPanel.setPreferredSize(new Dimension(600, 800));
		//ctrlsPanel.setMaximumSize(new Dimension(600, 800));
		ctrlsPanel.setLayout(new BoxLayout(ctrlsPanel, BoxLayout.PAGE_AXIS));
		//this.setModal(true);

		tomeGraph = atomeGraph;
		backingGraph = abackingGraph;
		numCtrls = anumCtrls;
		numBins = anumBins;

        JPanel ctrlControls = new JPanel(new FlowLayout());

		numCtrlsField = new JTextField(""+numCtrls, 10);
		JButton runCtrls = new JButton("GENERATE CONTROLS");
		runCtrls.setActionCommand("run");
		runCtrls.addActionListener(this);
        JButton saveCtrls = new JButton("SAVE CONTROL STATS");
   		saveCtrls.setActionCommand("save");
		saveCtrls.addActionListener(this);

		ctrlControls.add(new JLabel("Num. Controls:"));
		ctrlControls.add(numCtrlsField);
		ctrlControls.add(runCtrls);
        ctrlControls.add(saveCtrls);

		this.getContentPane().add(ctrlsPanel, BorderLayout.CENTER);
		this.getContentPane().add(ctrlControls, BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true);
		loadControlStats(tomeGraph, backingGraph, numCtrls, numBins);

	}
	
	public void loadControlStats(UndirectedSparseGraph tomeGraph, UndirectedSparseGraph backingGraph, int numCtrls, int numBins)
	{
		if (((Double)( (HashMap<String, Double>)(tomeGraph.getUserDatum("GraphStatistics"))).get("AveragePathLength")).isInfinite()){
			 JOptionPane.showMessageDialog(null, "AverageDistance of current graph is infinite! Try trimming the graph first...", "ERROR!",JOptionPane.ERROR_MESSAGE);
			 return;
		}
        ctrlsPanel.removeAll();
		ctrlsPanel.add(new JLabel("Calculating ConnectionControls..."));		
		ctrlsPanel.revalidate();
		ctrlsPanel.repaint();
		JOptionPane.showMessageDialog(null, "Calculating Connection Controls...","Please wait",JOptionPane.INFORMATION_MESSAGE);
        System.out.println("Calculating ConnectionControls!");
        try{
        	TomeGraphUtilities.addConnectionControlStats((UndirectedSparseGraph)tomeGraph,numCtrls);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Cannot add ConnectionControls! "+e.toString(),"ERROR!",JOptionPane.ERROR_MESSAGE);
		}
		ctrlsPanel.add(new JLabel("Calculating SelectionControls..."));
		ctrlsPanel.revalidate();
		ctrlsPanel.repaint();
        System.out.println("Calculating SelectionControls!");
		JOptionPane.showMessageDialog(null, "Calculating Selection Controls...","Please wait",JOptionPane.INFORMATION_MESSAGE);
		try{	   
	        TomeGraphUtilities.addSelectionControlStats((UndirectedSparseGraph)tomeGraph, (UndirectedSparseGraph) backingGraph, numCtrls);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Cannot add SelectionControls! "+e.toString(),"ERROR!",JOptionPane.ERROR_MESSAGE);
		}
	        
		ctrlsPanel.add(new JLabel("Done!"));

		JOptionPane.showMessageDialog(null, "Finished Generating Controls...","Please wait",JOptionPane.INFORMATION_MESSAGE);

        ctrlsPanel.removeAll();
        ctrlsPanel.add( new HistogramPanel("CONNECTION CONTROLS", (HashMap<String, Double>)tomeGraph.getUserDatum("GraphStatistics"), (Table)tomeGraph.getUserDatum("ConnectionControls"),numBins));
        ctrlsPanel.add( new HistogramPanel("SELECTION  CONTROLS", (HashMap<String, Double>)tomeGraph.getUserDatum("GraphStatistics"), (Table)tomeGraph.getUserDatum("SelectionControls"),numBins));
		ctrlsPanel.revalidate();
		ctrlsPanel.repaint();

	}

	public void actionPerformed(ActionEvent e)
	{
		if ("run".equals(e.getActionCommand()))
		{
			int newNumCtrls;
			try{
				newNumCtrls = Integer.parseInt(numCtrlsField.getText());
			}catch(Exception xcep){
				 JOptionPane.showMessageDialog(null, "Num Controls must be an integer >0!", "ERROR!",JOptionPane.ERROR_MESSAGE);
				 return;
			}
			if (newNumCtrls == 0){
				 JOptionPane.showMessageDialog(null, "Num Controls must be an integer >0!", "ERROR!",JOptionPane.ERROR_MESSAGE);
			}
			numCtrls = newNumCtrls;
			loadControlStats(tomeGraph, backingGraph, numCtrls, numBins);
				 
				
		}
		if ("save".equals(e.getActionCommand()))
		{
			JFileChooser chooser = new JFileChooser();

			int returnVal = chooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				
				FileOutputStream out; // declare a file output object
				PrintStream p; // declare a print stream object

				try
				{
						// Create a new file output stream
						out = new FileOutputStream(file);

						// Connect print stream to the output stream
						p = new PrintStream( out );

						HashMap<String, Double> graphProps = (HashMap<String, Double>)tomeGraph.getUserDatum("GraphStatistics");
						Table conTable = (Table)tomeGraph.getUserDatum("ConnectionControls");
						Table selTable = (Table)tomeGraph.getUserDatum("SelectionControls");

						p.println("#---SUMMARY---");
						p.println("#NumNodes:\t"+ tomeGraph.numVertices());
						p.println("#NumEdges:\t"+ tomeGraph.numEdges());
						p.println("#ClustCoeff:\t"+graphProps.get(TomeGraphUtilities.CLUSTERING_COEFFICIENT));
						p.println("#AvgPathLen:\t"+graphProps.get(TomeGraphUtilities.AVERAGE_PATH_LENGTH));
						p.println("#AvgDegree:\t"+graphProps.get(TomeGraphUtilities.AVERAGE_DEGREE));
						p.println("#-------------");										
						p.println("#---CONTROLS--");										
						p.println("\n\n");
						p.println("No.\tcon.ClustCoeff\tcon.AvgPathLen\tcon.AvgDegree\tsel.ClustCoeff\tsel.AvgPathLen\tsel.AvgDegree");
						
						for(int ctrlIndex = 0; ctrlIndex < numCtrls; ctrlIndex++)
						{
							
							p.println((ctrlIndex+1)+"\t"+conTable.getDouble(ctrlIndex, TomeGraphUtilities.ControlTableCols.CLUSTERING_COEFFICIENT)+
												"\t"+conTable.getDouble(ctrlIndex, TomeGraphUtilities.ControlTableCols.AVERAGE_PATH_LENGTH)+
												"\t"+conTable.getDouble(ctrlIndex, TomeGraphUtilities.ControlTableCols.AVERAGE_DEGREE)+
												"\t"+selTable.getDouble(ctrlIndex, TomeGraphUtilities.ControlTableCols.CLUSTERING_COEFFICIENT)+
												"\t"+selTable.getDouble(ctrlIndex, TomeGraphUtilities.ControlTableCols.AVERAGE_PATH_LENGTH)+
												"\t"+selTable.getDouble(ctrlIndex, TomeGraphUtilities.ControlTableCols.AVERAGE_DEGREE));
						}

						p.close();
				}
				catch (Exception xcep)
				{
					JOptionPane.showMessageDialog(null, "Error writing to file!\n"+xcep.toString(), "ERROR!",JOptionPane.ERROR_MESSAGE);					
				}
			
			}

		}
	}

}
