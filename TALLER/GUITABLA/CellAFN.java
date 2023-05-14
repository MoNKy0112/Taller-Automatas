package TALLER.GUITABLA;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import TALLER.Estado;

public class CellAFN {
    private JList<Estado> stateList;
    private ArrayList<Estado> selectedStates;
    
    public CellAFN(Estado[] states) {
        DefaultListModel<Estado> model = new DefaultListModel<>();
        for (Estado state : states) {
            model.addElement(state);
        }
        model.addElement(null);
        stateList = new JList<Estado>(model);
        stateList.setVisibleRowCount(3);
        stateList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        selectedStates = new ArrayList<>();
    }

    public JList<Estado> getStateList() {
        return stateList;
    }

    public ArrayList<Estado> getSelectedStates() {
        selectedStates.clear();
        int[] selectedIndices = stateList.getSelectedIndices();
        for (int i : selectedIndices) {
            selectedStates.add( (Estado) stateList.getModel().getElementAt(i));
        }
        return selectedStates;
    }

    public void setSelect(List<Integer> i){
        for (int j : i) {
            stateList.setSelectedIndex(j);
        }
    }
}
