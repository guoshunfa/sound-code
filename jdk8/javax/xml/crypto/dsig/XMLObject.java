package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public interface XMLObject extends XMLStructure {
   String TYPE = "http://www.w3.org/2000/09/xmldsig#Object";

   List getContent();

   String getId();

   String getMimeType();

   String getEncoding();
}
