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

package org.enhydra.jawe.components.graph;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SwingConstants;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.JaWEAction;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XMLElementChoiceButton;
import org.enhydra.jawe.base.controller.JaWEType;
import org.enhydra.jawe.base.controller.JaWETypes;
import org.enhydra.jawe.components.graph.actions.SetActivityMode;
import org.enhydra.jawe.components.graph.actions.SetArtifactMode;
import org.enhydra.jawe.components.graph.actions.SetAssociationMode;
import org.enhydra.jawe.components.graph.actions.SetLaneMode;
import org.enhydra.jawe.components.graph.actions.SetLaneModeFreeTextExpression;
import org.enhydra.jawe.components.graph.actions.SetSelectMode;
import org.enhydra.jawe.components.graph.actions.SetTransitionMode;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Transition;

/**
 *  Container for displaying menubar, toolbar, process graphs ...
 *
 *  @author Sasa Bojanic
 *  @author Danijel Predarski
 *  
 */
public class GraphControllerPanel extends JPanel implements JaWEComponentView {

	protected GraphController controller;

	// various things needed for initializing and further work
	protected JScrollPane graphScrollPane;
	protected JToolBar toolbar;
	protected XMLElementChoiceButton showParticipantChoiceButton;
	protected XMLElementChoiceButton asChoiceButton;
	protected XMLElementChoiceButton showActivityModes;
	protected JPanel toolbars=new JPanel();

	public GraphControllerPanel (GraphController controller) {
		this.controller=controller;
	}

	public void configure() {
	}

	public void init () {
		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BorderLayout());
		toolbars.setLayout(new BorderLayout());
		// creating toolbars
		toolbar = BarFactory.createToolbar("defaultToolbar", controller);
		toolbar.setFloatable(false);
		// creating button panel
		ImageIcon curIc = controller.getGraphSettings().getLanesIcon();
		showParticipantChoiceButton = new XMLElementChoiceButton(Participant.class,controller,curIc, true, new String[]
		                                                                                                              {"Id","Name","ParticipantType","Description"});
		showParticipantChoiceButton.setToolTipText(controller.getSettings().getLanguageDependentString("InsertExistingParticipant" + BarFactory.TOOLTIP_POSTFIX));
		curIc = controller.getGraphSettings().getActivitySetSelectIcon();
		asChoiceButton = new XMLElementChoiceButton(ActivitySet.class, controller, curIc, true, new String[]{"Id"});
		asChoiceButton.setToolTipText(controller.getSettings().getLanguageDependentString("SelectActivitySet" + BarFactory.TOOLTIP_POSTFIX));
		JToolBar part = new JToolBar();


		if (controller.getGraphSettings().useLaneChoiceButton())
			part.add(showParticipantChoiceButton);

		if (controller.getGraphSettings().useActivitySetChoiceButton())
			part.add(asChoiceButton);

		if (controller.getGraphSettings().useLaneChoiceButton() || controller.getGraphSettings().useActivitySetChoiceButton()) {
	//		toolbar.addSeparator();
			toolbar.add(part);
		}
		// creating working component
		graphScrollPane=createWorkingComponent();

