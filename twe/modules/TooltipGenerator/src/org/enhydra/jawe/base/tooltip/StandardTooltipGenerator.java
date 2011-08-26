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

package org.enhydra.jawe.base.tooltip;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.display.DisplayNameGenerator;
import org.enhydra.jawe.base.label.LabelGenerator;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLEmptyChoiceElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ArrayType;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.BasicType;
import org.enhydra.jxpdl.elements.DeclaredType;
import org.enhydra.jxpdl.elements.Join;
import org.enhydra.jxpdl.elements.ListType;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Split;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.utilities.SequencedHashMap;

/**
 * Used to generate tooltips for representing XPDL entities.
 * 
 * @author Sasa Bojanic
 */
public class StandardTooltipGenerator implements TooltipGenerator {

   /** Used for tooltips */
   public static final String EMPTY_STRING = "";

   /** Used for tooltips */
   public static final String HTML_OPEN = "<html>";

   /** Used for tooltips */
   public static final String HTML_CLOSE = "</html>";

   /** Used for tooltips */
   public static final String STRONG_OPEN = "<strong>";

   /** Used for tooltips */
   public static final String STRONG_CLOSE = "</strong>";

   /** Used for tooltips */
   public static final String LINE_BREAK = "<br>";

   /** Used for tooltips */
   public static final String COLON_SPACE = ": ";

   protected TooltipGeneratorSettings settings;

   public StandardTooltipGenerator() {
      settings = new TooltipGeneratorSettings();
      settings.init((JaWEComponent) null);
   }

   public StandardTooltipGenerator(TooltipGeneratorSettings settings) {
      this.settings = settings;
      this.settings.init((JaWEComponent) null);
   }

   public String getTooltip(Activity el) {
      LabelGenerator lg = JaWEManager.getInstance().getLabelGenerator();
      Map toDisplay = new SequencedHashMap();
      putKeyValue(toDisplay, el.get("Id"));
      putKeyValue(toDisplay, el.get("Name"));
      putKeyValue(toDisplay, el.get("Description"));
      if (el.getFirstPerformerObj() != null) {
         putKeyValue(toDisplay, el.getFirstPerformerObj());
      }
      putKeyValue(toDisplay, el.get("StartMode"));
      putKeyValue(toDisplay, el.get("FinishMode"));
      putKeyValue(toDisplay, el.get("Priority"));
      putKeyValue(toDisplay, el.get("Limit"));
      fillTypePartOfTooltip(el, toDisplay);
      return makeTooltip(el, toDisplay);
   }

   protected void fillTypePartOfTooltip(Activity el, Map toDisplay) {
      LabelGenerator lg = JaWEManager.getInstance().getLabelGenerator();
      String label, key;
      label = lg.getLabel(el.getActivityTypes());

      String type = JaWEManager.getInstance()
         .getJaWEController()
         .getTypeResolver()
         .getJaWEType(el)
         .getTypeId();
      key = settings.getLanguageDependentString(type); // lg.getLabel(tp);
      if (key == null || key.equals("")) {
         key = type;
      }
      toDisplay.put(label, key);

      if (type.equals(JaWEConstants.ACTIVITY_TYPE_TASK_APPLICATION)) {
         label = settings.getLanguageDependentString("ApplicationKey");
         key = String.valueOf(el.getActivityTypes()
            .getImplementation()
            .getImplementationTypes()
            .getTask()
            .getTaskTypes()
            .getTaskApplication()
            .getId());
         toDisplay.put(label, key);
      }
      if (type.equals(JaWEConstants.ACTIVITY_TYPE_TASK_SCRIPT)) {
         label = settings.getLanguageDependentString("ScriptTypeKey");
         key = String.valueOf(el.getActivityTypes()
            .getImplementation()
            .getImplementationTypes()
            .getTask()
            .getTaskTypes()
            .getTaskScript()
            .getScript().getScriptType());
         toDisplay.put(label, key);
         label = settings.getLanguageDependentString("ScriptKey");
         key = String.valueOf(el.getActivityTypes()
            .getImplementation()
            .getImplementationTypes()
            .getTask()
            .getTaskTypes()
            .getTaskScript()
            .getScript().toValue());
         toDisplay.put(label, key);
      }
      if (type.equals(JaWEConstants.ACTIVITY_TYPE_SUBFLOW)) {
         label = settings.getLanguageDependentString("ReferencedProcessKey");
         key = el.getActivityTypes()
            .getImplementation()
            .getImplementationTypes()
            .getSubFlow()
            .getId();
         if (key.equals("")) {
            key = settings.getLanguageDependentString("NoneKey");
         }
         toDisplay.put(label, key);
      }
      if (type.equals(JaWEConstants.ACTIVITY_TYPE_ROUTE)) {
         // tpSpecific=new XMLElement("Condition");
         // tpSpecific.setValue(getLoop().get("Condition").toString());
      }
      if (type.equals(JaWEConstants.ACTIVITY_TYPE_BLOCK)) {
         label = settings.getLanguageDependentString("ReferencedActivitySetKey");
         key = el.getActivityTypes().getBlockActivity().getActivitySetId();
         if (key.equals("")) {
            key = settings.getLanguageDependentString("NoneKey");
         }
         toDisplay.put(label, key);
      }
   }

