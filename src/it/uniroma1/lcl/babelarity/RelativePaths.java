package it.uniroma1.lcl.babelarity;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class store all the relative paths used.
 */
public interface RelativePaths {

  Path RESOURCES = Paths.get("resources");
  Path STOPWORDS = Paths.get("src").resolve("stopWords.txt");

  Path DOCUMENTS = RESOURCES.resolve("documents");
  Path CORPUS = RESOURCES.resolve("corpus");

  Path PARSED_DOCUMENTS = DOCUMENTS.resolve("parsed");

  Path LEMMATIZATIONS = RESOURCES.resolve("lemmatization-en.txt");
  Path DICTIONARY = RESOURCES.resolve("dictionary.txt");
  Path RELATIONS = RESOURCES.resolve("relations.txt");
  Path GLOSSES = RESOURCES.resolve("glosses.txt");

}
