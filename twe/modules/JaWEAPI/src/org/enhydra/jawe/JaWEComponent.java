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

package org.enhydra.jawe;

import java.util.List;

import org.enhydra.jawe.base.xpdlvalidator.ValidationError;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.elements.Package;

/**
 * This interface should be implemented by any class that tends to be a JaWE's component.
 */
public interface JaWEComponent {

   /** Constant representing the name for the main area. */
   public static final String MAIN_COMPONENT = "MAIN";

   /** Constant representing the name for the special area. */
   public static final String SPECIAL_COMPONENT = "SPECIAL";

   /** Constant representing the name for the tree area. */
   public static final String TREE_COMPONENT = "TREE";

   /** Constant representing the name for the other area. */
   public static final String OTHER_COMPONENT = "OTHER";

   /** Constant representing the name for the upper status area. */
   public static final String UPPER_STATUS_COMPONENT = "UPPER_STATUS";

   /** Constant representing the name for the lower status area. */
   public static final String LOWER_STATUS_COMPONENT = "LOWER_STATUS";

   /**
    * @return {@link JaWEComponentSettings} instance that holds information about
    *         component settings.
    */
   JaWEComponentSettings getSettings();

   /**
    * @return {@link JaWEComponentView} instance that describes the graphical view for the
    *         component.
    */
   JaWEComponentView getView();

   /**
    * @return Component name.
    */
   String getName();

   /**
    * @return Component type.
    */
   String getType();

   /**
    * Sets the component type.
    * 
    * @param type Component type.
    */
   public void setType(String type);

   /**
    * Adjusts XPDL Java model according to JaWE specifics.
    * 
    * @param pkg {@link Package} instance
    * @return true if anything changed to the Java model during adjustment.
    */
   boolean adjustXPDL(Package pkg);

   /**
    * Validates given {@link XMLElement} according to XPDL spec and XPDL schema rules.
    * 
    * @param el {@link XMLElement} instance to validate.
    * @param fullCheck if false, validation stops after first error is found.
    * @return The list of {@link ValidationError} elements, or null if there are no
    *         errors.
    */
   List checkValidity(XMLElement el, boolean fullCheck);

   /**
    * Checks if element can be created for the given {@link XMLCollection}.
    * 
    * @param col {@link XMLCollection} instance.
    * @return true if new {@link XMLElement} can be created for the given
    *         {@link XMLCollection}.
    */
   boolean canCreateElement(XMLCollection col);

   /**
    * Checks if given {@link XMLElement} can be inserted into the given
    * {@link XMLCollection}.
    * 
    * @param col {@link XMLCollection} instance.
    * @param el {@link XMLElement} instance.
    * @return true if given {@link XMLElement} can be inserted into the given
    *         {@link XMLCollection}.
    */
   boolean canInsertElement(XMLCollection col, XMLElement el);

   /**
    * Checks if given {@link XMLElement} can be modified.
    * 
    * @param el {@link XMLElement} instance.
    * @return true if given {@link XMLElement} can be modified.
    */
   public boolean canModifyElement(XMLElement el);

   /**
    * Checks if given {@link XMLElement} can be removed from the given
    * {@link XMLCollection}.
    * 
    * @param col {@link XMLCollection} instance.
    * @param el {@link XMLElement} instance.
    * @return true if given {@link XMLElement} can be removed from the given
    *         {@link XMLCollection}.
    */
   public boolean canRemoveElement(XMLCollection col, XMLElement el);

   /**
    * Checks if given {@link XMLElement} can be duplicated inside the given
    * {@link XMLCollection}.
    * 
    * @param col {@link XMLCollection} instance.
    * @param el {@link XMLElement} instance.
    * @return true if given {@link XMLElement} can be duplicated inside the given
    *         {@link XMLCollection}.
    */
   public boolean canDuplicateElement(XMLCollection col, XMLElement el);

   /**
    * Checks if given {@link XMLElement} can be repositioned within the given
    * {@link XMLCollection}.
    * 
    * @param col {@link XMLCollection} instance.
    * @param el {@link XMLElement} instance.
    * @return true if given {@link XMLElement} can be repositioned within the given
    *         {@link XMLCollection}.
    */
   public boolean canRepositionElement(XMLCollection col, XMLElement el);

   /**
    * Sets "update in progress" status.
    * 
    * @param inProgress "update in progress" status.
    */
   public void setUpdateInProgress(boolean inProgress);

   /**
    * @return true if update is in progress, false otherwise.
    */
   public boolean isUpdateInProgress();
}
