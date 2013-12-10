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

import java.io.OutputStream;

import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.shark.xpdl.XMLElement;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Section;

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
