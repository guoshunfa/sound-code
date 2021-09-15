package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;

public interface EncryptionMethod {
   String getAlgorithm();

   int getKeySize();

   void setKeySize(int var1);

   byte[] getOAEPparams();

   void setOAEPparams(byte[] var1);

   void setDigestAlgorithm(String var1);

   String getDigestAlgorithm();

   void setMGFAlgorithm(String var1);

   String getMGFAlgorithm();

   Iterator<Element> getEncryptionMethodInformation();

   void addEncryptionMethodInformation(Element var1);

   void removeEncryptionMethodInformation(Element var1);
}
