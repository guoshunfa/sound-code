package javax.xml.crypto.dsig;

import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.Data;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;

public interface Transform extends XMLStructure, AlgorithmMethod {
   String BASE64 = "http://www.w3.org/2000/09/xmldsig#base64";
   String ENVELOPED = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
   String XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
   String XPATH2 = "http://www.w3.org/2002/06/xmldsig-filter2";
   String XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";

   AlgorithmParameterSpec getParameterSpec();

   Data transform(Data var1, XMLCryptoContext var2) throws TransformException;

   Data transform(Data var1, XMLCryptoContext var2, OutputStream var3) throws TransformException;
}
