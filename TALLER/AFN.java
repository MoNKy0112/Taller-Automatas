package TALLER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import TALLER.GRAPH.AFGraphGUI;
import TALLER.GUITABLA.MatrixGUIAFN;

public class AFN {
    //Atributos
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
    private ArrayList<Estado> estadosInaccesibles = new ArrayList<>(
        estados.stream().filter(p -> !p.isAccesible()).collect(Collectors.toList())
    );


    //Metodos
    //B-1
    public AFN(Alfabeto alfabeto, ArrayList<Estado> estados, HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion) {
        this.alfabeto = alfabeto;
        this.estados = estados;
        this.funcionDeTransicion = funcionDeTransicion;
    }
    //B-1
    public AFN(Alfabeto alfabeto) {
        this.alfabeto = alfabeto;
        this.funcionDeTransicion = new HashMap<>();
    }
    //B-1
    public AFN() {
        this.funcionDeTransicion = new HashMap<>();
    }
    public AFN(String nombreArchivo) {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        
        ArrayList<Estado> estados = new ArrayList<>();
        HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion = new HashMap<>();
        ArrayList<Character> simbolos = new ArrayList<>();
        Map<String,Estado> mapEstados = new HashMap<>();
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File (nombreArchivo+".nfa");
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);
            
