package org.enhydra.jawe.base.panel.panels;

import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;

/**
 * Creates a table panel.
 * 
 * @author Sasa Bojanic
 * @author Zoran Milakovic
 * @author Miroslav Popov
 */
public class XMLSimpleTableSelPanel extends XMLSimpleTablePanel {

   public XMLSimpleTableSelPanel(PanelContainer pc, XMLCollection myOwner, List columnsToShow, List elementsToShow,
         String title, boolean hasBorder, boolean hasEmptyBorder, boolean automaticWidth) {
      super(pc, myOwner, columnsToShow, elementsToShow, title, hasBorder, hasEmptyBorder, automaticWidth);

   }

   protected void setupTable(boolean automaticWidth) {
      super.setupTable(automaticWidth);

      // selection listener for sending selection event
      ListSelectionModel lsm = allItems.getSelectionModel();
      lsm.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            // Ignore extra messages.
            if (e.getValueIsAdjusting())
               return;

            ListSelectionModel ls = (ListSelectionModel) e.getSource();
            if (ls.isSelectionEmpty()) {

            } else {
               try {
                  XMLElement el = getSelectedElement();
                  if (el != null) {
                     JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection(el, true);
                  }
               } catch (Exception ex) {
               }
            }
         }
      });

   }

}
