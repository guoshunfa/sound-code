package javax.xml.soap;

public abstract class SOAPConnection {
   public abstract SOAPMessage call(SOAPMessage var1, Object var2) throws SOAPException;

   public SOAPMessage get(Object to) throws SOAPException {
      throw new UnsupportedOperationException("All subclasses of SOAPConnection must override get()");
   }

   public abstract void close() throws SOAPException;
}
