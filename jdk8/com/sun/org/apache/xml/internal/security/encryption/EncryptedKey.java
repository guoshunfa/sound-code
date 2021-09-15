package com.sun.org.apache.xml.internal.security.encryption;

public interface EncryptedKey extends EncryptedType {
   String getRecipient();

   void setRecipient(String var1);

   ReferenceList getReferenceList();

   void setReferenceList(ReferenceList var1);

   String getCarriedName();

   void setCarriedName(String var1);
}
