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
