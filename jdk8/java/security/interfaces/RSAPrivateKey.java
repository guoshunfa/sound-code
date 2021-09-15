package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

public interface RSAPrivateKey extends PrivateKey, RSAKey {
   long serialVersionUID = 5187144804936595022L;

   BigInteger getPrivateExponent();
}
