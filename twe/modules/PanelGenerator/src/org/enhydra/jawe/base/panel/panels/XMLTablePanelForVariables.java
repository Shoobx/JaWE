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

package org.enhydra.jawe.base.panel.panels;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.ButtonPropertyChangedListener;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.controller.JaWESelectionManager;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XPDLConstants;
import org.enhydra.shark.xpdl.elements.BasicType;
import org.enhydra.shark.xpdl.elements.DataField;
import org.enhydra.shark.xpdl.elements.DataFields;
import org.enhydra.shark.xpdl.elements.DeclaredType;
import org.enhydra.shark.xpdl.elements.FormalParameter;
import org.enhydra.shark.xpdl.elements.FormalParameters;
import org.enhydra.shark.xpdl.elements.SchemaType;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

public class XMLTablePanelForVariables extends XMLTablePanel{
   public static final String CONVERT_ACTION = "Convert";

   protected Action convertElementAction;
   
   public XMLTablePanelForVariables(InlinePanel ipc,
                             XMLCollection myOwner,
                             List columnsToShow,
                             List elementsToShow,
                             String title,
                             boolean hasBorder,
                             boolean hasEmptyBorder,
                             boolean automaticWidth,
                             boolean miniDimension,
                             final boolean colors,
                             final boolean showArrows){
      super(ipc, myOwner, columnsToShow, elementsToShow, title, hasBorder, hasEmptyBorder,
            automaticWidth, miniDimension, colors, showArrows);
      
      
   }

