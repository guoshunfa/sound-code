package javax.xml.soap;

import java.util.Locale;
import javax.xml.namespace.QName;
import org.w3c.dom.Document;

public interface SOAPBody extends SOAPElement {
   SOAPFault addFault() throws SOAPException;

   SOAPFault addFault(Name var1, String var2, Locale var3) throws SOAPException;

   SOAPFault addFault(QName var1, String var2, Locale var3) throws SOAPException;

   SOAPFault addFault(Name var1, String var2) throws SOAPException;

   SOAPFault addFault(QName var1, String var2) throws SOAPException;

   boolean hasFault();

   SOAPFault getFault();

   SOAPBodyElement addBodyElement(Name var1) throws SOAPException;

   SOAPBodyElement addBodyElement(QName var1) throws SOAPException;

   SOAPBodyElement addDocument(Document var1) throws SOAPException;

   Document extractContentAsDocument() throws SOAPException;
}
