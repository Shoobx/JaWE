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

package org.enhydra.jawe.base.editor;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLElementChangeInfo;
import org.enhydra.shark.xpdl.elements.Package;

/**
 *  
 */
public class TogWEStandardXPDLElementEditor extends JDialog implements JaWEComponent, Observer, XPDLElementEditor {
   
   public final static int STATUS_OK = 0;
   public final static int STATUS_CANCEL = 1;
   protected int status = STATUS_OK;

   protected String type = JaWEComponent.OTHER_COMPONENT;
   
   protected InlinePanel inlinePanel;

   protected PanelSettings settings;  
   
   protected boolean updateInProgress;
   
   public TogWEStandardXPDLElementEditor(PanelSettings settings) {
      super(JaWEManager.getInstance().getJaWEController().getJaWEFrame());
      this.settings = settings;
      init();
   }

   //************* JDialog
   public String getName() {
      return "STANDARD_XPDL_EDITOR";
   }   
   
   public void requestFocus() {
      try {
         inlinePanel.getDisplay().requestFocus();
      } catch (Exception ex) {
//         dialog.requestFocus();
      }
   }
   //************* 
   
   //************* JaWEComponent
   public JaWEComponentSettings getSettings() {
      return settings;
   }

   public JaWEComponentView getView() {
      return inlinePanel;
   }

   public String getType() {
      return type;
   }   

   public void setType(String type) {
      this.type = type; 
   }
   
   public boolean adjustXPDL(Package pkg) {
      return false;
   }
   
   public List checkValidity (XMLElement el,boolean fullCheck) {
      return null;
   }
   
   public boolean canCreateElement(XMLCollection col) {
      return true;
   }

   public boolean canInsertElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canModifyElement(XMLElement col) {
      return true;
   }

   public boolean canRemoveElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canDuplicateElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canRepositionElement(XMLCollection col, XMLElement el) {
      return true;
   }
   //************* 
   
   //************* Observer
   public void update(Observable o, Object arg) {
      if (!isVisible()) return;
      if (!(arg instanceof XPDLElementChangeInfo))
         return;
      if( isUpdateInProgress() ) return; 
      XPDLElementChangeInfo info = (XPDLElementChangeInfo) arg;
      int action=info.getAction();      
      if (!(action == XMLElementChangeInfo.UPDATED ||
            action == XMLElementChangeInfo.INSERTED ||
            action == XMLElementChangeInfo.REMOVED ||      
            action == XPDLElementChangeInfo.SELECTED ||
            action == XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED ||
            action == XPDLElementChangeInfo.UNDO ||
            action == XPDLElementChangeInfo.REDO)) 
         return;
      
      XMLElement changedElement = info.getChangedElement();

      long start=System.currentTimeMillis();
      updateInProgress=true;
      try {
         if (action==XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED ||
               action==XPDLElementChangeInfo.UNDO ||
               action==XPDLElementChangeInfo.REDO) {
            if (action==XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED) {
               for (int i=0; i<info.getChangedSubElements().size(); i++) {
                  XPDLElementChangeInfo subinfo=(XPDLElementChangeInfo)info.getChangedSubElements().get(i);
                  if (subinfo.getAction()==XMLElementChangeInfo.REMOVED) {
                     inlinePanel.update(subinfo);
                  }
               }
            }          
            inlinePanel.setActiveElement(null);
         } else {
            inlinePanel.update(info);
         }
      } finally {
         updateInProgress=false;
      }
      if (changedElement != null) {
         String t=getEditingPanel().getTitle();
         if (t==null || t.equals("")) {
            t=JaWEManager.getInstance().getLabelGenerator().getLabel(changedElement);
         }
         setTitle(t);
      }
      long end=System.currentTimeMillis();
      double diffs=(end-start)/1000.0;
      JaWEManager.getInstance().getLoggingManager().debug("THE UPDATE OF TogWE Standard XPDL Editor COMPONENT LASTED FOR "+diffs+" SECONDS!");                       
      
      setDialogVisible();

   }
   //************* 
   