            // Lectura del fichero
            String linea;
            int status = -1;
            boolean flag=false;
            while((linea=br.readLine())!=null){
                flag=true;
                if(linea.equals("#alphabet")){
                    status = 0;
                    flag=false;
                }
                if(linea.equals("#states")){
                    status = 1;
                    flag=false;
                }
                if(linea.equals("#initial")){
                    status = 2;
                    flag=false;
                }
                if(linea.equals("#accepting")){
                    status = 3; 
                    flag=false;
                }
                if(linea.equals("#transitions")){
                    status = 4;
                    flag=false;
                }
                while(status==0 && flag){
                    if(linea.length()==1){
                        simbolos.add(linea.toCharArray()[0]);
                    }else if(linea.contains("-")){
                        String[] parts = linea.split("-");
                        int a = (int)parts[0].toCharArray()[0];
                        int b = (int)parts[1].toCharArray()[0];
                        for (int i=a;i<b+1;i++){
                            simbolos.add((char)i);
                        }
                    }
                    break;
                }
                while(status==1 && flag){
                    Estado nuevoEstado = new Estado();
                    mapEstados.put(linea, nuevoEstado);
                    estados.add(nuevoEstado);
                    break;
                }
                while(status==2 && flag){
                    Estado estado = mapEstados.get(linea);
                    
                    int index = estados.indexOf(estado);
                    estados.get(index).setInicial(true);
                    break;
                }
                while(status==3 && flag){
                    Estado estado = mapEstados.get(linea);
                    int index = estados.indexOf(estado);
                    estados.get(index).setAceptacion(true);
                    break;
                }
                while(status==4 && flag){
                    String[] parts = linea.split(":");
                    Estado estadoOrigen = mapEstados.get(parts[0]);
                    String[] parts2 = parts[1].split(">");
                    char simbolo = parts2[0].toCharArray()[0];
                    String[] parts3 = parts2[1].split(";");
                    List<Estado> ests = new ArrayList<>();
                    for(String st : parts3)ests.add(mapEstados.get(st));
                    HashMap<Character, List<Estado>> transiciones = funcionDeTransicion.getOrDefault(estadoOrigen, new HashMap<>());
                    transiciones.put(simbolo, ests);
                    funcionDeTransicion.put(estadoOrigen, transiciones);
                    break;
                }
                //System.out.println(linea);
            }
              
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta 
            // una excepcion.
            try{                    
               if( null != fr ){   
                  fr.close();     
               }                  
            }catch (Exception e2){ 
               e2.printStackTrace();
            }
        }
        
        Character[] sim = simbolos.toArray(new Character[simbolos.size()]);
        char[] simb = new char[simbolos.size()];
        for(int i=0;i<sim.length;i++) simb[i]=sim[i];
        char[] simbolosAlf = simb;
        Alfabeto alfabeto = new Alfabeto(simbolosAlf);
        this.alfabeto = alfabeto;
        //System.out.println();
        this.estados = estados;
        for(Estado estado:estados){
            if(estado.isInicial())this.setEstadoInicial(estado);
        }
        
        this.setEstadosDeAceptacion(estados.stream().filter(est -> est.isAceptacion())
        .collect(Collectors.toCollection(ArrayList::new)));
        
        this.funcionDeTransicion = funcionDeTransicion;
        //System.out.println(getAlfabeto());
        //System.out.println(estados);
        //System.out.println(getEstadoInicial());
        //System.out.println(getEstadosDeAceptacion());
        //System.out.println(getFuncionDeTransicion());
        //correjirCompletitud
        hallarEstadosInaccesibles();
        hallarEstadosLimbo();
    }

    
    public void imprimirAFNSimplificado(){
        System.out.println("#!dfa");
        System.out.println("#alphabet");
            for(char simbolo : alfabeto.getSimbolos()){
                System.out.println(simbolo);
            }
            System.out.println("#states");
            for(Estado estado : estados){
                System.out.println(estado.toString());
            }
            System.out.println("#initial");
            System.out.println(estadoInicial.toString());
            System.out.println("#accepting");
            for(Estado estado : estadosDeAceptacion){
                System.out.println(estado.toString());
            }
            System.out.println("#transitions");
            //System.out.println(this.getFuncionDeTransicion());
            for(Estado estado : estados){
                //System.out.println(funcionDeTransicion.get(estado));
                for(char simbolo : alfabeto.getSimbolos()){
                    if(funcionDeTransicion.containsKey(estado))
                    if(funcionDeTransicion.get(estado).containsKey(simbolo))
                    //System.out.println(simbolo+":"+funcionDeTransicion.get(estado).get(simbolo));
                    System.out.println(estado.toString()+":"+simbolo+">"+funcionDeTransicion.get(estado).get(simbolo).toString());
                }
            }

            AFGraphGUI gui = new AFGraphGUI(this);
            while(gui.isVisible()){
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    // handle exception
                }
            }
            //System.out.println("11111111111111111");
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


    //Soporte B-4
    private boolean dfsLimbo(Estado estadoActual, Set<Estado> visitados, Set<Estado> noMuertos){
        visitados.add(estadoActual);
        //System.out.println("visitados:"+visitados);
        for (char simbolo: alfabeto.getSimbolos()) {
            List<Estado> estadosSig = transiciones(estadoActual, simbolo);
            if(estadosSig!=null){
                for(Estado estadoSig : estadosSig){
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

    public void exportar(String nombreAarchivo){
        try {
            PrintWriter writer = new PrintWriter(nombreAarchivo+".nfa", "UTF-8");
            writer.println("#!nfa");
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
                for(char simbolo : alfabeto.getSimbolos()){
                    if(funcionDeTransicion.containsKey(estado)){
                        if(funcionDeTransicion.get(estado).containsKey(simbolo)){
                            if (!funcionDeTransicion.get(estado).get(simbolo).contains(null)) {
                                writer.print(estado.toString() + ":" + simbolo + ">");
                                List<Estado> estadosDest = funcionDeTransicion.get(estado).get(simbolo);
                                for (int i = 0; i < estadosDest.size() - 1; i++) writer.print(estadosDest.get(i) + ";");
                                writer.print(estadosDest.get(estadosDest.size() - 1));
                                writer.print("\n");
                            }
                        }
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    //B-8 nombre default
    public void exportar(){
        String nombreAarchivo = "nuevoAFN";
        try {
            PrintWriter writer = new PrintWriter("/"+nombreAarchivo+".nfa", "UTF-8");
            writer.println("#!nfa");
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
                    if(funcionDeTransicion.containsKey(estado)){
                        if(funcionDeTransicion.get(estado).containsKey(simbolo)){
                            if (!funcionDeTransicion.get(estado).get(simbolo).contains(null)) {
                                writer.print(estado.toString() + ":" + simbolo + ">");
                                List<Estado> estadosDest = funcionDeTransicion.get(estado).get(simbolo);
                                for (int i = 0; i < estadosDest.size() - 1; i++) writer.print(estadosDest.get(i) + ";");
                                writer.print(estadosDest.get(estadosDest.size() - 1));
                                writer.print("\n");
                            }
                        }
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public AFD AFNtoAFD(AFN afn){
        HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicionAFD = new HashMap<Estado, HashMap<Character, Estado>>();
        ArrayList<Estado> nuevosEstados = new ArrayList<>();
        Queue<Estado> queue = new LinkedList<>();
        Map<String,Estado> mapEstados = new HashMap<>();
        queue.addAll(afn.getEstados());
        nuevosEstados.addAll(queue);
        System.out.println("TABLA TRANSICIONES");
        while(!queue.isEmpty()){
            Estado estado = queue.poll();
            if(!nuevosEstados.contains(estado))nuevosEstados.add(estado);
            
            for(char simbolo:afn.getAlfabeto().getSimbolos()){
                Estado[] estadosSig = null;
                Estado[] estadosInt = estado.getEstados();
                
                // try {
                //     System.in.read();
                // } catch (Exception e) {
                //     //handle exception
                // }
                if(estadosInt==null || mapEstados.get(Arrays.toString(estadosInt))==null){
                    Set<Estado> setEstados = new HashSet<>();
                    if(afn.transiciones(estado, simbolo)!=null){
                        setEstados.addAll(afn.transiciones(estado, simbolo));
                        List<Estado> temp=new ArrayList<>(setEstados);
                        estadosSig = temp.toArray(new Estado[temp.size()]);
                    }
                }else{
                    Set<Estado> setEstados = new HashSet<>();
                    List<Estado> estadosSigL= new ArrayList<>();
                    for(Estado estad: estadosInt){
                        if(afn.transiciones(estad, simbolo)!=null){
                            setEstados.addAll(afn.transiciones(estad, simbolo));
                        }
                    }
                    estadosSigL = new ArrayList<>(setEstados);
                    estadosSig=estadosSigL.toArray(new Estado[estadosSigL.size()]);
                }
            
                HashMap<Character,Estado> transicion = funcionDeTransicionAFD.getOrDefault(estado, new HashMap<>());
                if(estadosSig!=null){
                    Estado newEstado = null;
                    if(estadosSig!=null)newEstado=mapEstados.get(Arrays.toString(estadosSig));
                    if(estadosSig.length>1 && newEstado==null){
                        newEstado = new Estado(estadosSig);
                        mapEstados.put(Arrays.toString(estadosSig), newEstado);
                        for(Estado est : estadosSig){
                            if(est.isAceptacion()){
                                newEstado.setAceptacion(true);
                            }
                        }
                    }else if(estadosSig.length==1 && newEstado==null){
                        newEstado = estadosSig[0];
                        mapEstados.put(Arrays.toString(estadosSig), newEstado);
                    }
                    if(newEstado!=null)
                    transicion.put(simbolo, newEstado);
                    //System.out.println(estado+"-"+simbolo+"="+transicion.get(simbolo));
                    //System.out.println(nuevosEstados);
                    if(newEstado!=null && !nuevosEstados.contains(newEstado)){
                        queue.offer(newEstado);
                    }
                }
                funcionDeTransicionAFD.put(estado, transicion);
                
                    System.out.print(estado.toString()+"="+funcionDeTransicionAFD.get(estado).get(simbolo)+"  |  ");
            }
            System.out.println();
        }
        
        
        AFD newAfd = new AFD(alfabeto, nuevosEstados, funcionDeTransicionAFD);
        ArrayList<Estado> estadosAcep = new ArrayList<>();
        for (Estado est:newAfd.getEstados()){
            if(est.isAceptacion())estadosAcep.add(est);
        }

        newAfd.setEstadosDeAceptacion(estadosAcep);
        newAfd.setEstadoInicial(afn.estadoInicial);
        return newAfd;
    }

    public boolean procesarCadena(String cadena){
        Estado estadoActual = estadoInicial;
        if(cadena.length()!=0){
            List<Estado> estados = transiciones(estadoActual, cadena.charAt(0));
            for (Estado est : estados){
                if(procesarCadena(est, cadena.substring(1)))return true;
            }
            return false;
        }
        return estadosDeAceptacion.contains(estadoActual);
    }
    private boolean procesarCadena(Estado estado,String cadena){
        Estado estadoActual = estado;
        //System.out.println(cadena.isEmpty()+"-tam:"+cadena.length());
        if(!cadena.isEmpty()){
            //System.out.println(estadoActual+"--"+ cadena.charAt(0)+"="+transiciones(estadoActual, cadena.charAt(0)));
            List<Estado> estados = transiciones(estadoActual, cadena.charAt(0));
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

    public boolean procesarCadenaConDetalles(String cadena){
        Estado estadoActual = estadoInicial;
        List<Estado> estados = transiciones(estadoActual, cadena.charAt(0));
        List<Estado> listaEstados = new ArrayList<>();
        for (Estado est : estados){
            if(procesarCadenaConDetalles(est, cadena.substring(1),listaEstados)){
                listaEstados.add(0, est);
                listaEstados.add(0, estadoActual);
                System.out.println(listaEstados);
                System.out.println(true);
                return true;
            }
        }
        System.out.println(false);
        return false;
    }
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

    public int computarTodosLosProcesamientos(String cadena,String nombreArchivo){
        Estado estadoActual = estadoInicial;
        List<Estado> estados = transiciones(estadoActual, cadena.charAt(0));
        List<Estado> listaEstados = new ArrayList<>();
        File file = null;
        for(int i = 0;i<3;i++){
            if(i==0)file = new File(nombreArchivo+"Abortadas.txt");
            if(i==1)file = new File(nombreArchivo+"Aceptadas.txt");
            if(i==2)file = new File(nombreArchivo+"Rechazadas.txt");
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("Archivo borrado exitosamente.");
                } else {
                    System.out.println("No se pudo borrar el archivo.");
                }
            } else {
                System.out.println("El archivo no existe.");
            }
        }   
        int procesos = computarTodosLosProcesamientos(estadoActual, cadena, nombreArchivo,listaEstados);
        
        
        
        
        return procesos;
    }

    private int computarTodosLosProcesamientos(Estado estado,String cadena,String nombreArchivo,
    List<Estado> listaEstados){
        int procesosInt=0;
        Estado estadoActual = estado;
        List<Estado> listaAct = new ArrayList<>(listaEstados);
        listaAct.add(estadoActual);
        //System.out.println(cadena.isEmpty()+"-tam:"+cadena.length());
        if(!cadena.isEmpty()){
            // System.out.println(estadoActual+"--"+ cadena.charAt(0)+"="+transiciones(estadoActual, cadena.charAt(0)));
            List<Estado> estados = transiciones(estadoActual, cadena.charAt(0));
            if (estados!=null){
                for (Estado est : estados){
                    //System.out.println(est+":"+cadena.substring(1));
                    procesosInt += computarTodosLosProcesamientos(est, cadena.substring(1), nombreArchivo, listaAct);
                }
            }else{
                System.out.println("abortado"+listaAct);
                try {
                    FileWriter writer = new FileWriter(nombreArchivo+"Abortadas.txt", true);
                    writer.write(listaAct.stream().map(Object::toString).collect(Collectors.joining(","))+"\n");
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                procesosInt +=1;
            }
        }else{
            if (estadosDeAceptacion.contains(estadoActual)) {
                System.out.println("aceptado"+listaAct);
                try {
                    FileWriter writer = new FileWriter(nombreArchivo+"Aceptadas.txt", true);
                    writer.write(listaAct.stream().map(Object::toString).collect(Collectors.joining(","))+"\n");
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                procesosInt +=1;
            }else{
                System.out.println("rechazado:"+listaAct);
                try {
                    FileWriter writer = new FileWriter(nombreArchivo+"Rechazadas.txt", true);
                    writer.write(listaAct.stream().map(Object::toString).collect(Collectors.joining(","))+"\n");
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                procesosInt +=1;
            }
        }
        return procesosInt;
    }

    public void ProcesarListaCadenas(ArrayList<String> listaCadenas, String nombreArchivo, boolean imprimirPantalla){
        boolean acept=false;
        String nombreAarchivo = "listaCadenasAFN";
        try {
            PrintWriter writer = new PrintWriter(nombreAarchivo+".txt", "UTF-8");
            for(String cadena:listaCadenas){
                acept=false;
                Estado estadoActual = estadoInicial;
                List<Estado> estados = transiciones(estadoActual, cadena.charAt(0));
                List<Estado> listaEstados = new ArrayList<>();
                writer.print(cadena+"\t");
                if(imprimirPantalla)System.out.print(cadena+"\t");
                for (Estado est : estados){
                    if(procesarListaCadenaConDetalles(est, cadena.substring(1),listaEstados)){
                        listaEstados.add(0, est);
                        listaEstados.add(0, estadoActual);
                        for(int i=0;i<cadena.length();i++){
                            writer.print("-> ("+listaEstados.get(i)+","+cadena.charAt(i)+")");
                            if(imprimirPantalla)System.out.print("-> ("+listaEstados.get(i)+","+cadena.charAt(i)+")");
                        }
                        writer.println("\t"+true);
                        System.out.println("\t"+true);
                        acept=true;
                        break;
                    }
                }
                if(!acept){
                    writer.println("\t"+true);
                    System.out.println("\t"+false);
                }            
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error con archivo");
        } 
    }
    
    private boolean procesarListaCadenaConDetalles(Estado estado,String cadena,List<Estado> listaEstados){
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

    public boolean procesarCadenaConversion(String cadena){
        AFD afd = AFNtoAFD(this);
        return afd.procesarCadena(cadena);
    }

    public boolean procesarCadenaConDetallesConversion(String cadena){
        AFD afd = AFNtoAFD(this);
        return afd.procesarCadenaConDetalles(cadena);
    }

    public void procesarListaCadenasConDetallesConversion(String[] cadenas,String nombreArchivo, boolean imprimirPantalla){
        AFD afd = AFNtoAFD(this);
        afd.procesarListaCadenas(cadenas, nombreArchivo, imprimirPantalla);
    }

    //Gettesrs y Setters
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

    public static void main(String[] args){
        // char[] simbolos= {'0','1'};
        // Alfabeto alf = new Alfabeto(simbolos);
        // ArrayList<Estado> estados= new ArrayList<>();
        // int cantEstados = 3;
        // for(int i=0;i<cantEstados;i++)estados.add(new Estado());
        // HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion = new HashMap<>();
        
        // AFN afn = new AFN(alf,estados,funcionDeTransicion);
        // afn.fillTransitions();
        // afn.setEstadoInicial(afn.getEstados().get(0));
        // ArrayList<Estado> estadosAcep = new ArrayList<>();
        // estadosAcep.add(afn.getEstados().get(2));
        // afn.setEstadosDeAceptacion(estadosAcep);
        // System.out.println(afn.getFuncionDeTransicion());
        // afn.exportar("testAFN2");
        
        AFN afn2 = new AFN("testAFN2AFD");
        afn2.imprimirAFNSimplificado();
        // System.out.println("PROCESAR CADENAS");
        // ArrayList<String> listaCadenas= new ArrayList<>();
        // listaCadenas.add("0001");
        // listaCadenas.add("01111");
        // listaCadenas.add("0101");
        // afn2.ProcesarListaCadenas(listaCadenas,"ListaCadenasAFN",true);  
        
        // System.out.println("ESTADOS INACCESIBLES");
        // System.out.println(afn2.hallarEstadosInaccesibles());
        //System.out.println(afn2.procesarCadenaConversion("101"));
        //System.out.println(afn2.computarTodosLosProcesamientos("001110100101001011011", "afnProcesos"));
        //System.out.println("----------------AFN2-----------");
        //System.out.println(afn2.getFuncionDeTransicion());
        AFD afd = afn2.AFNtoAFD(afn2);
        System.out.println("NUEVA FUNCION DE TRANSICION");
        System.out.println(afd.getFuncionDeTransicion());
        System.out.println("Estados de aceptacion");
        System.out.println(afd.getEstadosDeAceptacion());
        try {
            System.in.read();
        } catch (Exception e) {
            // TODO: handle exception
        }
        afd.verificarCorregirCompletitudAFD();
        afd.hallarEstadosInaccesibles();
        System.out.println("PROCESAR CADENA");
        String[] listaCadenas = {"01001","0100"};
        afd.procesarListaCadenas(listaCadenas,"listacadenasConversion",true);
        //afd.imprimirAFDSimplificado();
        //afd.simplificarAFD(afd);
        // //System.out.println(afd.getFuncionDeTransicion());
        // System.out.println(afn2.procesarCadenaConDetalles("000001"));
        // System.out.println(afn2.procesarCadenaConDetalles("00000111"));
    }
}
