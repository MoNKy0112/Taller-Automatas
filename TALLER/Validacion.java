package TALLER;

import java.util.ArrayList;
import java.util.Random;

public class Validacion {

    private int cantidadMaxCaracteres;
    private int cantidadCadenas = 500;

    public Validacion(int cantmax){
        this.cantidadMaxCaracteres = cantmax;
    }

    public void ValidarAFNtoAFD(ArrayList<AFN> listaAFNs){
        Random rand = new Random();
        boolean afnCad,afdCad;
        int dif=0;
        for (AFN afn : listaAFNs){
            ArrayList<String> cadenas = new ArrayList<>();
            Alfabeto alf = afn.getAlfabeto();
            for(int i=0;i<cantidadCadenas;i++){
                cadenas.add(alf.generarCadenaAleatoria(rand.nextInt(cantidadMaxCaracteres)));
            }
            for(String cadena : cadenas){
                afnCad=afn.procesarCadena(cadena);
                AFD afd = afn.AFNtoAFD(afn);
                afdCad=afd.procesarCadena(cadena);
                if(afnCad!=afdCad){
                    dif++;
                    System.out.println("La cadena: "+cadena+" da un resultado distinto en AFD y AFN");
                }
            }
        }
        System.out.println("Numero de cadenas con resultado distintos en afn y afd: "+dif);
    } 

    public static void main(String[] args){
        Validacion val = new Validacion(5);
        val.ValidarAFNtoAFD(null);
    }
}
