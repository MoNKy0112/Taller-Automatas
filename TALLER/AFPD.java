package TALLER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class AFPD {
    
    private Alfabeto alfabeto = new Alfabeto(null);
    private Alfabeto alfabetoPila = new Alfabeto(null);
    private ArrayList<Estado> estados = new ArrayList<Estado>();
    private Estado estadoInicial = estados.stream().filter(p -> p.isInicial()).findFirst().orElse(
        estados.stream().findFirst().orElse(null)
    );
    private ArrayList<Estado> estadosDeAceptacion = new ArrayList<>(
        estados.stream().filter(p -> p.isAceptacion()).collect(Collectors.toList())
    );
    private HashMap<Estado, HashMap<Character,Estado>> funcionDeTransicion;
    private HashMap<Estado, HashMap<String,String>> funcionDeTransicionPila;
    //El index del array 0 es para el hashmap char estado de las transiciones de estado
    //El index del array 0 es para el hashmap char char para los movimientos de la pila
    private ArrayList<Estado> estadosLimbo = new ArrayList<>(
        estados.stream().filter(p -> p.isLimbo()).collect(Collectors.toList())
    );
    private ArrayList<Estado> estadosInaccesibles = new ArrayList<>(
        estados.stream().filter(p -> !p.isAccesible()).collect(Collectors.toList())
    );
    private Stack pila = new Stack<Character>();
    //<>
    public AFPD(Alfabeto alfabetoCinta,Alfabeto alfabetoPila, ArrayList<Estado> estados,
     HashMap<Estado, HashMap<Character,Estado>> funcionDeTransicion,
     HashMap<Estado, HashMap<String,String>> funcionDeTransicionPila){
        this.alfabeto=alfabetoCinta;
        this.alfabetoPila=alfabetoPila;
        this.estados=estados;
        this.funcionDeTransicion=funcionDeTransicion;
        this.funcionDeTransicionPila=funcionDeTransicionPila;
    }

    public boolean modificarPila(Character parametro,Character operacion){
        if (!this.alfabetoPila.contieneSimbolo(parametro) && parametro!='$'){
            throw new IllegalArgumentException("El simbolo "+parametro+" no pertenece al alfabeto de la pila del automata");
        }
        if (!this.alfabetoPila.contieneSimbolo(operacion) && operacion!='$'){
            throw new IllegalArgumentException("El simbolo "+operacion+" no pertenece al alfabeto de la pila del automata");
        }
        if(parametro.equals('$')){
            if(operacion.equals(parametro)){
            }else{
                pila.push(operacion);
            }
        }else if(pila.isEmpty()){
            return false;
        }
        else if(pila.peek().equals(parametro)){
            if(operacion.equals('$')){
                pila.pop();
            }else if(operacion.equals(parametro)){
            }else{
                pila.pop();
                pila.push(operacion);
            }
        }else{
            return false;
        }
        return true;
    }
    //TODO probar en main, para esto falta crear las transiciones
    public boolean procesarCadena(String cadena,Character parametro, Character operacion) {
        Estado estadoActual = estadoInicial;

        for(int i=0;i<cadena.length();i++){
            if(!modificarPila(parametro, operacion))return false;
            estadoActual = transicion(estadoActual, cadena.charAt(i));
        }
        if(estadosDeAceptacion.contains(estadoActual) && pila.empty()){
            return true;
        }else{
            return false;
        }
    }

    public Estado transicion(Estado estadoOrigen, char simbolo){
        if (!this.alfabeto.contieneSimbolo(simbolo)){
            throw new IllegalArgumentException("El simbolo "+simbolo+" no pertenece al alfabeto del automata");
        }
        if (!contieneEstado(estadoOrigen)){
            throw new IllegalArgumentException("El estado " + estadoOrigen + " no pertenece al conjunto de estados del autómata.");
        }
        if (!funcionDeTransicion.get(estadoOrigen).containsKey(simbolo)){
            return null;
        }
        return funcionDeTransicion.get(estadoOrigen).get(simbolo);
    }

    private boolean contieneEstado(Estado estado){
        return this.estados.contains(estado);
    }



    public static void main(String[] args){
        char[] simbolos = {'0','1'};
        char[] simbolos2 = {'X','Y'};
        Alfabeto alf = new Alfabeto(simbolos);
        Alfabeto alfp = new Alfabeto(simbolos2);
        int numEstados = 3;
        ArrayList<Estado> estados = new ArrayList<Estado>();
        for (int i = 0; i < numEstados; i++) {
            estados.add(new Estado());
        }  
        AFPD afpd = new AFPD(alf, alfp, estados, null, null);
    }
}
