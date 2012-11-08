/*
 *      GraphDialog.java
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.decorators.*;
import edu.uci.ics.jung.graph.filters.*;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.statistics.*;
import edu.uci.ics.jung.utils.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.control.*;


public class GraphDialog extends JDialog implements ActionListener {

	private UndirectedSparseGraph wholeGraph;
	private UndirectedSparseGraph selectionGraph;
    private VisualizationViewer vv1;
	private VisualizationModel vm;
    private DefaultModalGraphMouse graphMouse ;
    private ScalingControl scaler;
	private boolean fullIsShown;

	private static final String title = "_.graph._";
	
	public GraphDialog(UndirectedSparseGraph fullGraph, UndirectedSparseGraph selectedGraph)
	{
		//preliminaries		
		//this.setUndecorated(true);
		//this.getRootPane().setBorder((Border)(BorderFactory.createTitledBorder(title)));
		this.setModal(true);
		//this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		/*this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				//setLabel("Thwarted user attempt to close window.");
			}
		});
		*/
		//load in argument variables
		wholeGraph = fullGraph;
		selectionGraph = selectedGraph;
		//build interface
		
		//make graph view	
		JPanel filterPanelTop = makeGraphPanel(selectedGraph);
		fullIsShown = false;
		//make control panel
		JPanel filterPanelBottom = new JPanel(new FlowLayout());
		//filterPanelBottom.setComponentOrientation(ComponentOrientation.LEFT_TO_LEFT);

		//make mode selector
		JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(((DefaultModalGraphMouse)vv1.getGraphMouse()).getModeListener());
		//make zoomIn button
		JButton zoomInButton = new JButton("+");
		zoomInButton.setActionCommand("+");
		zoomInButton.addActionListener(this);
		//make zoomOut button
		JButton zoomOutButton = new JButton("-");
		zoomOutButton.setActionCommand("-");
		zoomOutButton.addActionListener(this);
		//make show full graph button
		JButton showFullButton = new JButton("WHOLE");
		showFullButton.setActionCommand("whole");
		showFullButton.addActionListener(this);
		//make show selected button
		JButton showSelButton = new JButton("SELECTED");
		showSelButton.setActionCommand("selected");
		showSelButton.addActionListener(this);
		
		//make close button
		JButton closeButton = new JButton("CLOSE");
		closeButton.setActionCommand("close");
		closeButton.addActionListener(this);
		
		
		//layout buttons in control panel
		filterPanelBottom.add(modeBox);
		filterPanelBottom.add(zoomInButton);
		filterPanelBottom.add(zoomOutButton);
		filterPanelBottom.add(closeButton);
		filterPanelBottom.add(showFullButton);
		filterPanelBottom.add(showSelButton);
		
		//layout overall panel
		JPanel filterPanel = new JPanel(new BorderLayout());
		filterPanel.add(filterPanelTop, BorderLayout.CENTER);
		filterPanel.add(filterPanelBottom, BorderLayout.SOUTH);
		
		//activate dialog
		this.getContentPane().add(filterPanel);
		this.pack();
		this.setVisible(true);

	}
	
	private Layout makeLayout( UndirectedSparseGraph graph )
	{
		int graphDim = 600+graph.numVertices();

        // create one layout for the UndirectedSparseGraph
        ISOMLayout layout = new ISOMLayout(graph);
        //layout.setMaxIterations(1000);
        layout.initialize(new Dimension(graphDim,graphDim));
        
		return layout;
	}		
	
	private JPanel makeGraphPanel(UndirectedSparseGraph graph){

		scaler = new CrossoverScalingControl();
		PluggableRenderer pr1 = new PluggableRenderer();
        
        // the preferred size the view
        Dimension preferredSize1 = new Dimension(600,600);
        
		//create string labeller for vertices
		VertexStringer labeller = new VertexStringer(){
				public String getLabel(ArchetypeVertex v){
					return ((TomeVertex)v).getID();
				}
			};		
		pr1.setVertexStringer(labeller);	

		int graphDim = 600+graph.numVertices();

        // create one layout for the UndirectedSparseGraph
        Layout layout = makeLayout(graph);
        
        // create one model that both views will share
        vm = new DefaultVisualizationModel(layout, preferredSize1);
 
        // create view for the model
        vv1 = new VisualizationViewer(vm, pr1, preferredSize1);
        
        vv1.setBackground(Color.white);
        vv1.setPickSupport(new ShapePickSupport());
        
        // add default listener for ToolTips
        vv1.setToolTipFunction(new DefaultToolTipFunction());
        ToolTipManager.sharedInstance().setDismissDelay(10000);
        
        // create a GraphMouse for the main view
        graphMouse = new DefaultModalGraphMouse();
        vv1.setGraphMouse(graphMouse);

        JPanel graphPanel = new JPanel(new BorderLayout());
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv1);
        graphPanel.add(gzsp);
        
        return graphPanel;
	}
	
	private void swapGraph( UndirectedSparseGraph graph )
	{
		vm.setGraphLayout( makeLayout(graph) );
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if ("+".equals(e.getActionCommand()))
		{
			scaler.scale(vv1, 1.1f, vv1.getCenter());
		}
		
		if ("-".equals(e.getActionCommand()))
		{
			scaler.scale(vv1, 1/1.1f, vv1.getCenter());
		}
		
		if ("whole".equals(e.getActionCommand()))
		{
			if (!fullIsShown)
			{
				swapGraph( wholeGraph );
				fullIsShown = true;
			}
		}

		if ("selected".equals(e.getActionCommand()))
		{
			if (fullIsShown)
			{
				swapGraph( selectionGraph );
				fullIsShown = false;
			}
		}

		if ("close".equals(e.getActionCommand()))
		{
			this.setVisible(false);
		}

	}


}
