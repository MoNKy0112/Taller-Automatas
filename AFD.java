import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
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

    private boolean contieneEstado(Estado estado){
        return this.estados.contains(estado);
    }

    public ArrayList<Estado> hallarEstadosLimbo(){
        


        ArrayList<Estado> estadosLimbo =new ArrayList<>(
            estados.stream().filter(p -> p.isLimbo()).collect(Collectors.toList()));
        //determina estados limbo y modifica el atributo
        setEstadosLimbo(estadosLimbo);
        return estadosLimbo;
    }
    
    public ArrayList<Estado> hallarEstadosInaccesibles(){
        boolean[] accesibles = new boolean[estados.size()];
        Queue<Estado> queue = new LinkedList<>();
        queue.offer(estadoInicial);
        accesibles[estadoInicial.getId()] = true;

        while (!queue.isEmpty()) {
            Estado estadoActual = queue.poll();
            for (char simbolo: alfabeto.getSimbolos()) {
                Estado estadoSig = transicion(estadoActual, simbolo);
                if (estadoSig != null && !accesibles[estadoSig.getId()]) {
                    accesibles[estadoSig.getId()] = true;
                    queue.offer(estadoSig);
                }
            }
        }

        ArrayList<Estado> inaccesibles = new ArrayList<>();
        for (Estado estado : estados) {
            estado.setAccesible(accesibles[estado.getId()]);
            if (!accesibles[estado.getId()]) {
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
            throw new IllegalArgumentException("El estado "+ estadoOrigen +" no teine transicion definida con el simbolo " + simbolo);
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
        return false;
        //por hacer
    }
    public boolean procesarCadenaConDetalles(String cadena){
        return false;
        //por hacer
    }
    public void procesarListaCadenas(String[] cadenas,String nombreArchivo, boolean imprimirPantalla){
        //por hacer
    }
    public AFD hallarComplemento(AFD afdInput){
        AFD afd = new AFD(alfabeto, estados, funcionDeTrancision);
        return afd;
        //por hacer
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

    
}
