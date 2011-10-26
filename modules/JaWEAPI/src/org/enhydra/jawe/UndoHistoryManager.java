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

import java.util.List;

/**
 * Interface for handling undo/redo history.
 */
public interface UndoHistoryManager {

   /**
    * Initializes the UndoHistoryManager.
    * 
    * @param maxHistory The maximal number of "history entries".
    */
   void init(int maxHistory);

   /**
    * Registers another group of events into the history.
    * 
    * @param xpdlInfoList The list of {@link XPDLElementChangeInfo} objects.
    * @param selEv The current selection information.
    */
   void registerEvents(List xpdlInfoList, XPDLElementChangeInfo selEv);

   /**
    * Undoes the changes.
    */
   void undo();

   /**
    * Re-does the changes.
    */
   void redo();

   /**
    * @return true if changes can be undone.
    */
   boolean canUndo();

   /**
    * @return true if changes can be re-done.
    */
   boolean canRedo();

   /**
    * @return true if undo/re-do is in progress.
    */
   boolean isUndoOrRedoInProgress();

   /**
    * Cleans undo/redo history.
    */
   public void cleanHistory();

}
