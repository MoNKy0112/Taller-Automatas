package TALLER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
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
    private HashMap<Estado, HashMap<Character,ArrayList<Character[]>>> funcionDeTransicionPila;
    //El index del array 0 es para el hashmap char estado de las transiciones de estado
    //El index del array 0 es para el hashmap char char para los movimientos de la pila
    private Stack pila = new Stack<Character>();
    //<>
    public AFPD(Alfabeto alfabetoCinta,Alfabeto alfabetoPila, ArrayList<Estado> estados,
     HashMap<Estado, HashMap<Character,Estado>> funcionDeTransicion,
     HashMap<Estado, HashMap<Character,ArrayList<Character[]>>> funcionDeTransicionPila){
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
        HashMap<Estado, HashMap<Character, ArrayList<Character[]>>> funcionDeTransicionPila = new HashMap<>();
        ArrayList<Character> simbolos = new ArrayList<>();
        ArrayList<Character> simbolosPila = new ArrayList<>();
        Map<String,Estado> mapEstados = new HashMap<>();
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File (nombreArchivo+".dpda");
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

                    HashMap<Character, ArrayList<Character[]>> transicionesP = funcionDeTransicionPila.getOrDefault(estadoOrigen, new HashMap<>());
                    ArrayList<Character[]> simbsPila = new ArrayList<>();
                    Character[] aux = {simboloP,simboloP2};
                    simbsPila.add(aux);
                    transicionesP.put(simbolo, simbsPila);
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
        Character[] simP = simbolosPila.toArray(new Character[simbolosPila.size()]);
        char[] simbP = new char[simbolosPila.size()];
        for(int i=0;i<simP.length;i++){
            simbP[i]=simP[i];
        } 
        char[] simbolosAlfP = simbP;
        Alfabeto alfabetoP = new Alfabeto(simbolosAlfP);
        this.alfabeto = alfabeto;
        this.alfabetoPila = alfabetoP;
        this.estados = estados;
        this.funcionDeTransicion = funcionDeTransicion;
        this.funcionDeTransicionPila = funcionDeTransicionPila;
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
    public boolean procesarCadena(String cadena) {
        Estado estadoActual = estadoInicial;
        Character parametro=null,operacion = null;
        pila.clear();
        for(int i=0;i<cadena.length();i++){
            if(funcionDeTransicion.get(estadoActual).containsKey('$')){
                ArrayList<Character[]> lista = funcionDeTransicionPila.get(estadoActual).get('$');
                for(int j=0;j<lista.size();j++){
                    parametro=lista.get(j)[0];
                    operacion=lista.get(j)[1];
                    if(modificarPila(parametro, operacion)){ 
                        break;
                    }
                    if(j==lista.size()-1){
                        return false;
                    }
                }
                estadoActual = transicion(estadoActual, '$');
                i--;
            }else{
                ArrayList<Character[]> lista = funcionDeTransicionPila.get(estadoActual).get(cadena.charAt(i));
                for(int j=0;j<lista.size();j++){
                    parametro=lista.get(j)[0];
                    operacion=lista.get(j)[1];
                    if(modificarPila(parametro, operacion)){
                        break;
                    }
                    if(j==lista.size()-1){
                        return false;
                    }
                }
                estadoActual = transicion(estadoActual, cadena.charAt(i));
            }
        }
        if(estadosDeAceptacion.contains(estadoActual) && pila.empty()){
            return true;
        }else{
            return false;
        }
    }
    //TODO probar
    public boolean procesarCadenaConDetalles(String cadena) {
        Estado estadoActual = estadoInicial;
        Character parametro=null,operacion = null;
        pila.clear();
        System.out.println("Cadena: "+cadena);
        System.out.print("("+estadoActual+","+cadena+","+pila+")");
        for(int i=0;i<cadena.length();i++){
            if(funcionDeTransicion.get(estadoActual).containsKey('$')){
                ArrayList<Character[]> lista = funcionDeTransicionPila.get(estadoActual).get('$');
                for(int j=0;j<lista.size();j++){
                    parametro=lista.get(j)[0];
                    operacion=lista.get(j)[1];
                    if(modificarPila(parametro, operacion)){ 
                        break;
                    }
                    if(j==lista.size()-1){
                        System.out.println(">>Aborted");
                        return false;
                    }
                }
                estadoActual = transicion(estadoActual, '$');
                System.out.print("->("+estadoActual+","+cadena.substring(i+1, cadena.length())+","+pila+")");
                i--;
            }else{
                ArrayList<Character[]> lista = funcionDeTransicionPila.get(estadoActual).get(cadena.charAt(i));
                for(int j=0;j<lista.size();j++){
                    parametro=lista.get(j)[0];
                    operacion=lista.get(j)[1];
                    if(modificarPila(parametro, operacion)){
                        break;
                    }
                    if(j==lista.size()-1){
                        System.out.println(">>Aborted");
                        return false;
                    }
                }
                estadoActual = transicion(estadoActual, cadena.charAt(i));
                System.out.print("->("+estadoActual+","+cadena.substring(i+1, cadena.length())+","+pila+")");
            }
        }
        if(estadosDeAceptacion.contains(estadoActual) && pila.empty()){
            System.out.println(">>Accepted");
            return true;
        }else{
            System.out.println(">>Rejected");
            return false;
        }
    }

    public void procesarListaCadenas(ArrayList<String> listaCadenas,String nombreArchivo,boolean imprimirPantalla){
        boolean jump=false;
        if(nombreArchivo==null){
            nombreArchivo="defaultProcesarListaCadenasAFPD";
        }
        Estado estadoActual = estadoInicial;
        try {
            PrintWriter writer = new PrintWriter(nombreArchivo+".txt", "UTF-8");
            for(String cadena : listaCadenas){
                jump=false;
                estadoActual = estadoInicial;
                writer.print(cadena+"\t");
                if(imprimirPantalla)System.out.print(cadena+"\t");
                Character parametro=null,operacion = null;
                pila.clear();
                //System.out.println("Cadena: "+cadena);
                writer.print("("+estadoActual+","+cadena+","+pila+")");
                if(imprimirPantalla)System.out.print("("+estadoActual+","+cadena+","+pila+")");
                for(int i=0;i<cadena.length();i++){
                    if(funcionDeTransicion.get(estadoActual).containsKey('$')){
                        ArrayList<Character[]> lista = funcionDeTransicionPila.get(estadoActual).get('$');
                        for(int j=0;j<lista.size();j++){
                            parametro=lista.get(j)[0];
                            operacion=lista.get(j)[1];
                            if(modificarPila(parametro, operacion)){ 
                                break;
                            }
                            if(j==lista.size()-1){
                                writer.println("\t"+"no");
                                if(imprimirPantalla)System.out.println("\tno");
                                jump=true;
                                break;
                            }
                        }
                        if(jump)break;
                        estadoActual = transicion(estadoActual, '$');
                        writer.print("->("+estadoActual+","+cadena.substring(i+1, cadena.length())+","+pila+")");
                        if(imprimirPantalla)System.out.print("->("+estadoActual+","+cadena.substring(i+1, cadena.length())+","+pila+")");
                        i--;
                    }else{
                        ArrayList<Character[]> lista = funcionDeTransicionPila.get(estadoActual).get(cadena.charAt(i));
                        for(int j=0;j<lista.size();j++){
                            parametro=lista.get(j)[0];
                            operacion=lista.get(j)[1];
                            if(modificarPila(parametro, operacion)){
                                break;
                            }
                            if(j==lista.size()-1){
                                writer.println("\t"+"no");
                                if(imprimirPantalla)System.out.println("\tno");
                                jump=true;
                                break;
                            }
                        }
                        if(jump)break;
                        estadoActual = transicion(estadoActual, cadena.charAt(i));
                        writer.print("->("+estadoActual+","+cadena.substring(i+1, cadena.length())+","+pila+")");
                        if(imprimirPantalla)System.out.print("->("+estadoActual+","+cadena.substring(i+1, cadena.length())+","+pila+")");
                    }
                }
                if(jump)continue;
                if(estadosDeAceptacion.contains(estadoActual) && pila.empty()){
                    writer.println("\tyes");
                    if(imprimirPantalla)System.out.println("\tyes");
                    continue;
                }else{
                    writer.println("\tno");
                    if(imprimirPantalla)System.out.println("\tno");
                    continue;
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void crearTransicion(Estado estadoOrigen,Estado estadoDestino,
    Character simbL,Character simbPila1,Character simbPila2){
        //crea la transicion entre estados con determinado simbolo
        if(!verificarDeterminismo(estadoOrigen, simbL, simbPila1))return;
        HashMap<Character, Estado> transiciones = funcionDeTransicion.getOrDefault(estadoOrigen, new HashMap<>());
        transiciones.put(simbL, estadoDestino);
        funcionDeTransicion.put(estadoOrigen, transiciones);
        //crea la transicion de pila con determinado simbolo donde los indices pares del
        //ArrayList son lo que lee en el tope de pila y los impares son sus respectivas operaciones
        HashMap<Character,ArrayList<Character[]>> transicionesPila = funcionDeTransicionPila.getOrDefault(estadoOrigen, new HashMap<>());
        ArrayList<Character[]> aux1=new ArrayList<>();
        if(transicionesPila.containsKey(simbL)){
            aux1=transicionesPila.get(simbL);
        }
        Character[] aux = {simbPila1, simbPila2};
        aux1.add(aux);
        transicionesPila.put(simbL,aux1);
        funcionDeTransicionPila.put(estadoOrigen, transicionesPila);
    }

    private boolean verificarDeterminismo(Estado estadoOrigen,Character simbL,Character simbPila1){
        if((funcionDeTransicion.getOrDefault(estadoOrigen, new HashMap<>()).containsKey('$') && !simbL.equals('$'))
        || (!funcionDeTransicion.getOrDefault(estadoOrigen, new HashMap<>()).isEmpty() && simbL.equals('$'))){
            return false;
        }
        if(funcionDeTransicionPila.getOrDefault(estadoOrigen, new HashMap<>()).containsKey(simbL)){
            if(funcionDeTransicionPila.getOrDefault(estadoOrigen, new HashMap<>()).get(simbL).get(0)[0].equals('$')
            && !simbPila1.equals('$') || !funcionDeTransicionPila.getOrDefault(estadoOrigen, new HashMap<>()).get(simbL).get(0)[0].equals('$')
            && simbPila1.equals('$')) return false;
        }
        return true;
    }

    public void exportar(String nombreArchivo){
        if(nombreArchivo==null){
            nombreArchivo="probarAFPD2";
        }
        try {
            PrintWriter writer = new PrintWriter(nombreArchivo+".dpda", "UTF-8");
            writer.println("#!dpda");
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
            writer.println("#tapeAlphabet");
            for(char simbolo : alfabeto.getSimbolos()){
                writer.println(simbolo);
            }
            writer.println("#stackAlphabet");
            for(char simbolo : alfabetoPila.getSimbolos()){
                writer.println(simbolo);
            }
            writer.println("#transitions");
            for(Estado estado : estados){
                for(char simbolo : alfabeto.getSimbolos()){
                    if(funcionDeTransicion.containsKey(estado) && funcionDeTransicionPila.containsKey(estado)){
                        if(funcionDeTransicion.get(estado).containsKey(simbolo) && funcionDeTransicionPila.get(estado).containsKey(simbolo)){
                            writer.println(estado.toString()+":"+simbolo+":"
                            +funcionDeTransicionPila.get(estado).get(simbolo).get(0)[0]+
                            ">"+funcionDeTransicion.get(estado).get(simbolo).toString()
                            +":"+funcionDeTransicionPila.get(estado).get(simbolo).get(0)[1]);
                        }
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toStringAFPD(){
        System.out.println("#!dpda");
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
        System.out.println("#tapeAlphabet");
        for(char simbolo : alfabeto.getSimbolos()){
            System.out.println(simbolo);
        }
        System.out.println("#stackAlphabet");
        for(char simbolo : alfabetoPila.getSimbolos()){
            System.out.println(simbolo);
        }
        System.out.println("#transitions");
        for(Estado estado : estados){
            for(char simbolo : alfabeto.getSimbolos()){
                if(funcionDeTransicion.containsKey(estado) && funcionDeTransicionPila.containsKey(estado)){
                    if(funcionDeTransicion.get(estado).containsKey(simbolo) && funcionDeTransicionPila.get(estado).containsKey(simbolo)){
                        System.out.println(estado.toString()+":"+simbolo+":"
                        +funcionDeTransicionPila.get(estado).get(simbolo).get(0)[0]+
                        ">"+funcionDeTransicion.get(estado).get(simbolo).toString()
                        +":"+funcionDeTransicionPila.get(estado).get(simbolo).get(0)[1]);
                    }
                }
            }
        }
    }

    public Estado transicion(Estado estadoOrigen, Character simbolo){
        if (!this.alfabeto.contieneSimbolo(simbolo) && !simbolo.equals('$')){
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
        //PRUEBAFACTOR2
        // AFPD afpd2 = new AFPD("probarAFPD");
        // afpd2.toStringAFPD();
        //PRUEBAFACTOR3
        // AFPD afpd2 = new AFPD("probarAFPD");
        // afpd2.toStringAFPD();
        ////PRUEBAFACTOR4
        // char[] simbolos = {'a','b'};
        // char[] simbolos2 = {'A'};
        // Alfabeto alf = new Alfabeto(simbolos);
        // Alfabeto alfp = new Alfabeto(simbolos2);
        // int numEstados = 3;
        // ArrayList<Estado> estados = new ArrayList<Estado>();
        // for (int i = 0; i < numEstados; i++) {
        //     estados.add(new Estado());
        // }  
        // HashMap<Estado, HashMap<Character ,Estado>> funcionDeTransicion = new HashMap<>();
        // HashMap<Estado, HashMap<Character,ArrayList<Character[]>>> funcionDeTransicionPila= new HashMap<>();
        // AFPD afpd = new AFPD(alf, alfp, estados, funcionDeTransicion, funcionDeTransicionPila);
        // afpd.setEstadoInicial(estados.get(0));
        // ArrayList<Estado> estadosAcept = new ArrayList<>();
        // estadosAcept.add(estados.get(1));
        // estadosAcept.add(estados.get(0));
        // afpd.setEstadosDeAceptacion(estadosAcept);
        // afpd.crearTransicion(estados.get(0), estados.get(0), 'a', '$', 'A');
        // afpd.crearTransicion(estados.get(0), estados.get(1), 'b', 'A', '$');
        // afpd.crearTransicion(estados.get(1), estados.get(1), 'a', '$', 'A');
        // afpd.exportar("probarAFPD2");
        // afpd.toStringAFPD();
        ////PRUEBAFACTOR5
        // AFPD afpd = new AFPD("probarAFPD");
        // System.out.println(afpd.procesarCadenaConDetalles("aaabbb"));
        ////PRUEBAFACTOR6
        // AFPD afpd = new AFPD("probarAFPD");
        // ArrayList<String> cadenas = new ArrayList<>();
        // cadenas.add("aabb");
        // cadenas.add("aaabb");
        // afpd.procesarListaCadenas(cadenas, null, true);
        ////PRUEBAFACTOR8
        AFPD afpd = new AFPD("probarAFPD");
        ArrayList<String> cadenas = new ArrayList<>();
        cadenas.add("aa");
        cadenas.add("ab");
        afpd.procesarListaCadenas(cadenas, "cadenasAFPD", true);
    }
}
