/*
 *      TrimDialog.java
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
import java.util.*;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.impl.*;


public class TrimDialog extends JDialog implements ActionListener {

	private JCheckBox trimConnectedBox;
	private JCheckBox removeSelfLoopsBox;
	private JCheckBox trimSpikesBox;
	private UndirectedSparseGraph trimGraph;

	private static final String trimConnectedDesc = "Trim the graph to include only it's largest connected component. You must do this for any AveragePathLength to resolve.";
	private static final String removeSelfLoopsDesc = "Remove all self-loop edges in the graph.";
	private static final String trimSpikesDesc = "Remove nodes that are not connected to at least two other distinct nodes, the spikes on the periphery of the central network.";

	public TrimDialog()
	{
		this.setModal(true);
		this.setResizable(false);
		//currFilter = filt;
        
        
		JPanel filterPanelTop = new JPanel();
		filterPanelTop.setLayout(new BoxLayout(filterPanelTop, BoxLayout.PAGE_AXIS));

		trimConnectedBox = new JCheckBox("Trim to Connected Component");
		trimConnectedBox.setToolTipText(trimConnectedDesc);
		trimConnectedBox.setSelected(true);
		removeSelfLoopsBox = new JCheckBox("Remove Self-loops");
		removeSelfLoopsBox.setToolTipText(removeSelfLoopsDesc);
		trimSpikesBox = new JCheckBox("Trim Underconnected Nodes");
		trimSpikesBox.setToolTipText(trimSpikesDesc);
		
		filterPanelTop.add(trimConnectedBox);
		filterPanelTop.add(removeSelfLoopsBox);
		filterPanelTop.add(trimSpikesBox);


		JPanel filterPanelBottom = new JPanel(new FlowLayout());

		JButton trimButton = new JButton("Trim");
		trimButton.setActionCommand("trim");
		trimButton.addActionListener(this);
		//add cancel button
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		filterPanelBottom.add(trimButton);
		filterPanelBottom.add(cancelButton);
		
		JPanel filterPanel = new JPanel(new BorderLayout());
		//filterPanel.setPreferredSize(new Dimension(350,450));
		filterPanel.add(filterPanelTop, BorderLayout.CENTER);
		filterPanel.add(filterPanelBottom, BorderLayout.SOUTH);
		
		this.getContentPane().add(filterPanel);

	}
	
	public UndirectedSparseGraph getTrimmedGraph(UndirectedSparseGraph g){
		trimGraph =g;
		this.pack();
		this.setVisible(true);
		return trimGraph;
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if ("trim".equals(e.getActionCommand()))
		{
			if (trimConnectedBox.isSelected()){
				trimGraph = (UndirectedSparseGraph)TomeGraphUtilities.trimConnected(trimGraph);
			}
			if (removeSelfLoopsBox.isSelected()){
				trimGraph = (UndirectedSparseGraph)TomeGraphUtilities.removeSelfLoops(trimGraph);
			}
			if (trimSpikesBox.isSelected()){
				trimGraph = (UndirectedSparseGraph)TomeGraphUtilities.trimSpikes(trimGraph);
			}
			
			this.setVisible(false);
		}
		
		
		if ("cancel".equals(e.getActionCommand()))
		{
			this.setVisible(false);
		}
		
		
		
	}


}
