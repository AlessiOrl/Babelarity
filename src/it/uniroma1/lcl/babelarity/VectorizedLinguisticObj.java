package it.uniroma1.lcl.babelarity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class VectorizedLinguisticObj<R, T extends Number> extends HashMap<R, T[]> {

  private static final int MIN_VALUE = 2;
  private Queue<R> queue;
  private int maxLength;

  public VectorizedLinguisticObj(int maxLength) {
    super();
    this.maxLength = maxLength > MIN_VALUE ? maxLength : MIN_VALUE;
    this.queue = new LinkedList<>();
  }

  public VectorizedLinguisticObj() {
    this(20);
  }

  public T[] put(R obj, T[] vector) {
    if (this.size() == maxLength) this.remove(queue.poll());
    queue.add(obj);
    return super.put(obj, vector);
  }

}
