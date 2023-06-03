package TALLER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Prueba {
    public void porbarAFD(){
        Scanner myObj = new Scanner(System.in);
        Random ran = new Random();
        char[] simbolos = {'0','1'};
        Alfabeto alf = new Alfabeto(simbolos);
        int numEstados = 3;
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
        afd.setEstadoInicial(afd.getEstados().get(0));
        ArrayList<Estado> acept = new ArrayList<>();
        acept.add(afd.getEstados().get(2));
        afd.setEstadosDeAceptacion(acept);
        afd.fillTransitions();
        afd.verificarCorregirCompletitudAFD();
        afd.hallarEstadosInaccesibles();
        afd.hallarEstadosLimbo();

        int cantCad = 1;
        int maxSize = 2;
        try {
            System.out.println("Cantidad de cadenas: ");
            cantCad=myObj.nextInt();     
            System.out.println("Maximo tamaño de cadena: ");
            maxSize=myObj.nextInt();
        } catch (Exception e) {
            //handle exception
        }
        

        for(int i = 0;i<cantCad;i++){
            String cadena = "";
            for(int j = 0;j<ran.nextInt(maxSize-1)+1;j++){
                int a =ran.nextInt(afd.getAlfabeto().getSimbolos().length);
                cadena += afd.getAlfabeto().getSimbolos()[a];
            }
            System.out.println("cadena:"+cadena);
            afd.procesarCadena(cadena);
            afd.procesarCadenaConDetalles(cadena);
            
        }
        afd.exportar("probarAFD");
        
    }
    public void porbarAFN(){
        Scanner myObj = new Scanner(System.in);
        Random ran = new Random();
        char[] simbolos = {'0','1'};
        Alfabeto alf = new Alfabeto(simbolos);
        int numEstados = 3;
        ArrayList<Estado> estados = new ArrayList<Estado>();
        for (int i = 0; i < numEstados; i++) {
            estados.add(new Estado());
        }  
        
        HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion = new HashMap<>();
        /*for (Estado estado : estados) {
            HashMap<Character, Estado> transiciones = new HashMap<>();
            for (char simbolo : alf.getSimbolos()) {
                transiciones.put(simbolo, estados.get((int)(Math.random() * numEstados)));
            }
            funcionDeTransicion.put(estado, transiciones);
        }*/
        AFN afn = new AFN(alf, estados, funcionDeTransicion);
        afn.setEstadoInicial(afn.getEstados().get(0));
        ArrayList<Estado> acept = new ArrayList<>();
        acept.add(afn.getEstados().get(2));
        afn.setEstadosDeAceptacion(acept);
        afn.fillTransitions();
        afn.hallarEstadosInaccesibles();
        afn.hallarEstadosLimbo();

        int cantCad = 1;
        int maxSize = 2;
        try {
            System.out.println("Cantidad de cadenas: ");
            cantCad=myObj.nextInt();     
            System.out.println("Maximo tamaño de cadena: ");
            maxSize=myObj.nextInt();
        } catch (Exception e) {
            // handle exception
        }
        

        for(int i = 0;i<cantCad;i++){
            String cadena = "";
            for(int j = 0;j<ran.nextInt(maxSize-1)+1;j++){
                int a =ran.nextInt(afn.getAlfabeto().getSimbolos().length);
                cadena += afn.getAlfabeto().getSimbolos()[a];
            }
            System.out.println("cadena:"+cadena);
            afn.procesarCadena(cadena);
            afn.procesarCadenaConDetalles(cadena);
            
        }
        afn.exportar("probarAFN");
    }
    public void porbarAFNLambda(){
        char[] simbolos = {'0','1'};
        Alfabeto alf = new Alfabeto(simbolos);
        int numEstados = 3;
        ArrayList<Estado> estados = new ArrayList<Estado>();
        for (int i = 0; i < numEstados; i++) {
            estados.add(new Estado());
        }  
        
        HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion = new HashMap<>();
        AFN_Lambda afnl = new AFN_Lambda(alf, estados, funcionDeTransicion);
        afnl.setInicialWithGui();
        ArrayList<Estado> acept = new ArrayList<>();
        acept.add(afnl.getEstados().get(2));
        afnl.setEstadosDeAceptacion(acept);
        afnl.fillTransitions();
        System.out.println(afnl.getFuncionDeTransicion());
        List<Estado> listaEstados = new ArrayList<>();
        listaEstados.add(afnl.getEstados().get(0));listaEstados.add(afnl.getEstados().get(1));
        System.out.println(afnl.lambdaClausura(listaEstados));
        System.out.println(afnl.procesarCadena(""));
    }



    public void porbarAFNToAFD() throws Exception{
        Random ran = new Random();
        AFN afn = new AFN("testAFN");
        AFD afd = afn.AFNtoAFD(afn);

        afd.verificarCorregirCompletitudAFD();
        afd.hallarEstadosInaccesibles();
        afd.hallarEstadosLimbo();
        int cantCadenas=1;
        try {
            System.out.println("Cantidad de cadenas: ");
            cantCadenas=System.in.read();
        } catch (Exception e) {
            //handle exception
        }
        
        Alfabeto alf = afn.getAlfabeto();
        for(int i=0;i<cantCadenas;i++){
            String cadena = alf.generarCadenaAleatoria(ran.nextInt(11));
            System.out.println("cadena:"+cadena);
            System.out.println("AFN: "+afn.procesarCadena(cadena)+"__AFD: "+afd.procesarCadena(cadena));
        }
        
    }
    public void porbarAFNLambdaToAFN(){
        char[] simbolos = {'a','b'};
        Alfabeto alf = new Alfabeto(simbolos);
        int numEstados = 4;
        ArrayList<Estado> estados = new ArrayList<Estado>();
        for (int i = 0; i < numEstados; i++) {
            estados.add(new Estado());
        }  
        
        HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTransicion = new HashMap<>();
        AFN_Lambda afnl = new AFN_Lambda(alf, estados, funcionDeTransicion);
        afnl.setInicialWithGui();
        ArrayList<Estado> acept = new ArrayList<>();
        acept.add(afnl.getEstados().get(3));
        afnl.setEstadosDeAceptacion(acept);
        afnl.fillTransitions();
        System.out.println(afnl.getFuncionDeTransicion());
        AFN afn = afnl.AFN_LambdaToAFN(afnl);
        System.out.println(afn.getFuncionDeTransicion());
    }
    public void probarComplemento(){
        AFD afd = new AFD("test");
        afd.imprimirAFDSimplificado();
        AFD afdComp = afd.hallarComplemento();
        afdComp.imprimirAFDSimplificado();
        
        
        //afdComp.imprimirAFDSimplificado();
    }
    public void probarProductoCartesiano(){
        AFD afd1 = new AFD("test");
        AFD afd2 = new AFD("test2");

        AFD afdPC = afd1.productoCartesiano(afd1, afd2, "interseccion");

        afdPC.imprimirAFDSimplificado();
    }
    public void probarSimplificacion(){

        AFD afd = new AFD("test2");
        afd.simplificarAFD(afd);
        afd.imprimirAFDSimplificado();
    }

    













    public static void main(String[] args) throws Exception{
        Prueba prueba= new Prueba();
        //prueba.porbarAFD();
        //prueba.porbarAFNToAFD();
        //prueba.probarComplemento();
        //prueba.probarProductoCartesiano();
        //prueba.probarSimplificacion();
        //prueba.porbarAFNLambda();
        prueba.porbarAFNLambdaToAFN();
    }
}
