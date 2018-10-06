package it.uniroma1.lcl.babelarity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VectorizedWords implements Map<String, Float[]>
{

    private static int MIN_VALUE = 2;
    private HashMap<String, Float[]> vectorizedWords;
    private int maxLength;

    public VectorizedWords(int maxLength)
    {
        vectorizedWords = new HashMap<>();
        this.maxLength = maxLength > 2 ? maxLength : MIN_VALUE;
    }

    public VectorizedWords()
    {
        this(200);
    }

    @Override
    public int size()
    {
        return vectorizedWords.size();
    }

    @Override
    public boolean isEmpty()
    {
        return vectorizedWords.size() == 0;
    }

    @Override
    public boolean containsKey(Object key)
    {
        return vectorizedWords.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return vectorizedWords.containsValue(value);
    }

    @Override
    public Float[] get(Object key)
    {
        return vectorizedWords.get(key);
    }

    public Float[] put(String word, Float[] vector)
    {
        if (vectorizedWords.size() <= maxLength) return vectorizedWords.put(word, vector);
        this.remove();
        return this.put(word, vector);
    }

    @Override
    public Float[] remove(Object key)
    {
        return vectorizedWords.remove(key);
    }

    public Float[] remove()
    {
        return vectorizedWords.remove(vectorizedWords.keySet().iterator().next());
    }

    @Override
    public void putAll(Map<? extends String, ? extends Float[]> m)
    {
        vectorizedWords.putAll(m);
    }

    @Override
    public void clear()
    {
        vectorizedWords = new HashMap<>();
    }

    @Override
    public Set<String> keySet()
    {
        return vectorizedWords.keySet();
    }

    @Override
    public Collection<Float[]> values()
    {
        return vectorizedWords.values();
    }

    @Override
    public Set<Entry<String, Float[]>> entrySet()
    {
        return vectorizedWords.entrySet();
    }
}
