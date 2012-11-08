/*
 *      TomeTableModel.java
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

import javax.swing.table.AbstractTableModel;

public class TomeTableModel extends AbstractTableModel
{
	private TomeVertex[] vertices;
	private static String[] colnames = TomeVertex.COLUMN_NAMES;
	
	public TomeTableModel( Object[] vs )
	{
		vertices = new TomeVertex[vs.length];
		for (int i = 0; i < vs.length; i++)
		{
			vertices[i] = (TomeVertex)vs[i];
		}
	}
	
	public int getRowCount()
	{
		return vertices.length;
	}
	
	public TomeVertex getVertexAtRow( int row )
	{
		return vertices[row];
	}
	
	public String getVertexIDAtRow( int row )
	{
		return vertices[row].getID();
	}	
	
	public int getColumnCount()
	{
		return colnames.length;
	}
	
	public String getColumnName( int column )
	{
		return colnames[column];
	}
	
	public Object getValueAt( int row, int column )
	{
		return vertices[row].getVertexRow()[column];
	}

	public boolean isCellEditable(int row, int column )
	{
		return false;
	}
	

}