		//      JPanel wp=new JPanel();
		//      wp.setLayout(new BorderLayout());
		//      JToolBar toolbox = BarFactory.createToolbar("toolbox", controller);
		JToolBar mainToolbox = new JToolBar();
		mainToolbox.setFloatable(false);
		JToolBar toolbox = createToolbox();
		toolbox.setOrientation(SwingConstants.HORIZONTAL);
		mainToolbox.add(toolbox);
		toolbars.add(toolbar,BorderLayout.NORTH);
		toolbars.add(mainToolbox,BorderLayout.CENTER);
		//      wp.add(toolbox, BorderLayout.WEST);
		//      wp.add(graphScrollPane,BorderLayout.CENTER);
		add(toolbars,BorderLayout.NORTH);
		//      add(wp,BorderLayout.CENTER);
		add(graphScrollPane,BorderLayout.CENTER);
	}

	public JaWEComponent getJaWEComponent () {
		return controller;
	}

	public JComponent getDisplay () {
		return this;
	}

	protected JToolBar createToolbox () {
		String toolbarName="toolbox";
		String actionOrder = controller.getSettings().getToolbarActionOrder(toolbarName);

		JaWETypes jts=JaWEManager.getInstance().getJaWEController().getJaWETypes();

		JToolBar toolbar = new JToolBar();
		toolbar.setRollover(true);
		String[] act = XMLUtil.tokenize(actionOrder, BarFactory.ACTION_DELIMITER);

		for (int j=0; j<act.length; j++) {
			if (act[j].equals(BarFactory.ACTION_SEPARATOR)) {
				toolbar.addSeparator();
			} else if (act[j].equals("SetSelectMode")) {
				JaWEAction ja=new JaWEAction();
				ja.setAction(new SetSelectMode(controller));
				ja.setIcon(((GraphSettings)controller.getSettings()).getSelectionIcon());
				ja.setLangDepName(controller.getSettings().getLanguageDependentString("SelectionKey"));
				JButton b=BarFactory.createToolbarButton(ja, controller);
				b.setToolTipText(ja.getLangDepName());
				toolbar.add(b);
				controller.getSettings().addAction("SetSelectMode", ja);
			} else if (act[j].equals("SetLaneModeCommonExpression")) {
				JaWEAction ja=new JaWEAction();
				try {
					String clsName = "org.enhydra.jawe.components.graph.actions.SetLaneModeCommonExpression";
					ActionBase action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
							GraphController.class
					}).newInstance(new Object[] {
							controller
					});               
					ja.setAction(action);
				} catch (Exception ex) {}
				ja.setIcon(((GraphSettings)controller.getSettings()).getCommonExpresionLaneIcon());
				ja.setLangDepName(controller.getSettings().getLanguageDependentString("CommonExpressionParticipantKey"));
				JButton b=BarFactory.createToolbarButton(ja, controller);
				b.setToolTipText(ja.getLangDepName());
				toolbar.add(b);
				controller.getSettings().addAction("SetLaneModeCommonExpression", ja);
			} else if (act[j].equals("SetLaneModeFreeTextExpression")) {
				JaWEAction ja=new JaWEAction();
				ja.setAction(new SetLaneModeFreeTextExpression(controller));
				ja.setIcon(((GraphSettings)controller.getSettings()).getFreeTextLaneIcon());
				ja.setLangDepName(controller.getSettings().getLanguageDependentString("FreeTextExpressionParticipantKey"));
				JButton b=BarFactory.createToolbarButton(ja, controller);
				b.setToolTipText(ja.getLangDepName());
				toolbar.add(b);
				controller.getSettings().addAction("SetLaneModeFreeTextExpression", ja);
			} else if (act[j].startsWith("SetLaneMode")) {
				String type=act[j].substring("SetLaneMode".length());
				if (type.equals("*")) {
					List parTypes=jts.getTypes(Lane.class,null);
					for (int i=0; i<parTypes.size(); i++) {
						JaWEType jt=(JaWEType)parTypes.get(i);
						JaWEAction ja=new JaWEAction();
						ja.setAction(new SetLaneMode(controller,jt.getTypeId()));
						ja.setIcon(jt.getIcon());
						ja.setLangDepName(jt.getDisplayName());
						JButton b=BarFactory.createToolbarButton(ja, controller);
						b.setToolTipText(ja.getLangDepName());
						toolbar.add(b);
						controller.getSettings().addAction(jt.getTypeId(), ja);
					}
				} else if (!(type.equals("SetLaneModeCommonExpression") || type.equals("SetLaneModeFreeTextExpression"))){
					JaWEType jt=jts.getType(type);
					if (jt==null) continue;
					JaWEAction ja=new JaWEAction();
					ja.setAction(new SetLaneMode(controller,jt.getTypeId()));
					ja.setIcon(jt.getIcon());
					ja.setLangDepName(jt.getDisplayName());
					JButton b=BarFactory.createToolbarButton(ja, controller);
					b.setToolTipText(ja.getLangDepName());
					toolbar.add(b);
					controller.getSettings().addAction(jt.getTypeId(), ja);
				}
         } else if (act[j].startsWith("SetArtifactMode")) {
            String type=act[j].substring("SetArtifactMode".length());
            if (type.equals("*")) {
               List artTypes=jts.getTypes(Artifact.class,null);
               for (int i=0; i<artTypes.size(); i++) {
                  JaWEType jt=(JaWEType)artTypes.get(i);
                  JaWEAction ja=new JaWEAction();
                  ja.setAction(new SetArtifactMode(controller,jt.getTypeId()));
                  ja.setIcon(jt.getIcon());
                  ja.setLangDepName(jt.getDisplayName());
                  JButton b=BarFactory.createToolbarButton(ja, controller);
                  b.setToolTipText(ja.getLangDepName());
                  toolbar.add(b);
                  controller.getSettings().addAction(jt.getTypeId(), ja);
               }
            }
			} else if (act[j].startsWith("SetActivityMode")) {
				String type=act[j].substring("SetActivityMode".length());
				if (type.equals("*")) {
					
					addSetActivityModeComponent(jts, toolbar);
					
				} else {
					JaWEType jt=jts.getType(type);
					if (jt==null) continue;
					JaWEAction ja=new JaWEAction();
					ja.setAction(new SetActivityMode(controller,jt.getTypeId()));
					ja.setIcon(jt.getIcon());
					ja.setLangDepName(jt.getDisplayName());
					JButton b=BarFactory.createToolbarButton(ja, controller);
					b.setToolTipText(ja.getLangDepName());
					toolbar.add(b);
					controller.getSettings().addAction(jt.getTypeId(), ja);
				}
			} else if (act[j].startsWith("SetTransitionMode")) {
				String type=act[j].substring("SetTransitionMode".length());
				if (type.equals("*")) {
					
					addSetTransitionModeComponent(jts, toolbar);
					
				} else {
					JaWEType jt=jts.getType(type);
					if (jt==null) continue;
					JaWEAction ja=new JaWEAction();
					ja.setAction(new SetTransitionMode(controller,jt.getTypeId()));
					ja.setIcon(jt.getIcon());
					ja.setLangDepName(jt.getDisplayName());
					JButton b=BarFactory.createToolbarButton(ja, controller);
					b.setToolTipText(ja.getLangDepName());
					toolbar.add(b);
					controller.getSettings().addAction(jt.getTypeId(), ja);
				}
				
				
         } else if (act[j].startsWith("SetAssociationMode")) {
            String type=act[j].substring("SetAssociationMode".length());
            List ascTypes=jts.getTypes(Association.class,null);
            for (int i=0; i<ascTypes.size(); i++) {
               JaWEType jt=(JaWEType)ascTypes.get(i);
               JaWEAction ja=new JaWEAction();
               ja.setAction(new SetAssociationMode(controller,jt.getTypeId()));
               ja.setIcon(jt.getIcon());
               ja.setLangDepName(jt.getDisplayName());
               JButton b=BarFactory.createToolbarButton(ja, controller);
               b.setToolTipText(ja.getLangDepName());
               toolbar.add(b);
               controller.getSettings().addAction(jt.getTypeId(), ja);
            }                        
         }
		}

		toolbar.setName(controller.getSettings().getLanguageDependentString(toolbarName
				+ BarFactory.LABEL_POSTFIX));

		return toolbar;

	}
	
	
	protected void addSetTransitionModeComponent(JaWETypes jts, JToolBar toolbar){
		List traTypes=jts.getTypes(Transition.class,null);
		for (int i=0; i<traTypes.size(); i++) {
			JaWEType jt=(JaWEType)traTypes.get(i);
			JaWEAction ja=new JaWEAction();
			ja.setAction(new SetTransitionMode(controller,jt.getTypeId()));
			ja.setIcon(jt.getIcon());
			ja.setLangDepName(jt.getDisplayName());
			JButton b=BarFactory.createToolbarButton(ja, controller);
			b.setToolTipText(ja.getLangDepName());
			toolbar.add(b);
			controller.getSettings().addAction(jt.getTypeId(), ja);
		}
	}
	
	protected void addSetActivityModeComponent(JaWETypes jts, JToolBar toolbar){
		List actTypes=jts.getTypes(Activity.class,null);
		JToolBar mainAdditionalToolbar = new JToolBar();
		mainAdditionalToolbar.setFloatable(false);
		JToolBar additionalToolbar = new JToolBar();
		for (int i=0; i<actTypes.size(); i++) {
			JaWEType jt=(JaWEType)actTypes.get(i);
			JaWEAction ja=new JaWEAction();                  
			ja.setAction(new SetActivityMode(controller,jt.getTypeId()));
			ja.setIcon(jt.getIcon());
			ja.setLangDepName(jt.getDisplayName());
			JButton b=BarFactory.createToolbarButton(ja, controller);
			b.setBorderPainted(false);
			b.setToolTipText(ja.getLangDepName());
			if (controller.getGraphSettings().hasAdditionalToolbar() && hasAdditionalActivities(jt)) {//ovo je sad dodato							
				additionalToolbar.add(b);
				additionalToolbar.setOrientation(JToolBar.HORIZONTAL);	
				mainAdditionalToolbar.add(additionalToolbar);
				toolbars.add(mainAdditionalToolbar, BorderLayout.SOUTH);														
			}else {							
				toolbar.add(b);
			}
			controller.getSettings().addAction(jt.getTypeId(), ja);
		}
	}
	
	
	protected boolean hasAdditionalActivities(JaWEType jt) {
		if (jt.getTypeId().equals(JaWEConstants.ACTIVITY_TYPE_NO)
			|| jt.getTypeId().equals(JaWEConstants.ACTIVITY_TYPE_TASK_APPLICATION)
         || jt.getTypeId().equals(JaWEConstants.ACTIVITY_TYPE_TASK_SCRIPT)
			|| jt.getTypeId().equals(JaWEConstants.ACTIVITY_TYPE_BLOCK)
			|| jt.getTypeId().equals(JaWEConstants.ACTIVITY_TYPE_ROUTE_EXCLUSIVE)
         || jt.getTypeId().equals(JaWEConstants.ACTIVITY_TYPE_ROUTE_PARALLEL)
			|| jt.getTypeId().equals(JaWEConstants.ACTIVITY_TYPE_SUBFLOW)) {
				return false;
		}
		return true;
	}

	protected JScrollPane createWorkingComponent () {
		JScrollPane lGraphScrollPane= new JScrollPane();
		JViewport port = lGraphScrollPane.getViewport();
		port.setScrollMode(JViewport.BLIT_SCROLL_MODE);

		// Harald Meister: set bigger scroll-amounts, especially useful for
		// mouse-wheel-scolling in large workflows
		lGraphScrollPane.getVerticalScrollBar().setUnitIncrement(20);
		lGraphScrollPane.getHorizontalScrollBar().setUnitIncrement(40);

		return lGraphScrollPane;
	}

	public void graphSelected (Graph graph) {
		graphScrollPane.setViewportView(graph);
	}

	public void enableDisableButtons() {
		if (controller.getSelectedGraph() != null) {
			if (XMLUtil.getPackage(controller.getSelectedGraph().getXPDLObject()) == JaWEManager.getInstance().getJaWEController().getMainPackage())
				if (controller.getChoices(showParticipantChoiceButton).size() != 0) {
		            Object[] scells=controller.getSelectedGraph().getSelectionCells();
		            if (scells!=null && scells.length>0 && ((WorkflowElement)scells[0]).getPropertyObject() instanceof Lane) {		            
			            Lane parentL = (Lane) ((WorkflowElement)scells[0]).getPropertyObject();
			            GraphSwimlaneInterface gpar = controller.getSelectedGraph()
			               .getGraphManager()
			               .getGraphParticipant(parentL);
			            if (gpar!=null && (gpar.howManyChildActivitiesOrArtifacts() > 0 || ((Lane)gpar.getPropertyObject()).getPerformers().size()==0)) {
			               showParticipantChoiceButton.setEnabled(false);
			               return;
			            }
			         }
			         showParticipantChoiceButton.setEnabled(true);
				} else
					showParticipantChoiceButton.setEnabled(false);
			else
				showParticipantChoiceButton.setEnabled(false);
		} else {
			showParticipantChoiceButton.setEnabled(false);
		}

		if (controller.getChoices(asChoiceButton).size() != 0)
			asChoiceButton.setEnabled(true);
		else
			asChoiceButton.setEnabled(false);
	}
}
