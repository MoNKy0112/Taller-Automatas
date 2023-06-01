package TALLER.GUITABLA;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import TALLER.AFN;
import TALLER.AFN_Lambda;
import TALLER.Alfabeto;
import TALLER.Estado;

public class MatrixGUIAFN extends JFrame {
    private JPanel matrixPanel;
    private JPanel leftPanel;
    private int rows;
    private int cols;
    private Estado[] states;
    private CellAFN[][] cells;
    private AFN afn;
    private AFN_Lambda afnl;
    private char[] alphabet;
    private JButton saveButton;
    private HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion;

    public MatrixGUIAFN(AFN afn) {
        super("Matrix");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);

        this.afn = afn;
        Estado[] states = new Estado[afn.getEstados().size()];
        states = afn.getEstados().toArray(states);
        this.states = states;
        this.alphabet = afn.getAlfabeto().getSimbolos();
        this.funcionDeTransicion = afn.getFuncionDeTransicion();
        this.rows = this.states.length;
        this.cols = this.alphabet.length;
        cells = new CellAFN[rows][cols];

        matrixPanel = new JPanel();
        matrixPanel.setLayout(new GridLayout(rows, cols));

        // Agregar etiquetas con caracteres del alfabeto a la parte superior de la matriz
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, cols));
        for (int i = 0; i < cols; i++) {
            JLabel label = new JLabel(Character.toString(alphabet[i]), JLabel.CENTER);
            topPanel.add(label);
        }
        add(topPanel, BorderLayout.NORTH);

        // Agregar lista de estados a la izquierda de la matriz
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(rows, 1));
        for (int i = 0; i < rows; i++) {
            JLabel label = new JLabel(states[i].toString(), JLabel.CENTER);
            leftPanel.add(label);
        }
        add(leftPanel, BorderLayout.WEST);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new CellAFN(states);

                List<Estado> transiciones = funcionDeTransicion.getOrDefault(states[i],new HashMap<>()).getOrDefault(alphabet[j], new ArrayList<>());
                Estado[] estadosTransiciones = new Estado[transiciones.size()];
                estadosTransiciones = transiciones.toArray(estadosTransiciones);
                //cell.setTransitions(estadosTransiciones);
                List<Estado> est = null;
                try {
                    est = funcionDeTransicion.get(states[i]).get(alphabet[j]);
                } catch (Exception e) {
                    //handle exception
                }

                List<Integer> index = new ArrayList<>();                    
                
                if (est!=null){
                    for(Estado estad : est){
                        index.add(Arrays.asList(states).indexOf(estad));
                    }
                }
                cells[i][j].setSelect(index);

                //cells[i][j] = cell;
                matrixPanel.add(cells[i][j].getStateList());
            }
        }
        add(matrixPanel, BorderLayout.CENTER);

        saveButton = new JButton("Guardar");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarMatriz();
            }
        });
        add(saveButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    public MatrixGUIAFN(AFN_Lambda afnl) {
        super("Matrix");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);

        this.afnl = afnl;
        Estado[] states = new Estado[afnl.getEstados().size()];
        states = afnl.getEstados().toArray(states);
        this.states = states;
        this.alphabet = afnl.getAlfabeto().getSimbolos();
        this.funcionDeTransicion = afnl.getFuncionDeTransicion();
        this.rows = this.states.length;
        this.cols = this.alphabet.length;
        cells = new CellAFN[rows][cols];

        matrixPanel = new JPanel();
        matrixPanel.setLayout(new GridLayout(rows, cols));

        // Agregar etiquetas con caracteres del alfabeto a la parte superior de la matriz
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, cols));
        for (int i = 0; i < cols; i++) {
            JLabel label = new JLabel(Character.toString(alphabet[i]), JLabel.CENTER);
            topPanel.add(label);
        }
        add(topPanel, BorderLayout.NORTH);

        // Agregar lista de estados a la izquierda de la matriz
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(rows, 1));
        for (int i = 0; i < rows; i++) {
            JLabel label = new JLabel(states[i].toString(), JLabel.CENTER);
            leftPanel.add(label);
        }
        add(leftPanel, BorderLayout.WEST);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new CellAFN(states);
                List<Estado> transiciones = funcionDeTransicion.getOrDefault(states[i],new HashMap<>()).getOrDefault(alphabet[j], new ArrayList<>());
                Estado[] estadosTransiciones = new Estado[transiciones.size()];
                estadosTransiciones = transiciones.toArray(estadosTransiciones);
                //cell.setTransitions(estadosTransiciones);
                List<Estado> est = null;
                try {
                    est = funcionDeTransicion.get(states[i]).get(alphabet[j]);
                } catch (Exception e) {
                    //handle exception
                }

                List<Integer> index = new ArrayList<>();                    
                
                if (est!=null){
                    for(Estado estad : est){
                        index.add(Arrays.asList(states).indexOf(estad));
                    }
                }
                cells[i][j].setSelect(index);

                //cells[i][j] = cell;
                matrixPanel.add(cells[i][j].getStateList());
            }
        }
        add(matrixPanel, BorderLayout.CENTER);

        saveButton = new JButton("Guardar");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarMatriz();
            }
        });
        add(saveButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void guardarMatriz() {
        for (int i = 0; i < rows; i++) {
            HashMap<Character, List<Estado>> transiciones = funcionDeTransicion.getOrDefault(states[i], new HashMap<>());
            for (int j = 0; j < cols; j++) {
                
                List<Estado> estados = cells[i][j].getSelectedStates();
                if (estados == null) {
                    // Si no se ha seleccionado un estado, se guarda null en la matriz
                    //matrizCeldas[i][j] = null;
                } else {
                    // Si se ha seleccionado un estado, se guarda el estado en la matriz
                    //matrizCeldas[i][j] = estadoSeleccionado;
                    transiciones.put(alphabet[j], estados);
                }
            }
            funcionDeTransicion.put(states[i], transiciones);
        }
        if(afn!=null){
            afn.setFuncionDeTransicion(funcionDeTransicion);
        }
        if(afnl!=null){
            afnl.setFuncionDeTransicion(funcionDeTransicion);
        }
        
        this.dispose();
    }

    public static void main(String[] args) {
        Estado q0 = new Estado();
        Estado q1 = new Estado();
        Estado q2 = new Estado();
        ArrayList<Estado> estados = new ArrayList<>();
        estados.add(q0);
        estados.add(q1);
        estados.add(q2);

        char[] alfabeto = {'a', 'b'};
        Alfabeto alf = new Alfabeto(alfabeto);
        HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion = new HashMap<>();
        funcionDeTransicion.put(q0, new HashMap<Character, List<Estado>>());
        funcionDeTransicion.get(q0).put('a', Arrays.asList(q1));
        funcionDeTransicion.get(q0).put('b', Arrays.asList(q0));
        funcionDeTransicion.put(q1, new HashMap<Character, List<Estado>>());
        funcionDeTransicion.get(q1).put('a', Arrays.asList(q1));
        funcionDeTransicion.get(q1).put('b', Arrays.asList(q2));
        funcionDeTransicion.put(q2, new HashMap<Character, List<Estado>>());
        funcionDeTransicion.get(q2).put('a', Arrays.asList(q1));
        funcionDeTransicion.get(q2).put('b', Arrays.asList(q0));

        AFN afn = new AFN(alf,estados,funcionDeTransicion);

        afn.setFuncionDeTransicion(funcionDeTransicion);

        MatrixGUIAFN gui = new MatrixGUIAFN(afn);
        while (gui.isVisible()){
            try {
                Thread.sleep(100); // Esperar 100 milisegundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(afn.getFuncionDeTransicion());
    }
}