   public String getTooltip(ArrayType el) {
      return ResourceManager.getLanguageDependentString("SubTypeKey");
      // return generateStandardLabel(el);
   }

   public String getTooltip(Artifact el) {
      LabelGenerator lg = JaWEManager.getInstance().getLabelGenerator();
      Map toDisplay = new SequencedHashMap();
      putKeyValue(toDisplay, el.get("Id"));
      putKeyValue(toDisplay, el.get("Name"));
      putKeyValue(toDisplay, el.get("ArtifactType"));
      putKeyValue(toDisplay, el.get("TextAnnotation"));
      return makeTooltip(el, toDisplay);
   }
   
   public String getTooltip(Association el) {
      LabelGenerator lg = JaWEManager.getInstance().getLabelGenerator();
      Map toDisplay = new SequencedHashMap();
      putKeyValue(toDisplay, el.get("Id"));
      putKeyValue(toDisplay, el.get("Name"));
      putKeyValue(toDisplay, el.get("AssociationDirection"));
      putKeyValue(toDisplay, el.get("Source"));
      putKeyValue(toDisplay, el.get("Target"));
      return makeTooltip(el, toDisplay);
   }

   public String getTooltip(BasicType el) {
      return ResourceManager.getLanguageDependentString("SubTypeKey");
   }

   public String getTooltip(DeclaredType el) {
      return ResourceManager.getLanguageDependentString("SubTypeKey");
   }

   public String getTooltip(Join el) {
      return ResourceManager.getLanguageDependentString("JoinTypeKey");
   }

   public String getTooltip(ListType el) {
      return ResourceManager.getLanguageDependentString("SubTypeKey");
   }

   public String getTooltip(org.enhydra.jxpdl.elements.Package el) {
      Map toDisplay = new SequencedHashMap();
      putKeyValue(toDisplay, el.get("Id"));
      putKeyValue(toDisplay, el.get("Name"));
      putKeyValue(toDisplay, el.getPackageHeader().get("Description"));
      return makeTooltip(el, toDisplay);
   }

   public String getTooltip(Participant el) {
      Map toDisplay = new SequencedHashMap();
      putKeyValue(toDisplay, el.get("Id"));
      putKeyValue(toDisplay, el.get("Name"));
      putKeyValue(toDisplay, el.getParticipantType());
      putKeyValue(toDisplay, el.get("Description"));
      return makeTooltip(el, toDisplay);
   }

   public String getTooltip(Split el) {
      return ResourceManager.getLanguageDependentString("SplitTypeKey");
   }

   public String getTooltip(Transition el) {
      LabelGenerator lg = JaWEManager.getInstance().getLabelGenerator();
      Map toDisplay = new SequencedHashMap();
      putKeyValue(toDisplay, el.get("Id"));
      putKeyValue(toDisplay, el.get("Name"));
      putKeyValue(toDisplay, el.get("Description"));
      putKeyValue(toDisplay, el.get("From"));
      putKeyValue(toDisplay, el.get("To"));
      putKeyValue(toDisplay, el.getCondition().getTypeAttribute());
      toDisplay.put(lg.getLabel(el.getCondition()), el.getCondition().toValue());
      return makeTooltip(el, toDisplay);
   }

   public String getTooltip(WorkflowProcess el) {
      Map toDisplay = new SequencedHashMap();
      putKeyValue(toDisplay, el.get("Id"));
      putKeyValue(toDisplay, el.get("Name"));
      putKeyValue(toDisplay, el.get("AccessLevel"));
      putKeyValue(toDisplay, el.getProcessHeader().get("Description"));
      return makeTooltip(el, toDisplay);
   }

   public String getTooltip(XMLEmptyChoiceElement el) {
      return generateStandardTooltip(el);
   }

   public String getTooltip(XMLComplexChoice el) {
      return generateStandardTooltip(el);
   }

   public String getTooltip(XMLCollection el) {
      return generateStandardTooltip(el);
   }

   public String getTooltip(XMLCollectionElement el) {
      return generateStandardTooltip(el);
   }

