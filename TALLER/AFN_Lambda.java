package TALLER;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import TALLER.GUITABLA.EstadoInicialGUI;
import TALLER.GUITABLA.MatrixGUIAFN;

public class AFN_Lambda {
    private Alfabeto alfabeto = new Alfabeto(null);
    private ArrayList<Estado> estados = new ArrayList<Estado>();
    private Estado estadoInicial = estados.stream().filter(p -> p.isInicial()).findFirst().orElse(
        estados.stream().findFirst().orElse(null)
    );
    private ArrayList<Estado> estadosDeAceptacion = new ArrayList<>(
        estados.stream().filter(p -> p.isAceptacion()).collect(Collectors.toList())
    );
    private HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion;
    private ArrayList<Estado> estadosLimbo = new ArrayList<>(
        estados.stream().filter(p -> p.isLimbo()).collect(Collectors.toList())
    );

    public AFN_Lambda(Alfabeto alfabeto, ArrayList<Estado> estados, HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion){
        setAlfabeto(alfabeto);
        this.estados = estados;
        this.funcionDeTransicion = funcionDeTransicion;
    }

    public AFN_Lambda(Alfabeto alf){
        setAlfabeto(alfabeto);
        this.funcionDeTransicion = new HashMap<>();
    }



    private ArrayList<Estado> estadosInaccesibles = new ArrayList<>(
        estados.stream().filter(p -> !p.isAccesible()).collect(Collectors.toList())
    );
    //
    public List<Estado> lambdaClausura(Estado estado){
        Set<Estado> estados = new HashSet<>();
        Queue<Estado> queue = new LinkedList<>();
        estados.add(estado);
        queue.offer(estado);

        while(!queue.isEmpty()){
            Estado est = queue.poll();

            List<Estado> list = transiciones(est, '$');

            for(Estado estList : list){
                if(!estados.contains(estList)){
                    estados.add(estList);
                    queue.offer(estList);
                }
            }
            
        }
        List<Estado> estadosList = new ArrayList<>(estados);

        return estadosList;
    }

    public List<Estado> lambdaClausura(List<Estado> estado){
        Set<Estado> estados = new HashSet<>();
        Queue<Estado> queue = new LinkedList<>();
        for(Estado est:estado){
            estados.add(est);
            queue.offer(est);
        }
        
        while(!queue.isEmpty()){
            Estado est = queue.poll();

            List<Estado> list = transiciones(est, '$');

            for(Estado estList : list){
                if(!estados.contains(estList)){
                    estados.add(estList);
                    queue.offer(estList);
                }
            }
            
        }
        List<Estado> estadosList = new ArrayList<>(estados);

        return estadosList;
    }

