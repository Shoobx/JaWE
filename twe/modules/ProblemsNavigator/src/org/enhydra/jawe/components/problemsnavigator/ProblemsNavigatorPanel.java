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

package org.enhydra.jawe.components.problemsnavigator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.TableModel;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.xpdlvalidator.ValidationError;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * Panel for displaying problems.
 * 
 * @author Sasa Bojanic
 */
public class ProblemsNavigatorPanel extends JPanel implements JaWEComponentView {

   protected JToolBar toolbar;
   protected JLabel problemsInfo=new JLabel();
   protected JPanel innerPanel=new JPanel();
   protected JScrollPane scrollPane;
   protected ProblemsTablePanel table;   
   
   protected ProblemsNavigator controller;
   
   protected List allProblems=new ArrayList();
   
   public ProblemsNavigatorPanel(ProblemsNavigator treeViewCtrl) {
      this.controller = treeViewCtrl;
   }

   public void configure() {
      init();
   }

   public void init () {
      setBorder(BorderFactory.createEtchedBorder());
      setLayout(new BorderLayout());
      toolbar = BarFactory.createToolbar("defaultToolbar", controller);   
      toolbar.setFloatable(false); 
      if (toolbar.getComponentCount() > 0) {
         add(toolbar,BorderLayout.NORTH);
      }
      
      table=new ProblemsTablePanel(controller);

      innerPanel.setLayout(new BorderLayout());
      innerPanel.add(problemsInfo,BorderLayout.NORTH);
//      scrollPane = new JScrollPane();
//      scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//      scrollPane.setViewportView(table);
//      JViewport port = scrollPane.getViewport();      
//      port.setScrollMode(JViewport.BLIT_SCROLL_MODE);
//      scrollPane.setBackground(Color.lightGray);
      
      innerPanel.add(table,BorderLayout.CENTER);
      add(innerPanel,BorderLayout.CENTER);      
   }
   
   public TableModel getTableModel() {
      return table.getTable().getModel();
   }

   public JTable getTable() {
      return table.getTable();
   }

   public JaWEComponent getJaWEComponent() {
      return controller;
   }

   public JComponent getDisplay() {
      return this;
   }

   public void refreshProblemsPanel(XMLElement el,List problems,boolean specNotif) {          
      String lastSelectedErr=table.getSelectedErrId();
      table.cleanup();
      problemsInfo.setText("");
      if (el!=null) {
         if (problems!=null) {
            allProblems=new ArrayList(problems);
            XMLElement selel=JaWEManager.getInstance().getJaWEController().getSelectionManager().getSelectedElement();
            
            refreshT(selel, lastSelectedErr);
         }
         if (specNotif && problems!=null && problems.size()>0 && this.getParent() instanceof JTabbedPane) {
            JTabbedPane jtp=(JTabbedPane)this.getParent();
            Component c=jtp.getSelectedComponent();
            if (c!=this) {
               for (int i=0; i<jtp.getTabCount(); i++) {
                  c=jtp.getComponentAt(i);
                  if (c==this) {
                     jtp.setSelectedIndex(i);
                     break;
                  }
               }
            }
            this.requestFocus();         
         }
      }
   }
   
   public void refreshOnSelection(XMLElement selel) {          
      String lastSelectedErr=table.getSelectedErrId();
      table.cleanup();
      problemsInfo.setText("");
      refreshT(selel,lastSelectedErr);
   }
   
   protected void refreshT (XMLElement selel,String lastSelectedErr) {
      ActivitySet as=null;
      WorkflowProcess wp=null;
      org.enhydra.shark.xpdl.elements.Package pkg=null;
      XMLComplexElement topel = null;
      if (selel!=null) {
         as=XMLUtil.getActivitySet(selel);
         if (as!=null) {
            topel=as;
         }
         wp=XMLUtil.getWorkflowProcess(selel);
         if (topel==null && wp!=null) {
            topel=wp;
         }
         pkg=XMLUtil.getPackage(selel);
         if (topel==null && pkg!=null) {
            topel=pkg;
         }
      }
      List toHandle=new ArrayList(allProblems);
      if (as!=null || wp!=null || pkg!=null) {
         Iterator it=toHandle.iterator();
         while (it.hasNext()) {
            ValidationError ver=(ValidationError)it.next();
            if ((as!=null && XMLUtil.getActivitySet(ver.getElement())!=as) || (wp!=null && XMLUtil.getWorkflowProcess(ver.getElement())!=wp) || (pkg!=null && XMLUtil.getPackage(ver.getElement())!=pkg)) {
               it.remove();
            }
         }
      }
      
      int cntAll=toHandle.size();
      int cntErrs=Utils.countErrors(toHandle);

      String t = " ";
      if (topel!=null) {
         t+=JaWEManager.getInstance().getLabelGenerator().getLabel(topel)+ " '"+
            JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(topel)+ "': ";
      }
                  
      t+=(cntErrs)+" " + controller.getSettings().getLanguageDependentString("ErrorsKey")+", "   
         +(cntAll-cntErrs)+" " + controller.getSettings().getLanguageDependentString("WarningsKey");
      problemsInfo.setText(t);
      table.fillTableContent(toHandle);
      table.setSelectedElement(lastSelectedErr);      
   }
   
   protected boolean hasProblems () {
      return getTableModel().getRowCount()>0;
   }
   
   public void cleanup () {
      table.cleanup();
      problemsInfo.setText("");
      allProblems.clear();
   }
   
}