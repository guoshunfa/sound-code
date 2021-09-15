package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class SOAPIOException extends IOException {
   SOAPExceptionImpl soapException;

   public SOAPIOException() {
      this.soapException = new SOAPExceptionImpl();
      this.soapException.fillInStackTrace();
   }

   public SOAPIOException(String s) {
      this.soapException = new SOAPExceptionImpl(s);
      this.soapException.fillInStackTrace();
   }

   public SOAPIOException(String reason, Throwable cause) {
      this.soapException = new SOAPExceptionImpl(reason, cause);
      this.soapException.fillInStackTrace();
   }

   public SOAPIOException(Throwable cause) {
      super(cause.toString());
      this.soapException = new SOAPExceptionImpl(cause);
      this.soapException.fillInStackTrace();
   }

   public Throwable fillInStackTrace() {
      if (this.soapException != null) {
         this.soapException.fillInStackTrace();
      }

      return this;
   }

   public String getLocalizedMessage() {
      return this.soapException.getLocalizedMessage();
   }

   public String getMessage() {
      return this.soapException.getMessage();
   }

   public void printStackTrace() {
      this.soapException.printStackTrace();
   }

   public void printStackTrace(PrintStream s) {
      this.soapException.printStackTrace(s);
   }

   public void printStackTrace(PrintWriter s) {
      this.soapException.printStackTrace(s);
   }

   public String toString() {
      return this.soapException.toString();
   }
}
