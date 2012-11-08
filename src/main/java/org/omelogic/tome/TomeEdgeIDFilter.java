/*
 *      TomeEdgeIDFilter.java
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

import java.util.*;
import edu.uci.ics.jung.graph.filters.*;
import edu.uci.ics.jung.utils.*;
import edu.uci.ics.jung.graph.*;

public class TomeEdgeIDFilter extends GeneralEdgeAcceptFilter implements EfficientFilter
{
	Set<String> filterIDs;
	boolean strictFilter;
	
	public TomeEdgeIDFilter( Set<String> filtIDs, boolean strict){
		filterIDs = filtIDs;
		strictFilter = strict;
	}
	
	public boolean acceptEdge( Edge e )
	{
		Pair vertices = e.getEndpoints();
		String fr = ((TomeVertex)vertices.getFirst()).getID();
		String to = ((TomeVertex)vertices.getSecond()).getID();
		
		boolean accept;
		
		if (strictFilter){
			accept = filterIDs.contains(fr) && filterIDs.contains(to);
		}else{
			accept = filterIDs.contains(fr) || filterIDs.contains(to);
		}			
		
		return accept;

	}
	
	public Set<String> getIDList()
	{
		return filterIDs;
	}
	
	public int getSize()
	{
		return filterIDs.size();
	}
	
	public boolean isStrict()
	{
		return strictFilter;
	}
	
	public String getName()
	{
		if (strictFilter){
			return "Strict Edge ID Filter";
		}else{
			return "Loose Edge ID Filter";
		}
	}
}
