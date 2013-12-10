package org.enhydra.jawe.base.controller.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jawe.base.controller.SearchEntity;
import org.enhydra.jawe.base.controller.SearchUtility;
import org.enhydra.jawe.base.editor.StandardXPDLElementEditor;
import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLComplexChoice;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLSimpleElement;

/**
 * Class that implements search action.
 */
public class Search extends ActionBase {

   private SearchEntity searchEntity;

   public Search(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      setEnabled(getPackage() != null);
   }

   public void actionPerformed(ActionEvent e) {
      if (searchEntity == null) {
         searchEntity = new SearchEntity();
      }
      while (true) {
         StandardXPDLElementEditor ed = new StandardXPDLElementEditor();
         ed.editXPDLElement(searchEntity);

         if (ed.getStatus() == StandardXPDLElementEditor.STATUS_OK) {
            try {
               List searchResult = new ArrayList();
               if (searchEntity.getSearchScope().equals(SearchEntity.SCOPE_MAIN_PACKAGE)) {
                  searchResult = SearchUtility.searchElement(getPackage(), searchEntity);
               } else if (searchEntity.getSearchScope()
                  .equals(SearchEntity.SCOPE_ALL_PACKAGES)) {
                  Iterator it = JaWEManager.getInstance()
                     .getXPDLHandler()
                     .getAllPackages()
                     .iterator();
                  while (it.hasNext()) {
                     SearchUtility.searchElement((XMLComplexElement) it.next(),
                                                 searchEntity,
                                                 searchResult);
                  }
               } else {
                  XMLElement el = JaWEManager.getInstance()
                     .getJaWEController()
                     .getSelectionManager()
                     .getSelectedElement();
                  if (el instanceof XMLComplexElement) {
                     SearchUtility.searchElement((XMLComplexElement) el,
                                                 searchEntity,
                                                 searchResult);
                  } else if (el instanceof XMLCollection) {
                     SearchUtility.searchElement((XMLCollection) el,
                                                 searchEntity,
                                                 searchResult);
                  } else if (el instanceof XMLComplexChoice) {
                     SearchUtility.searchElement((XMLComplexChoice) el,
                                                 searchEntity,
                                                 searchResult);
                  } else if (el instanceof XMLSimpleElement) {
                     SearchUtility.searchElement((XMLSimpleElement) el,
                                                 searchEntity,
                                                 searchResult);
                  } else if (el instanceof XMLAttribute) {
                     SearchUtility.searchElement((XMLAttribute) el,
                                                 searchEntity,
                                                 searchResult);
                  }
               }
               XMLAttribute fakeForSearch = new XMLAttribute(null,
                                                             searchEntity.getContainingText(),
                                                             false);

               XPDLElementChangeInfo info = JaWEManager.getInstance()
                  .getJaWEController()
                  .createInfo(fakeForSearch,
                              Utils.makeSearchResultList(searchResult),
                              XPDLElementChangeInfo.SEARCH_RESULT);
               if (info != null) {
                  JaWEManager.getInstance().getJaWEController().sendEvent(info);
               }

               break;
            } catch (Exception ex) {
               ex.printStackTrace();
               JaWEManager.getInstance()
                  .getJaWEController()
                  .message(ResourceManager.getLanguageDependentString("ErrorSearchFailed")
                                 + "\n" + ex.getClass() + ":" + ex.getMessage(),
                           JOptionPane.ERROR_MESSAGE);
            }
         } else {
            break;
         }
      }
   }

}
