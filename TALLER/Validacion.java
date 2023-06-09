package TALLER;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Validacion {

    private int cantidadMaxCaracteres;
    private int cantidadCadenas = 5000;

    public Validacion(int cantmax){
        this.cantidadMaxCaracteres = cantmax;
    }

    public Validacion(int cantmax,int cantCadenas){
        this.cantidadMaxCaracteres = cantmax;
        this.cantidadCadenas=cantCadenas;
    }

    public void ValidarAFNtoAFD(ArrayList<AFN> listaAFNs){
        Random rand = new Random();
        boolean afnCad,afdCad;
        int dif=0, ig =0;
        for (AFN afn : listaAFNs){
            dif=0;ig=0;
            ArrayList<String> cadenas = new ArrayList<>();
            Alfabeto alf = afn.getAlfabeto();
            for(int i=0;i<cantidadCadenas;i++){
                cadenas.add(alf.generarCadenaAleatoria(rand.nextInt(cantidadMaxCaracteres)));
            }
            System.out.println("Cadenas generadas");
            for(String cadena : cadenas){
                afnCad=afn.procesarCadena(cadena);
                AFD afd = afn.AFNtoAFD(afn);
                afdCad=afd.procesarCadena(cadena);
                if(afnCad!=afdCad){
                    dif++;
                    System.out.println("La cadena: "+cadena+" da un resultado distinto en AFD y AFN");
                }else{
                    ig++;
                }
            }
            System.out.println("Numero de cadenas que dieron el mismo resultado: "+ig);
            System.out.println("Numero de cadenas con resultado distintos en afn y afd: "+dif);
        }

        
    } 
    //TODO PROBAR
    public void ValidarAFNLtoAFD(ArrayList<AFN_Lambda> listaAFNsLambdas){
        Random rand = new Random();
        boolean afnlCad,afdCad;
        int dif=0, ig =0;
        for (AFN_Lambda afnl : listaAFNsLambdas){
            dif=0;ig=0;
            ArrayList<String> cadenas = new ArrayList<>();
            Alfabeto alf = afnl.getAlfabeto();
            for(int i=0;i<cantidadCadenas;i++){
                cadenas.add(alf.generarCadenaAleatoria(rand.nextInt(cantidadMaxCaracteres)));
            }
            System.out.println("Cadenas generadas");
            for(String cadena : cadenas){
                afnlCad=afnl.procesarCadena(cadena);
                AFD afd = afnl.AFN_LambdaToAFD(afnl);
                afdCad=afd.procesarCadena(cadena);
                if(afnlCad!=afdCad){
                    dif++;
                    System.out.println("La cadena: "+cadena+" da un resultado distinto en AFD y AFN LAMBDA");
                }else{
                    ig++;
                }
            }
            System.out.println("Numero de cadenas que dieron el mismo resultado: "+ig);
            System.out.println("Numero de cadenas con resultado distintos en afn lambda y afd: "+dif);
        }

    } 

    public static void main(String[] args){
        Validacion val = new Validacion(40,500);
        ArrayList<AFN> listaAfns = new ArrayList<>();
        AFN afn1 = new AFN("probarAFN");
        AFN afn2 = new AFN("probarAFN");
        listaAfns.add(afn1);
        listaAfns.add(afn2);
        val.ValidarAFNtoAFD(listaAfns);

    }
}
