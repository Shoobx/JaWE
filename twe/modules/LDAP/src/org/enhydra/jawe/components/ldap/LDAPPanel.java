package org.enhydra.jawe.components.ldap;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.base.panel.panels.XMLRadioPanel;
import org.enhydra.jawe.base.panel.panels.XMLTextPanel;
import org.enhydra.shark.xpdl.XMLAttribute;

public class LDAPPanel extends JPanel implements JaWEComponentView {

   protected JToolBar toolbar;

   protected JPanel innerPanel = new JPanel();
   protected JLabel ldapInfo=new JLabel();

   protected LDAPTablePanel tablePanel;

   protected JScrollPane scrollPane;

   protected XMLRadioPanel objectFilterPanel;

   protected XMLTextPanel searchCriteria;

   protected LDAPController controller;

   public LDAPPanel(LDAPController controller) {
      this.controller = controller;

   }

   public void configure() {
      init();
   }

   public void init() {
      setBorder(BorderFactory.createEtchedBorder());
      setLayout(new BorderLayout());
      toolbar = BarFactory.createToolbar("defaultToolbar", controller);
      toolbar.setFloatable(false);
      if (toolbar.getComponentCount() > 0) {
         add(toolbar, BorderLayout.NORTH);
      }

      XMLAttribute search = new XMLAttribute(null, "UniqueAttributeSearchCriteria", true);
      XMLAttribute objectClassFilter = new XMLAttribute(null,
                                                        "ObjectClassFilter",
                                                        true,
                                                        ((LDAPSettings) controller.getSettings()).getLDAPObjectClassFilterChoices(),
                                                        0) {
         public void setValue(String v) {
            if (!choices.contains(v)) {
               choices.add(v);
            }
            this.value = v;
         }
      };
      objectClassFilter.setValue(((LDAPSettings) controller.getSettings()).getLDAPObjectClassFilter());
      objectFilterPanel = new XMLRadioPanel(null,
                                            objectClassFilter,
                                            "",
                                            false,
                                            false,
                                            false,
                                            true);
      searchCriteria = new XMLTextPanel(null, search, false, false, true);
      JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
      p.add(searchCriteria);
      p.add(Box.createHorizontalGlue());
      p.add(objectFilterPanel);

      tablePanel = new LDAPTablePanel(controller);

      innerPanel.setLayout(new BorderLayout());

      JPanel p2=new JPanel();
      p2.setLayout(new BorderLayout());
      p2.add(p,BorderLayout.NORTH);
      p2.add(ldapInfo,BorderLayout.CENTER);
      String t=" 0 " + controller.getSettings().getLanguageDependentString("EntriesKey");
      ldapInfo.setText(t);
      innerPanel.add(p2, BorderLayout.NORTH);
      innerPanel.add(tablePanel, BorderLayout.CENTER);

      add(innerPanel, BorderLayout.CENTER);
   }

   public String getSearchCriteria() {
      return searchCriteria.getText();
   }

   public String getSelectedObjectClass() {
      return (String) objectFilterPanel.getSelectedItem();
   }
   
   public boolean hasSelectedEntries () {
      return tablePanel.getTable().getSelectedRows().length>0;
   }
   
   public int howManyEntries () {
      return tablePanel.getTable().getRowCount();
   }

   public List getSelectedEntries () {
      return tablePanel.getSelectedElements();
   }

   public List getAllEntries () {
      return tablePanel.getAllElements();
   }
   
   public void refreshPanel (List infos) {
      tablePanel.cleanup();
      String t=" "+(infos.size())+" " + controller.getSettings().getLanguageDependentString("EntriesKey");
      ldapInfo.setText(t);
      tablePanel.fillTableContent(infos);
   }

   public JaWEComponent getJaWEComponent() {
      return controller;
   }

   public JComponent getDisplay() {
      return this;
   }

}
