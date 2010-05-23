package org.enhydra.jawe.components.graph.jped;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.enhydra.shark.xpdl.XMLElement;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Section;

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