    public boolean procesarCadena(String cadena){
        Estado estadoActual = estadoInicial;
        if(!cadena.isEmpty()){
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
        }else{
            System.out.println("SSSSS");
            return procesarCadena(estadoActual, cadena);
        }
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
            for(Estado estad : estados){
                if(estadosDeAceptacion.contains(estad)){
                    return true;
                }
                return false;
            }
            
        }
        return false;
    }
    //TODO PROBAR
    public boolean procesarCadenaConDetalles(String cadena){
        Estado estadoActual = estadoInicial;
        List<Estado> estados = transiciones(estadoActual, cadena.charAt(0));
        List<Estado> listaEstados = new ArrayList<>();
        for (Estado est : estados){
            if(procesarCadenaConDetalles(est, cadena.substring(1),listaEstados)){
                System.out.println(listaEstados);
                System.out.println(true);
                return true;
            }
        }
        System.out.println(false);
        return false;
    }
    //TODO PROBAR
    private boolean procesarCadenaConDetalles(Estado estado,String cadena,List<Estado> listaEstados){
        Estado estadoActual = estado;
        //System.out.println(cadena.isEmpty()+"-tam:"+cadena.length());
        if(!cadena.isEmpty()){
            //System.out.println(estadoActual+"--"+ cadena.charAt(0)+"="+transiciones(estadoActual, cadena.charAt(0)));
            List<Estado> estados = transiciones(estadoActual, cadena.charAt(0));
            if (estados!=null){
                for (Estado est : estados){
                    if(procesarCadenaConDetalles(est, cadena.substring(1),listaEstados)){
                        listaEstados.add(0, est);
                        return true;
                    }
                }
            }
        }else{
            return estadosDeAceptacion.contains(estadoActual) ? true : false;
        }
        return false;
    }

    public AFN AFN_LambdaToAFN(AFN_Lambda afnl){

        HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion = new HashMap<>();

        List<List<Estado>> listaLambdaClausura = new ArrayList<>();
        Alfabeto alf = afnl.getAlfabeto();
        ArrayList<Estado> estadosIn = afnl.getEstados();

        for(Estado est:estadosIn){
            listaLambdaClausura.add(afnl.lambdaClausura(est));
        }

        for(Estado est:estadosIn){
            List<Estado> lEst = afnl.lambdaClausura(est);
            HashMap<Character, List<Estado>> transiciones = funcionDeTransicion.getOrDefault(est, new HashMap<>());
            for(char simbolo:alf.getSimbolos()){
                if(simbolo!='$'){
                    Set<Estado> setTr = new HashSet<>();
                    for(Estado estLamb:lEst){
                        setTr.addAll(transiciones(estLamb, simbolo));
                    }
                    List<Estado> listTr = new ArrayList<>(setTr);
                    transiciones.put(simbolo,afnl.lambdaClausura(listTr));
                }
            }
            funcionDeTransicion.put(est,transiciones);
        }

        AFN afn = new AFN(alf,estadosIn,funcionDeTransicion);
        afn.setEstadoInicial(afnl.getEstadoInicial());
        
        return afn;
    }

    //TODO PROBAR
    public AFD AFN_LambdaToAFD(AFN_Lambda afnl){

        AFN afn = AFN_LambdaToAFN(afnl);
        AFD afd = afn.AFNtoAFD(afn);
        
        return afd;
    }
    //TODO PROBAR
    public boolean procesarCadenaConversion(String Cadena){
        AFD afd = AFN_LambdaToAFD(this);
        return afd.procesarCadena(Cadena);
    }
    //TODO PROBAR
    public boolean procesarCadenaConDetallesConversion(String Cadena){
        AFD afd = AFN_LambdaToAFD(this);
        return afd.procesarCadenaConDetalles(Cadena);
    }
    //TODO PROBAR
    public void procesarListaCadenasConversion(String[] cadenas,String nomreArchivo,boolean imprimirPantalla){
        AFD afd = AFN_LambdaToAFD(this);
        afd.procesarListaCadenas(cadenas, nomreArchivo, imprimirPantalla);
    }
    //TODO PROBAR
    public void exportar(String nombreAarchivo){
        try {
            PrintWriter writer = new PrintWriter(nombreAarchivo+".nfe", "UTF-8");
            writer.println("#!nfe");
            writer.println("#alphabet");
            for(char simbolo : alfabeto.getSimbolos()){
                writer.println(simbolo);
            }
            writer.println("#states");
            for(Estado estado : estados){
                writer.println(estado.toString());
            }
            writer.println("#initial");
            writer.println(estadoInicial.toString());
            writer.println("#accepting");
            for(Estado estado : estadosDeAceptacion){
                writer.println(estado.toString());
            }
            writer.println("#transitions");
            System.out.println("tramsitions");
            for(Estado estado : estados){
                for(char simbolo : alfabeto.getSimbolos()){
                    if(!funcionDeTransicion.get(estado).get(simbolo).contains(null)){
                        writer.print(estado.toString()+":"+simbolo+">");
                        List<Estado> estadosDest = funcionDeTransicion.get(estado).get(simbolo);
                        System.out.println(funcionDeTransicion.get(estado).get(simbolo));
                        for(int i=0;i<estadosDest.size()-1;i++) writer.print(estadosDest.get(i)+";");
                        writer.print(estadosDest.get(estadosDest.size()-1));
                        writer.print("\n");
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    //TODO PROBAR
    public void exportar(){
        String nombreAarchivo = "nuevoAFNL";
        try {
            PrintWriter writer = new PrintWriter("/"+nombreAarchivo+".nfe", "UTF-8");
            writer.println("#!nfe");
            writer.println("#alphabet");
            for(char simbolo : alfabeto.getSimbolos()){
                writer.println(simbolo);
            }
            writer.println("#states");
            for(Estado estado : estados){
                writer.println(estado.toString());
            }
            writer.println("#initial");
            writer.println(estadoInicial.toString());
            writer.println("#accepting");
            for(Estado estado : estadosDeAceptacion){
                writer.println(estado.toString());
            }
            writer.println("#transitions");
            for(Estado estado : estados){
                writer.print(estado.toString()+":");
                for(char simbolo : alfabeto.getSimbolos()){
                    writer.println(simbolo+">"+funcionDeTransicion.get(estado).get(simbolo).toString());
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        if(!funcionDeTransicion.containsKey(estadoOrigen)){
            return null;
        }
        if (!funcionDeTransicion.get(estadoOrigen).containsKey(simbolo)){
            return null;
        }
        
        return funcionDeTransicion.get(estadoOrigen).get(simbolo);
    }

    public void setInicialWithGui(){
        EstadoInicialGUI gui = new EstadoInicialGUI(this);
        while (gui.isVisible()){
            try {
                Thread.sleep(100); // Esperar 100 milisegundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Ahora el estado inicial es: "+this.getEstadoInicial());
    }

    public void fillTransitions(){
        MatrixGUIAFN gui = new MatrixGUIAFN(this);
        while (gui.isVisible() ){
            try {
                Thread.sleep(100); // Esperar 100 milisegundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        //     HashMap<Character, List<Estado>> transiciones = funcionDeTransicion.getOrDefault(est, new HashMap<>());
        //     char simb =  '$';
        //     List<Estado> tr = null;
        //     if(transiciones.containsKey(simb) || !transiciones.get(simb).contains(est)){
        //         tr = transiciones.get(simb);
        //         tr.add(est);
        //         transiciones.put(simb,tr);
        //     }
        //     funcionDeTransicion.put(est, transiciones);
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
    public HashMap<Estado, HashMap<Character, List<Estado>>> getFuncionDeTransicion() {
        return funcionDeTransicion;
    }
    public void setFuncionDeTransicion(HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion) {
        this.funcionDeTransicion = funcionDeTransicion;
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
