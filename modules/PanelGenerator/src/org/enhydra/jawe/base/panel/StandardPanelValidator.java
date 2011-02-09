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

package org.enhydra.jawe.base.panel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.base.panel.panels.XMLTabbedPanel;
import org.enhydra.jawe.base.transitionhandler.TransitionHandler;
import org.enhydra.jawe.components.graph.SizeObject;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLEmptyChoiceElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.XPDLValidationErrorIds;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Condition;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Transition;

/**
 * Class used to validate panels for all XPDL entities.
 * 
 * @author Sasa Bojanic
 */
public class StandardPanelValidator implements PanelValidator {

   protected Properties properties;

   public void configure(Properties props) throws Exception {
      this.properties = props;
   }

   public boolean validatePanel(SizeObject el, XMLPanel panel) {
      XMLPanel widthPanel = findPanel(panel, el.get("Width"));
      XMLPanel heightPanel = findPanel(panel, el.get("Height"));

      String w = (String) widthPanel.getValue();
      String h = (String) heightPanel.getValue();

      try {
         double width = Double.parseDouble(w);
         double height = Double.parseDouble(h);
         if (width <= 10 || height < 10) {
            XMLBasicPanel.errorMessage(panel.getWindow(),
                                       ResourceManager.getLanguageDependentString("ErrorMessageKey"),
                                       "",
                                       ResourceManager.getLanguageDependentString("MessageInvalidDimension"));
            return false;
         }
      } catch (Exception ex) {
         XMLBasicPanel.errorMessage(panel.getWindow(),
                                    ResourceManager.getLanguageDependentString("ErrorMessageKey"),
                                    "",
                                    ResourceManager.getLanguageDependentString("MessageInvalidValue"));
         return false;
      }
      return true;
   }

   public boolean validatePanel(XMLAttribute el, XMLPanel panel) {
      return standardPanelValidation(el, panel);
   }   
   
   public boolean validatePanel(XMLEmptyChoiceElement el, XMLPanel panel) {
      return standardPanelValidation(el, panel);
   }   

   public boolean validatePanel(XMLComplexChoice el, XMLPanel panel) {
      return standardPanelValidation(el, panel);
   }   

   public boolean validatePanel(XMLCollection el, XMLPanel panel) {
      return standardPanelValidation(el, panel);
   }   
   
   public boolean validatePanel(XMLComplexElement el, XMLPanel panel) {
      return standardPanelValidation(el, panel);
   }   

   public boolean validatePanel(XMLSimpleElement el, XMLPanel panel) {
      return standardPanelValidation(el, panel);
   }   

