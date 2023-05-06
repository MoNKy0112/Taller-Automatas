package TALLER;
import java.util.ArrayList;
import java.util.stream.Collectors;

import TALLER.GUITABLA.MatrixGUI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;
import java.util.Queue;



public class AFD {
    //Atributos
    private Alfabeto alfabeto = new Alfabeto(null);
    private ArrayList<Estado> estados = new ArrayList<Estado>();
    private Estado estadoInicial = estados.stream().filter(p -> p.isInicial()).findFirst().orElse(
        estados.stream().findFirst().orElse(null)
    );
    private ArrayList<Estado> estadosDeAceptacion = new ArrayList<>(
        estados.stream().filter(p -> p.isAceptacion()).collect(Collectors.toList())
    );
    private HashMap<Estado, HashMap<Character, Estado>> funcionDeTrancision;
    private ArrayList<Estado> estadosLimbo = new ArrayList<>(
        estados.stream().filter(p -> p.isLimbo()).collect(Collectors.toList())
    );
    private ArrayList<Estado> estadosInaccesibles = new ArrayList<>(
        estados.stream().filter(p -> !p.isAccesible()).collect(Collectors.toList())
    );

    ////Metodos
    //Constructor
    public AFD(Alfabeto alfabeto, ArrayList<Estado> estados, HashMap<Estado, HashMap<Character, Estado>> funcionDeTrancision) {
        this.alfabeto = alfabeto;
        this.estados = estados;
        this.funcionDeTrancision = funcionDeTrancision;
    }

    public void verificarCorregirCompletitudAFD(){
        Estado estadoLimboNuevo = null;
        for (Estado estado : estados){
            for (char simbolo: alfabeto.getSimbolos()){
                if (!funcionDeTrancision.get(estado).containsKey(simbolo)){
                    if(estadoLimboNuevo == null){
                        estadoLimboNuevo = new Estado();
                        //agregarEstado(estadoLimboNuevo);
                    }
                    addTransicion(estado, simbolo, estadoLimboNuevo);
                }
            }
        }
    }

    public void addTransicion(Estado estadoOrigen,char simbolo, Estado estadoDestino){
        if (!this.alfabeto.contieneSimbolo(simbolo)){
            throw new IllegalArgumentException("El simbolo "+simbolo+" no pertenece al alfabeto del automata");
        }
        if (!contieneEstado(estadoOrigen) || !contieneEstado(estadoDestino)){
            throw new IllegalArgumentException("El estado " + estadoOrigen + " o el estado " + estadoDestino + " no pertenece al conjunto de estados del autómata.");
        }
        HashMap<Character, Estado> transiciones = funcionDeTrancision.getOrDefault(estadoOrigen, new HashMap<>());
        transiciones.put(simbolo, estadoDestino);
        funcionDeTrancision.put(estadoOrigen, transiciones);
    }

