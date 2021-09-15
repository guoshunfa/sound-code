package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import java.util.Iterator;
import org.w3c.dom.Element;

public interface AgreementMethod {
   byte[] getKANonce();

   void setKANonce(byte[] var1);

   Iterator<Element> getAgreementMethodInformation();

   void addAgreementMethodInformation(Element var1);

   void revoveAgreementMethodInformation(Element var1);

   KeyInfo getOriginatorKeyInfo();

   void setOriginatorKeyInfo(KeyInfo var1);

   KeyInfo getRecipientKeyInfo();

   void setRecipientKeyInfo(KeyInfo var1);

   String getAlgorithm();
}
