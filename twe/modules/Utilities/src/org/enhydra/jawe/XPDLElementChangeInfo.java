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

package org.enhydra.jawe;

import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

import org.enhydra.jxpdl.XMLElementChangeInfo;

/**
 * Structure representing info for the change of some XPDL Element.
 *
 * @author Sasa Bojanic
 */
public class XPDLElementChangeInfo extends XMLElementChangeInfo {
   public static final int SELECTED=8;
   public static final int REFERENCES=9;
   public static final int VALIDATION_ERRORS=10;
   public static final int UNDOABLE_ACTION_STARTED=13;
   public static final int ADJUST_UNDOABLE_ACTION=14;
   public static final int UNDOABLE_ACTION_ENDED=15;
   public static final int UNDO=16;
   public static final int REDO=17;
   public static final int COPY=18;
   public static final int CUT=19;
   public static final int SEARCH_RESULT=20;
      
   private static Map actionToNameMap=new HashMap();
   static {
      actionToNameMap.put(new Integer(XPDLElementChangeInfo.SELECTED), "SELECTED");
      actionToNameMap.put(new Integer(XPDLElementChangeInfo.REFERENCES), "REFERENCES");
      actionToNameMap.put(new Integer(XPDLElementChangeInfo.VALIDATION_ERRORS), "VALIDATION_ERRORS");
      actionToNameMap.put(new Integer(XPDLElementChangeInfo.UNDOABLE_ACTION_STARTED), "UNDOABLE_ACTION_STARTED");      
      actionToNameMap.put(new Integer(XPDLElementChangeInfo.ADJUST_UNDOABLE_ACTION), "ADJUST_UNDOABLE_ACTION");      
      actionToNameMap.put(new Integer(XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED), "UNDOABLE_ACTION_ENDED");      
      actionToNameMap.put(new Integer(XPDLElementChangeInfo.UNDO), "UNDO");      
      actionToNameMap.put(new Integer(XPDLElementChangeInfo.REDO), "REDO");      
      actionToNameMap.put(new Integer(XPDLElementChangeInfo.COPY), "COPY");      
      actionToNameMap.put(new Integer(XPDLElementChangeInfo.CUT), "CUT");      
   }
   
   protected Observer source;
   
   public XPDLElementChangeInfo () {}
   
   public XPDLElementChangeInfo (Observer source,XMLElementChangeInfo info) {
      this.setAction(info.getAction());
      this.setChangedElement(info.getChangedElement());
      this.setChangedSubElements(info.getChangedSubElements());
      this.setNewValue(info.getNewValue());
      this.setOldValue(info.getOldValue());
      this.setSource(source);
   }
   
   public void setSource (Observer source) {
      this.source = source;
   }
   
   public Observer getSource () {
      return source;
   }
   
   public String getActionName () {
      String ret=super.getActionName();
      if (ret==null) {
         ret=(String)actionToNameMap.get(new Integer(action));
      }
      return ret;
   }
   
   public String toString () {
        
        String ret="Source="+ (source != null ?  source.getClass().getName() : "")+", "+ 
            " oldVal = "+getOldValue() + ", "+
            " newVal = "+getNewValue() + ", "+
            super.toString();
        return ret;
   }

}

