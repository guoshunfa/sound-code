package jdk.internal.util.xml;

public class XMLStreamException extends Exception {
   private static final long serialVersionUID = 1L;
   protected Throwable nested;

   public XMLStreamException() {
   }

   public XMLStreamException(String var1) {
      super(var1);
   }

   public XMLStreamException(Throwable var1) {
      super(var1);
      this.nested = var1;
   }

   public XMLStreamException(String var1, Throwable var2) {
      super(var1, var2);
      this.nested = var2;
   }

   public Throwable getNestedException() {
      return this.nested;
   }
}
