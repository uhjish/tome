/*
 *      HelpDialog.java
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

public class HelpDialog extends JDialog {
	
	static final String overallInstructions = 
        "<html>"+
        "<b><h2><center>ToME - Topology Math Explorer</center></h2></b>"+
        "<p>Provides basic support for exploring small-world graph properties for undirected graphs."+
        "<p>Provides novel algorithms for control-generation in this context."+
        
        "<p><p><b>Small-World Graph Statistics:</b>"+
        "<ul>"+
        "<li>Clustering Coefficient - <a href=\"http://en.wikipedia.org/wiki/Clustering_coefficient\">http://en.wikipedia.org/wiki/Clustering_coefficient</a>"+
        "<li>Average Path Length - <a href=\"http://en.wikipedia.org/wiki/Average_path_length\">http://en.wikipedia.org/wiki/Average_path_length</a>"+
        "<li>Degree Distribution - <a href=\"http://en.wikipedia.org/wiki/Degree_distribution\">http://en.wikipedia.org/wiki/Degree_distribution</a>"+
        "</ul><br>"+
        
        "<br>Note: Small world graph-statistics are meaningful only on connected graphs."+
        "<br>We load the entire graph here so that it can be viewed unmodified."+
		"<br>Please use the trim facility [see the Filter Help] to filter the graph to it's largest connected component."+
		"</html>";        

    static final String mainViewInstructions = 
        "<html>"+
        "<b><h2><center>MAIN VIEW</center></h2></b>"+
        "<p>This table shows the clustering coefficient, average path length, and average distance for each node in the current view. "+
        "<br>A summary of the graph's average properties is shown below the table. "+
        "<br><b>LOAD:</b>"+
        "<br>Load a new sif file to use as the backingGraph from which more focused subsets are filtered out."+
        "<br>You will be given the option to filter this immediately so properties are not unnecessarily calculated for large graphs."+
        "<br><b>SAVE:</b>"+
        "<br>Both the node-wise and graph-properties can be exported to a tab-separated text file using the save button."+
        "<br><b>FILT:</b>"+
		"<br>Allows filtering of the graph to include only a particular list of nodes. [See Filtering]."+
		"<br><b>TRIM:</b>"+
        "<br>Trim the currently shown subgraph based on some user criteria."+   
		"<br><b>RSET:</b>"+
        "<br>Reset the graph to include all the nodes and edges present after the initial loading & filter."+
        "<br><b>VIEW:</b>"+
        "<br>Show nodes around the currently selected rows. If none are selected show rows from the whole table."+
        "<br><b>CTRL:</b>"+
        "<br>Generate control statistics for the curent subgraph [See Controls tab]."+
        "<br><b>HELP:</b>"+
        "<br>Show this help dialog."+
        "</html>";


    static final String graphInstructions = 
        "<html>"+
        "<b><h2><center>GRAPH VIEW INTERACTION</center></h2></b>"+
        "<p>There are two modes, Transforming and Picking."+
        "<p>The modes are selected with a combo box."+
        
        "<p><p><b>Transforming Mode:</b>"+
        "<ul>"+
        "<li>Mouse1+drag pans the UndirectedSparseGraph"+
        "<li>Mouse1+Shift+drag rotates the UndirectedSparseGraph"+
        "<li>Mouse1+CTRL(or Command)+drag shears the UndirectedSparseGraph"+
        "</ul>"+
        
        "<b>Picking Mode:</b>"+
        "<ul>"+
        "<li>Mouse1 on a Vertex selects the vertex"+
        "<li>Mouse1 elsewhere unselects all Vertices"+
        "<li>Mouse1+Shift on a Vertex adds/removes Vertex selection"+
        "<li>Mouse1+drag on a Vertex moves all selected Vertices"+
        "<li>Mouse1+drag elsewhere selects Vertices in a region"+
        "<li>Mouse1+Shift+drag adds selection of Vertices in a new region"+
        "<li>Mouse1+CTRL on a Vertex selects the vertex and centers the display on it"+
        "</ul>"+
       "<b>Both Modes:</b>"+
       "<ul>"+
        "<li>Mousewheel scales with a crossover value of 1.0.<p>"+
        "     - scales the UndirectedSparseGraph layout when the combined scale is greater than 1<p>"+
        "     - scales the UndirectedSparseGraph view when the combined scale is less than 1";

    static final String filterInstructions = 
        "<html>"+
        "<b><h2><center>FILTERING</center></h2></b>"+
        "<p><p><b>Strict/Loose</b>"+
        "<br>Strict removes all nodes that are not on the list."+
        "<br>Loose leaves in nodes connected to nodes on the list."+
        "<p><p><b>Filter</b>"+
        "<br>Type in or paste in a list of ids corresponding to vertices, one id per line."+
        "<br>Then hit filter to filter out all other nodes from the graph."+
 		"<br>Note that this graph is not guaranteed to be fully connected."+       
        "<p><p><b>Clear</b>"+
        "<br>Clear the filtering list."+
        "<p><p><b>Cancel</b>"+
        "<br>Cancel this action."+
        "</html>";

    static final String trimInstructions = 
        "<html>"+
        "<b><h2><center>TRIMMING</center></h2></b>"+
        "<p><p><b>Trim Connected</b>"+
        "<br>Finds the largest connected component in the graph and discards the rest."+
        "<p><p><b>Remove Self-Loops</b>"+
        "<br>Removes edges between a node and itself."+
        "<p><p><b>Trim UnderConnected</b>"+
        "<br>Removes nodes from this graph that are not connected to at least two other nodes (not including itself)."+
        "<br>The process is repeated on the graph until there are no more nodes of this type."+
        "</html>";
        
    static final String controlInstructions = 
        "<html>"+
        "<b><h2><center>CONTROLS</center></h2></b>"+
        "<br><br>Two types of controls, described below, are generated for the graph-wise statistics."+
        "<br>These graphs are both based on the InitialGraph(untrimmed) from the id filtering on the network file."+
        "<p><p><b>Connection Controls</b>"+
        "<br>Given n nodes and v edges in the initial graph, a random graph with n nodes and v edges is created"+
        "<br>The largest connected component of this random graph is the used as a connection control graph."+
        "<br>If the initial graph was additionally trimmed of self loops and/or underconnected nodes, this one is too."+
        "<p><p><b>Selection Controls</b>"+
        "<br>Given an InitialGraph made from n vertexIDs, n vertexIDs are chosen from the backing graph."+
        "<br>They are loaded using either the loose or strict filtering method based on the original choice."+
        "<br>The largest connected component of this random graph is the used as a selection control graph."+
        "<br>If the initial graph was additionally trimmed of self loops and/or underconnected nodes, this one is too."+
        "<br><br>Num Controls of each type of control graph is created the resulting distribution for each statistic is shown."+
        "<br>The images of the distributions shown can be exported using the save function."+
        "<br><br>You can change the number of control graphs to generate using the Num Controls field."+
        "<br>Simply enter the new number and hit generate controls to rerun the process."+
        "<br><br>You may also export the control values for each of the connection and selection control graphs in a tsv format."+
        "</html>";
        
	private static final String title = "_.help._";

	public HelpDialog()
	{
	    super();

		JTabbedPane helpDialogTabs = new JTabbedPane();
        JPanel statsTableDialog = new JPanel();
        statsTableDialog.add( new JLabel(mainViewInstructions));
        JPanel mainInstructionsDialog = new JPanel();
        mainInstructionsDialog.add( new JLabel(overallInstructions));
        JPanel filterInstructionsDialog = new JPanel();
        filterInstructionsDialog.add( new JLabel(filterInstructions));
        JPanel trimInstructionsDialog = new JPanel();
        trimInstructionsDialog.add( new JLabel(trimInstructions));
        JPanel controlInstructionsDialog = new JPanel();
        controlInstructionsDialog.add( new JLabel(controlInstructions));
        JPanel graphInstructionsDialog = new JPanel();
        graphInstructionsDialog.add( new JLabel(graphInstructions));

		helpDialogTabs.add("ToME", mainInstructionsDialog);
		helpDialogTabs.add("Main", statsTableDialog);
		helpDialogTabs.add("Filtering", filterInstructionsDialog);
		helpDialogTabs.add("Trimming", trimInstructionsDialog);
		helpDialogTabs.add("Controls", controlInstructionsDialog);
		helpDialogTabs.add("Graph View", graphInstructionsDialog);
		
		this.getContentPane().add(helpDialogTabs);
		

	}
	
	public static void display(){
		HelpDialog help = new HelpDialog();
		help.pack();
		help.setVisible(true);
	}		


}
