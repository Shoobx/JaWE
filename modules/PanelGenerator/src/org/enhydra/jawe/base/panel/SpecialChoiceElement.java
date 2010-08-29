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

package org.enhydra.jawe.base.panel;

import java.util.ArrayList;
import java.util.List;

import org.enhydra.shark.xpdl.XMLComplexChoice;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLEmptyChoiceElement;


/**
 * Helper element for displaying choices of other elements.
 *
 *  @author Sasa Bojanic
 */
public class SpecialChoiceElement extends XMLComplexChoice {

      protected XMLElement controlled;
      protected String controlledSubElementName;
      protected String choosenSubElementName;
      protected XMLEmptyChoiceElement emptyChoice;
      protected boolean handleEmptyChoice;
      protected boolean useOriginalElementToName;
      
      public SpecialChoiceElement (
            XMLElement controlled,
            String controlledSubElementName,
            List chs,
            Object chsn,
            boolean handleEmptyChoice,
            String chsnSubElementName,
            String name,
            boolean isRequired) {
         this(controlled, controlledSubElementName, chs, chsn, handleEmptyChoice, chsnSubElementName, name, isRequired, true);
      }
      
      public SpecialChoiceElement (
                                   XMLElement controlled,
                                   String controlledSubElementName,
                                   List chs,
                                   Object chsn,
                                   boolean handleEmptyChoice,
                                   String chsnSubElementName,
                                   String name,
                                   boolean isRequired,
                                   boolean useOriginalElementToName) {

         super(null, name, isRequired);
         this.controlled=controlled;
         this.controlledSubElementName=controlledSubElementName;
         this.choosenSubElementName=chsnSubElementName;
         this.handleEmptyChoice=handleEmptyChoice;
         if (handleEmptyChoice || chsn==null) {
            emptyChoice=new XMLEmptyChoiceElement(null);
//            if (!handleEmptyChoice) {
//               emptyChoice.setValue("Unknown!!!");
//            }
         }
         if (chsn==null || chsn instanceof String) {            
            if (emptyChoice==null) {
               emptyChoice=new XMLEmptyChoiceElement(null);               
            }
            if (chsn!=null) {
               emptyChoice.setValue((String)chsn);
            }
            chs.add(0,emptyChoice);
            chsn=emptyChoice;
         }
         if (chs!=null) {
            this.choices=new ArrayList(chs);
         }
         this.choosen=(XMLElement)chsn;
         this.useOriginalElementToName=useOriginalElementToName;
      }
      
      public void setChoosen (XMLElement ch) {
//         System.out.println("SC "+ch);
         if (ch!=null) {
            super.setChoosen(ch);
            try {
               String newval=choosen.toValue();
               if (!choosenSubElementName.equals("")) {
                  if (!(choosen instanceof XMLEmptyChoiceElement)) {
                     newval=((XMLComplexElement)choosen).get(choosenSubElementName).toValue();
                  }
               }
               if (!controlledSubElementName.equals("")) {
                  ((XMLComplexElement)controlled).set(controlledSubElementName,newval);
               } else {
                  controlled.setValue(newval);
               }
            } catch (Exception ex) {ex.printStackTrace();}
         }
      }
      
      public void setValue (String v) {
//         System.out.println("SV "+v);
         super.setValue(v);
         if (emptyChoice!=null) {
            emptyChoice.setValue(v);
            choosen=emptyChoice;
         }
         if (!handleEmptyChoice) return;
         if (!controlledSubElementName.equals("")) {
            ((XMLComplexElement)controlled).set(controlledSubElementName,v);
         } else {
            controlled.setValue(v);
         }
      }
      
      protected void fillChoices () {
      }
      
      public void setReadOnly (boolean readOnly) {
         this.isReadOnly=readOnly;
      }
      
      public String toName () {
         if (useOriginalElementToName) {
            return controlled.toName(); 
         }
         return super.toName();         
      }
      
      public XMLElement getControlledElement () {
         return controlled;
      }
      
}
   
