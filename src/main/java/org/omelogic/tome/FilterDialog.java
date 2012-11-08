/*
 *      FilterDialog.java
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


public class FilterDialog extends JDialog implements ActionListener {

	private TomeEdgeIDFilter currFilter;
	private JTextArea filterListTextArea;
	private JComboBox filterTypeBox;
	
	public static final String filterDescription = "<html><b>TRANSIENT FILTER</b><br>Apply a filter to the current subgraph ensuring that: <ul><li>strict - only the listed nodes are included</li><li>loose - only nodes connected to those listed are included</li></ul><br>This filter can be removed with the RSET button.<br><i>Warning: This invalidates the SelectionControls generated!</i><br></html>";	
	public static final String permFilterDescription = "<html><b>INITIAL FILTER</b><br>Permanently filter the interaction network file to make a graph where: <ul><li>strict - only the listed nodes are included</li><li>loose - only nodes connected to those listed are included</li></ul><br>The resulting graph is the basis for control generation.<br></html>";	
	private static final String title = "_.filter._";
	private static final String[] filterTypes = {"Strict", "Loose"};

	public FilterDialog()
	{
		this.setModal(true);
		this.setResizable(false);
		//currFilter = filt;
        
        String desc = permFilterDescription;
        //if (transientFilter) desc = transFilterDescription;
        
		JPanel filterPanelTop = new JPanel(new BorderLayout());
		JLabel descLabel = new JLabel(desc);
		descLabel.setPreferredSize(new Dimension(300,200));
		filterPanelTop.add(descLabel, BorderLayout.NORTH);
        filterListTextArea = new JTextArea();
        JScrollPane filterListScroller = new JScrollPane(filterListTextArea);
        filterListScroller.setPreferredSize(new Dimension(300,400));

		filterPanelTop.add(filterListScroller, BorderLayout.CENTER);		


		JPanel filterPanelBottom = new JPanel(new FlowLayout());

		//type of filter strict or loose
		filterTypeBox = new JComboBox(filterTypes);
		//add filter button
		JButton filterButton = new JButton("Filter");
		filterButton.setActionCommand("filter");
		filterButton.addActionListener(this);
		//add clear button
		JButton clearButton = new JButton("Clear");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(this);
		//add cancel button
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		filterPanelBottom.add(filterTypeBox);
		filterPanelBottom.add(filterButton);
		filterPanelBottom.add(clearButton);
		filterPanelBottom.add(cancelButton);
		
		JPanel filterPanel = new JPanel(new BorderLayout());
		//filterPanel.setPreferredSize(new Dimension(350,450));
		filterPanel.add(filterPanelTop, BorderLayout.CENTER);
		filterPanel.add(filterPanelBottom, BorderLayout.SOUTH);
		

		//load previous values
		if (currFilter != null){
			Iterator<String> listIter = currFilter.getIDList().iterator();
			String filterListText = "";
			while(listIter.hasNext()){
				filterListText = filterListText + (String)listIter.next() + "\n";
			}
			filterListTextArea.setText(filterListText);

			if (currFilter.isStrict()){
				filterTypeBox.setSelectedIndex(0);
			}else{
				filterTypeBox.setSelectedIndex(1);
			}
		}


		this.getContentPane().add(filterPanel);

	}
	
	public TomeEdgeIDFilter getFilter(){
		this.pack();
		this.setVisible(true);
		return currFilter;
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if ("filter".equals(e.getActionCommand()))
		{
			String[] parsedList = (String[]) filterListTextArea.getText().split("\n");	
			Set<String> filterList = new HashSet<String>();
			String curStr;
			for (int i = 0; i < parsedList.length; i++){
				curStr = parsedList[i].replaceAll("\\s","");
				if (!curStr.equals("")){
					filterList.add(curStr);
				}
			}
			boolean strictness;
			if (filterTypeBox.getSelectedIndex() == 0){
				strictness = true;
			}else{
				strictness = false;
			}
			if (filterList.size() > 0){
				currFilter = new TomeEdgeIDFilter(filterList, strictness);
			}else{
				currFilter = null;
			}
			this.setVisible(false);
		}
		
		if ("clear".equals(e.getActionCommand()))
		{
			filterListTextArea.setText("");
		}
		
		if ("cancel".equals(e.getActionCommand()))
		{
			this.setVisible(false);
		}
		
		
		
	}


}