   public boolean validatePanel(XMLElement el, XMLPanel panel) {
      try {
         Class cl = el.getClass();
         Method m = null;
         try {
            m = this.getClass().getMethod("validatePanel", new Class[] {
                  cl, XMLPanel.class
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
         m = this.getClass().getMethod("validatePanel", new Class[] {
               cl, XMLPanel.class
         });
         // System.err.println("calling "+m.toString());
         return ((Boolean) m.invoke(this, new Object[] {
               el, panel
         })).booleanValue();
      } catch (Throwable e) {
         e.printStackTrace();
      }

      return standardPanelValidation(el, panel);
   }

   public boolean standardPanelValidation(XMLElement el, XMLPanel panel) {
      boolean idValidation = false;
      if (el instanceof XMLCollectionElement || el instanceof Package) {
         idValidation = true;
      } else if (el instanceof XMLAttribute
                 && el.toName().equals("Id")
                 && (el.getParent() instanceof XMLCollectionElement || el.getParent() instanceof Package)) {
         idValidation = true;
      }
      if (idValidation) {
         if (!validateId(panel, el)) {
            return false;
         }
      }

      if (el instanceof Transition
          || el instanceof Condition || el.getParent() instanceof Transition
          || el.getParent() instanceof Condition) {
         if (!validateTransition(panel, el)) {
            return false;
         }
      }

      if (el instanceof Association || el.getParent() instanceof Association) {
         if (!validateAssociation(panel, el)) {
            return false;
         }
      }

      return true;
   }

   protected boolean validateId(XMLPanel pnl, XMLElement el) {
      XMLComplexElement cel = null;
      String newId = null;
      XMLPanel idPanel = null;
      if (el instanceof XMLAttribute) {
         XMLElement parent = el.getParent();
         if (parent instanceof XMLCollectionElement) {
            cel = (XMLCollectionElement) parent;
            newId = cel.get("Id").toValue();
            if (pnl.getValue() instanceof String) {
               newId = ((String) pnl.getValue()).trim();
               idPanel = pnl;
            }
         } else if (parent instanceof Package) {
            cel = (Package) parent;
            newId = cel.get("Id").toValue();
            if (pnl.getValue() instanceof String) {
               newId = ((String) pnl.getValue()).trim();
               idPanel = pnl;
            }
         }
      } else if (el instanceof XMLComplexElement) {
         cel = (XMLComplexElement) el;
         newId = cel.get("Id").toValue();
         idPanel = findPanel(pnl, cel.get("Id"));
         if (idPanel == null) {
            idPanel = pnl;
         }
         if (idPanel != null) {
            if (idPanel.getValue() instanceof String) {
               newId = ((String) idPanel.getValue()).trim();
            }
            if (idPanel.getValue() instanceof XMLCollectionElement) {
               newId = ((XMLCollectionElement) idPanel.getValue()).getId().trim();
            }
         } else {
            // System.err.println("CCCCCCFFFFFFFIDPPPPPPPPPPPPP");
         }
      }

      System.err.println("Valid for "
                         + el + ", par=" + el.getParent() + ", newid=" + newId + ", idp="
                         + idPanel);
      boolean isValid = XMLUtil.isIdValid(newId);
      if (!isValid) {
         XMLBasicPanel.errorMessage(pnl.getWindow(),
                                    ResourceManager.getLanguageDependentString("ErrorMessageKey"),
                                    "",
                                    ResourceManager.getLanguageDependentString(XPDLValidationErrorIds.ERROR_INVALID_ID));
         idPanel.requestFocus();
         return false;
      }

      if (cel != null) {

         boolean isUniqueId = true;
         if (cel instanceof XMLCollectionElement) {
            isUniqueId = JaWEManager.getInstance()
               .getIdFactory()
               .isIdUnique((XMLCollectionElement) cel, newId);
         } else {
            Package fp = JaWEManager.getInstance().getXPDLHandler().getPackageById(newId);
            if (fp != null && fp != cel && fp.getId().equals(newId)) {
               isUniqueId = false;
            }
         }

         if (!isUniqueId) {
            XMLBasicPanel.errorMessage(pnl.getWindow(),
                                       ResourceManager.getLanguageDependentString("ErrorMessageKey"),
                                       "",
                                       ResourceManager.getLanguageDependentString(XPDLValidationErrorIds.ERROR_NON_UNIQUE_ID));
            idPanel.requestFocus();
            return false;
         }
      }
      return true;
   }

   protected boolean validateTransition(XMLPanel pnl, XMLElement el) {
      Transition tra = XMLUtil.getTransition(el);
      String oldFrom = tra.getFrom();
      String oldTo = tra.getTo();
      String newFrom = oldFrom;
      String newTo = oldTo;
      String newType = tra.getCondition().getType();

      if (el instanceof Transition) {
         XMLPanel ftPanel = findPanel(pnl, tra.get("From"));
         // System.err.println("Found panel From ->"+ftPanel);
         if (ftPanel != null) {
            Object v = ftPanel.getValue();
            // System.err.println("..... panel val ->"+v);
            if (v instanceof XMLElement) {
               if (v instanceof Activity) {
                  newFrom = ((Activity) v).getId();
               }
            } else if (v instanceof String) {
               newFrom = ((String) v).trim();
            }
         }
         ftPanel = findPanel(pnl, tra.get("To"));
         // System.err.println("Found panel To ->"+ftPanel);
         if (ftPanel != null) {
            Object v = ftPanel.getValue();
            // System.err.println("..... panel val ->"+v);
            if (v instanceof XMLElement) {
               if (v instanceof Activity) {
                  newTo = ((Activity) v).getId();
               }
            } else if (v instanceof String) {
               newTo = ((String) v).trim();
            }
         }
         ftPanel = findPanel(pnl, ((Condition) tra.get("Condition")).getTypeAttribute());
         // System.err.println("Found panel From ->"+ftPanel);
         if (ftPanel != null) {
            Object v = ftPanel.getValue();
            // System.err.println("..... panel val ->"+v);
            if (v instanceof String) {
               newType = ((String) v).trim();
            }
         }
      } else if (el instanceof XMLAttribute
                 && (el.toName().equals("From") || el.toName().equals("To"))) {
         Object v = pnl.getValue();
         String toOrFrom = null;
         if (v instanceof XMLElement) {
            if (v instanceof Activity) {
               toOrFrom = ((Activity) v).getId();
            }
         } else if (v instanceof String) {
            toOrFrom = ((String) v).trim();
         }
         if (toOrFrom != null) {
            if (el.toName().equals("From")) {
               newFrom = toOrFrom;
            } else {
               newTo = toOrFrom;
            }
         }
      } else if (el instanceof Condition) {
         XMLPanel ftPanel = findPanel(pnl,
                                      ((Condition) tra.get("Condition")).getTypeAttribute());
         // System.err.println("Found panel From ->"+ftPanel);
         if (ftPanel != null) {
            Object v = ftPanel.getValue();
            // System.err.println("..... panel val ->"+v);
            if (v instanceof String) {
               newType = ((String) v).trim();
            }
         }
      } else if (el instanceof XMLAttribute
                 && el.getParent() instanceof Condition && el.toName().equals("Type")) {
         Object v = pnl.getValue();
         if (v instanceof String) {
            newType = ((String) v).trim();
         }
      }

      // System.err.println("T=" + oldTo + ", F=" + oldFrom + ", NT=" + newTo + ", NF=" +
      // newFrom+", NT="+newType);

      Activities acts = (Activities) ((XMLCollectionElement) tra.getParent().getParent()).get("Activities");
      Activity actFrom = acts.getActivity(newFrom);
      Activity actTo = acts.getActivity(newTo);
      // System.err.println("AF=="+actFrom+", aTO="+actTo);
      if (actFrom == null || actTo == null)
         return false;

      TransitionHandler th = JaWEManager.getInstance().getTransitionHandler();
      List status = new ArrayList();
      boolean isExcTra = false;
      if (newType.equals(XPDLConstants.CONDITION_TYPE_EXCEPTION)
          || newType.equals(XPDLConstants.CONDITION_TYPE_DEFAULTEXCEPTION)) {
         isExcTra = true;
      }

      boolean ac = th.allowsConnection(actFrom, actTo, tra, isExcTra, status);
      // System.err.println("ALLOW C="+ac);

      if (!ac) {
         String errorMsg = "WarningSourceActivityCannotHaveMoreOutgoingTransitions";
         if (((Integer) status.get(0)).intValue() == 2) {
            errorMsg = "WarningTargetActivityCannotHaveMoreIncomingTransitions";
         } else if (((Integer) status.get(0)).intValue() == 3) {
            errorMsg = "ErrorActivityCannotHaveMoreThenOneIncomingOutgoingTransitionFromToTheSameActivity";
         }
         XMLBasicPanel.errorMessage(pnl.getWindow(),
                                    ResourceManager.getLanguageDependentString("ErrorMessageKey"),
                                    "",
                                    ResourceManager.getLanguageDependentString(errorMsg));

      }
      return ac;
   }

   protected boolean validateAssociation(XMLPanel pnl, XMLElement el) {
      Association assoc = XMLUtil.getAssociation(el);
      String oldFrom = assoc.getSource();
      String oldTo = assoc.getTarget();
      String newFrom = oldFrom;
      String newTo = oldTo;

      if (el instanceof Association) {
         XMLPanel ftPanel = findPanel(pnl, assoc.get("Source"));
         // System.err.println("Found panel From ->"+ftPanel);
         if (ftPanel != null) {
            Object v = ftPanel.getValue();
            // System.err.println("..... panel val ->"+v);
            if (v instanceof XMLCollectionElement) {
               newFrom = ((XMLCollectionElement) v).getId();
            } else if (v instanceof String) {
               newFrom = ((String) v).trim();
            }
         }
         ftPanel = findPanel(pnl, assoc.get("Target"));
         // System.err.println("Found panel To ->"+ftPanel);
         if (ftPanel != null) {
            Object v = ftPanel.getValue();
            // System.err.println("..... panel val ->"+v);
            if (v instanceof XMLCollectionElement) {
               newTo = ((XMLCollectionElement) v).getId();
            } else if (v instanceof String) {
               newTo = ((String) v).trim();
            }
         }
      } else if (el instanceof XMLAttribute
                 && (el.toName().equals("Source") || el.toName().equals("Target"))) {
         Object v = pnl.getValue();
         String toOrFrom = null;
         if (v instanceof XMLCollectionElement) {
            toOrFrom = ((XMLCollectionElement) v).getId();
         } else if (v instanceof String) {
            toOrFrom = ((String) v).trim();
         }
         if (toOrFrom != null) {
            if (el.toName().equals("Source")) {
               newFrom = toOrFrom;
            } else {
               newTo = toOrFrom;
            }
         }
      }

      // System.err.println("T=" + oldTo + ", F=" + oldFrom + ", NT=" + newTo + ", NF=" +
      // newFrom+", NT="+newType);
      Package pkg = XMLUtil.getPackage(el);
      XMLCollectionElement aFrom = pkg.getActivity(newFrom);
      if (aFrom == null) {
         aFrom = pkg.getArtifact(newFrom);
      }

      XMLCollectionElement aTo = pkg.getActivity(newTo);
      if (aTo == null) {
         aTo = pkg.getArtifact(newTo);
      }

      // System.err.println("AF=="+actFrom+", aTO="+actTo);
      if (aFrom == null
          || aTo == null || (aFrom instanceof Activity && aTo instanceof Activity)
          || (aFrom instanceof Artifact && aTo instanceof Artifact)) {
         XMLBasicPanel.errorMessage(pnl.getWindow(),
                                    ResourceManager.getLanguageDependentString("ErrorMessageKey"),
                                    "",
                                    ResourceManager.getLanguageDependentString("ErrorImproperAssociationConnection"));
         return false;
      }

      return true;
   }

   public static XMLPanel findPanel(XMLPanel p, XMLElement idEl) {
      if (p instanceof XMLTabbedPanel) {
         return ((XMLTabbedPanel) p).getPanelForElement(idEl);
      } else if (p instanceof XMLGroupPanel) {
         return ((XMLGroupPanel) p).getPanelForElement(idEl);
      }
      return null;
   }

   public Settings getSettings() {
      return null;
   }

}
