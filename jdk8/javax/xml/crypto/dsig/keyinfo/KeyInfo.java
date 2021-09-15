package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;

public interface KeyInfo extends XMLStructure {
   List getContent();

   String getId();

   void marshal(XMLStructure var1, XMLCryptoContext var2) throws MarshalException;
}
