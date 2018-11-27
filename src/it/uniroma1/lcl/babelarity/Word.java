package it.uniroma1.lcl.babelarity;

import java.util.Objects;

public class Word implements LinguisticObject {

  private String word;

  private Word(String word) {
    this.word = word;
  }

  public static Word fromString(String s) {
    return new Word(MiniBabelNet.takeWord(s));
  }

  public String toString() {
    return word;
  }

  @Override
  public int hashCode() {
    return Objects.hash(word);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof Word))
      return false;
    return ((Word) obj).word.equals(this.word);
  }
}
