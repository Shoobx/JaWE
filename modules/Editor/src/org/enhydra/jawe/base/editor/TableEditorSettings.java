/**
 * Miroslav Popov, Dec 2, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.editor;

import java.awt.Color;
import java.util.Properties;

import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.panel.PanelSettings;

/**
 * @author Miroslav Popov
 * 
 */
public class TableEditorSettings extends PanelSettings {

   public void init(JaWEComponent comp) {
      loadDefault(comp, new Properties());
   }

   public void loadDefault(JaWEComponent comp,Properties properties) {    
      arm = new AdditionalResourceManager(properties);

      componentSettings.put("UseScrollBar", new Boolean(properties.getProperty("InlinePanel.UseScrollBar",
            "true").equals("true")));

      componentSettings.put("DisplayTitle", new Boolean(properties.getProperty("InlinePanel.DisplayTitle",
            "false").equals("true")));

      componentSettings.put("XMLBasicPanel.RightAllignment", new Boolean(properties.getProperty(
            "XMLBasicPanel.RightAllignment", "false").equals("true")));

      componentSettings.put("EmptyBorder.TOP",
            new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.TOP", "0")));
      componentSettings.put("EmptyBorder.LEFT", new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.LEFT",
            "3")));
      componentSettings.put("EmptyBorder.BOTTOM", new Integer(properties.getProperty(
            "XMLBasicPanel.EmptyBorder.BOTTOM", "4")));
      componentSettings.put("EmptyBorder.RIGHT", new Integer(properties.getProperty("XMLBasicPanel.EmptyBorder.RIGHT",
            "3")));
      componentSettings.put("SimplePanelTextWidth", new Integer(properties.getProperty(
            "XMLBasicPanel.SimplePanelTextWidth", "250")));
      componentSettings.put("SimplePanelTextHeight", new Integer(properties.getProperty(
            "XMLBasicPanel.SimplePanelTextHeight", "20")));


      componentSettings.put("ShowColumns.XMLTablePanel.Applications", "Id Name");
      componentSettings.put("ShowColumns.XMLTablePanel.Participants", "Id Name ParticipantType");
      componentSettings.put("ShowColumns.XMLTablePanel.WorkflowProcesses", "Id Name AccessLevel");      

      Color color=null;
      try {
         color = Utils.getColor(ResourceManager.getResourceString(properties, "BackgroundColor"));
      } catch (Exception e) {
         color = Utils.getColor("R=245,G=245,B=245");
      }
      componentSettings.put("BackgroundColor", color);

   }   
}
