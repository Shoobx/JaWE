/**
 * Miroslav Popov, Dec 12, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.enhydra.jawe.base.controller.JaWEFrame;

/**
 * @author Miroslav Popov
 *
 */
public class JaWETabbedPane extends JTabbedPane implements MouseListener, ActionListener {   

   private boolean maximized = false;
   
   JMenuItem close;
   
   public JaWETabbedPane() {
      addMouseListener(this);
      close = new JMenuItem(ResourceManager.getLanguageDependentString("Close" + BarFactory.LABEL_POSTFIX));
      close.addActionListener(this);
   }
   
   public void mouseClicked(MouseEvent e) {
      if (e.getID() == MouseEvent.MOUSE_CLICKED && e.getClickCount() == 2) {
         if (maximized) {
            JaWEFrame jf = JaWEManager.getInstance().getJaWEController().getJaWEFrame();
            jf.restoreWorkingArea();
            maximized = false;
         } else {
            JaWEFrame jf = JaWEManager.getInstance().getJaWEController().getJaWEFrame();
            jf.maximizeComponent(this);
            maximized = true;
         }
      } else if (e.getID() == MouseEvent.MOUSE_CLICKED && SwingUtilities.isRightMouseButton(e)) {
         JPopupMenu popup = new JPopupMenu();         
         
         popup.add(close);
         popup.addSeparator();
         popup.addSeparator();
         
         List l = JaWEManager.getInstance().getComponentManager().getComponents();
         for (int i = 0; i < l.size(); i++) {
            JaWEComponent c = (JaWEComponent)l.get(i);
            if (c.getType() != JaWEComponent.UPPER_STATUS_COMPONENT
                  && c.getType() != JaWEComponent.LOWER_STATUS_COMPONENT) {
               JMenuItem t = new JMenuItem();
               t.setText(JaWEManager.getInstance().getJaWEController().getSettings().getLanguageDependentString("AddKey") + " "
                     + JaWEManager.getInstance().getJaWEController().getSettings().getLanguageDependentString(
                           c.getName() + BarFactory.LABEL_POSTFIX));
               t.setName(c.getName());
               t.addActionListener(this);
               popup.add(t);
            }
         }
         
         popup.show(this, e.getX(), e.getY());
      }
   }
   
   public void actionPerformed(ActionEvent e) {
      if (e.getSource() == close) {
         JComponent c = (JComponent)this.getSelectedComponent();
         
         JaWEComponent comp = findComponent(c);
         JaWEManager.getInstance().getJaWEController().removeJaWEComonent(comp);
      } else if (e.getSource() instanceof JMenuItem) {
         JMenuItem ji = (JMenuItem)e.getSource();
         String cn = ji.getName();
         JaWEComponent comp = JaWEManager.getInstance().getComponentManager().getComponent(cn);
         JaWEManager.getInstance().getJaWEController().changeJaWEComponentType(comp, this.getName());
         this.setSelectedComponent(comp.getView().getDisplay());
      }
   }
   
   protected JaWEComponent findComponent(JComponent c) {
      List l = JaWEManager.getInstance().getComponentManager().getComponents();
      for (int i = 0; i < l.size(); i++) {
         JaWEComponent comp = (JaWEComponent)l.get(i);
         
         if (comp.getView().getDisplay() == c)
            return comp;
      }

      return null;
   }
   
   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }
}
