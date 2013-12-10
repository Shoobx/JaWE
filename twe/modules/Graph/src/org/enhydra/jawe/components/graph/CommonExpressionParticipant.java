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

package org.enhydra.jawe.components.graph;

import org.enhydra.jawe.ResourceManager;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.elements.ActivitySet;

/**
 * Represents helper participant for entering performer expressions.
 */
public class CommonExpressionParticipant extends Participant {

   /** {@link WorkflowProcess} or {@link ActivitySet} instance. */
   protected XMLCollectionElement wpOrAs;

   /**
    * Creates new object.
    * 
    * @param parent The parent collection.
    * @param wpOrAs {@link WorkflowProcess} or {@link ActivitySet} instance.
    */
   public CommonExpressionParticipant(CommonExpressionParticipants parent,
                                      XMLCollectionElement wpOrAs) {
      super(parent);
      this.wpOrAs = wpOrAs;
      setDescription(ResourceManager.getLanguageDependentString("CommonExpressionParticipantDescription"));
      setReadOnly(true);
      this.isReadOnly = false;
      get("Id").setReadOnly(false);
   }

   /**
    * @return The {@link WorkflowProcess} or {@link ActivitySet} instance for which we
    *         define graphical participant object.
    */
   public XMLCollectionElement getGraphXPDLElement() {
      return wpOrAs;
   }

}
