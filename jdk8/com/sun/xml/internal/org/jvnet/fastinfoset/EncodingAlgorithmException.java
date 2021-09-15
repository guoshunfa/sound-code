package com.sun.xml.internal.org.jvnet.fastinfoset;

public class EncodingAlgorithmException extends FastInfosetException {
   public EncodingAlgorithmException(String message) {
      super(message);
   }

   public EncodingAlgorithmException(String message, Exception e) {
      super(message, e);
   }

   public EncodingAlgorithmException(Exception e) {
      super(e);
   }
}
