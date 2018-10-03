package it.uniroma1.lcl.babelarity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class Test
{

    public static void main(String[] args)
    {
        Path f = Paths.get("resources/corpus");
        ArrayList<Path> test = new ArrayList<>();
        try
        {
            Files.list(f).forEach(test::add);
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println(test);

    }
}
