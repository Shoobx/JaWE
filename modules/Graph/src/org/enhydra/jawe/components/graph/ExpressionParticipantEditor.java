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

package org.enhydra.jawe.components.graph;

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
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanel;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XPDLConstants;
import org.enhydra.shark.xpdl.elements.Activities;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.Participant;

/**
 * The dialog for showing special FreeTextExpression and CommonExpression participant
 * objects used for the JaWE's graph component.
 * @author Sasa Bojanic
 */
public class ExpressionParticipantEditor {
   public final static int STATUS_OK = 0;
   public final static int STATUS_CANCEL = 1;

   protected JDialog dialog;

   protected Properties properties=new Properties();

   protected Participant elementToEdit;
   protected XMLPanel panelToEdit=new XMLBasicPanel();
   protected JButton buttonOK;
   protected JButton buttonCancel;
   protected String title = null;
   protected XMLMultiLineTextPanel pIdPanel;
   
   protected int status = STATUS_OK;

   public void configure (Properties props) {
      this.properties=props;
   }

   public void setProperty (String key,String value) {
      properties.setProperty(key, value);
   }

   public ExpressionParticipantEditor (Participant el) {
       elementToEdit=el;
       dialog=new JDialog(JaWEManager.getInstance().getJaWEController().getJaWEFrame(),true);
       this.title = ResourceManager.getLanguageDependentString(el.toName()+"Key");
       
       initDialog();
   }

   public void setTitle (String title) {
      if (dialog!=null) {
         dialog.setTitle(title);
      }
   }

   public void editXPDLElement () {
      editXPDLElement(this.getParticipantPanel());
   }

   public void editXPDLElement (XMLPanel panel) {
      Container cp=dialog.getContentPane();
      cp.remove(panelToEdit);
      panelToEdit=panel;

      cp.add(panelToEdit,0);
      dialog.pack();

      dialog.setLocationRelativeTo(getParentWindow());
      dialog.pack();
      dialog.setVisible(true);
//      requestFocus();

   }

   public boolean canApplyChanges () {
      if (panelToEdit.validateEntry()) {
         String id=pIdPanel.getText();
         Participant p=CommonExpressionParticipants.getInstance().getCommonExpressionParticipant(((CommonExpressionParticipant)elementToEdit).getGraphXPDLElement(),id);
         if (p!=null && p!=elementToEdit) {
            return false;
         }
         return true;
      } 
      return false;      
   }

   public void applyChanges () {
      String oldId=elementToEdit.getId();
      String newId=pIdPanel.getText();
      if (!newId.equals(oldId)) {
         XMLCollectionElement wpOrAs=((CommonExpressionParticipant)elementToEdit).getGraphXPDLElement();
         List vo=GraphUtilities.getParticipantVisualOrder(wpOrAs);
         String toRemove=
            GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_PREFIX+
            oldId+
            GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_SUFIX;
         int ind=vo.indexOf(toRemove);
         vo.remove(ind);
         String toAdd=
            GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_PREFIX+
            newId+
            GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_SUFIX;

         vo.add(ind,toAdd);
         elementToEdit.setId(newId);
         GraphController gc=GraphUtilities.getGraphController();
         gc.setUpdateInProgress(true);
         JaWEManager.getInstance().getJaWEController().startUndouableChange();  
         List acts=((Activities)wpOrAs.get("Activities")).toElements();
         acts=GraphUtilities.getAllActivitiesForParticipantId(acts, oldId);
         for (int i=0; i<acts.size(); i++) {
            Activity act=(Activity)acts.get(i);
            int type=act.getActivityType();
            if (type==XPDLConstants.ACTIVITY_TYPE_NO || type==XPDLConstants.ACTIVITY_TYPE_TOOL) {
               act.setPerformer(newId);
            }
         }
         GraphUtilities.setNewParticipantId(acts, newId);
         String asId=null;
         if (wpOrAs instanceof ActivitySet) {
            asId=wpOrAs.getId();
         }
         GraphUtilities.adjustBubbles(XMLUtil.getWorkflowProcess(wpOrAs), asId, GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID, oldId, newId);
         GraphUtilities.setParticipantVisualOrder(wpOrAs, vo);
         List toSelect=new ArrayList();
         JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
         gc.setUpdateInProgress(false);
      }
   }