   public void convert() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      
      XMLElement el = jc.getSelectionManager().getSelectedElement();
      if (el!=null && el.getParent() instanceof  DataFields) {
         jc.startUndouableChange();
         
         FormalParameters fp;
         if (((WorkflowProcess) el.getParent().getParent()).getFormalParameters() != null)
            fp = ((WorkflowProcess) el.getParent().getParent()).getFormalParameters();
         else
            fp = new FormalParameters((WorkflowProcess) el.getParent().getParent());
         FormalParameter newFormalParam = JaWEManager.getInstance()
            .getXPDLObjectFactory()
            .createXPDLObject(fp, "",true);
         newFormalParam.setId(((DataField) el).getId());
         
         newFormalParam.setDescription(((DataField) el).getDescription());
         
         XMLElement choosen = ((DataField) el).getDataType().getDataTypes().getChoosen();
         if (choosen instanceof BasicType && ((BasicType) choosen).getType().equals(XPDLConstants.BASIC_TYPE_STRING))
            newFormalParam.getDataType().getDataTypes().getBasicType().setTypeSTRING();
         else if (choosen instanceof BasicType && ((BasicType) choosen).getType().equals(XPDLConstants.BASIC_TYPE_FLOAT))
            newFormalParam.getDataType().getDataTypes().getBasicType().setTypeFLOAT();
         else if (choosen instanceof BasicType && ((BasicType) choosen).getType().equals(XPDLConstants.BASIC_TYPE_INTEGER))
            newFormalParam.getDataType().getDataTypes().getBasicType().setTypeINTEGER();
         else if (choosen instanceof BasicType && ((BasicType) choosen).getType().equals(XPDLConstants.BASIC_TYPE_DATETIME))
            newFormalParam.getDataType().getDataTypes().getBasicType().setTypeDATETIME();
         else if (choosen instanceof BasicType && ((BasicType) choosen).getType().equals(XPDLConstants.BASIC_TYPE_BOOLEAN))
            newFormalParam.getDataType().getDataTypes().getBasicType().setTypeBOOLEAN();
         else if (choosen instanceof DeclaredType){            
            newFormalParam.getDataType().getDataTypes().setDeclaredType();
            newFormalParam.getDataType()
               .getDataTypes()
               .getDeclaredType()
               .setId(((DeclaredType)choosen).getId());
         }
         else if (choosen instanceof SchemaType)
            newFormalParam.getDataType().getDataTypes().setSchemaType();
         
            
//         JaWEManager.getInstance().getJaWEController().getEdit().delete();
         ((DataFields)getOwner()).remove(el);
         List toSelect = new ArrayList();
         toSelect.add(fp);
         jc.endUndouableChange(toSelect);
      }
      else if (el!=null && el.getParent() instanceof  FormalParameters){
         jc.startUndouableChange();
         

         DataFields df;
         if (((WorkflowProcess) el.getParent().getParent()).getDataFields() != null)
            df = ((WorkflowProcess) el.getParent().getParent()).getDataFields();
         else
            df = new DataFields((WorkflowProcess) el.getParent().getParent());
         DataField newDataField = JaWEManager.getInstance()
            .getXPDLObjectFactory()
            .createXPDLObject(df, "",true);
         newDataField.setId(((FormalParameter) el).getId());
         
         newDataField.setDescription(((FormalParameter) el).getDescription());
         XMLElement choosen = ((FormalParameter) el).getDataType().getDataTypes().getChoosen();
         if (choosen instanceof BasicType && ((BasicType) choosen).getType().equals(XPDLConstants.BASIC_TYPE_STRING))
            newDataField.getDataType().getDataTypes().getBasicType().setTypeSTRING();
         else if (choosen instanceof BasicType && ((BasicType) choosen).getType().equals(XPDLConstants.BASIC_TYPE_FLOAT))
            newDataField.getDataType().getDataTypes().getBasicType().setTypeFLOAT();
         else if (choosen instanceof BasicType && ((BasicType) choosen).getType().equals(XPDLConstants.BASIC_TYPE_INTEGER))
            newDataField.getDataType().getDataTypes().getBasicType().setTypeINTEGER();
         else if (choosen instanceof BasicType && ((BasicType) choosen).getType().equals(XPDLConstants.BASIC_TYPE_DATETIME))
            newDataField.getDataType().getDataTypes().getBasicType().setTypeDATETIME();
         else if (choosen instanceof BasicType && ((BasicType) choosen).getType().equals(XPDLConstants.BASIC_TYPE_BOOLEAN))
            newDataField.getDataType().getDataTypes().getBasicType().setTypeBOOLEAN();
         else if (choosen instanceof DeclaredType){            
            newDataField.getDataType().getDataTypes().setDeclaredType();
            newDataField.getDataType()
               .getDataTypes()
               .getDeclaredType()
               .setId(((DeclaredType)choosen).getId());
         }
         else if (choosen instanceof SchemaType)
            newDataField.getDataType().getDataTypes().setSchemaType();
         
//         JaWEManager.getInstance().getJaWEController().getEdit().delete();
         ((FormalParameters)getOwner()).remove(el);
         
         List toSelect = new ArrayList();
         toSelect.add(df);
         jc.endUndouableChange(toSelect);
         
      }
   }
   protected JPanel createToolbar(boolean useBasicToolbar) {
      convertElementAction = new AbstractAction(CONVERT_ACTION) {
         public void actionPerformed(ActionEvent ae) {
            
            XMLElement editElement = getSelectedElement();
            
            if (editElement != null) {
               XMLElement parent = editElement.getParent();
               JaWESelectionManager sm = JaWEManager.getInstance()
                  .getJaWEController()
                  .getSelectionManager();
               ipc.getJaWEComponent().setUpdateInProgress(true);
               
               allItems.clearSelection();
               sm.setSelection(editElement, false);
               convert();
               
               ipc.getPanelSettings().adjustActions();
               XMLElement newSelection = sm.getSelectedElement();

               if (newSelection != parent) {
                  if (!setSelectedElement(newSelection)) {
                     sm.setSelection(ipc.getActiveElement(), true);
                  }
               }

               lostFocusHandle = true;
               ipc.getJaWEComponent().setUpdateInProgress(false);

               adjustActions();
            }
         }
      };
      
      JPanel panel = new JPanel();
      
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

      JButton buttonNew = PanelUtilities.createToolbarButton(ipc.getSettings(),
                                                             newElementAction,
                                                             this);
      buttonNew.setRolloverEnabled(true);
      JButton buttonEdit = PanelUtilities.createToolbarButton(ipc.getSettings(),
                                                              editElementAction);
      buttonEdit.setRolloverEnabled(true);
      JButton buttonReferences = PanelUtilities.createToolbarButton(ipc.getSettings(),
                                                                    referencesElementAction);
      buttonReferences.setRolloverEnabled(true);
      JButton buttonDelete = PanelUtilities.createToolbarButton(ipc.getSettings(),
                                                                deleteElementAction);
      buttonDelete.setRolloverEnabled(true);
      JButton buttonDuplicate = PanelUtilities.createToolbarButton(ipc.getSettings(),
                                                                   duplicateElementAction);
      buttonDuplicate.setRolloverEnabled(true);
      JButton buttonConvert = createToolbarButtonConvert(ipc.getSettings(),
                                                                   convertElementAction);
        
      buttonConvert.setRolloverEnabled(true);

      panel.add(buttonNew);
      panel.add(Box.createRigidArea(new Dimension(3, 3)));
      panel.add(buttonEdit);
      panel.add(Box.createRigidArea(new Dimension(3, 3)));
      panel.add(buttonDelete);
      panel.add(Box.createRigidArea(new Dimension(5, 3)));
      
      if (!useBasicToolbar) {
         panel.add(buttonDuplicate);
         panel.add(Box.createRigidArea(new Dimension(3, 3)));
         panel.add(buttonReferences);
         panel.add(buttonConvert);
      }
      panel.add(Box.createHorizontalGlue());
      return panel;
   }
   public static JButton createToolbarButtonConvert(Settings s,
                                             Action a) {
      if (a == null)
         return null;
      

      String actionName = (String) a.getValue(Action.NAME);
      JButton b = null;
      
      ImageIcon curIc = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/convert_small.gif"));
      
      
         b = new JButton(curIc) {
            public float getAlignmentY() {
               return 0.5f;
            }
         };
      

      b.setName(actionName);
      b.setMargin(new Insets(1, 1, 1, 1));
      b.setRequestFocusEnabled(false);

      b.addActionListener(a);
      a.addPropertyChangeListener(new ButtonPropertyChangedListener(b));

      String tip = s.getLanguageDependentString(actionName + BarFactory.TOOLTIP_POSTFIX);
      if (tip != null) {
         b.setToolTipText(tip);
      }

      return b;
   }
   protected void adjustActions() {
      super.adjustActions();
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      JaWESelectionManager sm = jc.getSelectionManager();
      
      
      convertElementAction.setEnabled((getSelectedElement() != null && sm.canDuplicate()));
   }
   
   

}
