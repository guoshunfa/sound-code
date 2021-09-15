package javax.xml.crypto.dsig;

import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.XMLStructure;

public interface SignatureMethod extends XMLStructure, AlgorithmMethod {
   String DSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
   String RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
   String HMAC_SHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";

   AlgorithmParameterSpec getParameterSpec();
}
