/*
 * Created on Jul 19, 2005
 */
package org.enhydra.jawe.base.panel.panels;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.shark.xpdl.XMLElement;

/**
 * @author Zoran Milakovic
 */
public class XMLElementView {

   public final static int TONAME = 1;

   public final static int TOVALUE = 2;

   protected XMLElement element;

   protected String elementString = "";

   protected int type = 1;
   protected boolean lDepStr=true;
   
   protected PanelContainer pc;

   public XMLElementView(PanelContainer pc, XMLElement el, int type) {
      this.pc = pc;
      //System.err.println("creating new XMLElementView, el = "+el+", el.toName = "+el.toName()+",
      // el.toValue = "+el.toValue());
      this.element = el;
      this.type = type;
   }

   public XMLElementView(PanelContainer pc,String el,boolean lds) {
      this.pc=pc;
      this.elementString = el;
      this.lDepStr=lds;
   }

   public XMLElement getElement() {
      return element;
   }

   public void setElement(XMLElement el) {
      this.element = el;
   }

   public String getElementString() {
      return elementString;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof XMLElementView))
         return false;
      if (getElement() != null) {
         return getElement()==(((XMLElementView) obj).getElement());
      } else if (elementString != null) {
         return this.elementString.equals(((XMLElementView) obj).elementString);
      }
      return false;
   }

   public String toString() {
      if (this.element != null) {
         //            if(type == TONAME) {
         return JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(this.element);
//         if (pc!=null) {
//            
//            return pc.getDisplayNameGenerator().getDisplayName(this.element);
//         } else {
//            return ResourceManager.getLanguageDependentString(this.element.toName()+"Key");
//         }
         //            }
         //            if(type == TOVALUE) {
         //               if (element instanceof XMLBaseForCollectionAndComplex || element instanceof
         // XMLComplexChoice) {
         //                  return JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(this.element);
         //               } else {
         //                  return this.element.toValue();
         //               }
         //            }
         //            return "";
      }      
      String toRet;
      if (lDepStr) {
         if (pc!=null) {
            toRet=pc.getSettings().getLanguageDependentString(elementString + "Key");
         } else {
            toRet=ResourceManager.getLanguageDependentString(elementString+"Key");
         }
         if (toRet == null) {
            toRet = elementString;
         }
      } else {
         toRet=elementString;
      }
      return toRet;
      
   }
}