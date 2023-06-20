package TALLER.GRAPH;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.*;

import TALLER.AFD;
import TALLER.AFN;
import TALLER.Estado;

public class AFGraphGUI extends JFrame {
    private AFD afd;
    private AFN afn;
    private AFDGraphPanel graphPanel;

    public AFGraphGUI(AFD afd) {
        this.afd = afd;

        // Configuración básica de la ventana
        setTitle("AFD Graph GUI");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Crear un panel para dibujar el grafo
        AFDGraphPanel graphPanel = new AFDGraphPanel();
        add(graphPanel);

        // Agregar el oyente de clic al panel
        graphPanel.addMouseListener(new NodeClickListener());

        // Mostrar la ventana
        setVisible(true);
    }

    public AFGraphGUI(AFN afn) {
        this.afn = afn;

        // Configuración básica de la ventana
        setTitle("AFN Graph GUI");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Crear un panel para dibujar el grafo
        AFNGraphPanel graphPanel = new AFNGraphPanel();
        add(graphPanel);

        // Agregar el oyente de clic al panel
        graphPanel.addMouseListener(new NodeClickListener());

        // Mostrar la ventana
        setVisible(true);
    }

    private class AFDGraphPanel extends JPanel {
        private static final int NODE_RADIUS = 30;
        private static final int CIRCLE_RADIUS = 200;
        public static final int CENTER_X = 800 / 2;
        private static final int CENTER_Y = 600 / 2;
        private final double ARROW_ANGLE = Math.toRadians(30);
        private static final int ARROW_LENGTH = 15;

        private final Set<Estado> selectedNodes;

