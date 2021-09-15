package com.sun.xml.internal.messaging.saaj.packaging.mime;

public class MessagingException extends Exception {
   private Exception next;

   public MessagingException() {
   }

   public MessagingException(String s) {
      super(s);
   }

   public MessagingException(String s, Exception e) {
      super(s);
      this.next = e;
   }

   public Exception getNextException() {
      return this.next;
   }

   public synchronized boolean setNextException(Exception ex) {
      Object theEnd;
      for(theEnd = this; theEnd instanceof MessagingException && ((MessagingException)theEnd).next != null; theEnd = ((MessagingException)theEnd).next) {
      }

      if (theEnd instanceof MessagingException) {
         ((MessagingException)theEnd).next = ex;
         return true;
      } else {
         return false;
      }
   }

   public String getMessage() {
      if (this.next == null) {
         return super.getMessage();
      } else {
         Exception n = this.next;
         String s = super.getMessage();
         StringBuffer sb = new StringBuffer(s == null ? "" : s);

         while(n != null) {
            sb.append(";\n  nested exception is:\n\t");
            if (n instanceof MessagingException) {
               MessagingException mex = (MessagingException)n;
               sb.append(n.getClass().toString());
               String msg = mex.getSuperMessage();
               if (msg != null) {
                  sb.append(": ");
                  sb.append(msg);
               }

               n = mex.next;
            } else {
               sb.append(n.toString());
               n = null;
            }
         }

         return sb.toString();
      }
   }

   private String getSuperMessage() {
      return super.getMessage();
   }
}
