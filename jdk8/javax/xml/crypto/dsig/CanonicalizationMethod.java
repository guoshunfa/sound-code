package javax.xml.crypto.dsig;

import java.security.spec.AlgorithmParameterSpec;

public interface CanonicalizationMethod extends Transform {
   String INCLUSIVE = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
   String INCLUSIVE_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
   String EXCLUSIVE = "http://www.w3.org/2001/10/xml-exc-c14n#";
   String EXCLUSIVE_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";

   AlgorithmParameterSpec getParameterSpec();
}
