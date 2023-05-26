package TALLER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class AFN_Lambda {
    private Alfabeto alfabeto = new Alfabeto(null);
    private ArrayList<Estado> estados = new ArrayList<Estado>();
    private Estado estadoInicial = estados.stream().filter(p -> p.isInicial()).findFirst().orElse(
        estados.stream().findFirst().orElse(null)
    );
    private ArrayList<Estado> estadosDeAceptacion = new ArrayList<>(
        estados.stream().filter(p -> p.isAceptacion()).collect(Collectors.toList())
    );
    private HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTrancision;
    private ArrayList<Estado> estadosLimbo = new ArrayList<>(
        estados.stream().filter(p -> p.isLimbo()).collect(Collectors.toList())
    );
    private ArrayList<Estado> estadosInaccesibles = new ArrayList<>(
        estados.stream().filter(p -> !p.isAccesible()).collect(Collectors.toList())
    );
    //
    public List<Estado> lambdaClausura(Estado estado){
        List<Estado> estados = new ArrayList<>();
        estados.add(estado);
        estados.addAll(transiciones(estado, '$'));
        return estados;
    }






    public boolean procesarCadena(String cadena){
        Estado estadoActual = estadoInicial;
        List<Estado> estados = lambdaClausura(estadoActual);
        Set<Estado> setEstados = new HashSet<>();
        for (Estado est : estados){
            setEstados.addAll(transiciones(est, cadena.charAt(0)));
        }
        estados = new ArrayList<>(setEstados);
        setEstados = new HashSet<>();
        for (Estado est : estados){
            setEstados.addAll(lambdaClausura(est));
        }
        estados = new ArrayList<>(setEstados);
        for (Estado est : estados){
            if(procesarCadena(est, cadena.substring(1)))return true;
        }
        return false;
    }
    private boolean procesarCadena(Estado estado,String cadena){
        Estado estadoActual = estado;
        List<Estado> estados = lambdaClausura(estadoActual);
        
        //System.out.println(cadena.isEmpty()+"-tam:"+cadena.length());
        if(!cadena.isEmpty()){
            //System.out.println(estadoActual+"--"+ cadena.charAt(0)+"="+transiciones(estadoActual, cadena.charAt(0)));
            Set<Estado> setEstados = new HashSet<>();
            for (Estado est : estados){
                setEstados.addAll(transiciones(est, cadena.charAt(0)));
            }
            estados = new ArrayList<>(setEstados);
            setEstados = new HashSet<>();
            for (Estado est : estados){
                setEstados.addAll(lambdaClausura(est));
            }
            estados = new ArrayList<>(setEstados);
            if (estados!=null){
                for (Estado est : estados){
                    if(procesarCadena(est, cadena.substring(1)))return true;
                }
            }
        }else{
            return estadosDeAceptacion.contains(estadoActual) ? true : false;
        }
        return false;
    }

    public ArrayList<Estado> hallarEstadosInaccesibles(){
        Set<Estado> accesibles = new HashSet<>();
        Queue<Estado> queue = new LinkedList<>();
        queue.offer(estadoInicial);
        accesibles.add(estadoInicial);
        while (!queue.isEmpty()) {
            Estado estadoActual = queue.poll();
            for (char simbolo: alfabeto.getSimbolos()) {
                //System.out.println(estadoActual+"->"+simbolo);
                List<Estado> estadosSig = transiciones(estadoActual, simbolo);
                if(estadosSig!=null){
                    for(Estado estadoSig: estadosSig){
                        if (estadoSig != null && !accesibles.contains(estadoSig)) {
                            accesibles.add(estadoSig);
                            queue.offer(estadoSig);
                        }
                    }
                }
            }
        }
        
        ArrayList<Estado> inaccesibles = new ArrayList<>();
        for (Estado estado : estados) {
            estado.setAccesible(accesibles.contains(estado));
            if (!accesibles.contains(estado)) {
                inaccesibles.add(estado);
            }
        }

        //determina estados accesibles y modifica el atributo
        setEstadosInaccesibles(inaccesibles);
        return inaccesibles;
    }



    private boolean contieneEstado(Estado estado){
        return this.estados.contains(estado);
    }
    public List<Estado> transiciones(Estado estadoOrigen, char simbolo){
        if (!this.alfabeto.contieneSimbolo(simbolo)){
            throw new IllegalArgumentException("El simbolo "+simbolo+" no pertenece al alfabeto del automata");
        }
        if (!contieneEstado(estadoOrigen)){
            throw new IllegalArgumentException("El estado " + estadoOrigen + " no pertenece al conjunto de estados del autómata.");
        }
        if(!funcionDeTrancision.containsKey(estadoOrigen)){
            return null;
        }
        if (!funcionDeTrancision.get(estadoOrigen).containsKey(simbolo)){
            return null;
        }
        
        return funcionDeTrancision.get(estadoOrigen).get(simbolo);
    }



    // Getters & Setters
    public Alfabeto getAlfabeto() {
        return alfabeto;
    }
    public void setAlfabeto(Alfabeto alfabeto) {
        if(!alfabeto.contieneSimbolo('$')){
            char[] original = alfabeto.getSimbolos();
            char[] a =new char[original.length+1];
            for (int i = 0; i < original.length; i++) {
                a[i] = original[i];
            }
            a[a.length - 1] = '$';
            alfabeto.setSimbolos(a);
        }
        this.alfabeto = alfabeto;
    }
    public ArrayList<Estado> getEstados() {
        return estados;
    }

    public void setEstados(ArrayList<Estado> estados) {

        // for(Estado est:estados){
        //     HashMap<Character, List<Estado>> transiciones = funcionDeTrancision.getOrDefault(est, new HashMap<>());
        //     char simb =  '$';
        //     List<Estado> tr = null;
        //     if(transiciones.containsKey(simb) || !transiciones.get(simb).contains(est)){
        //         tr = transiciones.get(simb);
        //         tr.add(est);
        //         transiciones.put(simb,tr);
        //     }
        //     funcionDeTrancision.put(est, transiciones);
        // }
        
        this.estados = estados;

    }
    public Estado getEstadoInicial() {
        return estadoInicial;
    }
    public void setEstadoInicial(Estado estadoInicial) {
        estadoInicial.setInicial(true);
        this.estadoInicial = estadoInicial;
    }
    public ArrayList<Estado> getEstadosDeAceptacion() {
        return estadosDeAceptacion;
    }
    public void setEstadosDeAceptacion(ArrayList<Estado> estadosDeAceptacion) {
        for(Estado estado:estadosDeAceptacion)estado.setAceptacion(true);
        this.estadosDeAceptacion = estadosDeAceptacion;
    }
    public HashMap<Estado, HashMap<Character, List<Estado>>> getFuncionDeTrancision() {
        return funcionDeTrancision;
    }
    public void setFuncionDeTrancision(HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTrancision) {
        this.funcionDeTrancision = funcionDeTrancision;
    }
    public ArrayList<Estado> getEstadosLimbo() {
        return estadosLimbo;
    }
    public void setEstadosLimbo(ArrayList<Estado> estadosLimbo) {
        for(Estado estado:estadosLimbo)estado.setLimbo(true);
        this.estadosLimbo = estadosLimbo;
    }
    public ArrayList<Estado> getEstadosInaccesibles() {
        return estadosInaccesibles;
    }
    public void setEstadosInaccesibles(ArrayList<Estado> estadosInaccesibles) {
        for(Estado estado:estadosInaccesibles )estado.setAccesible(false);
        this.estadosInaccesibles = estadosInaccesibles;
    }

}
