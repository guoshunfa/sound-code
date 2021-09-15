package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import javax.xml.crypto.Data;

public interface ApacheData extends Data {
   XMLSignatureInput getXMLSignatureInput();
}
