package javax.xml.stream;

public class XMLStreamException extends Exception {
   protected Throwable nested;
   protected Location location;

   public XMLStreamException() {
   }

   public XMLStreamException(String msg) {
      super(msg);
   }

   public XMLStreamException(Throwable th) {
      super(th);
      this.nested = th;
   }

   public XMLStreamException(String msg, Throwable th) {
      super(msg, th);
      this.nested = th;
   }

   public XMLStreamException(String msg, Location location, Throwable th) {
      super("ParseError at [row,col]:[" + location.getLineNumber() + "," + location.getColumnNumber() + "]\nMessage: " + msg);
      this.nested = th;
      this.location = location;
   }

   public XMLStreamException(String msg, Location location) {
      super("ParseError at [row,col]:[" + location.getLineNumber() + "," + location.getColumnNumber() + "]\nMessage: " + msg);
      this.location = location;
   }

   public Throwable getNestedException() {
      return this.nested;
   }

   public Location getLocation() {
      return this.location;
   }
}
