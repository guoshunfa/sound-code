package javax.xml.soap;

public interface SOAPHeaderElement extends SOAPElement {
   void setActor(String var1);

   void setRole(String var1) throws SOAPException;

   String getActor();

   String getRole();

   void setMustUnderstand(boolean var1);

   boolean getMustUnderstand();

   void setRelay(boolean var1) throws SOAPException;

   boolean getRelay();
}
