package javax.xml.crypto.dsig.keyinfo;

import java.math.BigInteger;
import javax.xml.crypto.XMLStructure;

public interface X509IssuerSerial extends XMLStructure {
   String getIssuerName();

   BigInteger getSerialNumber();
}
