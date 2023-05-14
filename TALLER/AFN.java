package TALLER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

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
    private HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTrancision;
    private ArrayList<Estado> estadosLimbo = new ArrayList<>(
        estados.stream().filter(p -> p.isLimbo()).collect(Collectors.toList())
    );
    private ArrayList<Estado> estadosInaccesibles = new ArrayList<>(
        estados.stream().filter(p -> !p.isAccesible()).collect(Collectors.toList())
    );


    //Metodos
    //B-1
    public AFN(Alfabeto alfabeto, ArrayList<Estado> estados, HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTrancision) {
        this.alfabeto = alfabeto;
        this.estados = estados;
        this.funcionDeTrancision = funcionDeTrancision;
    }
    //B-1
    public AFN(Alfabeto alfabeto) {
        this.alfabeto = alfabeto;
        this.funcionDeTrancision = new HashMap<>();
    }
    //B-1
    public AFN() {
        this.funcionDeTrancision = new HashMap<>();
    }
    public AFN(String nombreArchivo) {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        
        ArrayList<Estado> estados = new ArrayList<>();
        HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTrancision = new HashMap<>();
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
                    HashMap<Character, List<Estado>> transiciones = funcionDeTrancision.getOrDefault(estadoOrigen, new HashMap<>());
                    transiciones.put(simbolo, ests);
                    funcionDeTrancision.put(estadoOrigen, transiciones);
                    break;
                }
                System.out.println(linea);
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
        
        String st = simbolos.toString();
        char[] simbolosAlf = st.toCharArray();
        Alfabeto alfabeto = new Alfabeto(simbolosAlf);
        this.alfabeto = alfabeto;
        this.estados = estados;
        for(Estado estado:estados){
            if(estado.isInicial())this.setEstadoInicial(estado);
        }
        
        this.setEstadosDeAceptacion(estados.stream().filter(est -> est.isAceptacion())
        .collect(Collectors.toCollection(ArrayList::new)));
        
        this.funcionDeTrancision = funcionDeTrancision;
        System.out.println(getAlfabeto());
        System.out.println(estados);
        System.out.println(getEstadoInicial());
        System.out.println(getEstadosDeAceptacion());
        System.out.println(getFuncionDeTrancision());
        //correjirCompletitud
        hallarEstadosInaccesibles();
        hallarEstadosLimbo();
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
        if (!funcionDeTrancision.get(estadoOrigen).containsKey(simbolo)){
            return null;
        }
        return funcionDeTrancision.get(estadoOrigen).get(simbolo);
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
        System.out.println("visitados:"+visitados);
        for (char simbolo: alfabeto.getSimbolos()) {
            List<Estado> estadosSig = transiciones(estadoActual, simbolo);
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
                List<Estado> estadosSig = transiciones(estadoActual, simbolo);
                for(Estado estadoSig: estadosSig){
                    if (estadoSig != null && !accesibles.contains(estadoSig)) {
                        accesibles.add(estadoSig);
                        queue.offer(estadoSig);
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
            System.out.println("tramsitions");
            for(Estado estado : estados){
                for(char simbolo : alfabeto.getSimbolos()){
                    if(!funcionDeTrancision.get(estado).get(simbolo).contains(null)){
                        writer.print(estado.toString()+":"+simbolo+">");
                        List<Estado> estadosDest = funcionDeTrancision.get(estado).get(simbolo);
                        System.out.println(funcionDeTrancision.get(estado).get(simbolo));
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
                    writer.println(simbolo+">"+funcionDeTrancision.get(estado).get(simbolo).toString());
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
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

    public static void main(String[] args){
        /*char[] simbolos= {'0','1'};
        Alfabeto alf = new Alfabeto(simbolos);
        ArrayList<Estado> estados= new ArrayList<>();
        int cantEstados = 3;
        for(int i=0;i<cantEstados;i++)estados.add(new Estado());
        HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTrancision = new HashMap<>();
        
        AFN afn = new AFN(alf,estados,funcionDeTrancision);
        afn.fillTransitions();
        afn.setEstadoInicial(afn.getEstados().get(0));
        ArrayList<Estado> estadosAcep = new ArrayList<>();
        estadosAcep.add(afn.getEstados().get(2));
        afn.setEstadosDeAceptacion(estadosAcep);
        System.out.println(afn.getFuncionDeTrancision());
        afn.exportar("testAFN");
        */
        AFN afn2 = new AFN("testAFN");
        System.out.println("----------------AFN2-----------");
        System.out.println(afn2.getFuncionDeTrancision());
    }

}
