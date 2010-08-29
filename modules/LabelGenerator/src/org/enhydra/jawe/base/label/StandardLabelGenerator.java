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
import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLComplexChoice;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLEmptyChoiceElement;
import org.enhydra.shark.xpdl.XMLSimpleElement;
import org.enhydra.shark.xpdl.elements.Activities;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.ActivitySets;
import org.enhydra.shark.xpdl.elements.ActivityTypes;
import org.enhydra.shark.xpdl.elements.ActualParameter;
import org.enhydra.shark.xpdl.elements.ActualParameters;
import org.enhydra.shark.xpdl.elements.Application;
import org.enhydra.shark.xpdl.elements.ApplicationTypes;
import org.enhydra.shark.xpdl.elements.Applications;
import org.enhydra.shark.xpdl.elements.ArrayType;
import org.enhydra.shark.xpdl.elements.Author;
import org.enhydra.shark.xpdl.elements.Automatic;
import org.enhydra.shark.xpdl.elements.BasicType;
import org.enhydra.shark.xpdl.elements.BlockActivity;
import org.enhydra.shark.xpdl.elements.Codepage;
import org.enhydra.shark.xpdl.elements.Condition;
import org.enhydra.shark.xpdl.elements.ConformanceClass;
import org.enhydra.shark.xpdl.elements.Cost;
import org.enhydra.shark.xpdl.elements.CostUnit;
import org.enhydra.shark.xpdl.elements.Countrykey;
import org.enhydra.shark.xpdl.elements.Created;
import org.enhydra.shark.xpdl.elements.DataField;
import org.enhydra.shark.xpdl.elements.DataFields;
import org.enhydra.shark.xpdl.elements.DataType;
import org.enhydra.shark.xpdl.elements.DataTypes;
import org.enhydra.shark.xpdl.elements.Deadline;
import org.enhydra.shark.xpdl.elements.DeadlineCondition;
import org.enhydra.shark.xpdl.elements.Deadlines;
import org.enhydra.shark.xpdl.elements.DeclaredType;
import org.enhydra.shark.xpdl.elements.Description;
import org.enhydra.shark.xpdl.elements.Documentation;
import org.enhydra.shark.xpdl.elements.Duration;
import org.enhydra.shark.xpdl.elements.EnumerationType;
import org.enhydra.shark.xpdl.elements.EnumerationValue;
import org.enhydra.shark.xpdl.elements.ExceptionName;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.ExtendedAttributes;
import org.enhydra.shark.xpdl.elements.ExternalPackage;
import org.enhydra.shark.xpdl.elements.ExternalPackages;
import org.enhydra.shark.xpdl.elements.ExternalReference;
import org.enhydra.shark.xpdl.elements.FinishMode;
import org.enhydra.shark.xpdl.elements.FormalParameter;
import org.enhydra.shark.xpdl.elements.FormalParameters;
import org.enhydra.shark.xpdl.elements.Icon;
import org.enhydra.shark.xpdl.elements.Implementation;
import org.enhydra.shark.xpdl.elements.ImplementationTypes;
import org.enhydra.shark.xpdl.elements.InitialValue;
import org.enhydra.shark.xpdl.elements.Join;
import org.enhydra.shark.xpdl.elements.Length;
import org.enhydra.shark.xpdl.elements.Limit;
import org.enhydra.shark.xpdl.elements.ListType;
import org.enhydra.shark.xpdl.elements.Manual;
import org.enhydra.shark.xpdl.elements.Member;
import org.enhydra.shark.xpdl.elements.Namespace;
import org.enhydra.shark.xpdl.elements.Namespaces;
import org.enhydra.shark.xpdl.elements.No;
import org.enhydra.shark.xpdl.elements.PackageHeader;
import org.enhydra.shark.xpdl.elements.Participant;
import org.enhydra.shark.xpdl.elements.ParticipantType;
import org.enhydra.shark.xpdl.elements.Participants;
import org.enhydra.shark.xpdl.elements.Performer;
import org.enhydra.shark.xpdl.elements.Priority;
import org.enhydra.shark.xpdl.elements.PriorityUnit;
import org.enhydra.shark.xpdl.elements.ProcessHeader;
import org.enhydra.shark.xpdl.elements.RecordType;
import org.enhydra.shark.xpdl.elements.RedefinableHeader;
import org.enhydra.shark.xpdl.elements.Responsible;
import org.enhydra.shark.xpdl.elements.Responsibles;
import org.enhydra.shark.xpdl.elements.Route;
import org.enhydra.shark.xpdl.elements.SchemaType;
import org.enhydra.shark.xpdl.elements.Script;
import org.enhydra.shark.xpdl.elements.SimulationInformation;
import org.enhydra.shark.xpdl.elements.Split;
import org.enhydra.shark.xpdl.elements.StartFinishModes;
import org.enhydra.shark.xpdl.elements.StartMode;
import org.enhydra.shark.xpdl.elements.SubFlow;
import org.enhydra.shark.xpdl.elements.TimeEstimation;
import org.enhydra.shark.xpdl.elements.Tool;
import org.enhydra.shark.xpdl.elements.Tools;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.TransitionRef;
import org.enhydra.shark.xpdl.elements.TransitionRefs;
import org.enhydra.shark.xpdl.elements.TransitionRestriction;
import org.enhydra.shark.xpdl.elements.TransitionRestrictions;
import org.enhydra.shark.xpdl.elements.Transitions;
import org.enhydra.shark.xpdl.elements.TypeDeclaration;
import org.enhydra.shark.xpdl.elements.TypeDeclarations;
import org.enhydra.shark.xpdl.elements.UnionType;
import org.enhydra.shark.xpdl.elements.ValidFrom;
import org.enhydra.shark.xpdl.elements.ValidTo;
import org.enhydra.shark.xpdl.elements.Vendor;
import org.enhydra.shark.xpdl.elements.Version;
import org.enhydra.shark.xpdl.elements.WaitingTime;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;
import org.enhydra.shark.xpdl.elements.WorkflowProcesses;
import org.enhydra.shark.xpdl.elements.WorkingTime;
import org.enhydra.shark.xpdl.elements.XPDLVersion;

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
   
   public String getLabel(Activities el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Activity el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ActivitySet el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ActivitySets el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ActivityTypes el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ActualParameter el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ActualParameters el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Application el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Applications el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ApplicationTypes el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ArrayType el) {
//       return XMLUtil.getLanguageDependentString("SubTypeKey");
      return generateStandardLabel(el);
   }

   public String getLabel(Author el) {
      return generateStandardLabel(el);
   }


   public String getLabel(Automatic el) {
      return generateStandardLabel(el);
   }

   public String getLabel(BasicType el) {
//       return XMLUtil.getLanguageDependentString("SubTypeKey");
      return generateStandardLabel(el);
   }

   public String getLabel(BlockActivity el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Codepage el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Condition el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ConformanceClass el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Cost el) {
      return generateStandardLabel(el);
   }

   public String getLabel(CostUnit el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Countrykey el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Created el) {
      return generateStandardLabel(el);
   }

   public String getLabel(DataField el) {
      return generateStandardLabel(el);
   }

   public String getLabel(DataFields el) {
      return generateStandardLabel(el);
   }

   public String getLabel(DataType el) {
      return generateStandardLabel(el);
   }

   public String getLabel(DataTypes el) {
      return settings.getLanguageDependentString("TypeKey");
   }

   public String getLabel(Deadline el) {
      return generateStandardLabel(el);
   }

   public String getLabel(DeadlineCondition el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Deadlines el) {
      return generateStandardLabel(el);
   }

   public String getLabel(DeclaredType el) {
//       return XMLUtil.getLanguageDependentString("SubTypeKey");
      return generateStandardLabel(el);
   }

   public String getLabel(Description el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Documentation el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Duration el) {
      return generateStandardLabel(el);
   }

   public String getLabel(EnumerationType el) {
      return generateStandardLabel(el);
   }

   public String getLabel(EnumerationValue el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ExceptionName el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ExtendedAttribute el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ExtendedAttributes el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ExternalPackage el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ExternalPackages el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ExternalReference el) {
      return generateStandardLabel(el);
   }

   public String getLabel(FinishMode el) {
      return generateStandardLabel(el);
   }

   public String getLabel(FormalParameter el) {
      return generateStandardLabel(el);
   }

   public String getLabel(FormalParameters el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Icon el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Implementation el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ImplementationTypes el) {
      return generateStandardLabel(el);
   }

   public String getLabel(InitialValue el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Join el) {
       return settings.getLanguageDependentString("JoinTypeKey");
   }

   public String getLabel(Length el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Limit el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ListType el) {
      return generateStandardLabel(el);
//       return XMLUtil.getLanguageDependentString("SubTypeKey");
   }

   public String getLabel(Manual el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Member el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Namespace el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Namespaces el) {
      return generateStandardLabel(el);
   }

   public String getLabel(No el) {
      return generateStandardLabel(el);
   }

   public String getLabel(org.enhydra.shark.xpdl.elements.Package el) {
      return generateStandardLabel(el);
   }

   public String getLabel(PackageHeader el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Participant el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Participants el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ParticipantType el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Performer el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Priority el) {
      return generateStandardLabel(el);
   }

   public String getLabel(PriorityUnit el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ProcessHeader el) {
      return generateStandardLabel(el);
   }

   public String getLabel(RecordType el) {
      return generateStandardLabel(el);
   }

   public String getLabel(RedefinableHeader el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Responsible el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Responsibles el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Route el) {
      return generateStandardLabel(el);
   }

   public String getLabel(SchemaType el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Script el) {
      return generateStandardLabel(el);
   }

   public String getLabel(SimulationInformation el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Split el) {
       return settings.getLanguageDependentString("SplitTypeKey");
   }

   public String getLabel(StartFinishModes el) {
      return generateStandardLabel(el.getParent());
   }

   public String getLabel(StartMode el) {
      return generateStandardLabel(el);
   }

   public String getLabel(SubFlow el) {
      return generateStandardLabel(el);
   }

   public String getLabel(TimeEstimation el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Tool el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Tools el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Transition el) {
      return generateStandardLabel(el);
   }

   public String getLabel(TransitionRef el) {
      return generateStandardLabel(el);
   }

   public String getLabel(TransitionRefs el) {
      return generateStandardLabel(el);
   }

   public String getLabel(TransitionRestriction el) {
      return generateStandardLabel(el);
   }

   public String getLabel(TransitionRestrictions el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Transitions el) {
      return generateStandardLabel(el);
   }

   public String getLabel(TypeDeclaration el) {
      return generateStandardLabel(el);
   }

   public String getLabel(TypeDeclarations el) {
      return generateStandardLabel(el);
   }

   public String getLabel(UnionType el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ValidFrom el) {
      return generateStandardLabel(el);
   }

   public String getLabel(ValidTo el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Vendor el) {
      return generateStandardLabel(el);
   }

   public String getLabel(Version el) {
      return generateStandardLabel(el);
   }

   public String getLabel(WaitingTime el) {
      return generateStandardLabel(el);
   }

   public String getLabel(WorkflowProcess el) {
      return generateStandardLabel(el);
   }

   public String getLabel(WorkflowProcesses el) {
      return generateStandardLabel(el);
   }

   public String getLabel(WorkingTime el) {
      return generateStandardLabel(el);
   }

   public String getLabel(XPDLVersion el) {
      return generateStandardLabel(el);
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
