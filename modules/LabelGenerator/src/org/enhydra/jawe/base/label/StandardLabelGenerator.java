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

package org.enhydra.jawe.base.label;

import java.lang.reflect.Method;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.Settings;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLEmptyChoiceElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.elements.BasicType;
import org.enhydra.jxpdl.elements.DataTypes;
import org.enhydra.jxpdl.elements.Join;
import org.enhydra.jxpdl.elements.Split;

/**
 *  Used to generate labels for all XPDL entities.
 *
 *  @author Sasa Bojanic
 */
public class StandardLabelGenerator implements LabelGenerator {

   protected LabelGeneratorSettings settings;
   
   public StandardLabelGenerator () {
      settings = new LabelGeneratorSettings();
      settings.init((JaWEComponent) null);
   }

   public StandardLabelGenerator(LabelGeneratorSettings settings) {
      this.settings = settings;
      this.settings.init((JaWEComponent) null);
   }
   
   public String getLabel(DataTypes el) {
      return settings.getLanguageDependentString("TypeKey");
   }

   public String getLabel(Join el) {
       return settings.getLanguageDependentString("JoinTypeKey");
   }

   public String getLabel(Split el) {
       return settings.getLanguageDependentString("SplitTypeKey");
   }

   public String getLabel (XMLEmptyChoiceElement el) {
      return "";
   }
   
   public String getLabel (XMLComplexChoice el) {
      return settings.getLanguageDependentString(el.toName()+"Key");
   }

   public String getLabel(XMLCollection el) {
      return generateStandardLabel(el);
   }

   public String getLabel(XMLCollectionElement el) {
      return generateStandardLabel(el);
   }

   public String getLabel(XMLComplexElement el) {
      return generateStandardLabel(el);
   }

   public String getLabel(XMLSimpleElement el) {
      return generateStandardLabel(el);
   }
   
   public String getLabel(XMLAttribute el) {
      if (el.getParent() instanceof Join || el.getParent() instanceof Split) {
         return getLabel(el.getParent());
      } else if (el.getParent() instanceof BasicType) {
         return settings.getLanguageDependentString("SubTypeKey");
      }
      return generateStandardLabel(el);
   }

   public String getLabel(XMLElement el) {

       try {
           Class cl = el.getClass();           
           Method m = null;
           try {
              m = this.getClass().getMethod("getLabel", new Class[] { cl } );
           } catch (Exception ex) {
              if (!(
                    cl==XMLSimpleElement.class || 
                    cl==XMLAttribute.class || 
                    cl==XMLComplexChoice.class ||
                    cl==XMLComplexElement.class ||
                    cl==XMLCollectionElement.class ||
                    cl==XMLCollection.class)) {
                 if (XMLComplexChoice.class.isAssignableFrom(cl)) {
                    cl=XMLComplexChoice.class;
                 } else if (XMLAttribute.class.isAssignableFrom(cl)) {
                    cl=XMLAttribute.class;
                 } else if (XMLSimpleElement.class.isAssignableFrom(cl)) {
                    cl=XMLSimpleElement.class;
                 } else if (XMLComplexElement.class.isAssignableFrom(cl)) {
                    cl=XMLComplexElement.class;
                 } else if (XMLCollection.class.isAssignableFrom(cl)) {
                    cl=XMLCollection.class;
                 }
              }             
           }
           m = this.getClass().getMethod("getLabel", new Class[] { cl } );
//System.err.println("calling "+m.toString());
           return (String) m.invoke(this, new Object[] { el });
       } catch (Throwable e) {
           e.printStackTrace();
       }

       return generateStandardLabel(el);
   }

   public String generateStandardLabel (XMLElement el) {
      return settings.getLanguageDependentString(el.toName()+"Key");
   }

   public Settings getSettings () {
      return settings;
   }
   

}
