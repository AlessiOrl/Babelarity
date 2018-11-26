package it.uniroma1.lcl.babelarity;

import java.util.HashMap;

public class VectorizedLinguisticObj<R, T extends Number> extends HashMap<R, T[]>
{

    private static int MIN_VALUE = 2;
    private int maxLength;

    public VectorizedLinguisticObj(int maxLength)
    {
        super();
        this.maxLength = maxLength > MIN_VALUE ? maxLength : MIN_VALUE;
    }

    public VectorizedLinguisticObj()
    {
        this(20);
    }

    public T[] put(R obj, T[] vector)
    {
        if (this.size() <= maxLength) return super.put(obj, vector);
        this.remove();
        return super.put(obj, vector);
    }

    private T[] remove()
    {
        return super.remove(this.keySet().iterator().next());
    }

}
