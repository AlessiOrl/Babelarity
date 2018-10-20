package it.uniroma1.lcl.babelarity;

import java.util.HashMap;
import java.util.HashSet;

public class Test
{

    public static void main(String[] args)
    {
        HashSet<String> set = new HashSet<>();
        HashMap<String, Integer> map = new HashMap<>();

        int n = 40000;
        int number = 100;
        for (int x = 0; x <= n; x++)
        {
            set.add(String.valueOf(x));
            map.put(String.valueOf(x), x);
        }
        long totalSet = 0;
        long totalMap = 0;
        for (int x = 0; x <= number; x++)
        {

            long start = System.nanoTime();
            boolean check = set.contains(String.valueOf(Math.round(Math.random() * n)));
            long end = System.nanoTime();

            totalSet+=end-start;

            start = System.nanoTime();
            check = map.containsKey(String.valueOf(Math.floor(Math.random() * n)));
            end = System.nanoTime();
            totalMap+=end-start;
        }
        System.out.println("CHECK FROM SET " + totalSet/number + " nanoSec on " + number + " iterations");
        System.out.println("CHECK FROM MAP " + totalMap/number + " nanoSec on "+ number + " iterations");


    }
}
