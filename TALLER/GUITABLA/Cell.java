package TALLER.GUITABLA;
import java.util.Arrays;

import javax.swing.JComboBox;

import TALLER.Estado;

public class Cell {
    private JComboBox<Estado> stateList;

    public Cell(Estado[] states) {
        stateList = new JComboBox<Estado>(states);
        stateList.addItem(null);
        stateList.setSelectedIndex(states.length);
    }
    
    public Estado getSelectedState() {
        Estado selectedState = (Estado) stateList.getSelectedItem();
        return selectedState==null ? null : selectedState;
    }
    
    public JComboBox<Estado> getStateList() {
        return stateList;
    }

    public void setSelect(int i){
        stateList.setSelectedIndex(i);
    }
}