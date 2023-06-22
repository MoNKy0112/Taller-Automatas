package TALLER;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class AF2P {
    private Alfabeto alfabeto = new Alfabeto(null);
    private Alfabeto alfabetoPila = new Alfabeto(null);
    private ArrayList<Estado> estados = new ArrayList<Estado>();
    private Estado estadoInicial = estados.stream().filter(p -> p.isInicial()).findFirst().orElse(
            estados.stream().findFirst().orElse(null));
    private ArrayList<Estado> estadosDeAceptacion = new ArrayList<>(
            estados.stream().filter(p -> p.isAceptacion()).collect(Collectors.toList()));
    private HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion;
    private HashMap<Estado, HashMap<Character, ArrayList<Character[]>>> funcionDeTransicionPila;
    private HashMap<Estado, HashMap<Character, ArrayList<Character[]>>> funcionDeTransicionPila2;
    // El index del array 0 es para el hashmap char estado de las transiciones de
    // estado
    // El index del array 0 es para el hashmap char char para los movimientos de la
    // pila
    private Stack pila = new Stack<Character>();

    // Metodos
    public AF2P(Alfabeto alfabetoCinta, Alfabeto alfabetoPila, ArrayList<Estado> estados,
            HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion,
            HashMap<Estado, HashMap<Character, ArrayList<Character[]>>> funcionDeTransicionPila,
            HashMap<Estado, HashMap<Character, ArrayList<Character[]>>> funcionDeTransicionPila2) {
        this.alfabeto = alfabetoCinta;
        this.alfabetoPila = alfabetoPila;
        this.estados = estados;
        this.funcionDeTransicion = funcionDeTransicion;
        this.funcionDeTransicionPila = funcionDeTransicionPila;
        this.funcionDeTransicionPila2 = funcionDeTransicionPila2;
    }

    public boolean modificarPila(Character parametro, Character operacion) {
        if (!this.alfabetoPila.contieneSimbolo(parametro) && parametro != '$') {
            throw new IllegalArgumentException("El simbolo " + parametro
                    + " no pertenece al alfabeto de la pila del automata");
        }
        if (!this.alfabetoPila.contieneSimbolo(operacion) && operacion != '$') {
            throw new IllegalArgumentException("El simbolo " + operacion
                    + " no pertenece al alfabeto de la pila del automata");
        }
        if (parametro.equals('$')) {
            if (operacion.equals(parametro)) {
            } else {
                pila.push(operacion);
            }
        } else if (pila.isEmpty()) {
            return false;
        } else if (pila.peek().equals(parametro)) {
            if (operacion.equals('$')) {
                pila.pop();
            } else if (operacion.equals(parametro)) {
            } else {
                pila.pop();
                pila.push(operacion);
            }
        } else {
            return false;
        }
        return true;
    }

    public void crearTransicion(Estado estadoOrigen, Estado estadoDestino, Character simbL,
            Character simb1Pila,
            Character simb2Pila, Character sim1Pila2, Character sim2Pila2) {
        // crea la transicion entre estados con determinado simbolo
        HashMap<Character, Estado> transiciones = funcionDeTransicion.getOrDefault(estadoOrigen,
                new HashMap<>());
        transiciones.put(simbL, estadoDestino);
        funcionDeTransicion.put(estadoOrigen, transiciones);
        // crea la transicion de pila con determinado simbolo donde los indices pares
        // del
        // ArrayList son lo que lee en el tope de pila y los impares son sus respectivas
        // operaciones
        HashMap<Character, ArrayList<Character[]>> transicionesPila = funcionDeTransicionPila
                .getOrDefault(estadoOrigen, new HashMap<>());
        ArrayList<Character[]> aux1 = new ArrayList<>();
        if (transicionesPila.containsKey(simbL)) {
            aux1 = transicionesPila.get(simbL);
        }
        Character[] aux = { simb1Pila, simb2Pila };
        aux1.add(aux);
        transicionesPila.put(simbL, aux1);
        funcionDeTransicionPila.put(estadoOrigen, transicionesPila);

        HashMap<Character, ArrayList<Character[]>> transicionesPila2 = funcionDeTransicionPila2
                .getOrDefault(estadoOrigen, new HashMap<>());
        ArrayList<Character[]> aux2 = new ArrayList<>();
        if (transicionesPila2.containsKey(simbL)) {
            aux2 = transicionesPila2.get(simbL);
        }
        Character[] auxp = { sim1Pila2, sim2Pila2 };
        aux2.add(auxp);
        transicionesPila2.put(simbL, aux2);
        funcionDeTransicionPila2.put(estadoOrigen, transicionesPila2);
    }

    private boolean contieneEstado(Estado estado) {
        return this.estados.contains(estado);
    }

    public boolean procesarCadena(String cadena) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        Estado estadoActual = estadoInicial;
        Character parametro = null, operacion = null;
        boolean existeAceptacion = false, isAborted = false;
        int l = 0;
        System.out.println("Cadena: " + cadena);
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < cadena.length(); j++) {
                pila.clear();
                System.out.print("(" + estadoActual + "," + cadena + "," + pila + ")");
                for (int i = 0; i < cadena.length(); i++) {
                    if (funcionDeTransicion.get(estadoActual).containsKey('$')) {
                        ArrayList<Character[]> lista = funcionDeTransicionPila.get(estadoActual)
                                .get('$');
                        for (int h = 0; h < lista.size(); j++) {
                            parametro = lista.get(j)[0];
                            operacion = lista.get(j)[1];
                            if (modificarPila(parametro, operacion)) {
                                break;
                            }
                            if (j == lista.size() - 1) {
                                System.out.println(">>Aborted");
                                return false;
                            }
                        }
                        // estadoActual = transicion(estadoActual, '$');
                        System.out.print("->(" + estadoActual + ","
                                + cadena.substring(i + 1, cadena.length())
                                + "," + pila + ")");
                        i--;
                    } else {
                        ArrayList<Character[]> lista = funcionDeTransicionPila.get(estadoActual)
                                .get(cadena.charAt(i));
                        if (i == 0) {
                            l = k;
                        } else {
                            l = j;
                        }
                        parametro = lista.get(l)[0];
                        operacion = lista.get(l)[1];
                        if (!modificarPila(parametro, operacion)) {
                            isAborted = true;
                            break;
                        }

                        // if (cadena.length() != 1)
                        // procesarCadenaConDetalles(
                        // cadena.substring(i + 1, cadena.length()),
                        // lleva);
                    }
                    // estadoActual = transicion(estadoActual, cadena.charAt(i));
                    System.out.print("->(" + estadoActual + ","
                            + cadena.substring(i + 1, cadena.length())
                            + "," + pila + ")");
                }

                if (estadosDeAceptacion.contains(estadoActual) && pila.empty() && !isAborted) {
                    System.out.println(">>Accepted");
                    existeAceptacion = true;
                } else if (!isAborted) {
                    System.out.println(">>Rejected");
                } else {
                    System.out.println(">>Aborted");
                }
            }

        }
        return existeAceptacion;
    }

    public boolean procesarCadenaConDetalles(String cadena) {
        Estado estadoActual = estadoInicial;
        Character parametro = null, operacion = null;
        boolean existeAceptacion = false, isAborted = false;
        int l = 0;
        System.out.println("Cadena: " + cadena);
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < cadena.length(); j++) {
                pila.clear();
                System.out.print("(" + estadoActual + "," + cadena + "," + pila + ")");
                for (int i = 0; i < cadena.length(); i++) {
                    if (funcionDeTransicion.get(estadoActual).containsKey('$')) {
                        ArrayList<Character[]> lista = funcionDeTransicionPila.get(estadoActual)
                                .get('$');
                        for (int h = 0; h < lista.size(); j++) {
                            parametro = lista.get(j)[0];
                            operacion = lista.get(j)[1];
                            if (modificarPila(parametro, operacion)) {
                                break;
                            }
                            if (j == lista.size() - 1) {
                                System.out.println(">>Aborted");
                                return false;
                            }
                        }
                        // estadoActual = transicion(estadoActual, '$');
                        System.out.print("->(" + estadoActual + ","
                                + cadena.substring(i + 1, cadena.length())
                                + "," + pila + ")");
                        i--;
                    } else {
                        ArrayList<Character[]> lista = funcionDeTransicionPila.get(estadoActual)
                                .get(cadena.charAt(i));
                        if (i == 0) {
                            l = k;
                        } else {
                            l = j;
                        }
                        parametro = lista.get(l)[0];
                        operacion = lista.get(l)[1];
                        if (!modificarPila(parametro, operacion)) {
                            isAborted = true;
                            break;
                        }

                        // if (cadena.length() != 1)
                        // procesarCadenaConDetalles(
                        // cadena.substring(i + 1, cadena.length()),
                        // lleva);
                    }
                    // estadoActual = transicion(estadoActual, cadena.charAt(i));
                    System.out.print("->(" + estadoActual + ","
                            + cadena.substring(i + 1, cadena.length())
                            + "," + pila + ")");
                }

                if (estadosDeAceptacion.contains(estadoActual) && pila.empty() && !isAborted) {
                    System.out.println(">>Accepted");
                    existeAceptacion = true;
                } else if (!isAborted) {
                    System.out.println(">>Rejected");
                } else {
                    System.out.println(">>Aborted");
                }
            }

        }
        return existeAceptacion;
    }

    public void procesarListaCadenas(List<String> cadenas, String nombreArchivo, boolean imprimirPantalla) {
        if (nombreArchivo == null) {
            nombreArchivo = "DefaultProcesarListaAFPN";
        }
        try {
            PrintWriter writer = new PrintWriter(nombreArchivo + ".pda", "UTF-8");
            for (String cadena : cadenas) {
                Estado estadoActual = estadoInicial;
                Character parametro = null, operacion = null;
                boolean existeAceptacion = false, isAborted = false;
                int l = 0, procesosAceptacion = 0, procesosRechazo = 0, procesos = 0;
                writer.print("Cadena: " + cadena + "\t");
                for (int k = 0; k < 2; k++) {
                    for (int j = 0; j < cadena.length(); j++) {
                        pila.clear();
                        writer.print("(" + estadoActual + "," + cadena + "," + pila + ")");
                        for (int i = 0; i < cadena.length(); i++) {
                            if (funcionDeTransicion.get(estadoActual).containsKey('$')) {
                                ArrayList<Character[]> lista = funcionDeTransicionPila
                                        .get(estadoActual)
                                        .get('$');
                                for (int h = 0; h < lista.size(); j++) {
                                    parametro = lista.get(j)[0];
                                    operacion = lista.get(j)[1];
                                    if (modificarPila(parametro, operacion)) {
                                        break;
                                    }
                                    if (j == lista.size() - 1) {
                                        writer.println(">>Aborted");
                                    }
                                }
                                // estadoActual = transicion(estadoActual, '$');
                                writer.print("->(" + estadoActual + ","
                                        + cadena.substring(i + 1,
                                                cadena.length())
                                        + "," + pila + ")");
                                i--;
                            } else {
                                ArrayList<Character[]> lista = funcionDeTransicionPila
                                        .get(estadoActual)
                                        .get(cadena.charAt(i));
                                if (i == 0) {
                                    l = k;
                                } else {
                                    l = j;
                                }
                                parametro = lista.get(l)[0];
                                operacion = lista.get(l)[1];
                                if (!modificarPila(parametro, operacion)) {
                                    isAborted = true;
                                    break;
                                }

                                // if (cadena.length() != 1)
                                // procesarCadenaConDetalles(
                                // cadena.substring(i + 1, cadena.length()),
                                // lleva);
                            }
                            // estadoActual = transicion(estadoActual, cadena.charAt(i));
                            writer.print("->(" + estadoActual + ","
                                    + cadena.substring(i + 1, cadena.length())
                                    + "," + pila + ")");
                        }
                        procesos++;
                        if (estadosDeAceptacion.contains(estadoActual) && pila.empty()
                                && !isAborted) {
                            writer.print(">>yes\t");
                            existeAceptacion = true;
                            procesosAceptacion++;
                        } else if (!isAborted) {
                            writer.print(">>no\t");
                            procesosRechazo++;
                        } else {
                            writer.print(">>Aborted\t");
                        }
                    }

                }
                writer.print(procesos + "\t");
                writer.print(procesosAceptacion + "\t");
                writer.println(procesosRechazo + "\t");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toStringAFPN() {
        System.out.println("#!pda");
        System.out.println("#states");
        for (Estado estado : estados) {
            System.out.println(estado.toString());
        }
        System.out.println("#initial");
        System.out.println(estadoInicial.toString());
        System.out.println("#accepting");
        for (Estado estado : estadosDeAceptacion) {
            System.out.println(estado.toString());
        }
        System.out.println("#tapeAlphabet");
        for (char simbolo : alfabeto.getSimbolos()) {
            System.out.println(simbolo);
        }
        System.out.println("#stackAlphabet");
        for (char simbolo : alfabetoPila.getSimbolos()) {
            System.out.println(simbolo);
        }
        System.out.println("#transitions");
        for (Estado estado : estados) {
            for (char simbolo : alfabeto.getSimbolos()) {
                if (funcionDeTransicion.containsKey(estado)
                        && funcionDeTransicionPila.containsKey(estado)) {
                    if (funcionDeTransicion.get(estado).containsKey(simbolo)
                            && funcionDeTransicionPila.get(estado).containsKey(simbolo)) {
                        List<Character[]> lista = funcionDeTransicionPila.get(estado)
                                .get(simbolo);
                        for (int i = 0; i < lista.size(); i++) {
                            System.out.println(estado.toString() + ":" + simbolo + ":"
                                    + funcionDeTransicionPila.get(estado)
                                            .get(simbolo)
                                            .get(i)[0]
                                    +
                                    ">"
                                    + funcionDeTransicion.get(estado).get(simbolo)
                                            .toString()
                                    + ":"
                                    + funcionDeTransicionPila.get(estado)
                                            .get(simbolo)
                                            .get(i)[1]);
                        }
                    }
                }
            }
        }
    }

    public void exportar(String nombreArchivo) {
        if (nombreArchivo == null) {
            nombreArchivo = "DefaultAF2P";
        }
        try {
            PrintWriter writer = new PrintWriter(nombreArchivo + ".msm", "UTF-8");
            writer.println("#!msm");
            writer.println("#states");
            for (Estado estado : estados) {
                writer.println(estado.toString());
            }
            writer.println("#initial");
            writer.println(estadoInicial.toString());
            writer.println("#accepting");
            for (Estado estado : estadosDeAceptacion) {
                writer.println(estado.toString());
            }
            writer.println("#tapeAlphabet");
            for (char simbolo : alfabeto.getSimbolos()) {
                writer.println(simbolo);
            }
            writer.println("#stackAlphabet");
            for (char simbolo : alfabetoPila.getSimbolos()) {
                writer.println(simbolo);
            }
            writer.println("#transitions");
            for (Estado estado : estados) {
                for (char simbolo : alfabeto.getSimbolos()) {
                    if (funcionDeTransicion.containsKey(estado)
                            && funcionDeTransicionPila.containsKey(estado)) {
                        if (funcionDeTransicion.get(estado).containsKey(simbolo)
                                && funcionDeTransicionPila.get(estado)
                                        .containsKey(simbolo)) {
                            List<Character[]> lista = funcionDeTransicionPila.get(estado)
                                    .get(simbolo);
                            for (int i = 0; i < lista.size(); i++) {
                                writer.println(estado.toString() + ":" + simbolo
                                        + ":"
                                        + funcionDeTransicionPila.get(estado)
                                                .get(simbolo)
                                                .get(i)[0]
                                        + ":"
                                        + funcionDeTransicionPila2.get(estado)
                                                .get(simbolo)
                                                .get(i)[0]
                                        + ">"
                                        + funcionDeTransicion.get(estado)
                                                .get(simbolo)
                                                .toString()
                                        + ":"
                                        + funcionDeTransicionPila.get(estado)
                                                .get(simbolo)
                                                .get(i)[1]
                                        + ":"
                                        + funcionDeTransicionPila2.get(estado)
                                                .get(simbolo)
                                                .get(i)[1]);
                            }
                        }
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters & Setters
    public void setEstadosDeAceptacion(ArrayList<Estado> estadosDeAceptacion) {
        for (Estado estado : estadosDeAceptacion)
            estado.setAceptacion(true);
        this.estadosDeAceptacion = estadosDeAceptacion;
    }

    public void setEstadoInicial(Estado estadoInicial) {
        estadoInicial.setInicial(true);
        this.estadoInicial = estadoInicial;
    }

    public static void main(String[] args) {
        char[] simbolos = { 'a', 'b', 'c' };
        char[] simbolos2 = { 'A' };
        Alfabeto alf = new Alfabeto(simbolos);
        Alfabeto alfp = new Alfabeto(simbolos2);
        int numEstados = 3;
        ArrayList<Estado> estados = new ArrayList<Estado>();
        for (int i = 0; i < numEstados; i++) {
            estados.add(new Estado());
        }
        HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion = new HashMap<>();
        HashMap<Estado, HashMap<Character, ArrayList<Character[]>>> funcionDeTransicionPila = new HashMap<>();
        HashMap<Estado, HashMap<Character, ArrayList<Character[]>>> funcionDeTransicionPila2 = new HashMap<>();
        AF2P msm = new AF2P(alf, alfp, estados, funcionDeTransicion, funcionDeTransicionPila, funcionDeTransicionPila2);
        msm.setEstadoInicial(estados.get(0));
        ArrayList<Estado> estadosAcept = new ArrayList<>();
        estadosAcept.add(estados.get(0));
        estadosAcept.add(estados.get(2));
        msm.setEstadosDeAceptacion(estadosAcept);
        msm.crearTransicion(estados.get(0), estados.get(0), 'a', '$', 'A', '$', 'A');
        msm.crearTransicion(estados.get(0), estados.get(1), 'b', 'A', '$', '$', '$');
        msm.crearTransicion(estados.get(1), estados.get(1), 'b', 'A', '$', '$', '$');
        msm.crearTransicion(estados.get(1), estados.get(2), 'c', '$', '$', 'A', '$');
        msm.crearTransicion(estados.get(2), estados.get(2), 'c', '$', '$', 'A', '$');

        msm.exportar("AF2PVideo");
    }
}
