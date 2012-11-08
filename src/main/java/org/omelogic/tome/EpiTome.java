/*
 *      EpiTome.java
 *      
 *      Copyright 2007 Ajish George <ajish@hocuslocus.com>
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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.io.*;
import java.lang.Object;
import java.lang.String;
import java.util.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.filechooser.*;

import org.apache.commons.collections.Predicate;

import edu.uci.ics.jung.algorithms.cluster.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.decorators.*;
import edu.uci.ics.jung.graph.filters.*;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.statistics.*;
import edu.uci.ics.jung.utils.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.control.*;

import org.omelogic.tome.histogram.*;

public class EpiTome extends JApplet {

	public static final int NUM_CONTROLS = 100;
	public static final int NUM_BINS_MAX = 100;

	private UndirectedSparseGraph backingGraph;
    private UndirectedSparseGraph tomeGraph;
    //private List<UndirectedSparseGraph> subClusters;
    private TomeEdgeIDFilter idFilter;
    private TomeTableModel tableModel;
    private JFrame tomeFrame;
    private JTable tomeTable;
    private JLabel tomeProps;
    
    private int initialNumNodes;
    private int initialNumEdges;
    
    public EpiTome() {
    	tomeFrame = new JFrame("T o M E | h o c u s l o c u s");
        tomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		init();    	
        tomeFrame.pack();
        tomeFrame.setVisible(true);

	}
	

	public void loadGraphStats()
	{
		if (tomeGraph.numEdges() == 0){
			return;
		}
		//calculate graph statistics
        System.out.println("Calculating GraphStats!");
        TomeGraphUtilities.addGraphStatistics((UndirectedSparseGraph)tomeGraph);
        System.out.println("numEdges: " + tomeGraph.numEdges());
        System.out.println("numVertices: " + tomeGraph.numVertices());
		HashMap<String, Double> graphProps = (HashMap<String,Double>)tomeGraph.getUserDatum("GraphStatistics");
		DecimalFormat fieldFormat = new DecimalFormat(": ##.###");
		tomeProps.setText("<html><code>GRAPH AVG PROPERTIES"
							+"<br>NumVertices: "+tomeGraph.numVertices()
							+"<br>NumEdges&nbsp;&nbsp;&nbsp;&nbsp;: "+tomeGraph.numEdges()
							+"<br>ClustCoeff "+fieldFormat.format(graphProps.get(TomeGraphUtilities.CLUSTERING_COEFFICIENT))
							+"<br>AvgPathLen "+fieldFormat.format(graphProps.get(TomeGraphUtilities.AVERAGE_PATH_LENGTH))
							+"<br>AvgDegree&nbsp;&nbsp;&nbsp;"+ fieldFormat.format(graphProps.get(TomeGraphUtilities.AVERAGE_DEGREE))
							+"</code></html>");
		//load stats into JTable
		tableModel = new TomeTableModel( tomeGraph.getVertices().toArray() );
		tomeTable.setModel(new TableSorter(tableModel, tomeTable.getTableHeader()));

	}
	
	public void loadSubGraph(UndirectedSparseGraph graph)
	{
        tomeGraph = graph;
        loadGraphStats();
        
    }
    
    public void loadInitialGraph( UndirectedSparseGraph graph ){
    	graph.setUserDatum(TomeGraphUtilities.NUM_INITIAL_NODE_LIST, new Integer(idFilter.getSize()), UserData.SHARED);
    	graph.addUserDatum(TomeGraphUtilities.INITIAL_IMPORT_STRICT, new Boolean(idFilter.isStrict()), UserData.SHARED);
    	graph.setUserDatum(TomeGraphUtilities.NUM_INITIAL_NODES, new Integer(graph.numVertices()), UserData.SHARED);
    	graph.setUserDatum(TomeGraphUtilities.NUM_INITIAL_EDGES, new Integer(graph.numEdges()), UserData.SHARED);
    	graph.addUserDatum(TomeGraphUtilities.TRIM_CONNECTED, new Boolean(false), UserData.SHARED);
		graph.addUserDatum(TomeGraphUtilities.REM_SELF_LOOPS, new Boolean(false), UserData.SHARED);
		graph.addUserDatum(TomeGraphUtilities.TRIM_SPIKES, new Boolean(false), UserData.SHARED);
        loadSubGraph( graph );
	
	}

	private UndirectedSparseGraph getSelectedNodeSubGraph()
	{
		
		int[] selectedRows = tomeTable.getSelectedRows();
		if (selectedRows.length == 0){
			return tomeGraph;
		}
		
		Set<String> rowIds = new HashSet<String>();
		
		for(int i =0; i < selectedRows.length; i++)
		{
			rowIds.add( tableModel.getVertexIDAtRow(i) );
		}
		
		TomeEdgeIDFilter selectedFilter = new TomeEdgeIDFilter( rowIds, false );
		
		return (UndirectedSparseGraph)TomeGraphUtilities.filterGraph( tomeGraph, selectedFilter );
				
	}

	public void init()
	{
		backingGraph = null;
		tomeGraph = null;
		idFilter = null;

		JPanel tablePanel = new JPanel();
		tomeTable = new JTable( );
		JScrollPane tomeTableScroller = new JScrollPane(tomeTable);
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.PAGE_AXIS));
		tablePanel.add(tomeTableScroller);
		tomeProps = new JLabel("");

		JPanel tomePropsPanel = new JPanel(new BorderLayout());
		tomePropsPanel.add(tomeProps, BorderLayout.LINE_START);
		//tablePanel.add(tomeTableFixed);
		tablePanel.add(tomePropsPanel);
		//JScrollPane tablePanelScroller = new JScrollPane(tablePanel);
		tablePanel.setPreferredSize(new Dimension(300,400));


		//####### CONTROLS #########################################
		JPanel controlPanel = new JPanel();
		JButton load = new JButton("LOAD");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent act){
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new SIFFileFilter());
				chooser.setMultiSelectionEnabled(false);
				int returnVal = chooser.showOpenDialog(tomeFrame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
				   System.out.println("You chose to open this file: " +
						chooser.getSelectedFile().getName());
				}else{
					return;
				}        	
				File sifFile = chooser.getSelectedFile();
				UndirectedSparseGraph result = null;
				try{
					result = SIFHandler.load(sifFile);
				}catch( Exception e){
					System.out.println(e.toString());
				}
				System.out.println("Loaded file!");
				backingGraph = result;
				FilterDialog filterMe = new FilterDialog();
				idFilter = filterMe.getFilter();
				loadInitialGraph((UndirectedSparseGraph)TomeGraphUtilities.filterGraph(backingGraph, idFilter));

			}
		});

		JButton save = new JButton("SAVE");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent act){
				if (tomeGraph == null){
					 JOptionPane.showMessageDialog(null, "No graph is loaded!", "ERROR!",JOptionPane.ERROR_MESSAGE);					
					return;
				}
				JFileChooser chooser = new JFileChooser();
				if (((Double)( (HashMap<String, Double>)(tomeGraph.getUserDatum("GraphStatistics"))).get("AveragePathLength")).isInfinite()){
					 JOptionPane.showMessageDialog(null, "Graph is not fully connected! This renders most output useless. Try trimming the graph first...", "ERROR!",JOptionPane.ERROR_MESSAGE);
					 return;
				}

				int returnVal = chooser.showSaveDialog(EpiTome.this);
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

							p.println("#---SUMMARY---");
							p.println("#NumNodes:\t"+ tomeGraph.numVertices());
							p.println("#NumEdges:\t"+ tomeGraph.numEdges());
							p.println("#ClustCoeff:\t"+graphProps.get(TomeGraphUtilities.CLUSTERING_COEFFICIENT));
							p.println("#AvgPathLen:\t"+graphProps.get(TomeGraphUtilities.AVERAGE_PATH_LENGTH));
							p.println("#AvgDegree:\t"+graphProps.get(TomeGraphUtilities.AVERAGE_DEGREE));
							p.println("#-------------");										
							p.println("\n\n");

							p.println("NodeID\tClustCoeff\tAvgPathLen\tAvgDegree");
							Iterator<TomeVertex> vertIter = tomeGraph.getVertices().iterator();
							while(vertIter.hasNext())
							{
								TomeVertex vert = vertIter.next();
								p.println(vert.getID()+"\t"+vert.getClusteringCoefficient()+"\t"+vert.getAverageDistance()+"\t"+vert.getDegree());
							}

							p.close();
					}
					catch (Exception e)
					{
						JOptionPane.showMessageDialog(null, "Error writing to file!\n"+e.toString(), "ERROR!",JOptionPane.ERROR_MESSAGE);					}
					}
				}
			});



        JButton trim = new JButton("TRIM");
        trim.addActionListener( new ActionListener()
        {
        	public void actionPerformed(ActionEvent e)
        	{
				if (tomeGraph == null){
					 JOptionPane.showMessageDialog(null, "No graph is loaded!", "ERROR!",JOptionPane.ERROR_MESSAGE);					
					return;
				}
				TrimDialog trimMe = new TrimDialog();
        		loadSubGraph(trimMe.getTrimmedGraph(tomeGraph));
	
        	}
        }); 

        JButton rset = new JButton("RSET");
        rset.addActionListener( new ActionListener()
        {
        	public void actionPerformed(ActionEvent e)
        	{
				if (backingGraph == null){
					 JOptionPane.showMessageDialog(null, "No graph is loaded!", "ERROR!",JOptionPane.ERROR_MESSAGE);					
					return;
				}
				loadInitialGraph((UndirectedSparseGraph)TomeGraphUtilities.filterGraph(backingGraph, idFilter));
        	}
        }); 

        JButton view = new JButton("VIEW");
        view.addActionListener( new ActionListener()
        {

        	public void actionPerformed(ActionEvent e)
        	{
				if (tomeGraph == null){
					 JOptionPane.showMessageDialog(null, "No graph is loaded!", "ERROR!",JOptionPane.ERROR_MESSAGE);					
					return;
				}
				GraphDialog graf = new GraphDialog(tomeGraph, getSelectedNodeSubGraph());
        	}
        }); 

        JButton ctrl = new JButton("CTRL");
        ctrl.addActionListener( new ActionListener()
        {
        	public void actionPerformed(ActionEvent e)
        	{
				if (tomeGraph == null){
					 JOptionPane.showMessageDialog(null, "No graph is loaded!", "ERROR!",JOptionPane.ERROR_MESSAGE);					
					return;
				}
				try{
        			ControlsDialog ctrls = new ControlsDialog( tomeGraph, backingGraph, NUM_CONTROLS, NUM_BINS_MAX);
				}catch (Exception excep){
				}
        	}
        }); 

        JButton help = new JButton("HELP");
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				HelpDialog.display();
            }
        });
        
        controlPanel.add(load);
        controlPanel.add(save);
        controlPanel.add(trim);
        controlPanel.add(rset);
        controlPanel.add(view);
        controlPanel.add(ctrl);
        controlPanel.add(help);
        

        //##########################################################
        
        Container content = tomeFrame.getContentPane();
                
        content.removeAll();
        content.add(tablePanel, BorderLayout.CENTER);
        content.add(controlPanel, BorderLayout.SOUTH);

    }
    

    
}
