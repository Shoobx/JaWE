package org.enhydra.jawe.components.graph.jped;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.shark.xpdl.XMLElement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Section;

/**
 * This is a simple utility class that process a pack of pdf plugins by dispatching
 * calls made during the process of creating a pdf document
 * @author delbd
 *
 */
public class PDFPluginNotifier {
	private Logger log = Logger.getLogger(PDFPluginNotifier.class.getName());
	
	private PDFPlugin[] pluginInterfaces=new PDFPlugin[] {new PDFPlugin()};
	private Section enclosingSection;
	public PDFPluginNotifier(){
		if (log.isLoggable(Level.FINE)){
		log.fine("there are "+pluginInterfaces.length+" pdf plugins");
		for (int i = 0;i<pluginInterfaces.length;i++)
			log.fine("plugins["+i+"]="+pluginInterfaces[i]);
		}
	}
	public Document processDocument(XMLElement element, Graph graph, OutputStream output) throws DocumentException{
		PDFDocumentChain chain = null;
		for (int i=pluginInterfaces.length-1;i>=0;i--)
			chain=new PDFDocumentChain(
					element,
					graph,
					output,
					pluginInterfaces[i],
					this,
					chain);
		Document d =  chain.processPlugin();
		return d;
		
	}
	public Section processSection(XMLElement element) throws DocumentException{
		PDFSectionChain chain = null;
		for (int i=pluginInterfaces.length-1;i>=0;i--)
			chain=new PDFSectionChain(
					element,
					pluginInterfaces[i],
					this,
					chain);
		Section s = chain.processPlugin();
		return s;
		
	}
	
	public Element processElement(XMLElement element) throws DocumentException{
		PDFElementChain chain = null;
		for (int i=pluginInterfaces.length-1;i>=0;i--)
			chain=new PDFElementChain(
					element,
					pluginInterfaces[i],
					this,
					chain);
		Element e = chain.processPlugin();
		return e;
		
	}

	public Section getEnclosingSection() {
		return enclosingSection;
	}

	public void setEnclosingSection(Section enclosingSection) {
		this.enclosingSection = enclosingSection;
	}
}
