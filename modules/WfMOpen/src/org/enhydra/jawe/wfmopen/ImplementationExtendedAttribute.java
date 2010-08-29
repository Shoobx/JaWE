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

package org.enhydra.jawe.wfmopen;

import java.util.ArrayList;
import java.util.List;

import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XPDLConstants;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImplementationExtendedAttribute extends XMLComplexElement {

   protected ExtendedAttribute ea;

   public ImplementationExtendedAttribute(ExtendedAttribute ea) {
      super(ea.getParent().getParent(), "ToolAgent", false);
      this.ea = ea;
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure();
   }

   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         value = "<vx:ToolAgent Class=\"" + getClassValue() + "\"";
         if (!getExecution().equals("")) {
            value += " Execution=\"" + getExecution() + "\">";
         } else {
            value += ">";
         }
         ExceptionMappings excMappings=getExceptionMappings();
         if (excMappings.size()>0) {
            value+="\n";
            value += "<vx:ExceptionMappings>";
            
            for (int i = 0; i < excMappings.size(); i++) {
               ExceptionMapping em = (ExceptionMapping) excMappings.get(i);
               value += "\n";
               value += "<vx:ExceptionMapping JavaException=\"" + em.getJavaException()+"\"";
               if (!em.getProcessException().equals("")) {
                  value += " ProcessException=\""+em.getProcessException()+"\"";
               }
               value+= "/>";
            }
            value += "\n";
            value += "</vx:ExceptionMappings>";            
         }
         PropertyList propList=getPropertyList();
         for (int i = 0; i < propList.size(); i++) {
            Property prop = (Property) propList.get(i);
            value += "\n";
            value += "<vx:Property Name=\"" + prop.getName() + "\">";
            value += prop.toValue();
            value += "</vx:Property>";
         }
         value += "\n";
         value += "</vx:ToolAgent>";
         ea.setValue(value);
      } else {
         this.value = v;
      }
   }
   public void setClassValue(String cls) {
      getClassAttribute().setValue(cls);
   }

   public String getClassValue() {
      return getClassAttribute().toValue();
   }

   public String getExecution() {
      return getExecutionAttribute().toValue();
   }

   public void setExecutionASYNCHR() {
      getExecutionAttribute().setValue(XPDLConstants.EXECUTION_ASYNCHR);
   }

   public void setExecutionSYNCHR() {
      getExecutionAttribute().setValue(XPDLConstants.EXECUTION_SYNCHR);
   }

   public XMLAttribute getClassAttribute() {
      return (XMLAttribute)get("Class");
   }

   public XMLAttribute getExecutionAttribute() {
      return (XMLAttribute)get("Execution");
   }

   public ExceptionMappings getExceptionMappings () {
      return (ExceptionMappings)get("ExceptionMappings");
   }

   public PropertyList getPropertyList () {
      return (PropertyList)get("PropertyList");      
   }
   
   protected void fillStructure () {
      XMLAttribute attrClass = new XMLAttribute(this, "Class", true);
      XMLAttribute attrExec = new XMLAttribute(this, "Execution", false, new String[] {
            XPDLConstants.EXECUTION_NONE,
            XPDLConstants.EXECUTION_ASYNCHR,
            XPDLConstants.EXECUTION_SYNCHR
      }, 0);
      ExceptionMappings excMappings = new ExceptionMappings(this);
      PropertyList pl = new PropertyList(this);
      
      add(attrClass);
      add(attrExec);
      add(excMappings);
      add(pl);
   }
   
   protected void handleStructure() {

      Node n = XMLUtil.parseExtendedAttributeContent(ea.toValue());
      List topC = getChildrenByName(n, "vx:ToolAgent");
      if (topC.size() > 0) {
         Node tan = (Node) topC.get(0);
         Node cln = getAttributeByName(tan, "Class");
         if (cln != null) {
            setClassValue(cln.getNodeValue());
         }
         Node en = getAttributeByName(tan, "Execution");
         if (en != null) {
            getExecutionAttribute().setValue(en.getNodeValue());
         }
         List empsl = getChildrenByName(tan, "vx:ExceptionMappings");
         if (empsl.size() > 0) {
            Node empsn = (Node) empsl.get(0);
            // printNode(empsn, " ", true);
            List empl = getChildrenByName(empsn, "vx:ExceptionMapping");
            List emels=new ArrayList();
            ExceptionMappings excMappings=getExceptionMappings();
            for (int j = 0; j < empl.size(); j++) {
               Node empn = (Node) empl.get(j);
               ExceptionMapping emp = (ExceptionMapping) excMappings.generateNewElement();
               Node nje = getAttributeByName(empn, "JavaException");
               if (nje != null) {
                  emp.setJavaException(nje.getNodeValue());
               }
               Node npe = getAttributeByName(empn, "ProcessException");
               if (npe != null) {
                  emp.setProcessException(npe.getNodeValue());
               }
               // printNode(pn, " ", true);
               emels.add(emp);
               emp.setParent(excMappings);
            }
            excMappings.createStructure(emels);
         }
         // printNode(tan, "", false);
         List pc = getChildrenByName(tan, "vx:Property");
         PropertyList plist=getPropertyList();
         List pels=new ArrayList();
         for (int j = 0; j < pc.size(); j++) {
            Node pn = (Node) pc.get(j);
            Property prop = (Property) plist.generateNewElement();
            Node nm = getAttributeByName(pn, "Name");
            if (nm != null) {
               prop.setName(nm.getNodeValue());
            }
            prop.setValue(XMLUtil.getChildNodesContent(pn));
            // printNode(pn, " ", true);
            pels.add(prop);
            prop.setParent(plist);
         }
         plist.createStructure(pels);
      }
   }

   protected void printNode(Node ni, String space, boolean pcr) {
      int noa = 0;
      int noc = 0;
      if (ni.getAttributes() != null) {
         noa = ni.getAttributes().getLength();
      }
      if (ni.getChildNodes() != null) {
         noc = ni.getChildNodes().getLength();
      }

      System.err.println(space
                         + "Node:n=" + ni.getNodeName() + ", np=" + ni.getPrefix()
                         + ", v=" + ni.getNodeValue() + ", t=" + ni.getNodeType()
                         + ", hmc=" + noc + ", hma=" + noa);
      if (ni.getAttributes() != null && ni.getAttributes().getLength() > 0) {
         for (int i = 0; i < ni.getAttributes().getLength(); i++) {
            Node na = ni.getAttributes().item(i);
            System.err.println(space
                               + "   " + "Attr:n=" + na.getNodeName() + ", np="
                               + na.getPrefix() + ", v=" + na.getNodeValue() + ", t="
                               + na.getNodeType());
         }
      }
      if (pcr) {
         if (ni.getChildNodes() != null) {
            for (int i = 0; i < ni.getChildNodes().getLength(); i++) {
               printNode(ni.getChildNodes().item(i), space + "   ", true);
            }
         }
      }
   }

   public Node getAttributeByName(Node n, String attrName) {
      NamedNodeMap attribs = n.getAttributes();
      if (attribs != null) {
         for (int i = 0; i < attribs.getLength(); ++i) {
            Node attr = attribs.item(i);
            if (attr.getNodeName().equals(attrName)) {
               return attr;
            }
         }
      }
      return null;
   }

   public List getChildrenByName(Node n, String childName) {
      List l = new ArrayList();
      NodeList children = n.getChildNodes();
      if (children != null) {
         for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeName().equals(childName)) {
               l.add(child);
            }
         }
      }
      return l;
   }

}
