package TALLER.Test;
import org.junit.jupiter.api.Test;

import TALLER.Estado;

public class EstadoTest {
    @Test
    void testToString() {

        Estado estado = new Estado();
        System.out.println(estado.toString()); 
        Estado estado1 = new Estado();
        System.out.println(estado1.toString()); 
    }
}
