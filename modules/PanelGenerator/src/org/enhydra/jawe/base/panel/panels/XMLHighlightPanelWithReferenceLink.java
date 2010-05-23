package org.enhydra.jawe.base.panel.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jawe.base.panel.SpecialChoiceElement;
import org.enhydra.shark.utilities.SequencedHashMap;
import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLEmptyChoiceElement;
import org.enhydra.shark.xpdl.XMLSimpleElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.Participant;
import org.enhydra.shark.xpdl.elements.Performer;
import org.enhydra.shark.xpdl.elements.Tool;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;
import org.jedit.syntax.JEditTextArea;

/**
 * Creates panel with JLabel and JEditPanel.
 * @author Sinisa Tutus
 */
public class XMLHighlightPanelWithReferenceLink extends XMLBasicPanel {

   private static Dimension refButDimension=new Dimension(25,20);
   public static Dimension textAreaDimensionMedium=new Dimension(300,90);
   Dimension textDim=new Dimension(250,20);
   protected XMLMultiLineHighlightPanelWithChoiceButton panel;
   protected JButton jb;   
   protected ArrayList participants;
   
   public XMLHighlightPanelWithReferenceLink (
         PanelContainer pc,
         Performer myOwner,
         List choices,
         boolean hasEmptyBorder,
         boolean isVertical,
         boolean isEditable,
         boolean isEnabled) {

      super(pc,myOwner,"",isVertical,false,hasEmptyBorder);

      boolean panelEnabled=true;
      if (pc!=null) {         
         
         Settings settings=pc.getSettings();
         
         // check if there is a property to disable panel
         String discbo = settings.getSettingString("XMLComboPanel.DisablePanel");
         String[] hstra = Utils.tokenize(discbo, " ");
         if (hstra!=null) {
            for (int i=0;i<hstra.length;i++) {
               if(hstra[i].equals(myOwner.toName())) {
                  panelEnabled=false;
                  break;
               }
            }
         }           
      }
      
      SequencedHashMap ch = XMLUtil.getPossibleParticipants(XMLUtil.getWorkflowProcess(this.myOwner),
              JaWEManager.getInstance()
                 .getXPDLHandler());
      
      participants = new ArrayList(ch.values());
      
      XMLChoiceButtonWithPopup variableList = null;
      List choice = null;    
      XMLElement holder = myOwner;
      
      if (XMLUtil.getWorkflowProcess(holder) != null && (holder instanceof Activity || holder instanceof Performer
              || holder instanceof Transition || holder instanceof Tool || holder instanceof WorkflowProcess)) {
      
	      choice = new ArrayList(XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(holder)).values());
	      
	      variableList = new XMLChoiceButtonWithPopup(this, choice, ((PanelSettings)pc.getSettings()).getInsertVariableDefaultIcon(), 
	              ((PanelSettings)pc.getSettings()).getInsertVariablePressedIcon());
      }
            
      Object chsn=null;
      XMLElement choosen=myOwner;
      if (choosen instanceof XMLComplexElement) { 
         chsn=new XMLElementView(pc,choosen, XMLElementView.TONAME );
      } else {
         chsn=new XMLElementView(pc,choosen, XMLElementView.TOVALUE );
      }
      String initText = null;
      if (chsn!=null) {
      	XMLElement element = ((XMLElementView)chsn).getElement();
      	if(element instanceof XMLCollectionElement)
      		initText = ((((XMLCollectionElement)element).getId()));
      	else 
      		initText = element.toValue();
      }
      
      panel =  new XMLMultiLineHighlightPanelWithChoiceButton(getPanelContainer(),
													              myOwner,
													              myOwner.toName(),
													              false,
													              true,
													              XMLMultiLineTextPanel.SIZE_MEDIUM,
													              false,
													              participants,
													              isEnabled,
													              initText);
      
      panel.setAlignmentX(Component.LEFT_ALIGNMENT);
      panel.setAlignmentY(Component.TOP_ALIGNMENT);           
      panel.setEnabled( isEnabled && panelEnabled);
      Iterator it = participants.iterator();
      while (it.hasNext()) {
			XMLElement el = ((XMLElement) it.next());
			if (!(el instanceof XMLEmptyChoiceElement)) {
				String scriptType = XMLUtil.getPackage(el).getScript().getType();
				panel.setHighlightScript(scriptType);
				break;
			}
		}

