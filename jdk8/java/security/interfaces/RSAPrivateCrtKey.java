package java.security.interfaces;

import java.math.BigInteger;

public interface RSAPrivateCrtKey extends RSAPrivateKey {
   long serialVersionUID = -5682214253527700368L;

   BigInteger getPublicExponent();

   BigInteger getPrimeP();

   BigInteger getPrimeQ();

   BigInteger getPrimeExponentP();

   BigInteger getPrimeExponentQ();

   BigInteger getCrtCoefficient();
}