        public AFDGraphPanel() {
            selectedNodes = new HashSet<>();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Obtener los estados del AFD
            Estado[] estados = afd.getEstados().toArray(new Estado[0]);

            // Calcular la posición de los nodos de forma circular
            int numEstados = estados.length;
            double angleStep = 2 * Math.PI / numEstados;

            Map<Estado, Point> nodePositions = new HashMap<>();

            for (int i = 0; i < numEstados; i++) {
                double angle = i * angleStep;
                int x = (int) (CENTER_X + CIRCLE_RADIUS * Math.cos(angle));
                int y = (int) (CENTER_Y + CIRCLE_RADIUS * Math.sin(angle));
                nodePositions.put(estados[i], new Point(x, y));
            }

            // Dibujar los estados y las transiciones del AFD
            for (Estado estado : estados) {
                Point nodePosition = nodePositions.get(estado);
                int x = nodePosition.x;
                int y = nodePosition.y;

                // Dibujar el círculo del estado
                //System.out.println(estado+"::"+estado.isAceptacion());
                if (estado.isAceptacion()) {
                    g.setColor(Color.CYAN);
                } else if(estado.isInicial()){
                    g.setColor(Color.green);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
                g.setColor(Color.BLACK);
                g.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

                // Cambiar el color y la opacidad del texto
                if (selectedNodes.contains(estado)) {
                    g.setColor(new Color(0, 0, 0, 255)); // Negro opaco
                } else {
                    g.setColor(new Color(0, 0, 0, 128)); // Negro semitransparente
                }

                // Dibujar el nombre del estado
                g.setColor(Color.RED);
                g.drawString(estado.toString(), x - NODE_RADIUS / 2, y);
            }

            // Conjunto para realizar seguimiento de las posiciones de las flechas
            Set<Point> arrowPositions = new HashSet<>();

            // Dibujar las transiciones del AFD
            for (Estado origen : estados) {
                Point start = nodePositions.get(origen);

                for (char simbolo : afd.getAlfabeto().getSimbolos()) {
                    Estado destino = afd.getFuncionDeTransicion().get(origen).get(simbolo);

                    if (destino == null) {
                        continue;
                    }

                    Point end = nodePositions.get(destino);

                    // Calcular el ángulo y la posición de la flecha
                    double angle = Math.atan2(end.y - start.y, end.x - start.x);
                    int arrowX = (int) (end.x - ARROW_LENGTH * Math.cos(angle));
                    int arrowY = (int) (end.y - ARROW_LENGTH * Math.sin(angle));

                    // Verificar si la posición de la flecha está ocupada
                    if (arrowPositions.contains(new Point(arrowX, arrowY))) {
                        // Calcular una nueva posición para la flecha
                        double newAngle = angle + ARROW_ANGLE;

                        arrowX = (int) (end.x - ARROW_LENGTH * Math.cos(newAngle));
                        arrowY = (int) (end.y - ARROW_LENGTH * Math.sin(newAngle));
                    }

                    // Agregar la posición de la flecha al conjunto
                    arrowPositions.add(new Point(arrowX, arrowY));

                    // Calcular las coordenadas de los puntos de la flecha
                    int[] arrowXPoints = { arrowX, (int) (arrowX - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE)),
                            (int) (arrowX - ARROW_LENGTH * Math.cos(angle - ARROW_ANGLE)) };
                    int[] arrowYPoints = { arrowY, (int) (arrowY - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE)),
                            (int) (arrowY - ARROW_LENGTH * Math.sin(angle - ARROW_ANGLE)) };

                    // Dibujar la flecha
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setStroke(new BasicStroke(2));
                    g2d.setColor(Color.BLACK);
                    g2d.draw(new Line2D.Double(start.x, start.y, end.x, end.y));
                    g2d.fill(new Polygon(arrowXPoints, arrowYPoints, 3));

                    // Cambiar la opacidad del texto
                    if (selectedNodes.contains(origen) && selectedNodes.contains(destino)) {
                        g2d.setColor(new Color(0, 0, 0, 255)); // Negro opaco
                    } else {
                        g2d.setColor(new Color(0, 0, 0, 128)); // Negro semitransparente
                    }
                    Font newfont = g2d.getFont().deriveFont(26F);
                    g.setFont(newfont);
                    // Dibujar el símbolo de la transición
                    g2d.drawString(Character.toString(simbolo), (start.x + end.x) / 2, (start.y + end.y) / 2);
                }
            }
        }
    }

    private class AFNGraphPanel extends JPanel {
        private static final int NODE_RADIUS = 30;
        private static final int CIRCLE_RADIUS = 200;
        public static final int CENTER_X = 800 / 2;
        private static final int CENTER_Y = 600 / 2;
        private final double ARROW_ANGLE = Math.toRadians(30);
        private static final int ARROW_LENGTH = 15;

        private final Set<Estado> selectedNodes;

        public AFNGraphPanel() {
            selectedNodes = new HashSet<>();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Obtener los estados del AFN
            Estado[] estados = afn.getEstados().toArray(new Estado[0]);

            // Calcular la posición de los nodos de forma circular
            int numEstados = estados.length;
            double angleStep = 2 * Math.PI / numEstados;

            Map<Estado, Point> nodePositions = new HashMap<>();

            for (int i = 0; i < numEstados; i++) {
                double angle = i * angleStep;
                int x = (int) (CENTER_X + CIRCLE_RADIUS * Math.cos(angle));
                int y = (int) (CENTER_Y + CIRCLE_RADIUS * Math.sin(angle));
                nodePositions.put(estados[i], new Point(x, y));
            }

            // Dibujar los estados y las transiciones del AFD
            for (Estado estado : estados) {
                Point nodePosition = nodePositions.get(estado);
                int x = nodePosition.x;
                int y = nodePosition.y;

                // Dibujar el círculo del estado
                //System.out.println(estado+"::"+estado.isAceptacion());
                if (estado.isAceptacion()) {
                    g.setColor(Color.CYAN);
                } else if(estado.isInicial()){
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
                g.setColor(Color.BLACK);
                g.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

                // Cambiar el color y la opacidad del texto
                if (selectedNodes.contains(estado)) {
                    g.setColor(new Color(0, 0, 0, 255)); // Negro opaco
                } else {
                    g.setColor(new Color(0, 0, 0, 128)); // Negro semitransparente
                }

                // Dibujar el nombre del estado
                g.setColor(Color.RED);
                g.drawString(estado.toString(), x - NODE_RADIUS / 2, y);
            }

            // Conjunto para realizar seguimiento de las posiciones de las flechas
            Set<Point> arrowPositions = new HashSet<>();

            // Dibujar las transiciones del AFD
            for (Estado origen : estados) {
                Point start = nodePositions.get(origen);

                for (char simbolo : afn.getAlfabeto().getSimbolos()) {
                    if(afn.getFuncionDeTransicion().containsKey(origen)){
                        if(afn.getFuncionDeTransicion().get(origen).containsKey(simbolo)){
                            ArrayList<Estado> destinos = (ArrayList<Estado>) afn.getFuncionDeTransicion().get(origen).get(simbolo);
                            if (destinos == null) {
                                continue;
                            }
                            for(Estado destino : destinos){
                                Point end = nodePositions.get(destino);

                                // Calcular el ángulo y la posición de la flecha
                                double angle = Math.atan2(end.y - start.y, end.x - start.x);
                                int arrowX = (int) (end.x - ARROW_LENGTH * Math.cos(angle));
                                int arrowY = (int) (end.y - ARROW_LENGTH * Math.sin(angle));

                                // Verificar si la posición de la flecha está ocupada
                                if (arrowPositions.contains(new Point(arrowX, arrowY))) {
                                    // Calcular una nueva posición para la flecha
                                    double newAngle = angle + ARROW_ANGLE;

                                    arrowX = (int) (end.x - ARROW_LENGTH * Math.cos(newAngle));
                                    arrowY = (int) (end.y - ARROW_LENGTH * Math.sin(newAngle));
                                }

                                // Agregar la posición de la flecha al conjunto
                                arrowPositions.add(new Point(arrowX, arrowY));

                                // Calcular las coordenadas de los puntos de la flecha
                                int[] arrowXPoints = { arrowX, (int) (arrowX - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE)),
                                        (int) (arrowX - ARROW_LENGTH * Math.cos(angle - ARROW_ANGLE)) };
                                int[] arrowYPoints = { arrowY, (int) (arrowY - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE)),
                                        (int) (arrowY - ARROW_LENGTH * Math.sin(angle - ARROW_ANGLE)) };

                                // Dibujar la flecha
                                Graphics2D g2d = (Graphics2D) g;
                                g2d.setStroke(new BasicStroke(2));
                                g2d.setColor(Color.BLACK);
                                g2d.draw(new Line2D.Double(start.x, start.y, end.x, end.y));
                                g2d.fill(new Polygon(arrowXPoints, arrowYPoints, 3));

                                // Cambiar la opacidad del texto
                                if (selectedNodes.contains(origen) && selectedNodes.contains(destino)) {
                                    g2d.setColor(new Color(0, 0, 0, 255)); // Negro opaco
                                } else {
                                    g2d.setColor(new Color(0, 0, 0, 128)); // Negro semitransparente
                                }
                                Font newfont = g2d.getFont().deriveFont(26F);
                                g.setFont(newfont);
                                // Dibujar el símbolo de la transición
                                g2d.drawString(Character.toString(simbolo), (start.x + end.x) / 2, (start.y + end.y) / 2);
                            }
                        }
                    }
                }
            }
        }
    }

    private class NodeClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point clickPoint = e.getPoint();
            System.out.print(clickPoint+"___");
            // Obtener los estados del AFD
            Estado[] estados = null;
            if(afd!=null){
                estados = afd.getEstados().toArray(new Estado[0]);
            }else if(afn!=null){
                estados = afn.getEstados().toArray(new Estado[0]);
            }
            

            // Calcular la posición de los nodos
            int numEstados = estados.length;
            double angleStep = 2 * Math.PI / numEstados;
            int CENTER_X = AFGraphGUI.this.getWidth() / 2;
            int CENTER_Y = AFGraphGUI.this.getHeight() / 2;
            int CIRCLE_RADIUS = Math.min(CENTER_X, CENTER_Y) - AFDGraphPanel.NODE_RADIUS - 10;

            Map<Estado, Point> nodePositions = new HashMap<>();

            for (int i = 0; i < numEstados; i++) {
                double angle = i * angleStep;
                int x = (int) (CENTER_X + CIRCLE_RADIUS * Math.cos(angle));
                int y = (int) (CENTER_Y + CIRCLE_RADIUS * Math.sin(angle));
                nodePositions.put(estados[i], new Point(x, y));
            }

            // Verificar si se hizo clic en un nodo
            for (Estado estado : estados) {
                Point nodePosition = nodePositions.get(estado);
                int x = nodePosition.x;
                int y = nodePosition.y;
                System.out.print(AFDGraphPanel.NODE_RADIUS+"___");
                System.out.println(clickPoint.distance(x, y));
                if (clickPoint.distance(x, y) <= AFDGraphPanel.NODE_RADIUS) {
                    // Se hizo clic en este nodo
                    if (AFGraphGUI.this.graphPanel.selectedNodes.contains(estado)) {
                        // Si el nodo ya estaba seleccionado, se deselecciona
                        AFGraphGUI.this.graphPanel.selectedNodes.remove(estado);
                    } else {
                        // Si el nodo no estaba seleccionado, se selecciona y se deseleccionan los demás
                        AFGraphGUI.this.graphPanel.selectedNodes.clear();
                        AFGraphGUI.this.graphPanel.selectedNodes.add(estado);
                    }

                    // Redibujar el panel
                    AFGraphGUI.this.graphPanel.repaint();
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        // Crea tu automata
        AFD afd = new AFD();

        // Agrega estados y transiciones a tu automata

        // Crea la GUI para mostrar el automata
        SwingUtilities.invokeLater(() -> {
            new AFGraphGUI(afd);
        });
    }
}
