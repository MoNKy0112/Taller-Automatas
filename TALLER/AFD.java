package TALLER;
import java.util.ArrayList;
import java.util.stream.Collectors;

import TALLER.GUITABLA.MatrixGUI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;
import java.util.Map;
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

    public AFD() {
        this.funcionDeTrancision = new HashMap<>();
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
                    agregarTransicion(estado, simbolo, estadoLimboNuevo);
                }
            }
        }
    }

    public void agregarTransicion(Estado estadoOrigen,char simbolo, Estado estadoDestino){
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
                if (!visitados.contains(estadoSig)){
                    //si no esta en ninguno de los dos, hay que recorrer mas y verificar si se llega a
                    //algun estado de aceptacion
                    if(dfsLimbo(estadoSig, visitados, noMuertos)){
                        noMuertos.add(estadoActual);
                        return true;
                    }
                }                 
                //De no ser asi por este camino solo hay estados limbo
                //Por lo cual debe revisar los demas caminos
            }
            
        }
        //Si ningun camino lo llevo a un estado de aceptacion, se entiende que este es un estado limbo
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
                Estado estadoSig = transicion(estadoActual, simbolo);
                if (estadoSig != null && !accesibles.contains(estadoSig)) {
                    accesibles.add(estadoSig);
                    queue.offer(estadoSig);
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
        System.out.println("proceso con cadena: "+cadena);
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

    public AFD hallarComplemento(){
        AFD afd = new AFD(alfabeto, estados, funcionDeTrancision);
        afd.setEstadoInicial(estadoInicial);
        ArrayList<Estado> aceptacion = new ArrayList<>();
        for (Estado estado: estados){
            if(!getEstadosDeAceptacion().contains(estado)){
                estado.setAceptacion(true);
                aceptacion.add(estado);
            } 
        }
        afd.setEstadosDeAceptacion(aceptacion);
        return afd;
    }

    public AFD productoCartesianoY(AFD afd1, AFD afd2){
        AFD resultado = new AFD();
        Set<Estado> estadosProducto = new HashSet<>();
        Map<String, Estado> estadoMap = new HashMap<>();
        ArrayList<Estado> estadosRes = new ArrayList<>();
        // Se crean todos los estados del producto cartesiano
        for (Estado estado1 : afd1.getEstados()) {
            for (Estado estado2 : afd2.getEstados()) {
                String estadoProducto = estado1.toString()+","+estado2.toString();
                Estado estado = new Estado(new Estado[] {estado1,estado2});
                estadosProducto.add(estado);
                System.out.println(estadoProducto+"->"+estado);
                estadoMap.put(estadoProducto, estado);
                estadosRes.add(estado);
                // Si ambos estados son de aceptación, entonces el estado del producto cartesiano también lo es
                if (afd1.getEstadosDeAceptacion().contains(estado1) && afd2.getEstadosDeAceptacion().contains(estado2)) {
                    resultado.agregarEstadoAceptacion(estado);
                }
            }
        }
        resultado.setEstados(estadosRes);
        // Se configuran los datos del AFD resultante
        resultado.setAlfabeto(afd1.getAlfabeto());
        resultado.setEstadoInicial(estadoMap.get(afd1.getEstadoInicial().toString()+","+afd2.getEstadoInicial().toString()));
        System.out.println(resultado.getEstadosDeAceptacion());
        // Se agregan las transiciones correspondientes
        for (Estado estado : estadosProducto) {
            for (char simbolo : resultado.getAlfabeto().getSimbolos()) {
                Estado[] estados = estado.getEstados();
                String est = afd1.transicion(estados[0], simbolo)+","+afd2.transicion(estados[1], simbolo);
                System.out.println(estado+":"+est+"-> "+estadoMap.get(est));
                Estado estado1 = estadoMap.get(est);
                resultado.agregarTransicion(estado, simbolo, estado1);
            }
        }

        return resultado;
    }

    public AFD productoCartesianoO(AFD afd1, AFD afd2){
        AFD resultado = new AFD();
        Set<Estado> estadosProducto = new HashSet<>();
        Map<String, Estado> estadoMap = new HashMap<>();
        ArrayList<Estado> estadosRes = new ArrayList<>();
        // Se crean todos los estados del producto cartesiano
        for (Estado estado1 : afd1.getEstados()) {
            for (Estado estado2 : afd2.getEstados()) {
                String estadoProducto = estado1.toString()+","+estado2.toString();
                Estado estado = new Estado(new Estado[] {estado1,estado2});
                estadosProducto.add(estado);
                System.out.println(estadoProducto+"->"+estado);
                estadoMap.put(estadoProducto, estado);
                estadosRes.add(estado);
                // Si ambos estados son de aceptación, entonces el estado del producto cartesiano también lo es
                if (afd1.getEstadosDeAceptacion().contains(estado1) || afd2.getEstadosDeAceptacion().contains(estado2)) {
                    resultado.agregarEstadoAceptacion(estado);
                }
            }
        }
        resultado.setEstados(estadosRes);
        // Se configuran los datos del AFD resultante
        resultado.setAlfabeto(afd1.getAlfabeto());
        resultado.setEstadoInicial(estadoMap.get(afd1.getEstadoInicial().toString()+","+afd2.getEstadoInicial().toString()));
        System.out.println(resultado.getEstadosDeAceptacion());
        // Se agregan las transiciones correspondientes
        for (Estado estado : estadosProducto) {
            for (char simbolo : resultado.getAlfabeto().getSimbolos()) {
                Estado[] estados = estado.getEstados();
                String est = afd1.transicion(estados[0], simbolo)+","+afd2.transicion(estados[1], simbolo);
                System.out.println(estado+":"+est+"-> "+estadoMap.get(est));
                Estado estado1 = estadoMap.get(est);
                resultado.agregarTransicion(estado, simbolo, estado1);
            }
        }

        return resultado;
    }
    
    public AFD productoCartesianoDiferencia(AFD afd1, AFD afd2){
        AFD resultado = new AFD();
        Set<Estado> estadosProducto = new HashSet<>();
        Map<String, Estado> estadoMap = new HashMap<>();
        ArrayList<Estado> estadosRes = new ArrayList<>();
        // Se crean todos los estados del producto cartesiano
        for (Estado estado1 : afd1.getEstados()) {
            for (Estado estado2 : afd2.getEstados()) {
                String estadoProducto = estado1.toString()+","+estado2.toString();
                Estado estado = new Estado(new Estado[] {estado1,estado2});
                estadosProducto.add(estado);
                System.out.println(estadoProducto+"->"+estado);
                estadoMap.put(estadoProducto, estado);
                estadosRes.add(estado);
                // Si ambos estados son de aceptación, entonces el estado del producto cartesiano también lo es
                if (afd1.getEstadosDeAceptacion().contains(estado1) && !afd2.getEstadosDeAceptacion().contains(estado2)) {
                    resultado.agregarEstadoAceptacion(estado);
                }
            }
        }
        resultado.setEstados(estadosRes);
        // Se configuran los datos del AFD resultante
        resultado.setAlfabeto(afd1.getAlfabeto());
        resultado.setEstadoInicial(estadoMap.get(afd1.getEstadoInicial().toString()+","+afd2.getEstadoInicial().toString()));
        System.out.println(resultado.getEstadosDeAceptacion());
        // Se agregan las transiciones correspondientes
        for (Estado estado : estadosProducto) {
            for (char simbolo : resultado.getAlfabeto().getSimbolos()) {
                Estado[] estados = estado.getEstados();
                String est = afd1.transicion(estados[0], simbolo)+","+afd2.transicion(estados[1], simbolo);
                System.out.println(estado+":"+est+"-> "+estadoMap.get(est));
                Estado estado1 = estadoMap.get(est);
                resultado.agregarTransicion(estado, simbolo, estado1);
            }
        }

        return resultado;
    }
    
    public AFD productoCartesianoDiferenciaSimetrica(AFD afd1, AFD afd2){
        AFD resultado = new AFD();
        Set<Estado> estadosProducto = new HashSet<>();
        Map<String, Estado> estadoMap = new HashMap<>();
        ArrayList<Estado> estadosRes = new ArrayList<>();
        // Se crean todos los estados del producto cartesiano
        for (Estado estado1 : afd1.getEstados()) {
            for (Estado estado2 : afd2.getEstados()) {
                String estadoProducto = estado1.toString()+","+estado2.toString();
                Estado estado = new Estado(new Estado[] {estado1,estado2});
                estadosProducto.add(estado);
                System.out.println(estadoProducto+"->"+estado);
                estadoMap.put(estadoProducto, estado);
                estadosRes.add(estado);
                // Si ambos estados son de aceptación, entonces el estado del producto cartesiano también lo es
                boolean cond1 = afd1.getEstadosDeAceptacion().contains(estado1);
                boolean cond2 = afd2.getEstadosDeAceptacion().contains(estado2);
                if ((cond1 || cond2) && !(cond1 && cond2)) {
                    resultado.agregarEstadoAceptacion(estado);
                }
            }
        }
        resultado.setEstados(estadosRes);
        // Se configuran los datos del AFD resultante
        resultado.setAlfabeto(afd1.getAlfabeto());
        resultado.setEstadoInicial(estadoMap.get(afd1.getEstadoInicial().toString()+","+afd2.getEstadoInicial().toString()));
        System.out.println(resultado.getEstadosDeAceptacion());
        // Se agregan las transiciones correspondientes
        for (Estado estado : estadosProducto) {
            for (char simbolo : resultado.getAlfabeto().getSimbolos()) {
                Estado[] estados = estado.getEstados();
                String est = afd1.transicion(estados[0], simbolo)+","+afd2.transicion(estados[1], simbolo);
                System.out.println(estado+":"+est+"-> "+estadoMap.get(est));
                Estado estado1 = estadoMap.get(est);
                resultado.agregarTransicion(estado, simbolo, estado1);
            }
        }

        return resultado;
    }

    private void agregarEstadoAceptacion(Estado estado) {
        estado.setAceptacion(true);
        estadosDeAceptacion.add(estado);
    }

    public AFD simplificarAFD(AFD afdInput){
        AFD sinInaccesibles = eliminarEstadosInaccesibles(afdInput);
        Estado[] estados = new Estado[sinInaccesibles.getEstados().size()];
        estados = sinInaccesibles.getEstados().toArray(estados);
        AFD nuevoAfd = new AFD();
        Character[][] TablaEquivalencia = new Character[estados.length][estados.length];
        ArrayList<ArrayList<Estado[]>> tablaTransiciones = new ArrayList<ArrayList<Estado[]>>();
        int iteracion = 1;
        for (int i=0;i<estados.length;i++){
            for (int j=i;j<estados.length;j++){
                if(estados[i].isAceptacion()!=estados[j].isAceptacion()){
                    TablaEquivalencia[i][j]=TablaEquivalencia[j][i]=(char)(iteracion+'0');
                }else if(i!=j){
                    ArrayList<Estado[]> inner= new ArrayList<>();
                    inner.add(new Estado[] {estados[i],estados[j]});
                    for (char simbolo:sinInaccesibles.getAlfabeto().getSimbolos()){
                        inner.add(new Estado[] {transicion(estados[i], simbolo) , transicion(estados[j], simbolo)});
                    }
                    tablaTransiciones.add(inner);
                }
            }
        }

        boolean cambio = true;

        while(cambio) {
            cambio = false;
            iteracion++;

            for(int i=0;i<tablaTransiciones.size();i++){
                for(int j=0;j<sinInaccesibles.getAlfabeto().size();j++){
                    Estado [] estadosAct = tablaTransiciones.get(i).get(j+1);
                    int x = sinInaccesibles.getEstados().indexOf(estadosAct[0]);
                    int y = sinInaccesibles.getEstados().indexOf(estadosAct[1]);
                    Estado [] temp = tablaTransiciones.get(i).get(j);
                    int x2 = sinInaccesibles.getEstados().indexOf(temp[0]);
                    int y2 = sinInaccesibles.getEstados().indexOf(temp[1]);
                    if((TablaEquivalencia[x2][y2] == null||TablaEquivalencia[y2][x2] == null)
                    && (TablaEquivalencia[x][y] != null || TablaEquivalencia[y][x] != null)){
                        TablaEquivalencia[x2][y2] = TablaEquivalencia[y2][x2] = (char)(iteracion+'0');
                        cambio = true;
                    }
                }
            }
        }


       
        for(int i=0;i<tablaTransiciones.size();i++){
            for(int j=0;j<sinInaccesibles.getAlfabeto().size()+1;j++){
                System.out.print(tablaTransiciones.get(i).get(j)[0]+","+tablaTransiciones.get(i).get(j)[1]+"->");
                
            }
            System.out.println(" ");
        }

        for (int i=0;i<estados.length;i++){
            for (int j=0;j<estados.length;j++){
                if(i!=j && TablaEquivalencia[i][j]==null)TablaEquivalencia[i][j]='E';
            }
        }
        for (int i=0;i<estados.length;i++){
            for (int j=0;j<i;j++){
                System.out.print(TablaEquivalencia[i][j]);
            }
            System.out.println();
        }

        ArrayList<ArrayList<Estado>> equivalentes = new ArrayList<ArrayList<Estado>>();
        ArrayList<Estado> revisados = new ArrayList<>();
        for (int i=0;i<estados.length;i++){
            if (!revisados.contains(estados[i])){
                ArrayList<Estado> inner= new ArrayList<>();
                inner.add(estados[i]);
                revisados.add(estados[i]);
                for (int j=0;j<estados.length;j++){
                    if(TablaEquivalencia[i][j]!=null){
                        if(TablaEquivalencia[i][j]=='E'){
                            inner.add(estados[j]);
                            revisados.add(estados[j]);
                        }  
                    }
                }   
                equivalentes.add(inner);
            } 
        }

        for (ArrayList<Estado> array : equivalentes){
            for (Estado estado : array){
                System.out.print(estado);
            }
            System.out.println();
        }

        
        
        
        /*
        ArrayList<Estado> yaRevisados = new ArrayList<>();
        ArrayList<ArrayList<Estado>> Equivalentes = new ArrayList<ArrayList<Estado>>();
        for (int i=1;i<estados.length;i++){
            for (int j=1;j<estados.length;j++){

                if(!yaRevisados.contains()){
                    if(TablaEquivalencia[i][j]!=null){

                    }
                }
            }
        }*/

        
        
        return nuevoAfd;
    }

    private AFD eliminarEstadosInaccesibles(AFD afdInput){
        ArrayList<Estado> nuevosEstados= new ArrayList<>();
        nuevosEstados.addAll(afdInput.getEstados());
        ArrayList<Estado> nuevosEstados2= new ArrayList<>();
        nuevosEstados2.addAll(nuevosEstados);
        HashMap<Estado, HashMap<Character,Estado>> nuevaFuncionTransicion = afdInput.getFuncionDeTrancision();
        for (Estado estado : nuevosEstados){
            if (!estado.isAccesible()){
                nuevaFuncionTransicion.remove(estado);
                nuevosEstados2.remove(estado);
            }
        }
        AFD nuevoAfd = new AFD(afdInput.getAlfabeto(),nuevosEstados2,nuevaFuncionTransicion);
        
        return nuevoAfd;
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
        char[] simbolos = {'a','b'};
        Alfabeto alf = new Alfabeto(simbolos);
        int numEstados = 5;
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
        estados.get(4).setAceptacion(true);
        estadosAcept.add(estados.get(4));
        //estadosAcept.add(estados.get(8));
        estados.get(0).setInicial(true);
        afd.setEstadoInicial(estados.get(0));
        System.out.println(afd.getEstadoInicial());
        afd.setEstadosDeAceptacion(estadosAcept);
        System.out.println(afd.getEstadosDeAceptacion());
        afd.hallarEstadosLimbo();
        System.out.println("Estados llimbo: "+afd.getEstadosLimbo());
        afd.hallarEstadosInaccesibles();
        afd.simplificarAFD(afd);
        /*System.out.println("Estados inaccesibles: "+afd.getEstadosInaccesibles());
        System.out.println(afd.procesarCadenaConDetalles("01"));
        System.out.println(afd.procesarCadenaConDetalles("010"));
        System.out.println(afd.procesarCadenaConDetalles("011"));
        
        System.out.println("---------------AFD2-------------");
        ArrayList<Estado> estados2 = new ArrayList<Estado>();
        for (int i = 0; i < numEstados; i++) {
            estados2.add(new Estado());
        }  
        AFD afd2 = new AFD(alf, estados2, funcionDeTransicion);
        afd2.fillTransitions();
        System.out.println("transiciones: "+afd2.getFuncionDeTrancision());
        ArrayList<Estado> estadosAcept2 = new ArrayList<>();
        estadosAcept2.add(estados2.get(0));
        //estadosAcept.add(estados.get(8));
        afd2.setEstadoInicial(estados2.get(0));
        System.out.println(afd2.getEstadoInicial());
        afd2.setEstadosDeAceptacion(estadosAcept2);
        System.out.println(afd2.getEstadosDeAceptacion());
        afd2.hallarEstadosLimbo();
        System.out.println("Estados llimbo: "+afd2.getEstadosLimbo());
        afd2.hallarEstadosInaccesibles();
        System.out.println("Estados inaccesibles: "+afd2.getEstadosInaccesibles());
        System.out.println(afd2.procesarCadenaConDetalles("01"));
        System.out.println(afd2.procesarCadenaConDetalles("010"));
        System.out.println(afd2.procesarCadenaConDetalles("011"));


        AFD afd3 = afd.productoCartesianoY(afd, afd2);
        System.out.println(afd3.getEstados()+","+afd3.getEstadoInicial()+","+afd3.getEstadosDeAceptacion());
        System.out.println(afd3.getFuncionDeTrancision());
        /*AFD afd2 = afd.hallarComplemento();
        afd2.hallarEstadosLimbo();
        System.out.println("Estados llimbo: "+afd2.getEstadosLimbo());
        afd2.hallarEstadosInaccesibles();
        System.out.println("Estados inaccesibles: "+afd2.getEstadosInaccesibles());
        System.out.println(afd2.procesarCadenaConDetalles("01"));
        System.out.println(afd2.procesarCadenaConDetalles("010"));
        System.out.println(afd2.procesarCadenaConDetalles("011"));*/
        System.exit(0);
    }
}
