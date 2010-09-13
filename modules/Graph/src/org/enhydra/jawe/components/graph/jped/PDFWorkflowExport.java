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

import java.io.FileOutputStream;
import java.util.logging.Logger;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;

public class PDFWorkflowExport {
	
	private static Logger log = Logger.getLogger(PDFWorkflowExport.class.getName());
	private static Rectangle EMPTY_BORDER = new Rectangle(0.0f,0.0f);
	{
		EMPTY_BORDER.setBorderWidth(0.0f);
	}
	
	
	private Graph graph;
	private JaWEManager jaWEManager;
	private WorkflowProcess process;
	private PDFPluginNotifier notifier;
	
	public PDFWorkflowExport (){
	}
	public Graph getGraph() {
		return graph;
	}
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	public JaWEManager getJaWEManager() {
		return jaWEManager;
	}
	public void setJaWEManager(JaWEManager jaWEManager) {
		this.jaWEManager = jaWEManager;
	}
	public WorkflowProcess getProcess() {
		return process;
	}
	public void setProcess(WorkflowProcess process) {
		this.process = process;
	}
	public boolean exportToFile(FileOutputStream output) throws Exception{
		notifier = new PDFPluginNotifier();
		Document document = notifier.processDocument(process, graph, output);
		document.close();
		return true;
	}
}
