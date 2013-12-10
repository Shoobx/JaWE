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

package org.enhydra.jawe;

import java.util.List;

/**
 * Used to handle undo history.
 * 
 * @author Sasa Bojanic
 */
public interface UndoHistoryManager {

   void init (int maxHistory);
   
   void registerEvents (List xpdlInfoList,XPDLElementChangeInfo selEv);
   
   void undo();
   void redo();

   boolean canUndo();
   boolean canRedo();
   
   boolean isUndoOrRedoInProgress ();

   public void cleanHistory ();
   
}
