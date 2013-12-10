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

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Package;

/**
 * The dialog for showing objects derived from {@link XMLPanel} classes. The
 * given XMLPanel object must have it's owner, which type is a class
 * derived from the {@link XMLElement} class, and serves as a representation
 * of an XML tag.
 * <p> The dialog enables editing of all editable fields contained
 * within given panel and after user presses OK button, the new values
 * contained within edited fields are set to corresponding members of
 * panel's owner.
 */
public class StandardXPDLElementEditor extends JDialog implements XPDLElementEditor {
   protected String type = JaWEComponent.OTHER_COMPONENT;
   
   public final static int STATUS_OK = 0;
   public final static int STATUS_CANCEL = 1;

   protected boolean isModified=false;
   
   protected Properties properties=new Properties();

   protected XPDLElementEditor parentEditor;
   protected XMLElement originalElement;
   protected JButton buttonOK;
   protected JButton buttonCancel;

   protected int status = STATUS_OK;

   protected InlinePanel inlinePanel;

   protected boolean undoableChange=false;
   
   private StandardXPDLElementEditorSettings settings;
   
   public void configure () {      
   }

   public void setProperty (String key,String value) {
      properties.setProperty(key, value);
   }

   public JaWEComponentSettings getSettings() {
      return settings;
   }
   
   public StandardXPDLElementEditor () {
      super(JaWEManager.getInstance().getJaWEController().getJaWEFrame(),true); 
      settings=new StandardXPDLElementEditorSettings();
      init();
   }

   public StandardXPDLElementEditor (boolean undoableChange) {
      super(JaWEManager.getInstance().getJaWEController().getJaWEFrame(),true);
      settings=new StandardXPDLElementEditorSettings();
      this.undoableChange=undoableChange;
      init();
   }

   public StandardXPDLElementEditor (StandardXPDLElementEditor parentEditor) {
      super(parentEditor,true); 
      settings=new StandardXPDLElementEditorSettings();
      init();
   }
   
