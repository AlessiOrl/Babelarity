package it.uniroma1.lcl.babelarity.linguisticobject;

import it.uniroma1.lcl.babelarity.MiniBabelNet;

import java.util.Objects;

/**
 * This class define a typology of {@link LinguisticObject LinguisticObject}.
 * A word is the most basic LinguisticObject in {@link MiniBabelNet MiniBabelNet}; <br> is made up only by a String that rapresent a lemma.
 *
 */
public class Word implements LinguisticObject {

  private String word;

  private Word(String word) {
    this.word = word;
  }

  /**
   * take a String an return the relative Word.
   * @param s
   * @return the lemma of the string. Null if there is no lemma in the database.
   */
  public static Word fromString(String s) {
    return new Word(MiniBabelNet.takeWord(s));
  }

  @Override
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