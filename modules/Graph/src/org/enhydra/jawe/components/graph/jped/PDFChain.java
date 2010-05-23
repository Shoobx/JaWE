package org.enhydra.jawe.components.graph.jped;

import java.io.OutputStream;

import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.shark.xpdl.XMLElement;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Section;

public abstract class PDFChain {

	private XMLElement element;
	private Section enclosing;
	private PDFPluginNotifier notifier;

	public PDFChain(XMLElement element, PDFPluginNotifier notifier){
		this.element=element;
		this.notifier=notifier;
	}
	public XMLElement getElement() {
		return element;
	}

	public Document startProcessDocument(XMLElement element, Graph graph, OutputStream output) throws DocumentException {
		return notifier.processDocument(element, graph, output);
	}

	public Element startProcessElement(XMLElement element) throws DocumentException {
		return notifier.processElement(element);
	}

	public Section startProcessSection(XMLElement element) throws DocumentException {
		return notifier.processSection(element);
	}
	public Section getEnclosingSection() {
		return notifier.getEnclosingSection();
	}
	public void setEnclosingSection(Section enclosingSection) {
		notifier.setEnclosingSection(enclosingSection);
	}	
		

}