   //************* XPDLElementEditor   
   public void configure() {
   }

   public XMLElement getEditingElement() {
      return inlinePanel.getActiveElement();
   }

   public void editXPDLElement(XMLElement el) {
      inlinePanel.setActiveElement(el);
      if (el != null) {
         String t=getEditingPanel().getTitle();
         if (t==null || t.equals("")) {
            t=JaWEManager.getInstance().getLabelGenerator().getLabel(el);
         }
         setTitle(t);
      }
      
      setDialogVisible();
   }

   public void editXPDLElement() {
      editXPDLElement(JaWEManager.getInstance().getJaWEController().getSelectionManager().getSelectedElement());
   }   
   
   public boolean canApplyChanges() {
      return true;
   }

   public int getStatus() {
      return status;
   }

   public Window getWindow() {
      return this;
   }

   public Window getParentWindow() {
      return JaWEManager.getInstance().getJaWEController().getJaWEFrame();
   }   
   
   public void close() {
      status = STATUS_CANCEL;
      if( inlinePanel.isModified() ) {
         int sw=inlinePanel.showModifiedWarning();
         if( sw == JOptionPane.CANCEL_OPTION || (sw==JOptionPane.YES_OPTION && inlinePanel.isModified())) return;
      }
      setVisible(false);
      inlinePanel.setActiveElement(null);
      JaWEManager.getInstance().getJaWEController().adjustActions();
      getInlinePanel().cleanup();
      dispose();
   }
   
   public XPDLElementEditor getParentEditor () {
      return null;
   }
   
   public void setModified (boolean modif) {      
   }   
   //*************    
   
   protected void init() {
      try {
         ClassLoader cl=getClass().getClassLoader();
         inlinePanel=(InlinePanel)cl.loadClass(JaWEManager.getInstance().getInlinePanelClassName()).newInstance();
      } catch (Exception ex) {
         String msg = "TogWEStandardXPDLElementEditor --> Problems while instantiating InlinePanel class '"
            + JaWEManager.getInstance().getInlinePanelClassName() + "' - using default implementation!";
         JaWEManager.getInstance().getLoggingManager().error(msg, ex);         
         inlinePanel=new InlinePanel();
      }
      try {
         inlinePanel.setJaWEComponent(this);
         // settings must be initialized after creation of InlinePanel
         this.settings.init(this);
         inlinePanel.init();

         JaWEManager.getInstance().getJaWEController().addObserver(this);

         initDialog();

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /** Returns the panel that is currently beeing edited. */
   public XMLPanel getEditingPanel() {
      return inlinePanel.getViewPanel();
   }

   protected void setDialogVisible() {
      setSize(this.inlinePanel.getDisplay().getSize());
      pack();
      if(!isVisible()) {
         setLocationRelativeTo(getParentWindow());
         setVisible(true);
      }
      JaWEManager.getInstance().getJaWEController().adjustActions();
   }

   protected void initDialog() {
      try {
         Container cp = getContentPane();
         cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

         cp.add(inlinePanel.getDisplay());
         getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
               KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "Cancel");
         getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(
               KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "Cancel");
         getRootPane().getActionMap().put("Cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               al.actionPerformed(e);
            }
         });
         addWindowListener(wl);

      } catch (Exception e) {
         e.printStackTrace();
      }
      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      setResizable(true);
   }

   protected WindowListener wl = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
         close();
      }
   };

   protected ActionListener al = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
         close();
      }
   };

   public InlinePanel getInlinePanel() {
      return this.inlinePanel;
   }
   
   public void setUpdateInProgress(boolean inProgress) {
      updateInProgress = inProgress;
   }
   
   public boolean isUpdateInProgress() {
      return updateInProgress;
   }
   

}