package TALLER.GRAPH;

import java.util.HashMap;

import TALLER.AFD;
import TALLER.Estado;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Graph extends Application{
    private AFD afd;

    public Graph(){
        this.afd=null;
    }

    public Graph(AFD afd){
        this.afd=afd;
    }

    public void setAFD(AFD afd){
       this.afd = afd;
    }

    public Group drawGraph() {
        Group group = new Group();
        HashMap<Estado, Circle> nodes = new HashMap<>();
        if (afd!=null){
                
            int nodeRadius = 25;
            int x = 150;
            int y = 150;

            for (Estado estado : afd.getEstados()) {
                Circle circle = new Circle(x, y, nodeRadius);
                Text text = new Text(estado.toString());

                if (estado.isInicial()) {
                    text.setText("→" + text.getText());
                }

                if (estado.isAceptacion()) {
                    text.setText("*" + text.getText());
                }

                circle.setFill(Color.WHITE);
                circle.setStroke(Color.BLACK);

                group.getChildren().add(circle);
                group.getChildren().add(text);
                nodes.put(estado, circle);

                x += 150;
                if (x > 450) {
                    x = 150;
                    y += 150;
                }
            }
            for (Estado origen : afd.getEstados()) {
                for (char simbolo : afd.getAlfabeto().getSimbolos()) {
                    Estado destino = afd.getFuncionDeTransicion().get(origen).get(simbolo);
                    Line line = new Line(
                            nodes.get(origen).getCenterX(),
                            nodes.get(origen).getCenterY(),
                            nodes.get(destino).getCenterX(),
                            nodes.get(destino).getCenterY()
                    );
                    Text text = new Text(Character.toString(simbolo));
    
                    double dx = nodes.get(destino).getCenterX() - nodes.get(origen).getCenterX();
                    double dy = nodes.get(destino).getCenterY() - nodes.get(origen).getCenterY();
                    double scale = Math.sqrt(dx * dx + dy * dy);
                    double factor = 1.0 / scale;
    
                    text.setTranslateX(
                            nodes.get(origen).getCenterX() + dx * factor * nodeRadius
                    );
                    text.setTranslateY(
                            nodes.get(origen).getCenterY() + dy * factor * nodeRadius
                    );
                    text.setStyle("-fx-font: 60 arial;");
    
                    group.getChildren().add(line);
                    group.getChildren().add(text);
                }
            }

        }

        return group;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AFD afd = this.afd; // crea tu automata
        
        Graph graph = new Graph(afd);
        graph.setAFD(afd);
        System.out.println(this.afd!=null);
        Scene scene = new Scene(graph.drawGraph(), 600, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(Graph.class, args);
    }
}
