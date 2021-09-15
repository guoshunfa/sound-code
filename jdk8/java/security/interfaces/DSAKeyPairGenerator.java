package java.security.interfaces;

import java.security.InvalidParameterException;
import java.security.SecureRandom;

public interface DSAKeyPairGenerator {
   void initialize(DSAParams var1, SecureRandom var2) throws InvalidParameterException;

   void initialize(int var1, boolean var2, SecureRandom var3) throws InvalidParameterException;
}
