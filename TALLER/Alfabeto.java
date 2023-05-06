package TALLER;
import java.util.Random;

public class Alfabeto {
    //Atributos
    private char[] simbolos;

    ////Metodos
    //Constructor
    public Alfabeto(char[] simbolos) {
        this.simbolos = simbolos;
    }

    //genera una cadena aleatoria de longitud n
    public String generarCadenaAleatoria(int n) {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            int randIndex = rand.nextInt(simbolos.length);
            sb.append(simbolos[randIndex]);
        }

        return sb.toString();
    }
    //devuelve la cantidad de simbolos existentes en el alfabeto
    public int size(){
        return simbolos.length;
    }
    //determina si el símbolo está contenido en el alfabeto
    public boolean contieneSimbolo(char simbolo) {
        for (char s : simbolos) {
            if (s == simbolo) {
                return true;
            }
        }
        return false;
    }

    //Getters & Setters
    public char[] getSimbolos() {
        return simbolos;
    }

    public void setSimbolos(char[] simbolos) {
        this.simbolos = simbolos;
    }
}