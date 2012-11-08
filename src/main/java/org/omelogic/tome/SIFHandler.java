/*
 *      GraphFileParser.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 *
 */

package org.omelogic.tome;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.utils.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.*;

public final class SIFHandler {

	
	public static UndirectedSparseGraph load( File file ) throws Exception
	{
		BufferedReader buffer;
		try{
		buffer = new BufferedReader( new InputStreamReader( new FileInputStream(file) ) );
		}catch(Exception e){
			throw new Exception("getGraphFromFileERR(31): Can't make BufferedReader from File "+e.toString()); 
		}

		return parseSIF(buffer);

		//throw new Exception("GraphFileHandlerERROR: Invalid format - " + format);
		
	}
	
	private static UndirectedSparseGraph parseSIF( BufferedReader buffer ) throws Exception
	{
		//boolean directed = false;
		UndirectedSparseGraph locutus = new UndirectedSparseGraph();
		
		HashMap<String, Vertex> nodemap = new HashMap<String, Vertex>();
		HashMap<String, Edge> edgemap = new HashMap<String, Edge>();
		int numIntLines =0;
		String line;
		for( ; ; )
		{
			try 
			{ //process each line in buffer
				line = buffer.readLine();
				
				if (line == null)
				{ //exit for loop condition
					break;
				}
			}catch (Exception e){
				
				//catch IOException
				//break and die 
				//fails to make LocusSet
				throw new Exception("OmeLogicOmeletERROR: Couldn't read in line from gtf file! " + e.toString());
			}
			//got a good line, now process it

			//ignore comments and continue
			if ( line.startsWith("#") )
			{
				continue;
			}

			//process locus line
			numIntLines++;

			String[] fields = line.split( "\\s+" );
			// lhs	int	rhs
			if (fields.length != 3)
			{
				throw new Exception("GraphFileHandler.readSIF: Not enough fields while reading SIF file! Line # " + Integer.toString(numIntLines));
			}

			String tId;
			String interaction;
			String qId;

			try{

			tId = fields[0];
			interaction = fields[1];
			qId = fields[2];
			}catch(Exception e){
				throw new Exception("getSIFGraph:89 "+e.toString());
			}
			Vertex qNode, tNode;
			
			tNode = nodemap.get(tId);			
			if (tNode == null)
			{
				tNode = (Vertex) locutus.addVertex(new TomeVertex(tId));
				nodemap.put(tId, tNode);
			}
			
			qNode = nodemap.get(qId);
			if (qNode==null)
			{
				qNode = (Vertex) locutus.addVertex(new TomeVertex(qId));
				nodemap.put(qId, qNode);
			}
			String edgeString="";
			if ( qId.compareTo(tId) < 0 ){
				edgeString = qId + tId;
			}else{
				edgeString = tId+qId;
			}
						
			if (!edgemap.containsKey(edgeString))
			{
				Edge edge = (Edge) locutus.addEdge(new UndirectedSparseEdge(qNode, tNode));
				edge.setUserDatum("interaction",interaction, UserData.SHARED);
				edgemap.put(edgeString, edge);
			}
		}
		
		return locutus;
	}



}
