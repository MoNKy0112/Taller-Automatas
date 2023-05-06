package TALLER;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

import TALLER.GUITABLA.MatrixGUI;

public class AFDTest {

    @Test
    AFD testAFD(){
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
        MatrixGUI gui = new MatrixGUI(afd);
        while(gui.isEnabled()){

        }
        return afd;
    }

    @Test
    void testAddTransicion() {
        
    }

    @Test
    void testExportar() {
        
    }

    @Test
    void testHallarComplemento() {
        
    }

    @Test
    void testHallarEstadosInaccesibles() {
        
    }

    @Test
    void testHallarEstadosLimbo() {
        
    }

    @Test
    void testImprimirAFDSimplificado() {
        
    }

    @Test
    void testProcesarCadena() {
        
    }

    @Test
    void testProcesarCadenaConDetalles() {
        
    }

    @Test
    void testProcesarListaCadenas() {
        
    }

    @Test
    void testToString() {
        AFD afd=testAFD();
        String str=afd.toString();
        System.out.println(str);
    }

    @Test
    void testVerificarCorregirCompletitudAFD() {
        
    }
}
