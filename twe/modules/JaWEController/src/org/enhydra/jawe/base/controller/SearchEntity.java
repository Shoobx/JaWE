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

package org.enhydra.jawe.base.controller;

import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLComplexElement;

/**
 * This is not a real WfMC element, but it is introduced to help for implementing XPDL
 * search capability.
 * 
 * @author Sasa Bojanic
 */
public class SearchEntity extends XMLComplexElement {

   public static final String SCOPE_MAIN_PACKAGE = "SCOPE_MAIN_PACKAGE";

   public static final String SCOPE_ALL_PACKAGES = "SCOPE_ALL_PACKAGES";

   public static final String SCOPE_SELECTED_ELEMENT = "SCOPE_SELECTED_ELEMENT";

   public SearchEntity() {
      super(null, true);
   }

   protected void fillStructure() {
      XMLAttribute searchForElement = new XMLAttribute(this,
                                                       "SearchForElement",
                                                       true,
                                                       new String[] {
                                                             "*",
                                                             "Activity",
                                                             "ActivitySet",
                                                             "Application",
                                                             "DataField",
                                                             "ExtendedAttribute",
                                                             "FormalParameter",
                                                             "Package",
                                                             "Participant",
                                                             "Transition",
                                                             "TypeDeclaration",
                                                             "WorkflowProcess"
                                                       },
                                                       0) {
         public void setValue(String v) {
            if (!choices.contains(v)) {
               choices.add(v);
            }
            this.value = v;
         }
      };

      XMLAttribute searchByProperty = new XMLAttribute(this,
                                                       "SearchByProperty",
                                                       true,
                                                       new String[] {
                                                             "*", "Description", "Value"
                                                       },
                                                       0) {
         public void setValue(String v) {
            if (!choices.contains(v)) {
               choices.add(v);
            }
            this.value = v;
         }
      };

      XMLAttribute searchByAttribute = new XMLAttribute(this,
                                                        "SearchByAttribute",
                                                        true,
                                                        new String[] {
                                                              "*", "Id", "Name", "Value"
                                                        },
                                                        0) {
         public void setValue(String v) {
            if (!choices.contains(v)) {
               choices.add(v);
            }
            this.value = v;
         }
      };

      XMLAttribute containingText = new XMLAttribute(this, "ContainingText", true);
      containingText.setValue("");
      XMLAttribute caseSensitive = new XMLAttribute(this,
                                                    "CaseSensitive",
                                                    true,
                                                    new String[] {
                                                          "true", "false"
                                                    },
                                                    0) {
         public void setValue(String v) {
            if (!choices.contains(v)) {
               choices.add(v);
            }
            this.value = v;
         }
      };

      caseSensitive.setValue("false");
      XMLAttribute searchScope = new XMLAttribute(this,
                                                  "SearchScope",
                                                  true,
                                                  new String[] {
                                                        SCOPE_MAIN_PACKAGE,
                                                        SCOPE_ALL_PACKAGES,
                                                        SCOPE_SELECTED_ELEMENT
                                                  },
                                                  1);

      super.add(searchForElement);
      super.add(searchByProperty);
      super.add(searchByAttribute);
      super.add(containingText);
      super.add(caseSensitive);
      super.add(searchScope);
   }

   public String getContainingText() {
      return get("ContainingText").toValue();
   }

   public void setContainingText(String ss) {
      set("ContainingText", ss);
   }

   public boolean isCaseSensitive() {
      return new Boolean(get("CaseSensitive").toValue()).booleanValue();
   }

   public void setCaseSensitive(boolean cs) {
      set("CaseSensitive", String.valueOf(cs));
   }

   public String getSearchForElement() {
      return get("SearchForElement").toValue();
   }

   public void setSearchForElement(String sb) {
      set("SearchForElement", sb);
   }

   public String getSearchByAttribute() {
      return get("SearchByAttribute").toValue();
   }

   public void setSearchByAttribute(String sf) {
      set("SearchByAttribute", sf);
   }

   public String getSearchByProperty() {
      return get("SearchByProperty").toValue();
   }

   public void setSearchByProperty(String sf) {
      set("SearchByProperty", sf);
   }

   public String getSearchScope() {
      return get("SearchScope").toValue();
   }

   public void setSearchScope(String ss) {
      set("SearchScope", ss);
   }

}
