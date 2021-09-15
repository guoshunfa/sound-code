package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

public interface XMLSignature extends XMLStructure {
   String XMLNS = "http://www.w3.org/2000/09/xmldsig#";

   boolean validate(XMLValidateContext var1) throws XMLSignatureException;

   KeyInfo getKeyInfo();

   SignedInfo getSignedInfo();

   List getObjects();

   String getId();

   XMLSignature.SignatureValue getSignatureValue();

   void sign(XMLSignContext var1) throws MarshalException, XMLSignatureException;

   KeySelectorResult getKeySelectorResult();

   public interface SignatureValue extends XMLStructure {
      String getId();

      byte[] getValue();

      boolean validate(XMLValidateContext var1) throws XMLSignatureException;
   }
}
