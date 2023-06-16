package TALLER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;


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
    private HashMap<Estado, HashMap<Character,Character>> funcionDeTransicionPila;
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
     HashMap<Estado, HashMap<Character,Character>> funcionDeTransicionPila){
        this.alfabeto=alfabetoCinta;
        this.alfabetoPila=alfabetoPila;
        this.estados=estados;
        this.funcionDeTransicion=funcionDeTransicion;
        this.funcionDeTransicionPila=funcionDeTransicionPila;
    }
    //TODO probar con archivo "probarAFPD"
    public AFPD(String nombreArchivo) {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        
        ArrayList<Estado> estados = new ArrayList<>();
        HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion = new HashMap<>();
        HashMap<Estado, HashMap<Character, Character>> funcionDeTransicionPila = new HashMap<>();
        ArrayList<Character> simbolos = new ArrayList<>();
        ArrayList<Character> simbolosPila = new ArrayList<>();
        Map<String,Estado> mapEstados = new HashMap<>();
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File (nombreArchivo+".dfa");
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);
            
            // Lectura del fichero
            String linea;
            int status = -1;
            boolean flag=false;
            while((linea=br.readLine())!=null){
                flag=true;
                if(linea.equals("#tapeAlphabet")){
                    status = 0;
                    flag=false;
                }
                if(linea.equals("#stackAlphabet")){
                    status = 5;
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
                while(status==5 && flag){
                    if(linea.length()==1){
                        simbolosPila.add(linea.toCharArray()[0]);
                    }else if(linea.contains("-")){
                        String[] parts = linea.split("-");
                        int a = (int)parts[0].toCharArray()[0];
                        int b = (int)parts[1].toCharArray()[0];
                        for (int i=a;i<b+1;i++){
                            simbolosPila.add((char)i);
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
                    this.setEstadoInicial(estado);
                    break;
                }
                while(status==3 && flag){
                    Estado estado = mapEstados.get(linea);
                    int index = estados.indexOf(estado);
                    estados.get(index).setAceptacion(true);
                    System.out.println(estado+"seted aceptation");
                    break;
                }
                while(status==4 && flag){
                    String[] p1 = linea.split(">");
                    String[] parts = p1[0].split(":");
                    Estado estadoOrigen = mapEstados.get(parts[0]);
                    char simbolo = parts[1].toCharArray()[0];
                    char simboloP = parts[2].toCharArray()[0];
                    String[] parts2 = p1[1].split(":");
                    Estado estadoDestino = mapEstados.get(parts2[0]);
                    char simboloP2 = parts2[1].toCharArray()[0];

                    //System.out.println("estDest"+estadoDestino);
                    HashMap<Character, Estado> transiciones = funcionDeTransicion.getOrDefault(estadoOrigen, new HashMap<>());
                    transiciones.put(simbolo, estadoDestino);
                    funcionDeTransicion.put(estadoOrigen, transiciones);

                    HashMap<Character, Character> transicionesP = funcionDeTransicionPila.getOrDefault(estadoOrigen, new HashMap<>());
                    transicionesP.put(simboloP, simboloP2);
                    funcionDeTransicionPila.put(estadoOrigen, transicionesP);
                    break;
                }
                //System.out.println(linea+"estado: "+status+flag);
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
        Character[] simP = simbolosPila.toArray(new Character[simbolos.size()]);
        char[] simbP = new char[simbolosPila.size()];
        for(int i=0;i<simP.length;i++) simbP[i]=simP[i];
        char[] simbolosAlfP = simbP;
        Alfabeto alfabetoP = new Alfabeto(simbolosAlfP);
        this.alfabeto = alfabeto;
        this.alfabetoPila = alfabetoP;
        this.estados = estados;
        this.funcionDeTransicion = funcionDeTransicion;
        //System.out.println(this.getFuncionDeTransicion());
        //correjirCompletitud
        this.setEstadosDeAceptacion(estados.stream().filter(est -> est.isAceptacion())
        .collect(Collectors.toCollection(ArrayList::new)));       
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
    //TODO probar
    public boolean procesarCadenaConDetalles(String cadena,Character parametro, Character operacion) {
        Estado estadoActual = estadoInicial;
        System.out.println("Cadena: "+cadena);
        System.out.print("("+estadoActual+","+cadena+","+pila+")");
        for(int i=0;i<cadena.length();i++){
            if(!modificarPila(parametro, operacion)){
                System.out.println("Aborted");
                return false;
            }
            estadoActual = transicion(estadoActual, cadena.charAt(i));
            System.out.print("("+estadoActual+","+cadena.substring(i, cadena.length())+","+pila+")");
        }
        if(estadosDeAceptacion.contains(estadoActual) && pila.empty()){
            System.out.println("Accepted");
            return true;
        }else{
            System.out.println("Rejected");
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

    public void setEstadosDeAceptacion(ArrayList<Estado> estadosDeAceptacion) {
        for(Estado estado:estadosDeAceptacion)estado.setAceptacion(true);
        this.estadosDeAceptacion = estadosDeAceptacion;
    }

    public void setEstadoInicial(Estado estadoInicial) {
        estadoInicial.setInicial(true);
        this.estadoInicial = estadoInicial;
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
