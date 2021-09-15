package javax.xml.crypto.dsig;

import java.io.InputStream;
import java.util.List;
import javax.xml.crypto.XMLStructure;

public interface SignedInfo extends XMLStructure {
   CanonicalizationMethod getCanonicalizationMethod();

   SignatureMethod getSignatureMethod();

   List getReferences();

   String getId();

   InputStream getCanonicalizedData();
}
