package javax.xml.soap;

public interface Node extends org.w3c.dom.Node {
   String getValue();

   void setValue(String var1);

   void setParentElement(SOAPElement var1) throws SOAPException;

   SOAPElement getParentElement();

   void detachNode();

   void recycleNode();
}