		if (isEditable) {
			panel.jta.addCaretListener(new CaretListener() {
				public void caretUpdate(CaretEvent e) {
					
					if (!(getInsertedText() instanceof XMLElement)) {
						jb.setEnabled(false);
					} else {
						jb.setEnabled(true);
					}					
				}
			});
		}

		add(panel);
      
      if (pc!=null) {
         jb = new JButton(((PanelSettings)pc.getSettings()).getArrowRightImageIcon());
      } else {
         jb = new JButton("->");
      }
      jb.setBorderPainted(false);
      jb.setAlignmentX(Component.LEFT_ALIGNMENT);
      jb.setAlignmentY(Component.TOP_ALIGNMENT);
      jb.setMinimumSize(new Dimension(refButDimension));
      jb.setMaximumSize(new Dimension(refButDimension));
      jb.setPreferredSize(new Dimension(refButDimension));
      jb.setRolloverEnabled(true);         
      jb.setContentAreaFilled(false);
      jb.setEnabled((getInsertedText() instanceof XMLElement));
      jb.addActionListener(new ActionListener(){
         public void actionPerformed( ActionEvent ae ){
            Object toShow=getInsertedText();
            if (toShow instanceof XMLElement) {
               ((InlinePanel)getPanelContainer()).displayGivenElement((XMLElement)toShow);
            }
         }
         
      });
      
      if(variableList!=null){
    	  variableList.setBorderPainted(false);
    	  variableList.setContentAreaFilled(false);
    	  variableList.setAlignmentX(Component.LEFT_ALIGNMENT);
    	  variableList.setAlignmentY(Component.TOP_ALIGNMENT);
    	  if(!isEnabled || choice.size()==0)
    		  variableList.setEnabled(false);
    	  
    	  panel.jspAndOpt.add(variableList, 0);    	  
      }      
      panel.jspAndOpt.add(jb);
   }

   
   public boolean validateEntry () {
      String siv="";
      Object selItem=getInsertedText();
      System.err.println("Validating entry for "+selItem);
      if (selItem!=null) {
//         System.err.println("SI="+selItem+", class="+selItem.getClass().getName());
         if (selItem instanceof XMLElement) {
            if (selItem instanceof XMLSimpleElement || selItem instanceof XMLAttribute /*|| selItem instanceof XMLEmptyChoiceElement*/) {
               siv=((XMLElement)selItem).toValue();
            } else {
               siv=((XMLElement)selItem).toName();
            }
         } else {
            siv=selItem.toString();
         }
      }
//      System.err.println("SIV="+siv);
//      System.err.println("IREQ="+getOwner().isRequired()+", iro="+getOwner().isReadOnly());
      if ((selItem==null || siv.trim().equals("")) &&
          getOwner().isRequired() && !getOwner().isReadOnly()) {
         int cs=((SpecialChoiceElement)myOwner).getChoices().size();
         Object ec=null;
         if (cs==1) {
            ec=((SpecialChoiceElement)myOwner).getChoices().get(0);
         }
//         System.err.println("CCCCSSSS="+cs+", ec="+ec);
         if (!(cs==0 || cs==1)) {
            XMLBasicPanel.defaultErrorMessage(this.getWindow(),panel.jl.getText());
            panel.jta.requestFocus();
            return false;
         }
      }
      return true;
   }

   public void setElements () {
      if (!getOwner().isReadOnly()) {
         Object sel=getInsertedText();         
         if (sel instanceof Participant) {
        	 myOwner.setValue(((Participant)sel).getId());
         } else if (sel instanceof String){
             myOwner.setValue(sel.toString());
         }
      }
   }

   public JEditTextArea getEditTextArea () {
      return panel.jta;
   }

   public Object getInsertedText() {
      try {
         Object el = null;    
         String compareString = null;
         el = panel.getText().trim();
         
         Iterator it = participants.iterator();
         while(it.hasNext()){
        	 
        	 XMLElement xmlEl = (XMLElement)it.next();
        	 if (xmlEl instanceof XMLCollectionElement) 
        		 compareString = (((XMLCollectionElement)xmlEl).getId());        	         	 
        	 else
        		 compareString = xmlEl.toName();
        	 
        	 if(el.equals(compareString))
        		 return xmlEl;
         }
         if (el != null) {
        	 return el.toString();
         }         
         return "";
      } catch (Exception ex) {
         ex.printStackTrace();
         return "";
      }
   }

   public Object getValue () {
      return getInsertedText();
   }
   
   public void cleanup () {
   }   
   
   public void appendText (String txt) {
	      panel.appendText(txt);
	      panel.jta.requestFocus();
   }
}

