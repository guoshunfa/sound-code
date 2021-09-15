package org.xml.sax;

public class SAXException extends Exception {
   private Exception exception;
   static final long serialVersionUID = 583241635256073760L;

   public SAXException() {
      this.exception = null;
   }

   public SAXException(String message) {
      super(message);
      this.exception = null;
   }

   public SAXException(Exception e) {
      this.exception = e;
   }

   public SAXException(String message, Exception e) {
      super(message);
      this.exception = e;
   }

   public String getMessage() {
      String message = super.getMessage();
      return message == null && this.exception != null ? this.exception.getMessage() : message;
   }

   public Exception getException() {
      return this.exception;
   }

   public Throwable getCause() {
      return this.exception;
   }

   public String toString() {
      return this.exception != null ? super.toString() + "\n" + this.exception.toString() : super.toString();
   }
}
