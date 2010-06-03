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
