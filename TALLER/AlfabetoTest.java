package TALLER;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import java.util.Random;

public class AlfabetoTest {

    Random rand = new Random();

    @Test
    void testGenerarCadenaAleatoria() {
        char [] simbolos = {'0','1'};
        Alfabeto alfabeto = new Alfabeto(simbolos);
        int n = rand.nextInt(15);
        String cadenaAleat = alfabeto.generarCadenaAleatoria(n);
        assertEquals(n, cadenaAleat.length());
        System.out.println(cadenaAleat);
    }
}
