package TALLER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MT {
    private Alfabeto alfabetoEntrada = new Alfabeto(null);
    private Alfabeto alfabetoCinta = new Alfabeto(null);
    private ArrayList<Estado> estados = new ArrayList<Estado>();
    private Estado estadoInicial = estados.stream().filter(p -> p.isInicial()).findFirst().orElse(
        estados.stream().findFirst().orElse(null)
    );
    private ArrayList<Estado> estadosDeAceptacion = new ArrayList<>(
        estados.stream().filter(p -> p.isAceptacion()).collect(Collectors.toList())
    );
    private HashMap<Estado, HashMap<Character,Estado>> funcionDeTransicion;
    private HashMap<Estado, HashMap<Character,Character[]>> funcionDeTransicionCinta;


    public MT(Alfabeto alfabetoEntrada,Alfabeto alfabetoCinta, ArrayList<Estado> estados,
     HashMap<Estado, HashMap<Character ,Estado>> funcionDeTransicion,
     HashMap<Estado, HashMap<Character,Character[]>> funcionDeTransicionCinta){
        this.alfabetoEntrada=alfabetoEntrada;
        this.alfabetoCinta=alfabetoCinta;
        this.estados=estados;
        this.funcionDeTransicion=funcionDeTransicion;
        this.funcionDeTransicionCinta=funcionDeTransicionCinta;
    }

    public MT(String nombreArchivo) {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        
        ArrayList<Estado> estados = new ArrayList<>();
        HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion = new HashMap<>();
        HashMap<Estado, HashMap<Character, Character[]>> funcionDeTransicionCinta = new HashMap<>();
        ArrayList<Character> simbolosEntrada = new ArrayList<>();
        ArrayList<Character> simbolosCinta = new ArrayList<>();
        Map<String,Estado> mapEstados = new HashMap<>();

        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File (nombreArchivo+".tm");
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);
            
            // Lectura del fichero
            String linea;
            int status = -1;
            boolean flag=false;
            while((linea=br.readLine())!=null){
                flag=true;
                if(linea.equals("#inputAlphabet")){
                    status = 0;
                    flag=false;
                }
                if(linea.equals("#tapeAlphabet")){
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
                        simbolosEntrada.add(linea.toCharArray()[0]);
                    }else if(linea.contains("-")){
                        String[] parts = linea.split("-");
                        int a = (int)parts[0].toCharArray()[0];
                        int b = (int)parts[1].toCharArray()[0];
                        for (int i=a;i<b+1;i++){
                            simbolosEntrada.add((char)i);
                        }
                    }
                    break;
                }
                while(status==5 && flag){
                    if(linea.length()==1){
                        simbolosCinta.add(linea.toCharArray()[0]);
                    }else if(linea.contains("-")){
                        String[] parts = linea.split("-");
                        int a = (int)parts[0].toCharArray()[0];
                        int b = (int)parts[1].toCharArray()[0];
                        for (int i=a;i<b+1;i++){
                            simbolosCinta.add((char)i);
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
                    Character[] simbolos = new Character[2];
                    String[] p1 = linea.split("\\?");
                    String[] parts = p1[0].split(":");
                    Estado estadoOrigen = mapEstados.get(parts[0]);
                    char simbolo = parts[1].toCharArray()[0];
                    
                    String[] parts2 = p1[1].split(":");
                    Estado estadoDestino = mapEstados.get(parts2[0]);
                    simbolos[0] = parts2[1].toCharArray()[0];
                    simbolos[1] = parts2[2].toCharArray()[0];

                    //System.out.println("estDest"+estadoDestino);
                    HashMap<Character, Estado> transiciones = funcionDeTransicion.getOrDefault(estadoOrigen, new HashMap<>());
                    transiciones.put(simbolo, estadoDestino);
                    funcionDeTransicion.put(estadoOrigen, transiciones);
                    HashMap<Character, Character[]> transicionesCinta = funcionDeTransicionCinta.getOrDefault(estadoOrigen, new HashMap<>());
                    transicionesCinta.put(simbolo, simbolos);
                    funcionDeTransicionCinta.put(estadoOrigen, transicionesCinta);
                    

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
        
        Character[] sim = simbolosEntrada.toArray(new Character[simbolosEntrada.size()]);
        char[] simb = new char[simbolosEntrada.size()];
        for(int i=0;i<sim.length;i++) simb[i]=sim[i];
        char[] simbolosAlf = simb;
        Alfabeto alfabeto = new Alfabeto(simbolosAlf);
        Character[] simP = simbolosCinta.toArray(new Character[simbolosCinta.size()]);
        char[] simbP = new char[simbolosCinta.size()];
        for(int i=0;i<simP.length;i++) simbP[i]=simP[i];
        char[] simbolosAlfP = simbP;
        Alfabeto alfabetoC = new Alfabeto(simbolosAlfP);
        this.alfabetoEntrada = alfabeto;
        this.alfabetoCinta = alfabetoC;
        this.estados = estados;
        this.funcionDeTransicion = funcionDeTransicion;
        this.funcionDeTransicionCinta = funcionDeTransicionCinta;
        //System.out.println(this.getFuncionDeTransicion());
        //correjirCompletitud
        this.setEstadosDeAceptacion(estados.stream().filter(est -> est.isAceptacion())
        .collect(Collectors.toCollection(ArrayList::new)));       
    }
    //TODO probar todo MT
    public int movimientoCinta(Character desp){
        if(desp.equals('-')){
            return 0;
        }else if(desp.equals('<')){
            return -1;
        }else if(desp.equals('>')){
            return 1;
        }else{
            throw new IllegalArgumentException("El simbolo "+desp+" no es un desplazamiento permitido");
        }
    }

    public boolean procesarCadena(String cadena){
        //char[] cad = cadena.toCharArray();
        Estado estadoActual = estadoInicial;
        int pos=0;
        while(!estadosDeAceptacion.contains(estadoActual)){
            if(pos==-1){
                cadena='!'+cadena.substring(0, cadena.length());
                pos++;
            }else if(pos==cadena.length()){
                cadena=cadena.substring(0, cadena.length())+'!';
            }
            char ch=cadena.substring(pos,pos+1).toCharArray()[0];
            if(!funcionDeTransicion.get(estadoActual).containsKey(ch)
            || !funcionDeTransicionCinta.get(estadoActual).containsKey(ch)){
                return false;
            }else{
                Character [] simbolos= funcionDeTransicionCinta.get(estadoActual).get(ch);
                estadoActual=funcionDeTransicion.get(estadoActual).get(ch);
                cadena=cadena.substring(0, pos)+simbolos[0]+cadena.substring(pos+1, cadena.length());
                pos=pos+movimientoCinta(simbolos[1]);
            }
        }
        return true;
    }

    public boolean procesarCadenaConDetalle(String cadena){
        //char[] cad = cadena.toCharArray();
        Estado estadoActual = estadoInicial;
        int pos=0;
        System.out.print("("+estadoActual+")"+cadena+" -> ");
        while(!estadosDeAceptacion.contains(estadoActual)){
            char ch=cadena.substring(pos,pos+1).toCharArray()[0];
            if(!funcionDeTransicion.get(estadoActual).containsKey(ch)
            || !funcionDeTransicionCinta.get(estadoActual).containsKey(ch)){
                return false;
            }else{
                Character [] simbolos= funcionDeTransicionCinta.get(estadoActual).get(ch);
                estadoActual=funcionDeTransicion.get(estadoActual).get(ch);
                cadena=cadena.substring(0, pos)+simbolos[0]+cadena.substring(pos+1, cadena.length());
                pos=pos+movimientoCinta(simbolos[1]);
            }
            if(pos==-1){
                cadena='!'+cadena.substring(0, cadena.length());
                pos++;
            }else if(pos==cadena.length()){
                cadena=cadena.substring(0, cadena.length())+'!';
            }
            System.out.print(cadena.substring(0, pos)+"("+estadoActual+")"+cadena.substring(pos,cadena.length())+" -> ");
        }
        return true;
    }

    public String procesarFuncion(String cadena){
        //char[] cad = cadena.toCharArray();
        Estado estadoActual = estadoInicial;
        int pos=0;
        while(!estadosDeAceptacion.contains(estadoActual)){
            if(pos==-1){
                cadena='!'+cadena.substring(0, cadena.length());
                pos++;
            }else if(pos==cadena.length()){
                cadena=cadena.substring(0, cadena.length())+'!';
            }
            char ch=cadena.substring(pos,pos+1).toCharArray()[0];
            //System.out.println(cadena+":"+pos+":"+estadoActual);
            if(!funcionDeTransicion.get(estadoActual).containsKey(ch)
            || !funcionDeTransicionCinta.get(estadoActual).containsKey(ch)){
                return cadena.substring(0, pos)+"("+estadoActual+")"+cadena.substring(pos,cadena.length());
            }else{
                Character [] simbolos= funcionDeTransicionCinta.get(estadoActual).get(ch);
                estadoActual=funcionDeTransicion.get(estadoActual).get(ch);
                cadena=cadena.substring(0, pos)+simbolos[0]+cadena.substring(pos+1, cadena.length());
                pos=pos+movimientoCinta(simbolos[1]);
            }
        }
        return cadena.substring(0, pos)+"("+estadoActual+")"+cadena.substring(pos,cadena.length());
    }

    public void procesarListaCadenas(ArrayList<String> listaCadenas,String nombreArchivo,
    boolean imprimirPantalla){
        boolean jump=false;
        if(nombreArchivo==null){
            nombreArchivo="defaultProcesarListaCadenasMT";
        }
        Estado estadoActual = estadoInicial;
        try {
            PrintWriter writer = new PrintWriter(nombreArchivo+".txt", "UTF-8");
            for(String cadena : listaCadenas){
                jump=false;
                estadoActual = estadoInicial;
                writer.print(cadena+"\t");
                if(imprimirPantalla)System.out.print(cadena+"\t");
                int pos=0;
                while(!estadosDeAceptacion.contains(estadoActual)){
                    if(pos==-1){
                        cadena='!'+cadena.substring(0, cadena.length());
                        pos++;
                    }else if(pos==cadena.length()){
                        cadena=cadena.substring(0, cadena.length())+'!';
                    }
                    char ch=cadena.substring(pos,pos+1).toCharArray()[0];
                    if(!funcionDeTransicion.get(estadoActual).containsKey(ch)
                    || !funcionDeTransicionCinta.get(estadoActual).containsKey(ch)){
                        writer.println(cadena.substring(0, pos)+"("+estadoActual+")"+cadena.substring(pos,cadena.length())+"\tNO");
                        if(imprimirPantalla)System.out.println(cadena.substring(0, pos)
                        +"("+estadoActual+")"+cadena.substring(pos,cadena.length())+"\tNO");
                        jump=true;
                        break;
                    }else{
                        Character [] simbolos= funcionDeTransicionCinta.get(estadoActual).get(ch);
                        estadoActual=funcionDeTransicion.get(estadoActual).get(ch);
                        cadena=cadena.substring(0, pos)+simbolos[0]+cadena.substring(pos+1, cadena.length());
                        pos=pos+movimientoCinta(simbolos[1]);
                    }
                }
                if(!jump){
                    writer.println(cadena.substring(0, pos)+"("+estadoActual+")"+cadena.substring(pos,cadena.length())+"\tYES");
                    if(imprimirPantalla)System.out.println(cadena.substring(0, pos)
                    +"("+estadoActual+")"+cadena.substring(pos,cadena.length())+"\tYES");
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportar(String nombreArchivo){
        if(nombreArchivo==null){
            nombreArchivo="probarMT";
        }
        try {
            PrintWriter writer = new PrintWriter(nombreArchivo+".tm", "UTF-8");
            writer.println("#!tm");
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
            writer.println("#inputAlphabet");
            for(char simbolo : alfabetoEntrada.getSimbolos()){
                writer.println(simbolo);
            }
            writer.println("#tapeAlphabet");
            for(char simbolo : alfabetoCinta.getSimbolos()){
                writer.println(simbolo);
            }
            writer.println("#transitions");
            for(Estado estado : estados){
                for(char simbolo : alfabetoCinta.getSimbolos()){
                    if(funcionDeTransicion.containsKey(estado) && funcionDeTransicionCinta.containsKey(estado)){
                        if(funcionDeTransicion.get(estado).containsKey(simbolo) && funcionDeTransicionCinta.get(estado).containsKey(simbolo)){
                            writer.println(estado.toString()+":"+simbolo+
                            "?"+funcionDeTransicion.get(estado).get(simbolo).toString()
                            +":"+funcionDeTransicionCinta.get(estado).get(simbolo)[0]
                            +":"+funcionDeTransicionCinta.get(estado).get(simbolo)[1]);
                        }
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toStringMT(){
        System.out.println("#!tm");
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
        System.out.println("#inputAlphabet");
        for(char simbolo : alfabetoEntrada.getSimbolos()){
            System.out.println(simbolo);
        }
        System.out.println("#tapeAlphabet");
        for(char simbolo : alfabetoCinta.getSimbolos()){
            System.out.println(simbolo);
        }
        System.out.println("#transitions");
        for(Estado estado : estados){
            for(char simbolo : alfabetoCinta.getSimbolos()){
                if(funcionDeTransicion.containsKey(estado) && funcionDeTransicionCinta.containsKey(estado)){
                    if(funcionDeTransicion.get(estado).containsKey(simbolo) && funcionDeTransicionCinta.get(estado).containsKey(simbolo)){
                        System.out.println(estado.toString()+":"+simbolo+
                        "?"+funcionDeTransicion.get(estado).get(simbolo).toString()
                        +":"+funcionDeTransicionCinta.get(estado).get(simbolo)[0]
                        +":"+funcionDeTransicionCinta.get(estado).get(simbolo)[1]);
                    }
                }
            }
        }
    }
    //Crea transicion determinada
    public void crearTransicion(Estado estadoOrigen,Estado estadoDestino,
    Character simbL,Character simbE,Character desp){
        Character[] simbolos = {simbE,desp};
        HashMap<Character, Estado> transiciones = funcionDeTransicion.getOrDefault(estadoOrigen, new HashMap<>());
        transiciones.put(simbL, estadoDestino);
        funcionDeTransicion.put(estadoOrigen, transiciones);
        HashMap<Character, Character[]> transicionesCinta = funcionDeTransicionCinta.getOrDefault(estadoOrigen, new HashMap<>());
        transicionesCinta.put(simbL, simbolos);
        funcionDeTransicionCinta.put(estadoOrigen, transicionesCinta);
    }

    //Retorna el estado al cual debe llegar segun delta con un simbolo desde Q estado
    public Estado transicion(Estado estadoOrigen, Character simbolo){
        for(int i=0;i<3;i++){
            if (!this.alfabetoCinta.contieneSimbolo(simbolo) && simbolo.equals('!')){
                throw new IllegalArgumentException("El simbolo "+simbolo+" no pertenece al alfabeto del automata");
            }
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

    public void setEstadoInicial(Estado estadoInicial) {
        estadoInicial.setInicial(true);
        this.estadoInicial = estadoInicial;
    }

    public void setEstadosDeAceptacion(ArrayList<Estado> estadosDeAceptacion) {
        for(Estado estado:estadosDeAceptacion)estado.setAceptacion(true);
        this.estadosDeAceptacion = estadosDeAceptacion;
    }

    public static void main(String[] args){
        
        ////PRUEBAFACTOR28
        // MT mt=new MT("probarMT");
        // mt.toStringMT();
        ////PRUEBAFACTOR29
        // MT mt=new MT("probarMT");
        // mt.toStringMT();

        ////PRUEBAFACTOR30
        // char[] simbolos = {'a','b'};
        // char[] simbolosCinta = {'a','b','X','Y','Z'};
        // Alfabeto alfabetoEntrada = new Alfabeto(simbolos);
        // Alfabeto alfabetoCinta = new Alfabeto(simbolosCinta);
        // int numEstados = 6;
        // ArrayList<Estado> estados = new ArrayList<Estado>();
        // for (int i = 0; i < numEstados; i++) {
        //     estados.add(new Estado());
        // }  
        // HashMap<Estado, HashMap<Character ,Estado>> funcionDeTransicion = new HashMap<>();
        // HashMap<Estado, HashMap<Character,Character[]>> funcionDeTransicionCinta= new HashMap<>();
        // MT mt=new MT(alfabetoEntrada,alfabetoCinta,estados,funcionDeTransicion,funcionDeTransicionCinta);
        // mt.setEstadoInicial(estados.get(0));
        // ArrayList<Estado> estadosAcept = new ArrayList<>();
        // estadosAcept.add(estados.get(5));
        // mt.setEstadosDeAceptacion(estadosAcept);
        // mt.crearTransicion(estados.get(0), estados.get(1), 'a', 'X', '>');
        // mt.crearTransicion(estados.get(0), estados.get(4), 'Y', 'Y', '>');
        // mt.crearTransicion(estados.get(0), estados.get(5), '!', '!', '-');
        // mt.crearTransicion(estados.get(1), estados.get(1), 'a', 'a', '>');
        // mt.crearTransicion(estados.get(1), estados.get(1), 'Y', 'Y', '>');
        // mt.crearTransicion(estados.get(1), estados.get(2), 'b', 'Y', '>');
        // mt.crearTransicion(estados.get(2), estados.get(2), 'b', 'b', '>');
        // mt.crearTransicion(estados.get(2), estados.get(2), 'Z', 'Z', '>');
        // mt.crearTransicion(estados.get(2), estados.get(3), 'c', 'Z', '<');
        // mt.crearTransicion(estados.get(3), estados.get(0), 'X', 'X', '>');
        // mt.crearTransicion(estados.get(3), estados.get(3), 'a', 'a', '<');
        // mt.crearTransicion(estados.get(3), estados.get(3), 'b', 'b', '<');
        // mt.crearTransicion(estados.get(3), estados.get(3), 'Y', 'Y', '<');
        // mt.crearTransicion(estados.get(3), estados.get(3), 'Z', 'Z', '<');
        // mt.crearTransicion(estados.get(4), estados.get(4), 'Y', 'Y', '>');
        // mt.crearTransicion(estados.get(4), estados.get(4), 'Z', 'Z', '>');
        // mt.crearTransicion(estados.get(4), estados.get(5), '!', '!', '-');
        // mt.exportar("ProbarMT2");
        // mt.toStringMT();
        ////PRUEBAFACTOR31
        // MT mt = new MT("probarMT2");
        // System.out.println(mt.procesarCadenaConDetalle("aabbcc"));
        //PRUEBAFACTOR32
        // MT mt = new MT("probarMT2");
        // ArrayList<String> cadenas = new ArrayList<>();
        // cadenas.add("aabbcc");cadenas.add("aaabbcc");cadenas.add("aaabbbccc");cadenas.add("aabbccc");
        // mt.procesarListaCadenas(cadenas, "cadenasMT", true);
        ////PRUEBAFACTOR33
        // MT mt = new MT("probarMT2");
        // System.out.println(mt.procesarFuncion("aabbcc"));
    }
}
