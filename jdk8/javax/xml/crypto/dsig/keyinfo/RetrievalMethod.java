package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;

public interface RetrievalMethod extends URIReference, XMLStructure {
   List getTransforms();

   String getURI();

   Data dereference(XMLCryptoContext var1) throws URIReferenceException;
}
