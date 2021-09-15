package javax.xml.soap;

import java.util.Iterator;
import javax.xml.namespace.QName;

public interface Detail extends SOAPFaultElement {
   DetailEntry addDetailEntry(Name var1) throws SOAPException;

   DetailEntry addDetailEntry(QName var1) throws SOAPException;

   Iterator getDetailEntries();
}