   public String getTooltip(XMLComplexElement el) {
      return generateStandardTooltip(el);
   }

   public String getTooltip(XMLSimpleElement el) {
      return generateStandardTooltip(el);
   }

   public String getTooltip(XMLAttribute el) {
      return generateStandardTooltip(el);
   }

   public String getTooltip(XMLElement el) {
      try {
         Class cl = el.getClass();
         Method m = null;
         try {
            m = this.getClass().getMethod("getTooltip", new Class[] {
               cl
            });
         } catch (Exception ex) {
            if (!(cl == XMLSimpleElement.class
                  || cl == XMLAttribute.class || cl == XMLComplexChoice.class
                  || cl == XMLComplexElement.class || cl == XMLCollectionElement.class || cl == XMLCollection.class)) {
               if (XMLComplexChoice.class.isAssignableFrom(cl)) {
                  cl = XMLComplexChoice.class;
               } else if (XMLAttribute.class.isAssignableFrom(cl)) {
                  cl = XMLAttribute.class;
               } else if (XMLSimpleElement.class.isAssignableFrom(cl)) {
                  cl = XMLSimpleElement.class;
               } else if (XMLComplexElement.class.isAssignableFrom(cl)) {
                  cl = XMLComplexElement.class;
               } else if (XMLCollection.class.isAssignableFrom(cl)) {
                  cl = XMLCollection.class;
               }
            }
         }
         m = this.getClass().getMethod("getTooltip", new Class[] {
            cl
         });
         // System.err.println("calling "+m.toString());
         return (String) m.invoke(this, new Object[] {
            el
         });
      } catch (Throwable e) {
         e.printStackTrace();
      }

      return generateStandardTooltip(el);

   }

   public String generateStandardTooltip(XMLElement el) {
      // Map toDisplay = new SequencedHashMap();
      // if (el instanceof XMLCollection) {
      //
      // } else if (el instanceof XMLCollectionElement) {
      //
      // } else if (el instanceof XMLComplexElement) {
      //
      // } else if (el instanceof XMLComplexChoice) {
      //
      // } else if (el instanceof XMLAttribute) {
      //
      // }
      return ResourceManager.getLanguageDependentString(el.toName() + "Key");
   }

   /**
    * Neat little thing. Makes HTML formated string for tooltip (made of property names
    * and coresponding values).
    */
   protected String makeTooltip(XMLElement el,Map elements) {
      if (elements == null)
         return "";
      String s = HTML_OPEN;
      Iterator it = elements.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         s += makeAnotherHtmlLine((String) me.getKey(), (String) me.getValue());
      }
      s = s.substring(0, s.length() - LINE_BREAK.length());
      s += HTML_CLOSE;
      return s;
   }

   /** Helps when generating tooltip for some element. */
   protected String makeAnotherHtmlLine(String label, String text) {
      int MAX_LENGTH = 100;
      int MAX_LINES_PER_TEXT = 15;
      String textToAppend = "";
      textToAppend += STRONG_OPEN;
      textToAppend += label + COLON_SPACE;
      textToAppend += STRONG_CLOSE;
      String val = text;
      val = val.replaceAll("<", "&lt;");
      val = val.replaceAll(">", "&gt;");
      int vl = val.length();
      if (vl > MAX_LENGTH) {
         String newVal = "";
         int hm = vl / MAX_LENGTH;
         for (int i = 0; i <= hm; i++) {
            int startI = i * MAX_LENGTH;
            int endI = (i + 1) * MAX_LENGTH;
            if (endI > vl) {
               endI = vl;
            }
            newVal = newVal + val.substring(startI, endI);
            if (i == MAX_LINES_PER_TEXT) {
               newVal = newVal + " ...";
               break;
            }
            if (i < hm) {
               newVal += LINE_BREAK;
               newVal += makeEmptyHTMLText((label + COLON_SPACE).length());
            }
         }
         val = newVal;
      }
      textToAppend += val;
      textToAppend += LINE_BREAK;

      return textToAppend;
   }

   protected String makeEmptyHTMLText(int length) {
      if (length < 0)
         return null;
      String es = "";
      for (int i = 0; i < length; i++) {
         es += "&nbsp;";
      }
      return es;
   }

   protected void putKeyValue(Map toPut, XMLElement el) {
      LabelGenerator lg = JaWEManager.getInstance().getLabelGenerator();
      DisplayNameGenerator dng = JaWEManager.getInstance().getDisplayNameGenerator();
      toPut.put(lg.getLabel(el), dng.getDisplayName(el));
   }

   public Settings getSettings() {
      return settings;
   }

}
