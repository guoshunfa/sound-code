package com.sun.xml.internal.messaging.saaj;

import java.io.PrintStream;
import java.io.PrintWriter;
import javax.xml.soap.SOAPException;

public class SOAPExceptionImpl extends SOAPException {
   private Throwable cause;

   public SOAPExceptionImpl() {
      this.cause = null;
   }

   public SOAPExceptionImpl(String reason) {
      super(reason);
      this.cause = null;
   }

   public SOAPExceptionImpl(String reason, Throwable cause) {
      super(reason);
      this.initCause(cause);
   }

   public SOAPExceptionImpl(Throwable cause) {
      super(cause.toString());
      this.initCause(cause);
   }

   public String getMessage() {
      String message = super.getMessage();
      return message == null && this.cause != null ? this.cause.getMessage() : message;
   }

   public Throwable getCause() {
      return this.cause;
   }

   public synchronized Throwable initCause(Throwable cause) {
      if (this.cause != null) {
         throw new IllegalStateException("Can't override cause");
      } else if (cause == this) {
         throw new IllegalArgumentException("Self-causation not permitted");
      } else {
         this.cause = cause;
         return this;
      }
   }

   public void printStackTrace() {
      super.printStackTrace();
      if (this.cause != null) {
         System.err.println("\nCAUSE:\n");
         this.cause.printStackTrace();
      }

   }

   public void printStackTrace(PrintStream s) {
      super.printStackTrace(s);
      if (this.cause != null) {
         s.println("\nCAUSE:\n");
         this.cause.printStackTrace(s);
      }

   }

   public void printStackTrace(PrintWriter s) {
      super.printStackTrace(s);
      if (this.cause != null) {
         s.println("\nCAUSE:\n");
         this.cause.printStackTrace(s);
      }

   }
}
