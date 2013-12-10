package org.enhydra.jawe.base.panel.panels;

import java.awt.Window;

import javax.swing.JPanel;

import org.enhydra.jawe.base.editor.XPDLElementEditor;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.shark.xpdl.XMLElement;

/**
 * Standard interface for creating all panels for editing XPDL content.
 * 
 *  @author Sasa Bojanic
 */
public abstract class XMLPanel extends JPanel{
   
   public abstract void setOwner (XMLElement el);

   public abstract XPDLElementEditor getEditor();
   
   public abstract XMLElement getOwner ();
   
//   public abstract XMLElement getOrginalOwner ();

   public abstract void setBorder (String title,boolean hasBorder,boolean hasEmptyBorder);

   public abstract String getTitle();

   public abstract boolean validateEntry ();

   // Always returns true, this is set because of panels that are never empty
   // but this method is used when checking emptiness of group panel,
   // and panels that do not override this method should not be ever considered
   public abstract boolean isEmpty ();

   public abstract void setElements ();
   
   public abstract Object getValue ();
   
   public abstract void updateView();

   public abstract void cleanup ();
//   public abstract JComponent getMainComponent();
   
   public abstract Window getWindow ();
   
   public abstract PanelContainer getPanelContainer ();
   
   public abstract void canceled ();
}
