package it.uniroma1.lcl.babelarity;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface RelativePaths {

  Path RESOURCES = Paths.get("resources");

  Path DOCUMENTS = RESOURCES.resolve("documents");
  Path CORPUS = RESOURCES.resolve("corpus");

  Path PARSED_DOCUMENTS = DOCUMENTS.resolve("parsed");

  Path LEMMATIZATIONS = RESOURCES.resolve("lemmatization-en.txt");
  Path DICTIONARY = RESOURCES.resolve("dictionary.txt");
  Path RELATIONS = RESOURCES.resolve("relations.txt");
  Path STOPWORDS = RESOURCES.resolve("stopWords.txt");
  Path GLOSSES = RESOURCES.resolve("glosses.txt");

}
