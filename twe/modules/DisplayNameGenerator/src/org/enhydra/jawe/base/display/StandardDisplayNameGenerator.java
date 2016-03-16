/**
 * Together Workflow Editor
 * Copyright (C) 2011 Together Teamsolutions Co., Ltd. 
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

package org.enhydra.jawe.base.display;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.label.LabelGenerator;
import org.enhydra.jawe.base.xpdlhandler.XPDLHandler;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLBaseForCollectionAndComplex;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLEmptyChoiceElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActualParameter;
import org.enhydra.jxpdl.elements.BasicType;
import org.enhydra.jxpdl.elements.Condition;
import org.enhydra.jxpdl.elements.DataType;
import org.enhydra.jxpdl.elements.DataTypes;
import org.enhydra.jxpdl.elements.Deadline;
import org.enhydra.jxpdl.elements.DeclaredType;
import org.enhydra.jxpdl.elements.EnumerationType;
import org.enhydra.jxpdl.elements.ExpressionType;
import org.enhydra.jxpdl.elements.ExternalPackage;
import org.enhydra.jxpdl.elements.ExternalReference;
import org.enhydra.jxpdl.elements.FormalParameters;
import org.enhydra.jxpdl.elements.Join;
import org.enhydra.jxpdl.elements.Member;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.ParticipantType;
import org.enhydra.jxpdl.elements.RecordType;
import org.enhydra.jxpdl.elements.Responsible;
import org.enhydra.jxpdl.elements.Route;
import org.enhydra.jxpdl.elements.Split;
import org.enhydra.jxpdl.elements.SubFlow;
import org.enhydra.jxpdl.elements.TaskApplication;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.TransitionRef;
import org.enhydra.jxpdl.elements.Transitions;
import org.enhydra.jxpdl.elements.TypeDeclaration;
import org.enhydra.jxpdl.elements.UnionType;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.utilities.SequencedHashMap;

/**
 * Used to generate strings for graphical representation of all XPDL entities.
 * 
 * @author Sasa Bojanic
 */
public class StandardDisplayNameGenerator implements DisplayNameGenerator {

   protected DisplayNameGeneratorSettings settings;

   public StandardDisplayNameGenerator() {
      settings = new DisplayNameGeneratorSettings();
      settings.init((JaWEComponent) null);
   }

   public StandardDisplayNameGenerator(DisplayNameGeneratorSettings settings) {
      this.settings = settings;
      this.settings.init((JaWEComponent) null);
   }

   public String getDisplayName(ActualParameter el) {
      Map vars = XMLUtil.getPossibleVariables(el);
      String vId = el.toValue();
      if (!vId.equals("")) {
         XMLCollectionElement dfOrFp = (XMLCollectionElement) vars.get(vId);
         if (dfOrFp != null) {
            return getDisplayName(dfOrFp);
         }
      }
      return vId;
   }

