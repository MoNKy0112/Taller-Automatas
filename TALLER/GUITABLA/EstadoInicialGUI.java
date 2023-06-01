package TALLER.GUITABLA;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import TALLER.AFD;
import TALLER.AFN;
import TALLER.AFN_Lambda;
import TALLER.Alfabeto;
import TALLER.Estado;

public class EstadoInicialGUI extends JFrame implements ActionListener {
    private JComboBox<Estado> estadoInicialBox;
    private JButton seleccionarButton;
    private List<Estado> estados;
    private AFD afd=null;
    private AFN afn=null;
    private AFN_Lambda afnl=null;


    public EstadoInicialGUI(AFD afd) {
        
        super("Seleccionar estado inicial");
        this.afd=afd;
        List<Estado> estados = afd.getEstados();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);

        this.estados = estados;

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Seleccione el estado inicial:");
        panel.add(label, BorderLayout.NORTH);

        estadoInicialBox = new JComboBox<Estado>(estados.toArray(new Estado[estados.size()]));
        estadoInicialBox.setPreferredSize(new Dimension(150, 30));
        panel.add(estadoInicialBox, BorderLayout.CENTER);

        seleccionarButton = new JButton("Seleccionar");
        seleccionarButton.addActionListener(this);
        panel.add(seleccionarButton, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
    
    public EstadoInicialGUI(AFN afn) {
        
        super("Seleccionar estado inicial");
        this.afn=afn;
        List<Estado> estados = afn.getEstados();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);

        this.estados = estados;

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Seleccione el estado inicial:");
        panel.add(label, BorderLayout.NORTH);

        estadoInicialBox = new JComboBox<Estado>(estados.toArray(new Estado[estados.size()]));
        estadoInicialBox.setPreferredSize(new Dimension(150, 30));
        panel.add(estadoInicialBox, BorderLayout.CENTER);

        seleccionarButton = new JButton("Seleccionar");
        seleccionarButton.addActionListener(this);
        panel.add(seleccionarButton, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    public EstadoInicialGUI(AFN_Lambda afnl) {
        
        super("Seleccionar estado inicial");
        this.afnl=afnl;
        List<Estado> estados = afnl.getEstados();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);

        this.estados = estados;

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Seleccione el estado inicial:");
        panel.add(label, BorderLayout.NORTH);

        estadoInicialBox = new JComboBox<Estado>(estados.toArray(new Estado[estados.size()]));
        estadoInicialBox.setPreferredSize(new Dimension(150, 30));
        panel.add(estadoInicialBox, BorderLayout.CENTER);

        seleccionarButton = new JButton("Seleccionar");
        seleccionarButton.addActionListener(this);
        panel.add(seleccionarButton, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == seleccionarButton) {
            Estado estadoInicial = (Estado) estadoInicialBox.getSelectedItem();

            if(afd!=null){
                afd.setEstadoInicial(estadoInicial);
            }
            if(afn!=null){
                afn.setEstadoInicial(estadoInicial);
            }
            if(afnl!=null){
                afnl.setEstadoInicial(estadoInicial);
            }
            dispose(); // cierra la ventana
            // Aquí podrías retornar el estado seleccionado a otra parte del programa
            

            //System.out.println("El estado seleccionado es: " + estadoInicial.toString());
        }
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

        AFN_Lambda afn = new AFN_Lambda(alf,estados,funcionDeTransicion);

        afn.setFuncionDeTransicion(funcionDeTransicion);

        EstadoInicialGUI gui = new EstadoInicialGUI(afn);
        while (gui.isVisible()){
            try {
                Thread.sleep(100); // Esperar 100 milisegundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion2 = new HashMap<>();

        AFD afd = new AFD(alf,estados,funcionDeTransicion2);

        EstadoInicialGUI gui2 = new EstadoInicialGUI(afd);
        while (gui2.isVisible()){
            try {
                Thread.sleep(100); // Esperar 100 milisegundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(afd.getEstadoInicial());
    }
}
