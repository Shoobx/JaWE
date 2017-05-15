/**
 * Together Workflow Editor
 * Copyright (C) 2011 Together Teamsolutions Co., Ltd. 
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

/*
 * Created on Jul 19, 2005
 */
package org.enhydra.jawe.base.panel.panels;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jxpdl.XMLElement;

/**
 * @author Zoran Milakovic
 */
public class XMLElementView {

   public final static int TONAME = 1;

   public final static int TOVALUE = 2;

   protected XMLElement element;

   protected String elementString = "";

   protected int type = 1;

   protected boolean lDepStr = true;

   protected PanelContainer pc;

   protected String emptyStringTranslation;

   public XMLElementView(PanelContainer pc, XMLElement el, int type) {
      this.pc = pc;
      // System.err.println("creating new XMLElementView, el = "+el+", el.toName = "+el.toName()+",
      // el.toValue = "+el.toValue());
      this.element = el;
      this.type = type;
   }

   public XMLElementView(PanelContainer pc, String el, boolean lds) {
      this.pc = pc;
      this.elementString = el;
      this.lDepStr = lds;
   }

   public XMLElementView(PanelContainer pc, String el, boolean lds, String emptyStringTranslation) {
      this(pc, el, lds);
      this.emptyStringTranslation = emptyStringTranslation;
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
         return getElement() == (((XMLElementView) obj).getElement());
      } else if (elementString != null) {
         return this.elementString.equals(((XMLElementView) obj).elementString);
      }
      return false;
   }

   public String toString() {
      if (this.element != null) {
         // if(type == TONAME) {
         String ret = JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(this.element);
         if (ret == null) {
            ret = this.element.toName();
         }
         if (ret.equals(""))
            ret = " ";
         return ret;
         // if (pc!=null) {
         //
         // return pc.getDisplayNameGenerator().getDisplayName(this.element);
         // } else {
         // return
         // ResourceManager.getLanguageDependentString(this.element.toName()+"Key");
         // }
         // }
         // if(type == TOVALUE) {
         // if (element instanceof XMLBaseForCollectionAndComplex || element instanceof
         // XMLComplexChoice) {
         // return
         // JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(this.element);
         // } else {
         // return this.element.toValue();
         // }
         // }
         // return "";
      }
      String toRet;
      if (lDepStr) {
         if ("".equals(elementString) && emptyStringTranslation != null) {
            toRet = emptyStringTranslation;
         } else {
            if (pc != null) {
               toRet = pc.getSettings().getLanguageDependentString(elementString + "Key");
            } else {
               toRet = ResourceManager.getLanguageDependentString(elementString + "Key");
            }
         }
         if (toRet == null) {
            toRet = elementString;
         }
      } else {
         toRet = elementString;
      }
      if (toRet.equals(""))
         toRet = " ";
      return toRet;

   }
}
