package com.sun.xml.internal.org.jvnet.mimepull;

public class MIMEParsingException extends RuntimeException {
   public MIMEParsingException() {
   }

   public MIMEParsingException(String message) {
      super(message);
   }

   public MIMEParsingException(String message, Throwable cause) {
      super(message, cause);
   }

   public MIMEParsingException(Throwable cause) {
      super(cause);
   }
}