   public String getDisplayName(BasicType el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(Condition el) {
      return el.toValue();
   }

   public String getDisplayName(DataType el) {
      return getDisplayName(el.getDataTypes());
   }

   public String getDisplayName(DataTypes el) {
      // return generateStandardDisplayName(el);
      XMLBaseForCollectionAndComplex ch = (XMLBaseForCollectionAndComplex) el.getChoosen();
      LabelGenerator lg = JaWEManager.getInstance().getLabelGenerator();
      String disp = lg.getLabel(ch);
      if (ch instanceof BasicType) {
         disp += " -> " + settings.getLanguageDependentString(((BasicType) ch).getType() + "Key");
      } else if (ch instanceof ExternalReference) {
         disp += " -> " + ((ExternalReference) ch).getLocation();
      } else if (ch instanceof DeclaredType) {
         String tdId = ((DeclaredType) ch).getId();
         TypeDeclaration td = XMLUtil.getTypeDeclaration(JaWEManager.getInstance().getXPDLHandler(), XMLUtil.getPackage(el), tdId);
         disp += " -> " + getDisplayName(td);
      }
      return disp;
   }

   public String getDisplayName(Deadline el) {
      return el.getDeadlineDuration();
   }

   public String getDisplayName(DeclaredType el) {
      LabelGenerator lg = JaWEManager.getInstance().getLabelGenerator();
      return lg.getLabel(el);
      // return generateStandardDisplayName(el);
   }

   public String getDisplayName(EnumerationType el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(ExternalPackage el) {
      // return generateStandardDisplayName(el);
      return el.getHref();
   }

   public String getDisplayName(ExternalReference el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(FormalParameters el) {
      if (el.getParent() instanceof XMLComplexChoice) {
         return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
      }
      return String.valueOf(el.size());
   }

   public String getDisplayName(Join el) {
      return generateStandardDisplayName(el.getTypeAttribute());
   }

   public String getDisplayName(Member el) {
      return getDisplayName(el.getDataTypes());
   }

   public String getDisplayName(ParticipantType el) {
      return generateStandardDisplayName(el.getTypeAttribute());
   }

   public String getDisplayName(RecordType el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(Responsible el) {
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(el);
      Package pkg = XMLUtil.getPackage(el);
      XPDLHandler xpdlh = JaWEManager.getInstance().getXPDLHandler();
      Map ps = null;
      if (wp != null) {
         ps = XMLUtil.getPossibleParticipants(wp, xpdlh);
      } else if (pkg != null) {
         ps = XMLUtil.getPossibleParticipants(pkg, xpdlh);
      }
      String pId = el.toValue();
      if (ps != null && !pId.equals("")) {
         Participant p = (Participant) ps.get(pId);
         if (p != null) {
            return getDisplayName(p);
         }
      }
      return pId;
   }

   public String getDisplayName(Route el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(Split el) {
      return generateStandardDisplayName(el.getTypeAttribute());
   }

   public String getDisplayName(SubFlow el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(TaskApplication el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(TransitionRef el) {
      return getDisplayName(el.get("Id"));
   }

   public String getDisplayName(UnionType el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(XMLAttribute el) {
      if (el.getParent() instanceof Transition && (el.toName().equals("From") || el.toName().equals("To"))) {
         XMLCollectionElement wpOrAs = XMLUtil.getActivitySetOrWorkflowProcess(el);
         SequencedHashMap pas = getPossibleActivities(wpOrAs);
         Activity act = (Activity) pas.get(el.toValue());
         if (act != null) {
            return generateStandardDisplayName(act);
         }
      } else if (el.getParent() instanceof TransitionRef) {
         XMLCollectionElement wpOrAs = XMLUtil.getActivitySetOrWorkflowProcess(el);
         Transitions tras = (Transitions) wpOrAs.get("Transitions");
         Transition tra = tras.getTransition(el.toValue());
         if (tra != null) {
            SequencedHashMap pas = getPossibleActivities(wpOrAs);
            Activity act = (Activity) pas.get(tra.getTo());
            if (act != null) {
               return generateStandardDisplayName(act);
            }
         }
      }
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(XMLEmptyChoiceElement el) {
      String v = el.toValue();
      // try {
      // v=settings.getLanguageDependentString(v+"Key");
      // System.out.println("LDS for "+el.toValue()+"Key is "+v);
      // if (v==null) {
      // v=el.toValue();
      // }
      // } catch (Exception ex) {
      // System.out.println("Failed getting LDS for "+el.toValue()+"Key");
      // v=el.toValue();
      // }
      return v;
   }

   public String getDisplayName(XMLComplexChoice el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(XMLCollection el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(XMLCollectionElement el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(XMLComplexElement el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(XMLSimpleElement el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(XMLElement el) {
      try {
         Class cl = el.getClass();
         Method m = null;
         try {
            m = this.getClass().getMethod("getDisplayName", new Class[] {
               cl
            });
         } catch (Exception ex) {
            if (!(cl == XMLSimpleElement.class
                  || cl == XMLAttribute.class || cl == XMLComplexChoice.class || cl == XMLComplexElement.class || cl == XMLCollectionElement.class || cl == XMLCollection.class)) {
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
         m = this.getClass().getMethod("getDisplayName", new Class[] {
            cl
         });
         // System.err.println("calling "+m.toString());
         return (String) m.invoke(this, new Object[] {
            el
         });
      } catch (Throwable e) {
         e.printStackTrace();
      }

      return generateStandardDisplayName(el);
   }

   public String generateStandardDisplayName(XMLElement el) {
      String disp = "";
      if (el instanceof XMLCollection) {
         disp = String.valueOf(((XMLCollection) el).size());
      } else if (el instanceof XMLComplexElement) {
         XMLElement nme = ((XMLComplexElement) el).get("Name");
         if (nme != null) {
            disp = nme.toValue();
         }
         if (disp.equals("")) {
            XMLElement id = ((XMLComplexElement) el).get("Id");
            if (id != null) {
               disp = id.toValue();
            }
         }
         if (disp.equals("")) {
            if (el instanceof ExpressionType) {
               disp = el.toValue();
            } else {
               disp = JaWEManager.getInstance().getLabelGenerator().getLabel(el);
            }
         }
      } else if (el instanceof XMLComplexChoice) {
         disp = getDisplayName(((XMLComplexChoice) el).getChoosen());
      } else if (el instanceof XMLAttribute && ((XMLAttribute) el).getChoices() != null) {
         disp = settings.getLanguageDependentString(el.toValue() + "Key");
         if (disp == null || disp.equals("")) {
            disp = el.toValue();
         }
      } else {
         disp = el.toValue();
      }
      return disp;
   }

   public DisplayNameGeneratorSettings getSetting() {
      return settings;
   }

   public Settings getSettings() {
      return settings;
   }

   public SequencedHashMap getPossibleActivities(XMLCollectionElement wpOrAs) {
      SequencedHashMap toRet = new SequencedHashMap();
      List acts = ((Activities) wpOrAs.get("Activities")).toElements();
      Iterator it = acts.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         toRet.put(act.getId(), act);
      }
      return toRet;
   }

}
