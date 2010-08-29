/**
* Together Workflow Editor
* Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
* 
* This program is free software: you can redistribute it and/or modify 
* it under the terms of the GNU General Public License as published by 
* the Free Software Foundation, either version 3 of the License, or 
* (at your option) any later version. 
*
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
* GNU General Public License for more details. 
*
* You should have received a copy of the GNU General Public License 
* along with this program. If not, see http://www.gnu.org/licenses
*/

package org.enhydra.jawe.components.graph.jped;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.enhydra.shark.xpdl.XMLElement;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Section;

public class PDFSectionChain extends PDFChain  {

	private static Logger log = Logger.getLogger(PDFSectionChain.class.getName());
	private PDFPlugin plugin;
	private PDFSectionChain next;
	public PDFSectionChain(XMLElement element, 
			PDFPlugin plugin,
			PDFPluginNotifier notifier,
			PDFSectionChain next){
		super(element,notifier);
		this.plugin=plugin;
		this.next=next;
		
	}
	public Section processPlugin() throws DocumentException{
		if (log.isLoggable(Level.FINEST))
			log.finest("processing Section on plugin "+plugin);
		return plugin.processSection(this);
	}
	public Section next() throws DocumentException{
		if (next!=null)
			return next.processPlugin();
		return null;
	}

}
