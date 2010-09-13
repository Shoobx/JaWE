/**
* Together Workflow Editor
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

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.shark.xpdl.XMLElement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;

public class PDFDocumentChain extends PDFChain {
	private static Logger log = Logger.getLogger(PDFDocumentChain.class.getName());
	
	private PDFPlugin plugin;
	private PDFDocumentChain next;
	private Graph graph;
	private OutputStream output;
	public PDFDocumentChain(XMLElement element, 
			Graph graph,
			OutputStream output,
			PDFPlugin plugin,
			PDFPluginNotifier notifier,
			PDFDocumentChain next){
		super(element,notifier);
		this.graph=graph;
		this.output=output;
		this.plugin=plugin;
		this.next=next;
	}
	public Document processPlugin() throws DocumentException{
		if (log.isLoggable(Level.FINEST))
			log.finest("processing Document on plugin "+plugin);
		return plugin.processDocument(this);
	}
	public Document next() throws DocumentException {
		if (next!=null)
			return next.processPlugin();
		return null;
	}

	public Graph getGraph() {
		return graph;
	}
	
	public OutputStream getOutputStream(){
		return output;
	}

}
