package javax.xml.crypto.dsig;

import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.XMLStructure;

public interface DigestMethod extends XMLStructure, AlgorithmMethod {
   String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
   String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
   String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
   String RIPEMD160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";

   AlgorithmParameterSpec getParameterSpec();
}
