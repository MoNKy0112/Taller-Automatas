package TALLER;
public class Estado {
    private static int cont = -1;
    private int id;
    private boolean limbo, accesible, aceptacion=false, inicial;
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
        this.inicial = false;
        this.accesible = true;
        this.aceptacion = false;
        this.limbo = false;
    }

    public Estado() {
        this.id = ++cont;
        this.inicial = false;
        this.accesible = true;
        this.aceptacion = false;
        this.limbo = false;
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
        if(estados!=null){
            String txt = "";
            for (int i=0;i<estados.length-1;i++){
                txt=txt+estados[i].toString()+",";
            }
            txt+=estados[estados.length-1].toString();
        }
        return "q" + id;
    }

    
}