    public void fillTransitions(){
        MatrixGUI gui = new MatrixGUI(this);
        while (gui.isVisible() ){
            try {
                Thread.sleep(100); // Esperar 100 milisegundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean contieneEstado(Estado estado){
        return this.estados.contains(estado);
    }

    public ArrayList<Estado> hallarEstadosLimbo(){
        Set<Estado> visitados = new HashSet<>();
        Set<Estado> noMuertos = new HashSet<>();
        
        noMuertos.addAll(estadosDeAceptacion);
        visitados.addAll(estadosDeAceptacion);

        for(Estado estado : estados){
            if(!visitados.contains(estado)){
                dfsLimbo(estado, visitados, noMuertos);
                
            }
        }

        ArrayList<Estado> estadosLimbo =new ArrayList<>();

        for(Estado estado: estados){
            estado.setLimbo(!noMuertos.contains(estado));
            if(estado.isLimbo()){
                estadosLimbo.add(estado);
                //System.out.println("-------------"+visitados+","+noMuertos);
            }
        }
        
        //determina estados limbo y modifica el atributo
        setEstadosLimbo(estadosLimbo);
        return estadosLimbo;
    }

    private boolean dfsLimbo(Estado estadoActual, Set<Estado> visitados, Set<Estado> noMuertos){
        visitados.add(estadoActual);
        System.out.println("visitados:"+visitados);
        for (char simbolo: alfabeto.getSimbolos()) {
            Estado estadoSig = transicion(estadoActual, simbolo);
            if(estadoSig != null){
                //Verifica si esta en noMuertos, de ser asi el estadoActual tambien seria un noMuerto
                if (noMuertos.contains(estadoSig)){
                    noMuertos.add(estadoActual);
                    return true;
                }
                //Verifica si esta en visitados pero no en noMuertos, por lo cual seria un estado muerto
                if (visitados.contains(estadoSig))return false; 
                
                //si no esta en ninguno de los dos, hay que recorrer mas y verificar si se llega a
                //algun estado de aceptacion
                if(dfsLimbo(estadoSig, visitados, noMuertos)){
                    noMuertos.add(estadoActual);
                    return true;
                }
                //De no ser asi por este camino solo hay estados limbo, por lo cual este es un estado limbo
                
            }
            
        }
        return false;
        
    }
    
    public ArrayList<Estado> hallarEstadosInaccesibles(){
        boolean[] accesibles = new boolean[estados.size()];
        Queue<Estado> queue = new LinkedList<>();
        queue.offer(estadoInicial);
        accesibles[estadoInicial.getId()-1] = true;

        while (!queue.isEmpty()) {
            Estado estadoActual = queue.poll();
            for (char simbolo: alfabeto.getSimbolos()) {
                Estado estadoSig = transicion(estadoActual, simbolo);
                if (estadoSig != null && !accesibles[estadoSig.getId()-1]) {
                    accesibles[estadoSig.getId()-1] = true;
                    queue.offer(estadoSig);
                }
            }
        }

        ArrayList<Estado> inaccesibles = new ArrayList<>();
        for (Estado estado : estados) {
            estado.setAccesible(accesibles[estado.getId()-1]);
            if (!accesibles[estado.getId()-1]) {
                inaccesibles.add(estado);
            }
        }

        //determina estados accesibles y modifica el atributo
        setEstadosInaccesibles(inaccesibles);
        return inaccesibles;
    }

    private Estado transicion(Estado estadoOrigen, char simbolo){
        if (!this.alfabeto.contieneSimbolo(simbolo)){
            throw new IllegalArgumentException("El simbolo "+simbolo+" no pertenece al alfabeto del automata");
        }
        if (!contieneEstado(estadoOrigen)){
            throw new IllegalArgumentException("El estado " + estadoOrigen + " no pertenece al conjunto de estados del autómata.");
        }
        if (!funcionDeTrancision.get(estadoOrigen).containsKey(simbolo)){
            return null;
        }
        return funcionDeTrancision.get(estadoOrigen).get(simbolo);
    }
    public void imprimirAFDSimplificado(){
        //por hacer
    }
    public void exportar(){
        //por hacer
    }
    public boolean procesarCadena(String cadena){
        Estado estadoActual = estadoInicial;
        for(int i=0;i<cadena.length();i++){
            estadoActual = transicion(estadoActual, cadena.charAt(i));
        }
        return estadosDeAceptacion.contains(estadoActual) ? true : false;
        
        
    }
    public boolean procesarCadenaConDetalles(String cadena){
        Estado estadoActual = estadoInicial;
        System.out.println("Estado inicial: "+estadoActual);
        for(int i=0;i<cadena.length();i++){
            estadoActual = transicion(estadoActual, cadena.charAt(i));
            System.out.println("con el caracter "+cadena.charAt(i)+" llega al estado: "+estadoActual);
        }
        return estadosDeAceptacion.contains(estadoActual) ? true : false;
        
    }
    public void procesarListaCadenas(String[] cadenas,String nombreArchivo, boolean imprimirPantalla){
        //TODO 
    }
    public AFD hallarComplemento(AFD afdInput){
        AFD afd = new AFD(alfabeto, estados, funcionDeTrancision);
        return afd;
        //TODO
    }

    // Getters & Setters
    public Alfabeto getAlfabeto() {
        return alfabeto;
    }
    public void setAlfabeto(Alfabeto alfabeto) {
        this.alfabeto = alfabeto;
    }
    public ArrayList<Estado> getEstados() {
        return estados;
    }
    public void setEstados(ArrayList<Estado> estados) {
        this.estados = estados;
    }
    public Estado getEstadoInicial() {
        return estadoInicial;
    }
    public void setEstadoInicial(Estado estadoInicial) {
        this.estadoInicial = estadoInicial;
    }
    public ArrayList<Estado> getEstadosDeAceptacion() {
        return estadosDeAceptacion;
    }
    public void setEstadosDeAceptacion(ArrayList<Estado> estadosDeAceptacion) {
        this.estadosDeAceptacion = estadosDeAceptacion;
    }
    public HashMap<Estado, HashMap<Character, Estado>> getFuncionDeTrancision() {
        return funcionDeTrancision;
    }
    public void setFuncionDeTrancision(HashMap<Estado, HashMap<Character, Estado>> funcionDeTrancision) {
        this.funcionDeTrancision = funcionDeTrancision;
    }
    public ArrayList<Estado> getEstadosLimbo() {
        return estadosLimbo;
    }
    public void setEstadosLimbo(ArrayList<Estado> estadosLimbo) {
        this.estadosLimbo = estadosLimbo;
    }
    public ArrayList<Estado> getEstadosInaccesibles() {
        return estadosInaccesibles;
    }
    public void setEstadosInaccesibles(ArrayList<Estado> estadosInaccesibles) {
        this.estadosInaccesibles = estadosInaccesibles;
    }

    @Override
    public String toString() {
        return "AFD [alfabeto=" + alfabeto + ", estados=" + estados + ", estadoInicial=" + estadoInicial
                + ", estadosDeAceptacion=" + estadosDeAceptacion + ", funcionDeTrancision=" + funcionDeTrancision
                + ", estadosLimbo=" + estadosLimbo + ", estadosInaccesibles=" + estadosInaccesibles + "]";
    }

    public static void main(String[] args){
        char[] simbolos = {'0','1'};
        Alfabeto alf = new Alfabeto(simbolos);
        int numEstados = 2;
        ArrayList<Estado> estados = new ArrayList<Estado>();
        for (int i = 0; i < numEstados; i++) {
            estados.add(new Estado());
        }  
        HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion = new HashMap<>();
        /*for (Estado estado : estados) {
            HashMap<Character, Estado> transiciones = new HashMap<>();
            for (char simbolo : alf.getSimbolos()) {
                transiciones.put(simbolo, estados.get((int)(Math.random() * numEstados)));
            }
            funcionDeTransicion.put(estado, transiciones);
        }*/
        AFD afd = new AFD(alf, estados, funcionDeTransicion);
        afd.fillTransitions();
        System.out.println("transiciones: "+afd.getFuncionDeTrancision());
        ArrayList<Estado> estadosAcept = new ArrayList<>();
        estadosAcept.add(estados.get(0));
        //estadosAcept.add(estados.get(8));
        afd.setEstadoInicial(estados.get(0));
        System.out.println(afd.getEstadoInicial());
        afd.setEstadosDeAceptacion(estadosAcept);
        System.out.println(afd.getEstadosDeAceptacion());
        afd.hallarEstadosLimbo();
        System.out.println(afd.getEstadosLimbo());
        afd.hallarEstadosInaccesibles();
        System.out.println(afd.getEstadosInaccesibles());
        System.out.println(afd.procesarCadenaConDetalles("01"));
        System.out.println(afd.procesarCadenaConDetalles("010"));
        System.out.println(afd.procesarCadenaConDetalles("011"));
        System.exit(0);
    }
}
