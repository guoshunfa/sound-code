package com.sun.xml.internal.stream.buffer;

public class XMLStreamBufferException extends Exception {
   public XMLStreamBufferException(String message) {
      super(message);
   }

   public XMLStreamBufferException(String message, Exception e) {
      super(message, e);
   }

   public XMLStreamBufferException(Exception e) {
      super((Throwable)e);
   }
}
