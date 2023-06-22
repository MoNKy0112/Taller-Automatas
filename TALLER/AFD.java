package TALLER;

import java.util.ArrayList;
import java.util.stream.Collectors;

import TALLER.GRAPH.AFGraphGUI;
import TALLER.GUITABLA.MatrixGUI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class AFD {
    // Atributos
    private Alfabeto alfabeto = new Alfabeto(null);
    private ArrayList<Estado> estados = new ArrayList<Estado>();
    private Estado estadoInicial = estados.stream().filter(p -> p.isInicial()).findFirst().orElse(
            estados.stream().findFirst().orElse(null));
    private ArrayList<Estado> estadosDeAceptacion = new ArrayList<>(
            estados.stream().filter(p -> p.isAceptacion()).collect(Collectors.toList()));
    private HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion;
    private ArrayList<Estado> estadosLimbo = new ArrayList<>(
            estados.stream().filter(p -> p.isLimbo()).collect(Collectors.toList()));
    private ArrayList<Estado> estadosInaccesibles = new ArrayList<>(
            estados.stream().filter(p -> !p.isAccesible()).collect(Collectors.toList()));

    //// Metodos
    // Constructor
    // B-1
    public AFD(Alfabeto alfabeto, ArrayList<Estado> estados,
            HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion) {
        this.alfabeto = alfabeto;
        this.estados = estados;
        this.funcionDeTransicion = funcionDeTransicion;
    }

    // B-1
    public AFD(Alfabeto alfabeto) {
        this.alfabeto = alfabeto;
        this.funcionDeTransicion = new HashMap<>();
    }

    // B-1
    public AFD() {
        this.funcionDeTransicion = new HashMap<>();
    }

    // B-2
    public AFD(String nombreArchivo) {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        ArrayList<Estado> estados = new ArrayList<>();
        HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion = new HashMap<>();
        ArrayList<Character> simbolos = new ArrayList<>();
        Map<String, Estado> mapEstados = new HashMap<>();
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File(nombreArchivo + ".dfa");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);

            // Lectura del fichero
            String linea;
            int status = -1;
            boolean flag = false;
            while ((linea = br.readLine()) != null) {
                flag = true;
                if (linea.equals("#alphabet")) {
                    status = 0;
                    flag = false;
                }
                if (linea.equals("#states")) {
                    status = 1;
                    flag = false;
                }
                if (linea.equals("#initial")) {
                    status = 2;
                    flag = false;
                }
                if (linea.equals("#accepting")) {
                    status = 3;
                    flag = false;
                }
                if (linea.equals("#transitions")) {
                    status = 4;
                    flag = false;
                }
                while (status == 0 && flag) {
                    if (linea.length() == 1) {
                        simbolos.add(linea.toCharArray()[0]);
                    } else if (linea.contains("-")) {
                        String[] parts = linea.split("-");
                        int a = (int) parts[0].toCharArray()[0];
                        int b = (int) parts[1].toCharArray()[0];
                        for (int i = a; i < b + 1; i++) {
                            simbolos.add((char) i);
                        }
                    }
                    break;
                }
                while (status == 1 && flag) {
                    Estado nuevoEstado = new Estado();
                    mapEstados.put(linea, nuevoEstado);
                    estados.add(nuevoEstado);
                    break;
                }
                while (status == 2 && flag) {
                    Estado estado = mapEstados.get(linea);
                    int index = estados.indexOf(estado);
                    estados.get(index).setInicial(true);
                    this.setEstadoInicial(estado);
                    break;
                }
                while (status == 3 && flag) {
                    Estado estado = mapEstados.get(linea);
                    int index = estados.indexOf(estado);
                    estados.get(index).setAceptacion(true);
                    System.out.println(estado + "seted aceptation");
                    break;
                }
                while (status == 4 && flag) {
                    String[] parts = linea.split(":");
                    Estado estadoOrigen = mapEstados.get(parts[0]);
                    String[] parts2 = parts[1].split(">");
                    char simbolo = parts2[0].toCharArray()[0];
                    Estado estadoDestino = mapEstados.get(parts2[1]);
                    // System.out.println("estDest"+estadoDestino);
                    HashMap<Character, Estado> transiciones = funcionDeTransicion.getOrDefault(estadoOrigen,
                            new HashMap<>());
                    transiciones.put(simbolo, estadoDestino);
                    funcionDeTransicion.put(estadoOrigen, transiciones);
                    break;
                }
                // System.out.println(linea+"estado: "+status+flag);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta
            // una excepcion.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        Character[] sim = simbolos.toArray(new Character[simbolos.size()]);
        char[] simb = new char[simbolos.size()];
        for (int i = 0; i < sim.length; i++)
            simb[i] = sim[i];
        char[] simbolosAlf = simb;
        Alfabeto alfabeto = new Alfabeto(simbolosAlf);
        this.alfabeto = alfabeto;
        this.estados = estados;
        this.funcionDeTransicion = funcionDeTransicion;
        // System.out.println(this.getFuncionDeTransicion());
        // correjirCompletitud
        this.setEstadosDeAceptacion(estados.stream().filter(est -> est.isAceptacion())
                .collect(Collectors.toCollection(ArrayList::new)));
        hallarEstadosInaccesibles();
        hallarEstadosLimbo();

    }

    // B-3
    public void verificarCorregirCompletitudAFD() {
        Estado estadoLimboNuevo = null;
        //Revisar cada estado, cada simbolo si existe una determinada transicion
        for (Estado estado : estados) {
            for (char simbolo : alfabeto.getSimbolos()) {
                if (!funcionDeTransicion.get(estado).containsKey(simbolo)) {
                    //en caso de no existir transicion, se genera una hacia el limbo nuevo
                    if (estadoLimboNuevo == null) {
                        estadoLimboNuevo = new Estado();
                    }
                    agregarTransicionLimbo(estado, simbolo, estadoLimboNuevo);
                }
            }
        }
        //agregamos las transiciones del limbo hacia si mismo
        if (estadoLimboNuevo != null) {
            agregarEstado(estadoLimboNuevo);
            for (char simbolo : alfabeto.getSimbolos()) {
                agregarTransicionLimbo(estadoLimboNuevo, simbolo, estadoLimboNuevo);
            }
        }
    }

    private void agregarEstado(Estado estadoLimboNuevo) {
        this.estados.add(estadoLimboNuevo);
    }

    private void agregarTransicionLimbo(Estado estadoOrigen, char simbolo, Estado estadoDestino) {
        if (!this.alfabeto.contieneSimbolo(simbolo)) {
            throw new IllegalArgumentException("El simbolo " + simbolo + " no pertenece al alfabeto del automata");
        }
        if (!contieneEstado(estadoOrigen)) {
            throw new IllegalArgumentException(
                    "El estado " + estadoOrigen + " no pertenece al conjunto de estados del autómata.");
        }
        HashMap<Character, Estado> transiciones = funcionDeTransicion.getOrDefault(estadoOrigen, new HashMap<>());
        transiciones.put(simbolo, estadoDestino);
        funcionDeTransicion.put(estadoOrigen, transiciones);
    }

    // Agrega transicion individual --Soporte
    public void agregarTransicion(Estado estadoOrigen, char simbolo, Estado estadoDestino) {
        if (!this.alfabeto.contieneSimbolo(simbolo)) {
            throw new IllegalArgumentException("El simbolo " + simbolo + " no pertenece al alfabeto del automata");
        }
        if (!contieneEstado(estadoOrigen) || !contieneEstado(estadoDestino)) {
            throw new IllegalArgumentException("El estado " + estadoOrigen + " o el estado " + estadoDestino
                    + " no pertenece al conjunto de estados del autómata.");
        }
        HashMap<Character, Estado> transiciones = funcionDeTransicion.getOrDefault(estadoOrigen, new HashMap<>());
        transiciones.put(simbolo, estadoDestino);
        funcionDeTransicion.put(estadoOrigen, transiciones);
    }

    // completa tabla de transiciones con una GUI --adicional
    public void fillTransitions() {
        MatrixGUI gui = new MatrixGUI(this);
        while (gui.isVisible()) {
            try {
                Thread.sleep(100); // Esperar 100 milisegundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // verifica si afd tiene cierto estado --Soporte
    private boolean contieneEstado(Estado estado) {
        return this.estados.contains(estado);
    }

    // B-4
    public ArrayList<Estado> hallarEstadosLimbo() {
        Set<Estado> visitados = new HashSet<>();
        Set<Estado> noMuertos = new HashSet<>();

        noMuertos.addAll(estadosDeAceptacion);
        visitados.addAll(estadosDeAceptacion);

        for (Estado estado : estados) {
            if (!visitados.contains(estado)) {
                //depth first search para hallar estados no muertos
                dfsLimbo(estado, visitados, noMuertos);

            }
        }

        ArrayList<Estado> estadosLimbo = new ArrayList<>();

        for (Estado estado : estados) {
            estado.setLimbo(!noMuertos.contains(estado));
            if (estado.isLimbo()) {
                estadosLimbo.add(estado);
                // System.out.println("-------------"+visitados+","+noMuertos);
            }
        }

        // determina estados limbo y modifica el atributo
        setEstadosLimbo(estadosLimbo);
        return estadosLimbo;
    }

    // Soporte B-4
    //depth first search
    private boolean dfsLimbo(Estado estadoActual, Set<Estado> visitados, Set<Estado> noMuertos) {
        visitados.add(estadoActual);
        //System.out.println("visitados:" + visitados);
        for (char simbolo : alfabeto.getSimbolos()) {
            Estado estadoSig = transicion(estadoActual, simbolo);
            if (estadoSig != null) {
                // Verifica si esta en noMuertos, de ser asi el estadoActual tambien seria un
                // noMuerto
                if (noMuertos.contains(estadoSig)) {
                    noMuertos.add(estadoActual);
                    return true;
                }
                // Verifica si esta en visitados pero no en noMuertos, por lo cual seria un
                // estado muerto
                if (!visitados.contains(estadoSig)) {
                    // si no esta en ninguno de los dos, hay que recorrer mas y verificar si se
                    // llega a
                    // algun estado de aceptacion
                    if (dfsLimbo(estadoSig, visitados, noMuertos)) {
                        noMuertos.add(estadoActual);
                        return true;
                    }
                }
                // De no ser asi por este camino solo hay estados limbo
                // Por lo cual debe revisar los demas caminos
            }

        }
        // Si ningun camino lo llevo a un estado de aceptacion, se entiende que este es
        // un estado limbo
        return false;

    }

    // B-5
    public ArrayList<Estado> hallarEstadosInaccesibles() {
        Set<Estado> accesibles = new HashSet<>();
        Queue<Estado> queue = new LinkedList<>();
        queue.offer(estadoInicial);
        accesibles.add(estadoInicial);

        //recorremos las trancisiones de cada estado en la cola
        while (!queue.isEmpty()) {
            Estado estadoActual = queue.poll();
            //System.out.println(estadoActual);
            for (char simbolo : alfabeto.getSimbolos()) {
                Estado estadoSig = transicion(estadoActual, simbolo);
                //si el estado no es nulo y tampoco esta en accesibles aun, lo agregamos a la cola
                if (estadoSig != null && !accesibles.contains(estadoSig)) {
                    accesibles.add(estadoSig);
                    queue.offer(estadoSig);
                }
            }
        }
        //todos los estaados que no esten en accesibles son añadidos a inaccesibles
        ArrayList<Estado> inaccesibles = new ArrayList<>();
        for (Estado estado : estados) {
            estado.setAccesible(accesibles.contains(estado));
            if (!accesibles.contains(estado)) {
                inaccesibles.add(estado);
            }
        }

        // determina estados accesibles y modifica el atributo
        setEstadosInaccesibles(inaccesibles);
        return inaccesibles;
    }

    // Soporte
    public Estado transicion(Estado estadoOrigen, char simbolo) {
        if (!this.alfabeto.contieneSimbolo(simbolo)) {
            throw new IllegalArgumentException("El simbolo " + simbolo + " no pertenece al alfabeto del automata");
        }
        if (!contieneEstado(estadoOrigen)) {
            throw new IllegalArgumentException(
                    "El estado " + estadoOrigen + " no pertenece al conjunto de estados del autómata.");
        }
        if (!funcionDeTransicion.get(estadoOrigen).containsKey(simbolo)) {
            return null;
        }
        return funcionDeTransicion.get(estadoOrigen).get(simbolo);
    }

    // B-7
    public void imprimirAFDSimplificado() {
        System.out.println("#!dfa");
        System.out.println("#alphabet");
        for (char simbolo : alfabeto.getSimbolos()) {
            System.out.println(simbolo);
        }
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
        System.out.println("#transitions");
        //System.out.println(this.getFuncionDeTransicion());
        for (Estado estado : estados) {
            //System.out.println(funcionDeTransicion.get(estado));
            for (char simbolo : alfabeto.getSimbolos()) {
                //System.out.println(simbolo + ":" + funcionDeTransicion.get(estado).get(simbolo));
                System.out.println(estado.toString() + ":" + simbolo + ">"
                        + funcionDeTransicion.get(estado).get(simbolo).toString());
            }
        }

        AFGraphGUI gui = new AFGraphGUI(this);
        while (gui.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                // handle exception
            }
        }
    }

    // B-8
    public void exportar(String nombreAarchivo) {
        try {
            PrintWriter writer = new PrintWriter(nombreAarchivo + ".dfa", "UTF-8");
            writer.println("#!dfa");
            writer.println("#alphabet");
            for (char simbolo : alfabeto.getSimbolos()) {
                writer.println(simbolo);
            }
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
            writer.println("#transitions");
            for (Estado estado : estados) {
                for (char simbolo : alfabeto.getSimbolos()) {
                    writer.println(estado.toString() + ":" + simbolo + ">"
                            + funcionDeTransicion.get(estado).get(simbolo).toString());
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // B-8 nombre default
    public void exportar() {
        String nombreAarchivo = "nuevoAFD";
        try {
            PrintWriter writer = new PrintWriter(nombreAarchivo + ".dfa", "UTF-8");
            writer.println("#!dfa");
            writer.println("#alphabet");
            for (char simbolo : alfabeto.getSimbolos()) {
                writer.println(simbolo);
            }
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
            writer.println("#transitions");
            for (Estado estado : estados) {
                for (char simbolo : alfabeto.getSimbolos()) {
                    writer.print(estado.toString() + ":");
                    writer.println(simbolo + ">" + funcionDeTransicion.get(estado).get(simbolo).toString());
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // B-9
    public boolean procesarCadena(String cadena) {
        Estado estadoActual = estadoInicial;
        for (int i = 0; i < cadena.length(); i++) {
            estadoActual = transicion(estadoActual, cadena.charAt(i));
        }
        return estadosDeAceptacion.contains(estadoActual) ? true : false;

    }

    // B-10
    public boolean procesarCadenaConDetalles(String cadena) {
        System.out.println("proceso con cadena: " + cadena);
        Estado estadoActual = estadoInicial;
        System.out.println("Estado inicial: " + estadoActual);
        for (int i = 0; i < cadena.length(); i++) {
            estadoActual = transicion(estadoActual, cadena.charAt(i));
            System.out.println("con el caracter " + cadena.charAt(i) + " llega al estado: " + estadoActual);
        }
        System.out.println(estadosDeAceptacion.contains(estadoActual));
        return estadosDeAceptacion.contains(estadoActual) ? true : false;

    }

    // B-11
    public void procesarListaCadenas(String[] cadenas, String nombreArchivo, boolean imprimirPantalla) {
        if (nombreArchivo == null) {
            nombreArchivo = "defaultProcesarListaCadenasAFD";
        }
        Estado estadoActual = estadoInicial;
        try {
            PrintWriter writer = new PrintWriter(nombreArchivo + ".txt", "UTF-8");
            for (String cadena : cadenas) {
                estadoActual = estadoInicial;
                writer.print(cadena + "\t");
                if (imprimirPantalla)
                    System.out.print(cadena + "\t");
                for (int i = 0; i < cadena.length(); i++) {
                    writer.print("(" + estadoActual + "," + cadena.charAt(i) + ") -> ");
                    estadoActual = transicion(estadoActual, cadena.charAt(i));
                    if (imprimirPantalla)
                        System.out.print("(" + estadoActual + "," + cadena.charAt(i) + ") -> ");
                }
                writer.println("(" + estadoActual + ")\t" + estadosDeAceptacion.contains(estadoActual));
                if (imprimirPantalla)
                    System.out.println("(" + estadoActual + ")\t" + estadosDeAceptacion.contains(estadoActual));
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // B-12
    public AFD hallarComplemento() {
        AFD afd = new AFD(alfabeto, estados, funcionDeTransicion);
        afd.setEstadoInicial(estadoInicial);
        ArrayList<Estado> aceptacion = new ArrayList<>();
        for (Estado estado : afd.getEstados()) {
            if (!getEstadosDeAceptacion().contains(estado)) {
                // estado.setAceptacion(true);
                aceptacion.add(estado);
            } else {
                estado.setAceptacion(false);
            }
        }
        afd.setEstadosDeAceptacion(aceptacion);
        return afd;
    }

    // B-13
    public AFD productoCartesianoY(AFD afd1, AFD afd2) {
        AFD resultado = new AFD();
        Set<Estado> estadosProducto = new HashSet<>();
        Map<String, Estado> estadoMap = new HashMap<>();
        ArrayList<Estado> estadosRes = new ArrayList<>();
        // Se crean todos los estados del producto cartesiano
        for (Estado estado1 : afd1.getEstados()) {
            for (Estado estado2 : afd2.getEstados()) {
                String estadoProducto = estado1.toString() + "," + estado2.toString();
                Estado estado = new Estado(new Estado[] { estado1, estado2 });
                estadosProducto.add(estado);
                System.out.println(estadoProducto + "->" + estado);
                estadoMap.put(estadoProducto, estado);
                estadosRes.add(estado);
                // Si ambos estados son de aceptación, entonces el estado del producto
                // cartesiano también lo es
                if (afd1.getEstadosDeAceptacion().contains(estado1)
                        && afd2.getEstadosDeAceptacion().contains(estado2)) {
                    resultado.agregarEstadoAceptacion(estado);
                }
            }
        }
        resultado.setEstados(estadosRes);
        // Se configuran los datos del AFD resultante
        resultado.setAlfabeto(afd1.getAlfabeto());
        resultado.setEstadoInicial(
                estadoMap.get(afd1.getEstadoInicial().toString() + "," + afd2.getEstadoInicial().toString()));
        System.out.println(resultado.getEstadosDeAceptacion());
        // Se agregan las transiciones correspondientes
        for (Estado estado : estadosProducto) {
            for (char simbolo : resultado.getAlfabeto().getSimbolos()) {
                Estado[] estados = estado.getEstados();
                String est = afd1.transicion(estados[0], simbolo) + "," + afd2.transicion(estados[1], simbolo);
                System.out.println(estado + ":" + simbolo + "-> " + estadoMap.get(est));
                Estado estado1 = estadoMap.get(est);
                resultado.agregarTransicion(estado, simbolo, estado1);
            }
        }

        return resultado;
    }

    // B-14
    public AFD productoCartesianoO(AFD afd1, AFD afd2) {
        AFD resultado = new AFD();
        Set<Estado> estadosProducto = new HashSet<>();
        Map<String, Estado> estadoMap = new HashMap<>();
        ArrayList<Estado> estadosRes = new ArrayList<>();
        // Se crean todos los estados del producto cartesiano
        for (Estado estado1 : afd1.getEstados()) {
            for (Estado estado2 : afd2.getEstados()) {
                String estadoProducto = estado1.toString() + "," + estado2.toString();
                Estado estado = new Estado(new Estado[] { estado1, estado2 });
                estadosProducto.add(estado);
                System.out.println(estadoProducto + "->" + estado);
                estadoMap.put(estadoProducto, estado);
                estadosRes.add(estado);
                // Si ambos estados son de aceptación, entonces el estado del producto
                // cartesiano también lo es
                if (afd1.getEstadosDeAceptacion().contains(estado1)
                        || afd2.getEstadosDeAceptacion().contains(estado2)) {
                    resultado.agregarEstadoAceptacion(estado);
                }
            }
        }
        resultado.setEstados(estadosRes);
        // Se configuran los datos del AFD resultante
        resultado.setAlfabeto(afd1.getAlfabeto());
        resultado.setEstadoInicial(
                estadoMap.get(afd1.getEstadoInicial().toString() + "," + afd2.getEstadoInicial().toString()));
        System.out.println(resultado.getEstadosDeAceptacion());
        // Se agregan las transiciones correspondientes
        for (Estado estado : estadosProducto) {
            for (char simbolo : resultado.getAlfabeto().getSimbolos()) {
                Estado[] estados = estado.getEstados();
                String est = afd1.transicion(estados[0], simbolo) + "," + afd2.transicion(estados[1], simbolo);
                System.out.println(estado + ":" + simbolo + "-> " + estadoMap.get(est));
                Estado estado1 = estadoMap.get(est);
                resultado.agregarTransicion(estado, simbolo, estado1);
            }
        }

        return resultado;
    }

    // B-15
    public AFD productoCartesianoDiferencia(AFD afd1, AFD afd2) {
        AFD resultado = new AFD();
        Set<Estado> estadosProducto = new HashSet<>();
        Map<String, Estado> estadoMap = new HashMap<>();
        ArrayList<Estado> estadosRes = new ArrayList<>();
        // Se crean todos los estados del producto cartesiano
        for (Estado estado1 : afd1.getEstados()) {
            for (Estado estado2 : afd2.getEstados()) {
                String estadoProducto = estado1.toString() + "," + estado2.toString();
                Estado estado = new Estado(new Estado[] { estado1, estado2 });
                estadosProducto.add(estado);
                System.out.println(estadoProducto + "->" + estado);
                estadoMap.put(estadoProducto, estado);
                estadosRes.add(estado);
                // Si ambos estados son de aceptación, entonces el estado del producto
                // cartesiano también lo es
                if (afd1.getEstadosDeAceptacion().contains(estado1)
                        && !afd2.getEstadosDeAceptacion().contains(estado2)) {
                    resultado.agregarEstadoAceptacion(estado);
                }
            }
        }
        resultado.setEstados(estadosRes);
        // Se configuran los datos del AFD resultante
        resultado.setAlfabeto(afd1.getAlfabeto());
        resultado.setEstadoInicial(
                estadoMap.get(afd1.getEstadoInicial().toString() + "," + afd2.getEstadoInicial().toString()));
        System.out.println(resultado.getEstadosDeAceptacion());
        // Se agregan las transiciones correspondientes
        for (Estado estado : estadosProducto) {
            for (char simbolo : resultado.getAlfabeto().getSimbolos()) {
                Estado[] estados = estado.getEstados();
                String est = afd1.transicion(estados[0], simbolo) + "," + afd2.transicion(estados[1], simbolo);
                System.out.println(estado + ":" + simbolo + "-> " + estadoMap.get(est));
                Estado estado1 = estadoMap.get(est);
                resultado.agregarTransicion(estado, simbolo, estado1);
            }
        }

        return resultado;
    }

    // B-16
    public AFD productoCartesianoDiferenciaSimetrica(AFD afd1, AFD afd2) {
        AFD resultado = new AFD();
        Set<Estado> estadosProducto = new HashSet<>();
        Map<String, Estado> estadoMap = new HashMap<>();
        ArrayList<Estado> estadosRes = new ArrayList<>();
        // Se crean todos los estados del producto cartesiano
        for (Estado estado1 : afd1.getEstados()) {
            for (Estado estado2 : afd2.getEstados()) {
                String estadoProducto = estado1.toString() + "," + estado2.toString();
                Estado estado = new Estado(new Estado[] { estado1, estado2 });
                estadosProducto.add(estado);
                System.out.println(estadoProducto + "->" + estado);
                estadoMap.put(estadoProducto, estado);
                estadosRes.add(estado);
                // Si ambos estados son de aceptación, entonces el estado del producto
                // cartesiano también lo es
                boolean cond1 = afd1.getEstadosDeAceptacion().contains(estado1);
                boolean cond2 = afd2.getEstadosDeAceptacion().contains(estado2);
                if ((cond1 || cond2) && !(cond1 && cond2)) {
                    resultado.agregarEstadoAceptacion(estado);
                }
            }
        }
        resultado.setEstados(estadosRes);
        // Se configuran los datos del AFD resultante
        resultado.setAlfabeto(afd1.getAlfabeto());
        resultado.setEstadoInicial(
                estadoMap.get(afd1.getEstadoInicial().toString() + "," + afd2.getEstadoInicial().toString()));
        System.out.println(resultado.getEstadosDeAceptacion());
        // Se agregan las transiciones correspondientes
        for (Estado estado : estadosProducto) {
            for (char simbolo : resultado.getAlfabeto().getSimbolos()) {
                Estado[] estados = estado.getEstados();
                String est = afd1.transicion(estados[0], simbolo) + "," + afd2.transicion(estados[1], simbolo);
                System.out.println(estado + ":" + simbolo + "-> " + estadoMap.get(est));
                Estado estado1 = estadoMap.get(est);
                resultado.agregarTransicion(estado, simbolo, estado1);
            }
        }

        return resultado;
    }

    // B-17
    public AFD productoCartesiano(AFD afd1, AFD afd2, String operacion) {
        switch (operacion) {
            case "interseccion":
                return productoCartesianoY(afd1, afd2);
            case "union":
                return productoCartesianoO(afd1, afd2);
            case "diferencia":
                return productoCartesianoDiferencia(afd1, afd2);
            case "diferencia simetrica":
                return productoCartesianoDiferenciaSimetrica(afd1, afd2);
            default:
                System.err.println("Operacion invalida");
                return null;
        }
    }

    // B-18
    public AFD simplificarAFD(AFD afdInput) {
        AFD sinInaccesibles = eliminarEstadosInaccesibles(afdInput);
        Estado[] estados = new Estado[sinInaccesibles.getEstados().size()];
        estados = sinInaccesibles.getEstados().toArray(estados);
        AFD nuevoAfd = new AFD(afdInput.getAlfabeto());
        Character[][] TablaEquivalencia = new Character[estados.length][estados.length];
        ArrayList<ArrayList<Estado[]>> tablaTransiciones = new ArrayList<ArrayList<Estado[]>>();
        int iteracion = 1;
        for (int i = 0; i < estados.length; i++) {
            for (int j = i; j < estados.length; j++) {
                if (estados[i].isAceptacion() != estados[j].isAceptacion()) {
                    TablaEquivalencia[i][j] = TablaEquivalencia[j][i] = (char) (iteracion + '0');
                } else if (i != j) {
                    ArrayList<Estado[]> inner = new ArrayList<>();
                    inner.add(new Estado[] { estados[i], estados[j] });
                    for (char simbolo : sinInaccesibles.getAlfabeto().getSimbolos()) {
                        inner.add(new Estado[] { transicion(estados[i], simbolo), transicion(estados[j], simbolo) });
                    }
                    tablaTransiciones.add(inner);
                }
            }
        }

        boolean cambio = true;

        while (cambio) {
            cambio = false;
            iteracion++;

            for (int i = 0; i < tablaTransiciones.size(); i++) {
                for (int j = 0; j < sinInaccesibles.getAlfabeto().size(); j++) {
                    Estado[] estadosAct = tablaTransiciones.get(i).get(j + 1);
                    int x = sinInaccesibles.getEstados().indexOf(estadosAct[0]);
                    int y = sinInaccesibles.getEstados().indexOf(estadosAct[1]);
                    Estado[] temp = tablaTransiciones.get(i).get(j);
                    int x2 = sinInaccesibles.getEstados().indexOf(temp[0]);
                    int y2 = sinInaccesibles.getEstados().indexOf(temp[1]);
                    if ((TablaEquivalencia[x2][y2] == null || TablaEquivalencia[y2][x2] == null)
                            && (TablaEquivalencia[x][y] != null || TablaEquivalencia[y][x] != null)) {
                        TablaEquivalencia[x2][y2] = TablaEquivalencia[y2][x2] = (char) (iteracion + '0');
                        cambio = true;
                    }
                }
            }
        }

        System.out.println("-----Tabla transiciones----------");
        for (int i = 0; i < tablaTransiciones.size(); i++) {
            for (int j = 0; j < sinInaccesibles.getAlfabeto().size() + 1; j++) {
                System.out.print(tablaTransiciones.get(i).get(j)[0] + "," + tablaTransiciones.get(i).get(j)[1] + "->");

            }
            System.out.println(" ");
        }
        System.out.println("---------------------------------");
        for (int i = 0; i < estados.length; i++) {
            for (int j = 0; j < estados.length; j++) {
                if (i != j && TablaEquivalencia[i][j] == null)
                    TablaEquivalencia[i][j] = 'E';
            }
        }
        System.out.println("-----Tabla Equivalencias----------");
        for (int i = 0; i < estados.length; i++) {
            for (int j = 0; j < i; j++) {
                System.out.print(TablaEquivalencia[i][j]);
            }
            System.out.println();
        }
        System.out.println("---------------------------------");

        ArrayList<ArrayList<Estado>> equivalentes = new ArrayList<ArrayList<Estado>>();
        ArrayList<Estado> revisados = new ArrayList<>();
        for (int i = 0; i < estados.length; i++) {
            if (!revisados.contains(estados[i])) {
                ArrayList<Estado> inner = new ArrayList<>();
                inner.add(estados[i]);
                revisados.add(estados[i]);
                for (int j = 0; j < estados.length; j++) {
                    if (TablaEquivalencia[i][j] != null) {
                        if (TablaEquivalencia[i][j] == 'E') {
                            inner.add(estados[j]);
                            revisados.add(estados[j]);
                        }
                    }
                }
                equivalentes.add(inner);
            }
        }
        System.out.println("------Estados equivalentes-----------");
        for (ArrayList<Estado> array : equivalentes) {
            for (Estado estado : array) {
                System.out.print(estado);
            }
            System.out.println();
        }
        System.out.println("---------------------------------");
        ArrayList<Estado> estadosNuevos = new ArrayList<>();
        Map<Estado, Estado> estadoMap = new HashMap<>();
        for (ArrayList<Estado> array : equivalentes) {
            Estado[] estadosNE = new Estado[array.size()];
            estadosNE = array.toArray(estadosNE);
            // crear estado nuevo
            Estado nuevoEstado = new Estado(estadosNE);
            for (Estado estado : estadosNE) {
                // establecer si es inicial
                if (estado.isInicial()) {
                    nuevoEstado.setInicial(true);
                }
                // establecer si es de aceptacion
                if (estado.isAceptacion()) {
                    nuevoEstado.setAceptacion(true);
                }
                estadoMap.put(estado, nuevoEstado);
            }
            estadosNuevos.add(nuevoEstado);
        }
        nuevoAfd.setEstados(estadosNuevos);
        nuevoAfd.setEstadoInicial(estadosNuevos.get(0));
        ArrayList<Estado> nuevosEstadosAcept = new ArrayList<>();
        // agregamos las transiciones
        for (Estado estado : estadosNuevos) {
            if (estado.isAceptacion()) {
                nuevosEstadosAcept.add(estado);
            }
            for (char simbolo : afdInput.getAlfabeto().getSimbolos()) {
                Estado[] estados1 = estado.getEstados();
                Estado estado1 = estadoMap.get(transicion(estados1[0], simbolo));
                // solo tomamos la transicion del primer elemento ya que se entiende que si esta
                // bien
                // simplificado, todos los estados internos de un estado nuevo iran a un mismo
                // estado nuevo
                nuevoAfd.agregarTransicion(estado, simbolo, estado1);
            }
        }

        return nuevoAfd;
    }

    // Soporte
    private void agregarEstadoAceptacion(Estado estado) {
        estado.setAceptacion(true);
        estadosDeAceptacion.add(estado);
    }

    // Soporte
    private AFD eliminarEstadosInaccesibles(AFD afdInput) {
        ArrayList<Estado> nuevosEstados = new ArrayList<>();
        nuevosEstados.addAll(afdInput.getEstados());
        ArrayList<Estado> nuevosEstados2 = new ArrayList<>();
        nuevosEstados2.addAll(nuevosEstados);
        HashMap<Estado, HashMap<Character, Estado>> nuevaFuncionTransicion = afdInput.getFuncionDeTransicion();
        for (Estado estado : nuevosEstados) {
            if (!estado.isAccesible()) {
                nuevaFuncionTransicion.remove(estado);
                nuevosEstados2.remove(estado);
            }
        }
        AFD nuevoAfd = new AFD(afdInput.getAlfabeto(), nuevosEstados2, nuevaFuncionTransicion);

        return nuevoAfd;
    }

    // Getters & Setters
    public Alfabeto getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto(Alfabeto alfabeto) {
        this.alfabeto = alfabeto;
    }

    public ArrayList<Estado> getEstados() {
        return estados;
    }

    public void setEstados(ArrayList<Estado> estados) {
        this.estados = estados;
    }

    public Estado getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(Estado estadoInicial) {
        estadoInicial.setInicial(true);
        this.estadoInicial = estadoInicial;
    }

    public ArrayList<Estado> getEstadosDeAceptacion() {
        return estadosDeAceptacion;
    }

    public void setEstadosDeAceptacion(ArrayList<Estado> estadosDeAceptacion) {
        for (Estado estado : estadosDeAceptacion)
            estado.setAceptacion(true);
        this.estadosDeAceptacion = estadosDeAceptacion;
    }

    public HashMap<Estado, HashMap<Character, Estado>> getFuncionDeTransicion() {
        return funcionDeTransicion;
    }

    public void setFuncionDeTransicion(HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion) {
        this.funcionDeTransicion = funcionDeTransicion;
    }

    public ArrayList<Estado> getEstadosLimbo() {
        return estadosLimbo;
    }

    public void setEstadosLimbo(ArrayList<Estado> estadosLimbo) {
        for (Estado estado : estadosLimbo)
            estado.setLimbo(true);
        this.estadosLimbo = estadosLimbo;
    }

    public ArrayList<Estado> getEstadosInaccesibles() {
        return estadosInaccesibles;
    }

    public void setEstadosInaccesibles(ArrayList<Estado> estadosInaccesibles) {
        for (Estado estado : estadosInaccesibles)
            estado.setAccesible(false);
        this.estadosInaccesibles = estadosInaccesibles;
    }

    // B-6
    @Override
    public String toString() {
        return "AFD [alfabeto=" + alfabeto + ", estados=" + estados + ", estadoInicial=" + estadoInicial
                + ", estadosDeAceptacion=" + estadosDeAceptacion + ", funcionDeTransicion=" + funcionDeTransicion
                + ", estadosLimbo=" + estadosLimbo + ", estadosInaccesibles=" + estadosInaccesibles + "]";
    }

    public static void main(String[] args) {
        // char[] simbolos = { 'a' ,'b'};
        // Alfabeto alf = new Alfabeto(simbolos);
        // int numEstados = 3;
        // ArrayList<Estado> estados = new ArrayList<Estado>();
        // for (int i = 0; i < numEstados; i++) {
        //     estados.add(new Estado());
        // }
        // HashMap<Estado, HashMap<Character, Estado>> funcionDeTransicion = new HashMap<>();
        /*
         * for (Estado estado : estados) {
         * HashMap<Character, Estado> transiciones = new HashMap<>();
         * for (char simbolo : alf.getSimbolos()) {
         * transiciones.put(simbolo, estados.get((int)(Math.random() * numEstados)));
         * }
         * funcionDeTransicion.put(estado, transiciones);
         * }
         */
        AFD afd = new AFD("ejemploSimp");
        // afd.fillTransitions();
        // ArrayList<Estado> estadosAcept = new ArrayList<>();
        // estadosAcept.add(estados.get(2));
        // afd.setEstadoInicial(estados.get(0));
        // afd.setEstadosDeAceptacion(estadosAcept);
        // System.out.println(afd.funcionDeTransicion);
        // try {
        //     System.in.read();
        // } catch (Exception e) {
        //     // TODO: handle exception
        // }
        //afd.verificarCorregirCompletitudAFD();
        

        afd.imprimirAFDSimplificado();
        AFD afd2 = afd.simplificarAFD(afd);
        try {
            System.in.read();
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        afd2.imprimirAFDSimplificado();
        // String[] listaCadenas ={"aab","aaba","baaba"};
        // afd.procesarListaCadenas(listaCadenas,"ListCadAFD",true);

        //afd.exportar("ejemploProcesar");

        // AFD afds = afd.simplificarAFD(afd);
        // afds.imprimirAFDSimplificado();
    }
}
