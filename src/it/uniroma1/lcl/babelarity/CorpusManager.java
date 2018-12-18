package it.uniroma1.lcl.babelarity;

import it.uniroma1.lcl.babelarity.linguisticobject.Document;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class parse the given {@link Document Documents}.
 */
public class CorpusManager implements Iterable<Document> {

  private static CorpusManager instance;
  private static HashSet<Document> parsedDocuments;

  private static HashSet<String> stopWords;

  private CorpusManager() {
    parsedDocuments = new HashSet<>();
    CorpusManager.parseStopWords();
  }

  /**
   * Retrun the instance of the class
   * @return a {@link CorpusManager} object.
   */
  public static CorpusManager getInstance() {
    if (instance == null)
      instance = new CorpusManager();
    return instance;
  }

  /**
   * This method parse the StopWords file
   */
  private static void parseStopWords() {
    try (Stream<String> streamStopWords = Files.lines(RelativePaths.STOPWORDS)) {
      stopWords = streamStopWords.collect(Collectors.toCollection(HashSet::new));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @return The {@link HashSet} of the parsed stopWords.
   */
  public static HashSet<String> getStopWords() {
    return stopWords;
  }

  /**
   * @return a new instance of {@link Document} parsing a text file taken via the given path.
   */
  public Document parseDocument(Path path) {
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      String[] fstLine = reader.readLine()
                               .split("\t");
      Document doc = new Document(fstLine[0], fstLine[1], reader.lines()
                                                                .collect(Collectors.joining("\n")), path);
      parsedDocuments.add(doc);
      return doc;

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @return Load from the disk l'object {@link Document} identified by ID.
   */
  public Document loadDocument(String id) {
    try (FileInputStream streamFile = new FileInputStream(RelativePaths.PARSED_DOCUMENTS.resolve(id + ".ser").toFile()); ObjectInputStream streamObj = new ObjectInputStream(streamFile)) {
      return (Document) streamObj.readObject();
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  /**
   * Save on disk l'object {@link Document} given in the input.
   */
  public void saveDocument(Document document) {
    try (FileOutputStream streamFile = new FileOutputStream(RelativePaths.PARSED_DOCUMENTS.resolve(document.getId() + ".ser").toFile());
         ObjectOutputStream oos = new ObjectOutputStream(streamFile)) {
      oos.writeObject(document);
    } catch (IOException e) {
      System.out.println(e);
    }
  }


  @Override
  public Iterator<Document> iterator() {
    return parsedDocuments.iterator();
  }
}
