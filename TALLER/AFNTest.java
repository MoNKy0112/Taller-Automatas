package TALLER;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

public class AFNTest {
    @Test 
    AFN testAFN(){
        char[] simbolos= {'0','1'};
        Alfabeto alf = new Alfabeto(simbolos);
        ArrayList<Estado> estados= new ArrayList<>();
        int cantEstados = 3;
        for(int i=0;i<cantEstados;i++)estados.add(new Estado());
        HashMap<Estado, HashMap<Character, List<Estado>>> funcionDeTrancision = new HashMap<>();
        
        AFN afn = new AFN(alf,estados,funcionDeTrancision);
        return afn;
    }

    @Test
    void testFillTransitions() {
        AFN afn = testAFN();
        afn.fillTransitions();
        System.out.println(afn.getFuncionDeTrancision());
    }

    @Test
    void testHallarEstadosInaccesibles() {
        AFN afn = testAFN();
        afn.fillTransitions();
        afn.setEstadoInicial(afn.getEstados().get(0));
        System.out.println(afn.hallarEstadosInaccesibles());
    }

    @Test
    void testHallarEstadosLimbo() {
        AFN afn = testAFN();
        afn.fillTransitions();
        afn.setEstadoInicial(afn.getEstados().get(0));
        ArrayList<Estado> estadosAcep = new ArrayList<>();
        estadosAcep.add(afn.getEstados().get(2));
        afn.setEstadosDeAceptacion(estadosAcep);
        System.out.println(afn.hallarEstadosLimbo());
    }

    @Test
    void testExportar() {
        AFN afn = testAFN();
        afn.fillTransitions();
        afn.setEstadoInicial(afn.getEstados().get(0));
        ArrayList<Estado> estadosAcep = new ArrayList<>();
        estadosAcep.add(afn.getEstados().get(2));
        afn.setEstadosDeAceptacion(estadosAcep);
        System.out.println(afn.getFuncionDeTrancision());
        afn.exportar("testAFN");
    }

    @Test
    void testExportar2() {
        
    }
}
