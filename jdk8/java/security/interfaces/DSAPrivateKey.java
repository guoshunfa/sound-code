package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

public interface DSAPrivateKey extends DSAKey, PrivateKey {
   long serialVersionUID = 7776497482533790279L;

   BigInteger getX();
}
