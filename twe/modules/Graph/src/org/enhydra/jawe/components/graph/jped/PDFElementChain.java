package org.enhydra.jawe.components.graph.jped;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.enhydra.shark.xpdl.XMLElement;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;

public class PDFElementChain extends PDFChain {

	private static Logger log = Logger.getLogger(PDFElementChain.class.getName());
	private PDFPlugin plugin;
	private PDFElementChain next;
	public PDFElementChain(XMLElement element, 
			PDFPlugin plugin,
			PDFPluginNotifier notifier,
			PDFElementChain next){
		super(element,notifier);
		this.plugin=plugin;
		this.next=next;
		
	}
	public Element processPlugin() throws DocumentException{
		if (log.isLoggable(Level.FINEST))
			log.finest("processing Element on plugin "+plugin);
		return plugin.processElement(this);
	}
	public Element next() throws DocumentException {
		if (next!=null)
			return next.processPlugin();
		return null;
	}

}
