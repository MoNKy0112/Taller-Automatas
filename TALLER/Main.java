package TALLER;

import java.util.Arrays;

public class Main {
    public static void main(String[] args){
        Estado[] estadosNE = {new Estado(),new Estado(),new Estado()};
        String[] strings = Arrays.stream(estadosNE).map(Object::toString).
        toArray(String[]::new);
        System.out.println(String.join(",", strings));
    }
}

