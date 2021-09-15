package com.sun.xml.internal.org.jvnet.fastinfoset;

public class ExternalVocabulary {
   public final String URI;
   public final Vocabulary vocabulary;

   public ExternalVocabulary(String URI, Vocabulary vocabulary) {
      if (URI != null && vocabulary != null) {
         this.URI = URI;
         this.vocabulary = vocabulary;
      } else {
         throw new IllegalArgumentException();
      }
   }
}
