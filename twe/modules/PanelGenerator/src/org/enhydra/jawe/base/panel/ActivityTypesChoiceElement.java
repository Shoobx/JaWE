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

import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivityTypes;
import org.enhydra.jxpdl.elements.EventTypes;
import org.enhydra.jxpdl.utilities.SequencedHashMap;


/**
 * Helper element for displaying choices of other elements.
 *
 *  @author Sasa Bojanic
 */
public class ActivityTypesChoiceElement extends XMLComplexChoice {

      protected ActivityTypes controlled;
      
      public ActivityTypesChoiceElement (ActivityTypes controlled) {
         super(null, controlled.toName(), true);
         this.controlled=controlled;
         int type = ((Activity)controlled.getParent()).getActivityType();
         SequencedHashMap chsMap=new SequencedHashMap();
         chsMap.put(new Integer(XPDLConstants.ACTIVITY_TYPE_BLOCK),controlled.getBlockActivity());
         chsMap.put(new Integer(XPDLConstants.ACTIVITY_TYPE_NO),controlled.getImplementation().getImplementationTypes().getNo());
         chsMap.put(new Integer(XPDLConstants.ACTIVITY_TYPE_ROUTE),controlled.getRoute());
         chsMap.put(new Integer(XPDLConstants.ACTIVITY_TYPE_SUBFLOW),controlled.getImplementation().getImplementationTypes().getSubFlow());
         chsMap.put(new Integer(XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION),controlled.getImplementation().getImplementationTypes().getTask().getTaskTypes().getTaskApplication());
         chsMap.put(new Integer(XPDLConstants.ACTIVITY_TYPE_EVENT_START),controlled.getEvent().getEventTypes().getStartEvent());
         chsMap.put(new Integer(XPDLConstants.ACTIVITY_TYPE_EVENT_END),controlled.getEvent().getEventTypes().getEndEvent());
         choosen=(XMLElement)chsMap.get(new Integer(type));
         this.choices=new ArrayList(chsMap.values());
      }
      
      public void setChoosen (XMLElement ch) {
         super.setChoosen(ch);
         if (ch.getParent()==controlled) {
            controlled.setChoosen(ch);
         } else if (ch.getParent() instanceof EventTypes){
            controlled.setEvent();
            controlled.getEvent().getEventTypes().setChoosen(ch);
         } else {
            controlled.setImplementation();
            if (ch.getParent()==controlled.getImplementation().getImplementationTypes()) {
               controlled.getImplementation().getImplementationTypes().setChoosen(ch);
            } else {
               controlled.getImplementation().getImplementationTypes().setTask();
               controlled.getImplementation().getImplementationTypes().getTask().getTaskTypes().setChoosen(ch);               
            }
         }
      }
            
      protected void fillChoices () {
      }

      public void setReadOnly (boolean readOnly) {
         this.isReadOnly=readOnly;
      }
      
      public String toName () {
         return controlled.toName();
      }

      public XMLElement getControlledElement () {
         return controlled;
      }
}
   
