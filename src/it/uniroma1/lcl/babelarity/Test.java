package it.uniroma1.lcl.babelarity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Test
{

    public static void main(String[] args)
    {
        Path f = Paths.get("resources/corpus");
        ArrayList<Path> test = new ArrayList<>();
        try
        {
            Files.list(f).forEach(test::add);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println(test);

    }
}
