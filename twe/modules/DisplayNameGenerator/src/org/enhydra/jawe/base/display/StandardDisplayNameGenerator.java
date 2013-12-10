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
import org.enhydra.shark.utilities.SequencedHashMap;
import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLBaseForCollectionAndComplex;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLComplexChoice;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLEmptyChoiceElement;
import org.enhydra.shark.xpdl.XMLSimpleElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.*;
import org.enhydra.shark.xpdl.elements.Package;

/**
 *  Used to generate strings for graphical representation of all XPDL entities.
 *
 *  @author Sasa Bojanic
 */
public class StandardDisplayNameGenerator implements DisplayNameGenerator {
  
   protected DisplayNameGeneratorSettings settings;
  
   public StandardDisplayNameGenerator () {
      settings = new DisplayNameGeneratorSettings();
      settings.init((JaWEComponent) null);
   }
   
   public StandardDisplayNameGenerator(DisplayNameGeneratorSettings settings) {
      this.settings = settings;
      this.settings.init((JaWEComponent) null);
   }
   
   public String getDisplayName(Activities el) {
      return generateStandardDisplayName(el);
   }
   
   public String getDisplayName(Activity el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ActivitySet el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ActivitySets el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ActivityTypes el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ActualParameter el) {
      WorkflowProcess wp=XMLUtil.getWorkflowProcess(el);
      Map vars=XMLUtil.getPossibleVariables(wp);
      String vId=el.toValue();
      if (!vId.equals("")) {
         XMLCollectionElement dfOrFp=(XMLCollectionElement)vars.get(vId);
         if (dfOrFp!=null) {
            return getDisplayName(dfOrFp);
         }
      }
      return vId;      
   }

   public String getDisplayName(ActualParameters el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Application el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Applications el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ApplicationTypes el) {
      return generateStandardDisplayName(el);
   }
   
   public String getDisplayName(ArrayType el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Author el) {
      return generateStandardDisplayName(el);
   }


