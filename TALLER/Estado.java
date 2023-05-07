package TALLER;
public class Estado {
    private static int cont = 0;
    private int id;
    private boolean limbo, accesible, aceptacion, inicial;
    private Estado[] estados;

    

    public Estado(boolean inicial, boolean aceptacion, boolean accesible, boolean limbo) {
        this.id = ++cont;
        this.limbo = limbo;
        this.accesible = accesible;
        this.aceptacion = aceptacion;
        this.inicial = inicial;
    }    

    public Estado(Estado[] estados) {
        this.id = ++cont;
        this.estados = estados;
    }

    public Estado() {
        this.id = ++cont;
    }

    public Estado(int id) {
        this.id = id;
    }


    //Getters & Setters
    public void setNombre(Estado[] estados) {
        this.estados = estados;
    }

    public Estado[] getEstados() {
        return estados;
    }

    public boolean isLimbo() {
        return limbo;
    }

    public void setLimbo(boolean limbo) {
        this.limbo = limbo;
    }

    public boolean isAccesible() {
        return accesible;
    }

    public void setAccesible(boolean accesible) {
        this.accesible = accesible;
    }

    public boolean isAceptacion() {
        return aceptacion;
    }

    public void setAceptacion(boolean aceptacion) {
        this.aceptacion = aceptacion;
    }

    public boolean isInicial() {
        return inicial;
    }

    public void setInicial(boolean inicial) {
        this.inicial = inicial;
    }

    public static int getCont() {
        return cont;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "q" + id;
    }

    
}
