package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

public interface ECPrivateKey extends PrivateKey, ECKey {
   long serialVersionUID = -7896394956925609184L;

   BigInteger getS();
}
