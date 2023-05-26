package TALLER.GUITABLA;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


import TALLER.AFD;
import TALLER.Estado;

public class MatrixGUI extends JFrame {
    private JPanel matrixPanel;
    private JPanel leftPanel;
    private int rows;
    private int cols;
    private Estado[] states;
    private Cell[][] cells;
    private AFD afd;
    private char[] alphabet;
    private JButton saveButton;
    private HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion;

    public MatrixGUI(AFD afd) {
        super("Matrix");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        
        this.afd = afd;
        Estado[] states = new Estado[afd.getEstados().size()];
        states = afd.getEstados().toArray(states);
        this.states = states;
        this.alphabet = afd.getAlfabeto().getSimbolos();
        this.funcionDeTransicion = afd.getFuncionDeTransicion();
        this.rows = this.states.length;
        this.cols = this.alphabet.length;
        cells = new Cell[rows][cols];
        
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
                cells[i][j] = new Cell(states);
                int index = states.length;
                Estado est = null;
                try {
                    est = funcionDeTransicion.get(states[i]).get(alphabet[j]);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                
                if (est!=null){
                    index = Arrays.asList(states).indexOf(est);
                }
                System.out.println();
                cells[i][j].setSelect(index);
                //cells[i][j].getStateList().setPreferredSize(new Dimension(10, 10)); // Establecer tamaño de la celda
                matrixPanel.add(cells[i][j].getStateList());
            }
        }
        
        add(matrixPanel, BorderLayout.CENTER);
        setVisible(true);

        saveButton = new JButton("Guardar");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarCeldas();
            }
        });
        add(saveButton, BorderLayout.SOUTH);
    }
    
    public Estado getCellState(int row, int col) {
        return cells[row][col].getSelectedState();
    }

    private void guardarCeldas() {
        //Estado[][] matrizCeldas = new Estado[rows][cols];
        
        
        for (int i = 0; i < rows; i++) {
            HashMap<Character, Estado> transiciones = funcionDeTransicion.getOrDefault(states[i], new HashMap<>());
            for (int j = 0; j < cols; j++) {
                Cell celda = cells[i][j];
                Estado estadoSeleccionado = celda.getSelectedState();
                //System.out.println(estadoSeleccionado);
                if (estadoSeleccionado == null) {
                    // Si no se ha seleccionado un estado, se guarda null en la matriz
                    //matrizCeldas[i][j] = null;
                } else {
                    // Si se ha seleccionado un estado, se guarda el estado en la matriz
                    //matrizCeldas[i][j] = estadoSeleccionado;
                    transiciones.put(alphabet[j], estadoSeleccionado);
                    
                }
            }
            funcionDeTransicion.put(states[i], transiciones);
        }
        afd.setFuncionDeTransicion(funcionDeTransicion);
        
        // Aquí se puede hacer algo con la matriz de celdas guardada, como enviarla a otra clase o guardarla en un archivo.
        // Por ejemplo, se puede imprimir en la consola la matriz de celdas guardada:
        //System.out.println(Arrays.deepToString(matrizCeldas));
        //return matrizCeldas;
        this.dispose();
        //System.out.println(afd.getFuncionDeTransicion());
        //System.out.println(funcionDeTransicion);
    }

    /*public static void main(String[] args) {
        AFD afd = AFDTest.testAFD();
        char[] alphabet = afd.getAlfabeto().getSimbolos();
        Estado[] states = new Estado[afd.getEstados().size()];
        states = afd.getEstados().toArray(states);
        System.out.println(afd.getFuncionDeTransicion());
        MatrixGUI gui = new MatrixGUI(afd);
        while (gui.isVisible()){
            System.out.println("1");
        }
        System.out.println("aaa"+afd.getFuncionDeTransicion());
    }*/
}