package TALLER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class AFPN {
        // Atributos

        private Alfabeto alfabeto = new Alfabeto(null);
        private Alfabeto alfabetoPila = new Alfabeto(null);
        private ArrayList<Estado> estados = new ArrayList<Estado>();
        private Estado estadoInicial = estados.stream().filter(p -> p.isInicial()).findFirst().orElse(
                        estados.stream().findFirst().orElse(null));
        private ArrayList<Estado> estadosDeAceptacion = new ArrayList<>(
                        estados.stream().filter(p -> p.isAceptacion()).collect(Collectors.toList()));
        private HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion;
        private HashMap<Estado, HashMap<Character, ArrayList<Character[]>>> funcionDeTransicionPila;
        // El index del array 0 es para el hashmap char estado de las transiciones de
        // estado
        // El index del array 0 es para el hashmap char char para los movimientos de la
        // pila
        private Stack pila = new Stack<Character>();

        // Metodos
        public AFPN(Alfabeto alfabetoCinta, Alfabeto alfabetoPila, ArrayList<Estado> estados, Estado estadoInicial,
                        HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion,
                        HashMap<Estado, HashMap<Character, ArrayList<Character[]>>> funcionDeTransicionPila) {
                this.alfabeto = alfabetoCinta;
                this.alfabetoPila = alfabetoPila;
                this.estados = estados;
                this.funcionDeTransicion = funcionDeTransicion;
                this.funcionDeTransicionPila = funcionDeTransicionPila;
                this.estadoInicial = estadoInicial;
        }

}
