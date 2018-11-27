package it.uniroma1.lcl.babelarity;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Objects;

public class Document implements LinguisticObject, Serializable {

  private String id;
  private String title;
  private String content;

  public Document(String title, String id, String content, Path path) {
    this.id = id;
    this.title = title;
    this.content = content;
  }

  /**
   * @return restituisce l' id del documento
   */

  public String getId() {
    return id;
  }

  /**
   * @return il titolo del documuento
   */
  public String getTitle() {
    return title;
  }

  /**
   * @return il contenuto del documento sotto forma di stringa
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
