package org.enhydra.jawe.components.graph.actions.jped;

import java.awt.event.ActionEvent;
import java.io.FileOutputStream;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.jped.PDFWorkflowExport;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

public class SaveAsPDF extends ActionBase {

    public SaveAsPDF(JaWEComponent jawecomponent) {
        super(jawecomponent);
    }

    public void enableDisableAction() {
        GraphController gc = (GraphController) jawecomponent;

        if (gc.getSelectedGraph() != null)
            setEnabled(true);
        else
            setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            GraphController gc = (GraphController) jawecomponent;
            if (gc.getSelectedGraph() != null)
                if (gc.getSelectedGraph().getXPDLObject() instanceof WorkflowProcess) {
                    String file = JaWEManager.getInstance().getJaWEController().saveDialog(
                            ResourceManager.getLanguageDependentString("SaveAsPDFLabel"),
                            3,
                            JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(
                                    ((GraphController) jawecomponent).getSelectedGraph().getXPDLObject()));
                    if (file != null && file.length() > 0) {
                        saveGraphAsPDF(file, gc.getSelectedGraph(), (WorkflowProcess) gc.getSelectedGraph()
                                .getXPDLObject());
                    }
                }
        } catch (Exception ex) {
            ex.printStackTrace();
            String msg = ResourceManager.getLanguageDependentString("ErrorPDFSavingFailed");
            JaWEManager.getInstance().getJaWEController().message(msg,
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public void saveGraphAsPDF(final String file, final Graph graph, final WorkflowProcess process)
            throws Exception {
        PDFWorkflowExport export = new PDFWorkflowExport();
        try {
            export.setGraph(graph);
            export.setJaWEManager(JaWEManager.getInstance());
            export.setProcess(process);
            export.exportToFile(new FileOutputStream(file));
        } catch (Exception ex) {
            ex.printStackTrace();
            String msg = ResourceManager.getLanguageDependentString("ErrorPDFSavingFailed");
            JaWEManager.getInstance().getJaWEController().message(msg,
                    JOptionPane.WARNING_MESSAGE);
        }
    }

}
