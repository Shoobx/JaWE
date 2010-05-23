package org.enhydra.jawe.shark;

import java.util.Map;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.display.DisplayNameGenerator;
import org.enhydra.jawe.base.tooltip.StandardTooltipGenerator;
import org.enhydra.jawe.base.tooltip.TooltipGeneratorSettings;
import org.enhydra.shark.utilities.SequencedHashMap;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.ExtendedAttributes;

/**
 * Used to generate tooltips for representing XPDL entities.
 * 
 * @author Sasa Bojanic
 */
public class SharkTooltipGenerator extends StandardTooltipGenerator {

   public SharkTooltipGenerator() throws Exception {
      super();
   }

   public SharkTooltipGenerator(TooltipGeneratorSettings settings) {
      super(settings);
   }

   public String getTooltip(Activity el) {
      String tt = super.getTooltip(el);

      Map vars = XMLUtil.getWorkflowProcess(el).getAllVariables();
      ExtendedAttributes eas = el.getExtendedAttributes();
      Map toDisplay = new SequencedHashMap();
      DisplayNameGenerator dng = JaWEManager.getInstance().getDisplayNameGenerator();
      for (int i = 0; i < eas.size(); i++) {
         ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
         if (ea.getName().equals(SharkConstants.VTP_VIEW)
             || ea.getName().equals(SharkConstants.VTP_UPDATE)) {
            XMLCollectionElement var = (XMLCollectionElement) vars.get(ea.getVValue());
            if (var != null) {
               putEAKeyValue(dng, toDisplay, var, ea.getName()
                  .equals(SharkConstants.VTP_VIEW));
            }
         }
      }
      if (toDisplay.size() > 0) {
         String tt2 = makeTooltip(toDisplay);
         tt = tt.substring(0, tt.length() - HTML_CLOSE.length());
         tt += LINE_BREAK;
         tt += "<hr>";
         tt = tt + tt2.substring(HTML_OPEN.length(), tt2.length());
      }
      return tt;
   }

   protected void putEAKeyValue(DisplayNameGenerator dng,
                                Map toPut,
                                XMLCollectionElement el,
                                boolean readOnly) {
      String n = settings.getLanguageDependentString("ReadOnlyKey");
      if (!readOnly) {
         n = settings.getLanguageDependentString("EditKey");
      }
      toPut.put(dng.getDisplayName(el), n);
   }

}
