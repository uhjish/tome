/*
 *      SIFFileFilter.java
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

import javax.swing.filechooser.*;
import java.io.*;

public class SIFFileFilter extends javax.swing.filechooser.FileFilter {


	
	public boolean accept(File f) {

		if (f.isDirectory()) {
			return true;
		}

		String fname = f.toString();

		if (fname != null) {
			if (fname.matches(".+\\.[sS][iI][fF]$"))
			{
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	public String getDescription(){
		return "Simple Interaction Format Files";
	}


}
