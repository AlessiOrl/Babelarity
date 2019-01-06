package it.uniroma1.lcl.babelarity;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Objects;

/**
 * This class define a typology of {@link LinguisticObject LinguisticObject}.
 * A Document is made up by an ID a title and a content.
 */
public class Document implements LinguisticObject, Serializable {

  private String id;
  private String title;
  private String content;

  /**
   * The constructor of the class.
   * @param title : the title of the Document
   * @param id : the ID of the Docuemnt
   * @param content : the content of the Document
   * @param path : the Path where the Document file is located
   */
  public Document(String title, String id, String content, Path path) {
    this.id = id;
    this.title = title;
    this.content = content;
  }

  /**
   * @return the ID of the Document
   */

  public String getId() {
    return id;

  }

  /**
   * @return the title of the Document
   */
  public String getTitle() {
    return title;
  }

  /**
   * @return the content of the Document in a String form
   */
  public String getContent() {
    return content;
  }

  @Override
  public String toString() {
    return title + "\t" + id + "\n" + content;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    if (o == this)
      return true;
    if (!(o instanceof Document))
      return false;
    Document oDocument = (Document) o;
    return this.id.equals(oDocument.id) && this.title.equals(oDocument.title) && this.content.equals(oDocument.content);

  }
}
