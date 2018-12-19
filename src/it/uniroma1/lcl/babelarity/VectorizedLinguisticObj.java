package it.uniroma1.lcl.babelarity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class extend the SuperClass {@link HashMap}, his aim is to create a kind of cache to save and store the vector created for the {@code Cosine Similarity} algorithm.
 * The default parameter for the MAXIMUM storage is 20, but it can be changed during the creation.
 * The MINIMUM size of storage is two, is final because the {@code Cosine Similarity} algorithm work with two vectors.
 *
 * @param <R> "IL TIPO DA CUI CREIAMO IL VETTORE" todo:tradurre
 * @param <T> The type of all the elements of the vector.
 */
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

  /**
   * This method put a new vector in the object and, if there is more vectors than the maximum size, decide which one should be removed.
   *
   * @param obj
   * @param vector
   * @return the new vector.
   */
  public T[] put(R obj, T[] vector) {
    if (this.size() == maxLength)
      this.remove(queue.poll());
    queue.add(obj);
    return super.put(obj, vector);
  }
}
