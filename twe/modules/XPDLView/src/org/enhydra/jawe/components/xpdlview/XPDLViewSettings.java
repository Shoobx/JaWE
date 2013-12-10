/**
 * Miroslav Popov, Nov 29, 2005 miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.xpdlview;

import java.awt.Color;
import java.net.URL;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWE;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;

/**
 * @author Miroslav Popov
 */
public class XPDLViewSettings extends JaWEComponentSettings {

    public void init(JaWEComponent comp) {
        PROPERTYFILE_PATH = "org/enhydra/jawe/components/xpdlview/properties/";
        PROPERTYFILE_NAME = "togwexpdlview.properties";
        super.init(comp);
    }

    public void loadDefault(JaWEComponent comp, Properties properties) {
        // defaults
        arm = new AdditionalResourceManager(properties);

        componentSettings.put("ShowHighlight", new Boolean(properties.getProperty("ShowHighlight", "false")
                .equals("true")));
        componentSettings.put("ShowXPDLDetails", new Boolean(properties.getProperty("ShowXPDLDetails",
                "false").equals("true")));

        ImageIcon findNext;
        URL iconURL = ResourceManager.getResource(properties, "Image.FindNext");
        if (iconURL != null)
            findNext = new ImageIcon(iconURL);
        else
            findNext = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
                    "org/enhydra/jawe/images/findnext.png"));
        componentSettings.put("FindNext", findNext);

        Color color = null;
        try {
            color = Utils.getColor(ResourceManager.getResourceString(properties, "BackgroundColor"));
        } catch (Exception e) {
            color = Utils.getColor("R=245,G=245,B=245");
        }
        componentSettings.put("BackgroundColor", color);

        componentSettings.putAll(Utils.loadAllMenusAndToolbars(properties));
        componentAction.putAll(Utils.loadActions(properties, comp, componentAction));
    }

    public boolean showHighlight() {
        return ((Boolean) componentSettings.get("ShowHighlight")).booleanValue();
    }

    public boolean showXPDLDetails() {
        return ((Boolean) componentSettings.get("ShowXPDLDetails")).booleanValue();
    }

    public ImageIcon getFindNextIcon() {
        return (ImageIcon) componentSettings.get("FindNext");
    }

    public Color getBackgroundColor() {
        return (Color) componentSettings.get("BackgroundColor");
    }

}
