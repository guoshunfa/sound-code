package com.sun.org.apache.xml.internal.security.exceptions;

import com.sun.org.apache.xml.internal.security.utils.I18n;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;

public class XMLSecurityException extends Exception {
   private static final long serialVersionUID = 1L;
   protected String msgID;

   public XMLSecurityException() {
      super("Missing message string");
      this.msgID = null;
   }

   public XMLSecurityException(String var1) {
      super(I18n.getExceptionMessage(var1));
      this.msgID = var1;
   }

   public XMLSecurityException(String var1, Object[] var2) {
      super(MessageFormat.format(I18n.getExceptionMessage(var1), var2));
      this.msgID = var1;
   }

   public XMLSecurityException(Exception var1) {
      super("Missing message ID to locate message string in resource bundle \"com/sun/org/apache/xml/internal/security/resource/xmlsecurity\". Original Exception was a " + var1.getClass().getName() + " and message " + var1.getMessage(), var1);
   }

   public XMLSecurityException(String var1, Exception var2) {
      super(I18n.getExceptionMessage(var1, var2), var2);
      this.msgID = var1;
   }

   public XMLSecurityException(String var1, Object[] var2, Exception var3) {
      super(MessageFormat.format(I18n.getExceptionMessage(var1), var2), var3);
      this.msgID = var1;
   }

   public String getMsgID() {
      return this.msgID == null ? "Missing message ID" : this.msgID;
   }

   public String toString() {
      String var1 = this.getClass().getName();
      String var2 = super.getLocalizedMessage();
      if (var2 != null) {
         var2 = var1 + ": " + var2;
      } else {
         var2 = var1;
      }

      if (super.getCause() != null) {
         var2 = var2 + "\nOriginal Exception was " + super.getCause().toString();
      }

      return var2;
   }

   public void printStackTrace() {
      synchronized(System.err) {
         super.printStackTrace(System.err);
      }
   }

   public void printStackTrace(PrintWriter var1) {
      super.printStackTrace(var1);
   }

   public void printStackTrace(PrintStream var1) {
      super.printStackTrace(var1);
   }

   public Exception getOriginalException() {
      return this.getCause() instanceof Exception ? (Exception)this.getCause() : null;
   }
}
