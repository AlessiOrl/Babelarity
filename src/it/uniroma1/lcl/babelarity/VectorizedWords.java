package it.uniroma1.lcl.babelarity;

import java.util.HashMap;

public class VectorizedWords extends HashMap<String, Float[]>
{

    //TODO: provare ad usare una coda per decidere chi eliminare dalla mappa (probabile indexing tramite queue)
    private static int MIN_VALUE = 2;
    private int maxLength;

    public VectorizedWords(int maxLength)
    {
        super();
        this.maxLength = maxLength > MIN_VALUE ? maxLength : MIN_VALUE;
    }

    public VectorizedWords()
    {
        this(200);
    }

    public Float[] put(String word, Float[] vector)
    {
        if (this.size() <= maxLength) return super.put(word, vector);
        this.remove();
        return super.put(word, vector);
    }

    private Float[] remove()
    {
        return super.remove(this.keySet().iterator().next());
    }

}
