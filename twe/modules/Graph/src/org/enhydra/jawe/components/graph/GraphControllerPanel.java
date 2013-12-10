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
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.XMLElementChoiceButton;
import org.enhydra.jawe.base.controller.JaWEType;
import org.enhydra.jawe.base.controller.JaWETypes;
import org.enhydra.jawe.components.graph.actions.SetActivityMode;
import org.enhydra.jawe.components.graph.actions.SetEndMode;
import org.enhydra.jawe.components.graph.actions.SetParticipantMode;
import org.enhydra.jawe.components.graph.actions.SetParticipantModeFreeTextExpression;
import org.enhydra.jawe.components.graph.actions.SetSelectMode;
import org.enhydra.jawe.components.graph.actions.SetStartMode;
import org.enhydra.jawe.components.graph.actions.SetTransitionMode;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.Participant;
import org.enhydra.shark.xpdl.elements.Transition;

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
		ImageIcon curIc = controller.getGraphSettings().getParticipantsIcon();
		showParticipantChoiceButton = new XMLElementChoiceButton(Participant.class,controller,curIc, true, new String[]
		                                                                                                              {"Id","Name","ParticipantType","Description"});
		showParticipantChoiceButton.setToolTipText(controller.getSettings().getLanguageDependentString("InsertExistingParticipant" + BarFactory.TOOLTIP_POSTFIX));
		curIc = controller.getGraphSettings().getActivitySetSelectIcon();
		asChoiceButton = new XMLElementChoiceButton(ActivitySet.class, controller, curIc, true, new String[]{"Id"});
		asChoiceButton.setToolTipText(controller.getSettings().getLanguageDependentString("SelectActivitySet" + BarFactory.TOOLTIP_POSTFIX));
		JToolBar part = new JToolBar();


		if (controller.getGraphSettings().useParticipantChoiceButton())
			part.add(showParticipantChoiceButton);

		if (controller.getGraphSettings().useActivitySetChoiceButton())
			part.add(asChoiceButton);

		if (controller.getGraphSettings().useParticipantChoiceButton() || controller.getGraphSettings().useActivitySetChoiceButton()) {
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
		String[] act = Utils.tokenize(actionOrder, BarFactory.ACTION_DELIMITER);

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
			} else if (act[j].equals("SetParticipantModeCommonExpression")) {
				JaWEAction ja=new JaWEAction();
				try {
					String clsName = "org.enhydra.jawe.components.graph.actions.SetParticipantModeCommonExpression";
					ActionBase action = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
							GraphController.class
					}).newInstance(new Object[] {
							controller
					});               
					ja.setAction(action);
				} catch (Exception ex) {}
				ja.setIcon(((GraphSettings)controller.getSettings()).getCommonExpresionParticipantIcon());
				ja.setLangDepName(controller.getSettings().getLanguageDependentString("CommonExpressionParticipantKey"));
				JButton b=BarFactory.createToolbarButton(ja, controller);
				b.setToolTipText(ja.getLangDepName());
				toolbar.add(b);
				controller.getSettings().addAction("SetParticipantModeCommonExpression", ja);
			} else if (act[j].equals("SetParticipantModeFreeTextExpression")) {
				JaWEAction ja=new JaWEAction();
				ja.setAction(new SetParticipantModeFreeTextExpression(controller));
				ja.setIcon(((GraphSettings)controller.getSettings()).getFreeTextParticipantIcon());
				ja.setLangDepName(controller.getSettings().getLanguageDependentString("FreeTextExpressionParticipantKey"));
				JButton b=BarFactory.createToolbarButton(ja, controller);
				b.setToolTipText(ja.getLangDepName());
				toolbar.add(b);
				controller.getSettings().addAction("SetParticipantModeFreeTextExpression", ja);
			} else if (act[j].equals("SetStartMode")) {
				JaWEAction ja=new JaWEAction();
				ja.setAction(new SetStartMode(controller));
				ja.setIcon(((GraphSettings)controller.getSettings()).getBubbleStartIcon());
				ja.setLangDepName(controller.getSettings().getLanguageDependentString("StartBubbleKey"));
				JButton b=BarFactory.createToolbarButton(ja, controller);
				b.setToolTipText(ja.getLangDepName());
				toolbar.add(b);
				controller.getSettings().addAction("SetStartMode", ja);
			} else if (act[j].equals("SetEndMode")) {
				JaWEAction ja=new JaWEAction();
				ja.setAction(new SetEndMode(controller));
				ja.setIcon(((GraphSettings)controller.getSettings()).getBubbleEndIcon());
				ja.setLangDepName(controller.getSettings().getLanguageDependentString("EndBubbleKey"));
				JButton b=BarFactory.createToolbarButton(ja, controller);
				b.setToolTipText(ja.getLangDepName());
				toolbar.add(b);
				controller.getSettings().addAction("SetEndMode", ja);
			} else if (act[j].startsWith("SetParticipantMode")) {
				String type=act[j].substring("SetParticipantMode".length());
				if (type.equals("*")) {
					List parTypes=jts.getTypes(Participant.class,null);
					for (int i=0; i<parTypes.size(); i++) {
						JaWEType jt=(JaWEType)parTypes.get(i);
						JaWEAction ja=new JaWEAction();
						ja.setAction(new SetParticipantMode(controller,jt.getTypeId()));
						ja.setIcon(jt.getIcon());
						ja.setLangDepName(jt.getDisplayName());
						JButton b=BarFactory.createToolbarButton(ja, controller);
						b.setToolTipText(ja.getLangDepName());
						toolbar.add(b);
						controller.getSettings().addAction(jt.getTypeId(), ja);
					}
				} else if (!(type.equals("SetParticipantModeCommonExpression") || type.equals("SetParticipantModeFreeTextExpression"))){
					JaWEType jt=jts.getType(type);
					if (jt==null) continue;
					JaWEAction ja=new JaWEAction();
					ja.setAction(new SetParticipantMode(controller,jt.getTypeId()));
					ja.setIcon(jt.getIcon());
					ja.setLangDepName(jt.getDisplayName());
					JButton b=BarFactory.createToolbarButton(ja, controller);
					b.setToolTipText(ja.getLangDepName());
					toolbar.add(b);
					controller.getSettings().addAction(jt.getTypeId(), ja);
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
			|| jt.getTypeId().equals(JaWEConstants.ACTIVITY_TYPE_TOOL)
			|| jt.getTypeId().equals(JaWEConstants.ACTIVITY_TYPE_BLOCK)
			|| jt.getTypeId().equals(JaWEConstants.ACTIVITY_TYPE_ROUTE)
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
				if (controller.getChoices(showParticipantChoiceButton).size() != 0)
					showParticipantChoiceButton.setEnabled(true);
				else
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