   public void requestFocus() {
      try {
//         if (panelToEdit instanceof XMLGroupPanel) {
//            if (panelToEdit.getComponent(0).isEnabled()) {
//               panelToEdit.getComponent(0).requestFocus();
//            } else {
//               panelToEdit.getComponent(1).requestFocus();
//            }
//         }
      } catch (Exception ex) {
         panelToEdit.requestFocus();
      }
   }

   public int getStatus() {
       return status;
   }

   public Window getWindow () {
      return dialog;
   }

   public Window getParentWindow () {
      return JaWEManager.getInstance().getJaWEController().getJaWEFrame();
   }

   protected void initDialog () {
      try {
         JPanel buttonPanel=new JPanel();
         buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));

         buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
         buttonPanel.setAlignmentY(Component.TOP_ALIGNMENT);

         buttonOK=new JButton(ResourceManager.getLanguageDependentString("OKKey"));

         buttonCancel=new JButton(ResourceManager.getLanguageDependentString("CancelKey"));

         buttonPanel.add(Box.createHorizontalGlue());
         buttonPanel.add(buttonOK);
         buttonPanel.add(Box.createHorizontalStrut(4));
         buttonPanel.add(buttonCancel);
         buttonPanel.add(Box.createHorizontalStrut(4));

         Container cp=dialog.getContentPane();
         cp.setLayout(new BoxLayout(cp,BoxLayout.Y_AXIS));

         cp.add(panelToEdit);
         cp.add(Box.createVerticalStrut(5));
         cp.add(buttonPanel);

         // action listener for confirming
         buttonOK.addActionListener(okl);

//       action listener for cancel
         buttonCancel.addActionListener(cl);
         dialog.addWindowListener(wl);

         dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0,false),"Cancel");
         dialog.getRootPane().getActionMap().put("Cancel", new AbstractAction() {
                  public void actionPerformed(ActionEvent e) {
                     cl.actionPerformed(e);
                  }
               });

      } catch (Exception e) {
         e.printStackTrace();
      }
      dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      dialog.setResizable(true);
      //dialog.setLocationRelativeTo(getParentWindow());
      buttonOK.setDefaultCapable(true);
      dialog.getRootPane().setDefaultButton(buttonOK);
      dialog.setTitle(this.title);

   }

   
   protected WindowListener wl=new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
          //make configurable?
          status = STATUS_CANCEL;
          dialog.dispose();
      }
   };

   protected ActionListener cl=new ActionListener(){
      public void actionPerformed( ActionEvent ae ){
         status = STATUS_CANCEL;
         dialog.dispose();
      }
   };
   
   protected ActionListener okl=new ActionListener(){
      public void actionPerformed( ActionEvent ae ){
         if (elementToEdit == null || panelToEdit.getOwner().isReadOnly()) {
            status = STATUS_CANCEL;
            dialog.dispose();
         } else if (canApplyChanges()) {
            applyChanges();
            status = STATUS_OK;
            dialog.dispose();
            if (dialog.getParent() != null){
               dialog.getParent().repaint();//do repaint
            }
         }
      }
   };
   
   protected XMLGroupPanel getParticipantPanel () {
      List toShow=new ArrayList();
      if (elementToEdit instanceof CommonExpressionParticipant) {
         pIdPanel=new XMLMultiLineTextPanel(null, elementToEdit.get("Id"),"Xpression",false,true,XMLMultiLineTextPanel.SIZE_MEDIUM,false,!elementToEdit.isReadOnly());
         toShow.add(pIdPanel);
         XMLPanel descPnl=new XMLMultiLineTextPanel(null, elementToEdit.get("Description"),true,XMLMultiLineTextPanel.SIZE_MEDIUM,true,false);
         toShow.add(descPnl);
      }
      XMLGroupPanel gp=new XMLGroupPanel(null, elementToEdit,toShow,"",true,false,true);
      return gp;
   }

}