   protected void init () {
      try {
         ClassLoader cl=getClass().getClassLoader();
         inlinePanel=(InlinePanel)cl.loadClass(JaWEManager.getInstance().getInlinePanelClassName()).newInstance();
      } catch (Exception ex) {
         String msg = "StandardXPDLElementEditor --> Problems while instantiating InlinePanel class '"
            + JaWEManager.getInstance().getInlinePanelClassName() + "' - using default implementation!";
         JaWEManager.getInstance().getLoggingManager().error(msg, ex);         
         inlinePanel=new InlinePanel();
      }
      try {
         inlinePanel.setJaWEComponent(this);
         // settings must be initialized after creation of InlinePanel
         settings.init(this);
         inlinePanel.init();
   
         initDialog();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }
   
   public String getLanguageDependentString(String nm) {
      return ResourceManager.getLanguageDependentString(nm);
   }
   
   public void setTitle (String title) {
      super.setTitle(title);
   }

   public XPDLElementEditor getParentEditor () {
      return parentEditor;
   }

   /** Returns the panel that is currently beeing edited. */
   public XMLPanel getEditingPanel () {
      return inlinePanel.getViewPanel();
   }

   public XMLElement getEditingElement() {
      return originalElement;
   }
   
   public void editXPDLElement (XMLElement el) {
      inlinePanel.setActiveElement(el);
      if (el != null) {
         String t=getEditingPanel().getTitle();
         if (t==null || t.equals("")) {
            t=JaWEManager.getInstance().getLabelGenerator().getLabel(el);
         }
         setTitle(t);
      }
      setSize(this.inlinePanel.getDisplay().getSize());
      pack();      
      setLocationRelativeTo(getParentWindow());
      setVisible(true);      
   }

   public void editXPDLElement () {
      editXPDLElement(JaWEManager.getInstance().getJaWEController().getSelectionManager().getSelectedElement());
   }


   public boolean canApplyChanges () {
      return inlinePanel.canApplyChanges();
   }

   public void applyChanges () {      
      XMLElement el=inlinePanel.getActiveElement();
      JaWEController jc=JaWEManager.getInstance().getJaWEController();
      if (undoableChange) {
         jc.startUndouableChange();         
      }
      inlinePanel.apply();
      if (undoableChange) {
         List toSelect = new ArrayList();
         toSelect.add(el);
         jc.endUndouableChange(toSelect);
      }
   }


   public int getStatus() {
       return status;
   }

   public Window getWindow () {
      return this;
   }

   public Window getParentWindow () {
      if (parentEditor==null) return JaWEManager.getInstance().getJaWEController().getJaWEFrame();
      return parentEditor.getWindow();
   }

   protected void initDialog () {
      try {
         JPanel buttonPanel=new JPanel();
         buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));

         buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
         buttonPanel.setAlignmentY(Component.TOP_ALIGNMENT);

         buttonOK=new JButton(getLanguageDependentString("OKKey"));

         buttonCancel=new JButton(getLanguageDependentString("CancelKey"));

         buttonPanel.add(Box.createHorizontalGlue());
         buttonPanel.add(buttonOK);
         buttonPanel.add(Box.createHorizontalStrut(4));
         buttonPanel.add(buttonCancel);
         buttonPanel.add(Box.createHorizontalStrut(4));

         Container cp=getContentPane();
         cp.setLayout(new BoxLayout(cp,BoxLayout.Y_AXIS));

         cp.add(inlinePanel);
         cp.add(Box.createVerticalStrut(5));
         cp.add(buttonPanel);

         // action listener for confirming
         buttonOK.addActionListener(okl);

//       action listener for cancel
         buttonCancel.addActionListener(al);
         addWindowListener(wl);

         getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0,false),"Cancel");
         getRootPane().getActionMap().put("Cancel", new AbstractAction() {
                  public void actionPerformed(ActionEvent e) {
                     al.actionPerformed(e);
                  }
               });

      } catch (Exception e) {
         e.printStackTrace();
      }
      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      setResizable(true);
      buttonOK.setDefaultCapable(true);
      getRootPane().setDefaultButton(buttonOK);
   }

   
   protected WindowListener wl=new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
         close();
      }
   };

   JDialog dialog=this;
   protected ActionListener al=new ActionListener(){
      public void actionPerformed( ActionEvent ae ){
         close();
      }
   };
   
   public void close () {
      if ((inlinePanel.isModified() || isModified)
            && properties.getProperty("XPDLElementEditor.ConfirmCancelOnDataChange", "true").equals("true")) {
         int yn = JOptionPane.showConfirmDialog(dialog, getLanguageDependentString("WarningReallyQuit"), "",
               JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

         if (yn == JOptionPane.NO_OPTION) {
            return;
         }
      }
      status = STATUS_CANCEL;
      dispose();      
   }

   protected ActionListener okl=new ActionListener(){
      public void actionPerformed( ActionEvent ae ){
         if (inlinePanel.getViewPanel().getOwner().isReadOnly()) {
            status = STATUS_CANCEL;
            dispose();
         } else if (canApplyChanges()) {
            applyChanges();
            status = STATUS_OK;
            dispose();
//            if (getParent() != null){
//               getParent().repaint();//do repaint
//            }
            if (parentEditor!=null) {
               parentEditor.setModified(true);
            }
            
         }
      }
   };
   
   public void setModified (boolean modif) {
      isModified=true;
   }
   
   public JaWEComponentView getView() {
      return inlinePanel;
   }

   public JComponent getDisplay() {
      return inlinePanel.getDisplay();
   }

   public String getType() {
      return type;
   }   

   public void setType(String type) {
      this.type = type; 
   }
   
   public String getName() {
      return "STANDARD_XPDL_EDITOR";
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

   public boolean canModifyElement(XMLCollection col, XMLElement el) {
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

   public Properties getProperties() {
      return properties;
   }

   public void setUpdateInProgress(boolean inProgress) {
      
   }
   
   public boolean isUpdateInProgress() {
      return false;
   }
   
}