   public String getDisplayName(Automatic el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(BasicType el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(BlockActivity el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Codepage el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Condition el) {
      return el.toValue();
   }

   public String getDisplayName(ConformanceClass el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Cost el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(CostUnit el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Countrykey el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Created el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(DataField el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(DataFields el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(DataType el) {
      return getDisplayName(el.getDataTypes());
   }

   public String getDisplayName(DataTypes el) {
      //return generateStandardDisplayName(el);
      XMLBaseForCollectionAndComplex ch=(XMLBaseForCollectionAndComplex)el.getChoosen();
      LabelGenerator lg=JaWEManager.getInstance().getLabelGenerator();      
      String disp=lg.getLabel(ch);
      if (ch instanceof BasicType) {
         disp+=" -> "+ settings.getLanguageDependentString(((BasicType)ch).getType() + "Key");
      } else if (ch instanceof DeclaredType) {
         String tdId=((DeclaredType)ch).getId();
         TypeDeclaration td=null;
         if (el.getParent() instanceof TypeDeclaration) {
            td=((TypeDeclarations)el.getParent().getParent()).getTypeDeclaration(tdId);
         } else {
            td =XMLUtil.getPackage(el).getTypeDeclaration(tdId);
         }
         disp+=" -> "+getDisplayName(td);
      }
      return disp;
   }

   public String getDisplayName(Deadline el) {
      return el.getDeadlineCondition();
   }

   public String getDisplayName(DeadlineCondition el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Deadlines el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(DeclaredType el) {
      LabelGenerator lg=JaWEManager.getInstance().getLabelGenerator();      
      return lg.getLabel(el);
      //return generateStandardDisplayName(el);
   }

   public String getDisplayName(Description el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Documentation el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Duration el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(EnumerationType el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(EnumerationValue el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ExceptionName el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ExtendedAttribute el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ExtendedAttributes el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ExternalPackage el) {
      //return generateStandardDisplayName(el);
      return el.getHref();
   }

   public String getDisplayName(ExternalPackages el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ExternalReference el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(FinishMode el) {
      return generateStandardDisplayName(el.getStartFinishModes());
   }

   public String getDisplayName(FormalParameter el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(FormalParameters el) {
      if (el.getParent() instanceof XMLComplexChoice) {
         return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
      } 
      return String.valueOf(el.size());      
   }

   public String getDisplayName(Icon el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Implementation el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ImplementationTypes el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(InitialValue el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Join el) {
       return generateStandardDisplayName(el.getTypeAttribute());
   }

   public String getDisplayName(Length el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Limit el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ListType el) {
       return generateStandardDisplayName(el);
   }

   public String getDisplayName(Manual el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Member el) {
      return getDisplayName(el.getDataTypes());
   }

   public String getDisplayName(Namespace el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Namespaces el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(No el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(org.enhydra.shark.xpdl.elements.Package el) {
      return generateStandardDisplayName(el);
   }
   
   public String getDisplayName(PackageHeader el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Participant el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Participants el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ParticipantType el) {
      return generateStandardDisplayName(el.getTypeAttribute());
   }

   public String getDisplayName(Performer el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Priority el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(PriorityUnit el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ProcessHeader el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(RecordType el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(RedefinableHeader el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Responsible el) {
      WorkflowProcess wp=XMLUtil.getWorkflowProcess(el);
      Package pkg=XMLUtil.getPackage(el);
      XPDLHandler xpdlh=JaWEManager.getInstance().getXPDLHandler();
      Map ps=null;
      if (wp!=null) {
         ps=XMLUtil.getPossibleParticipants(wp, xpdlh);
      } else if (pkg!=null) {
         ps=XMLUtil.getPossibleParticipants(pkg, xpdlh);
      }
      String pId=el.toValue();
      if (ps!=null && !pId.equals("")) {
         Participant p=(Participant)ps.get(pId);
         if (p!=null) {
            return getDisplayName(p);
         }
      }
      return pId;
   }

   public String getDisplayName(Responsibles el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Route el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(SchemaType el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Script el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(SimulationInformation el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Split el) {
      return generateStandardDisplayName(el.getTypeAttribute());
   }

   public String getDisplayName(StartFinishModes el) {
      return generateStandardDisplayName(el.getChoosen());
   }

   public String getDisplayName(StartMode el) {
      return generateStandardDisplayName(el.getStartFinishModes());
   }

   public String getDisplayName(SubFlow el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(TimeEstimation el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Tool el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Tools el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(Transition el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(TransitionRef el) {
      return getDisplayName(el.get("Id"));
   }

   public String getDisplayName(TransitionRefs el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(TransitionRestriction el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(TransitionRestrictions el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Transitions el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(TypeDeclaration el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(TypeDeclarations el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(UnionType el) {
      return JaWEManager.getInstance().getLabelGenerator().getLabel(el);
   }

   public String getDisplayName(ValidFrom el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(ValidTo el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Vendor el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(Version el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(WaitingTime el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(WorkflowProcess el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(WorkflowProcesses el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(WorkingTime el) {
      return generateStandardDisplayName(el);
   }

   public String getDisplayName(XPDLVersion el) {
      return generateStandardDisplayName(el);
   }

   
   public String getDisplayName(XMLAttribute el) {
      if (el.getParent() instanceof Transition && (el.toName().equals("From") || el.toName().equals("To"))) {
         XMLCollectionElement wpOrAs=XMLUtil.getActivitySet(el);
         if (wpOrAs==null) {
            wpOrAs=XMLUtil.getWorkflowProcess(el);
         }
         SequencedHashMap pas=getPossibleActivities(wpOrAs);
         Activity act=(Activity)pas.get(el.toValue());
         if (act!=null) {
            return generateStandardDisplayName(act);
         }
      } else if (el.getParent() instanceof TransitionRef) {
         XMLCollectionElement wpOrAs=XMLUtil.getActivitySet(el);
         if (wpOrAs==null) {
            wpOrAs=XMLUtil.getWorkflowProcess(el);
         }
         Transitions tras=(Transitions)wpOrAs.get("Transitions");
         Transition tra=tras.getTransition(el.toValue());
         if (tra!=null) {
            SequencedHashMap pas=getPossibleActivities(wpOrAs);
            Activity act=(Activity)pas.get(tra.getTo());
            if (act!=null) {
               return generateStandardDisplayName(act);
            }    
         }
      }
      return generateStandardDisplayName(el);
   }

   public String getDisplayName (XMLEmptyChoiceElement el) {
      String v=el.toValue();
//      try {
//         v=settings.getLanguageDependentString(v+"Key");
//         System.out.println("LDS for "+el.toValue()+"Key is "+v);
//         if (v==null) {
//            v=el.toValue();
//         }
//      } catch (Exception ex) {
//         System.out.println("Failed getting LDS for "+el.toValue()+"Key");
//         v=el.toValue();
//      }
      return v;
   }

   public String getDisplayName (XMLComplexChoice el) {
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
   
   public String getDisplayName (XMLElement el) {
      try {
         Class cl = el.getClass();           
         Method m = null;
         try {
            m = this.getClass().getMethod("getDisplayName", new Class[] { cl } );
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
         m = this.getClass().getMethod("getDisplayName", new Class[] { cl } );
//          System.err.println("calling "+m.toString());
         return (String) m.invoke(this, new Object[] { el });
      } catch (Throwable e) {
          e.printStackTrace();
      }
      
      return generateStandardDisplayName(el);
   }
   
   public String generateStandardDisplayName (XMLElement el) {
      String disp="";
      if (el instanceof XMLCollection) {
         disp=String.valueOf(((XMLCollection)el).size());
      } else if (el instanceof XMLComplexElement) {
         XMLElement nme=((XMLComplexElement)el).get("Name");
         if (nme!=null) {
            disp=nme.toValue();
         }
         if (disp.equals("")) {
            XMLElement id=((XMLComplexElement)el).get("Id");
            if (id!=null) {
               disp=id.toValue();
            }
         }
         if (disp.equals("")) {
            disp=JaWEManager.getInstance().getLabelGenerator().getLabel(el);
         }
      } else if (el instanceof XMLComplexChoice) {
         disp=getDisplayName(((XMLComplexChoice)el).getChoosen());
      } else if (el instanceof XMLAttribute && ((XMLAttribute)el).getChoices()!=null){
         disp= settings.getLanguageDependentString(el.toValue()+"Key");
         if  (disp==null || disp.equals("")) {
            disp=el.toValue();
         }
      } else {
         disp=el.toValue();
      }      
      return disp;
   }
   
   public DisplayNameGeneratorSettings getSetting() {
      return settings;
   }
   
   public Settings getSettings () {
      return settings;
   }
   
   public SequencedHashMap getPossibleActivities (XMLCollectionElement wpOrAs) {
      SequencedHashMap toRet=new SequencedHashMap();
      List acts=((Activities)wpOrAs.get("Activities")).toElements();
      Iterator it=acts.iterator();
      while (it.hasNext()) {
         Activity act=(Activity)it.next();
         toRet.put(act.getId(),act);
      }
      return toRet;
   }

   
}
