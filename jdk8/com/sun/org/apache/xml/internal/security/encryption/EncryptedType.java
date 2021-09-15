package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.keys.KeyInfo;

public interface EncryptedType {
   String getId();

   void setId(String var1);

   String getType();

   void setType(String var1);

   String getMimeType();

   void setMimeType(String var1);

   String getEncoding();

   void setEncoding(String var1);

   EncryptionMethod getEncryptionMethod();

   void setEncryptionMethod(EncryptionMethod var1);

   KeyInfo getKeyInfo();

   void setKeyInfo(KeyInfo var1);

   CipherData getCipherData();

   EncryptionProperties getEncryptionProperties();

   void setEncryptionProperties(EncryptionProperties var1);
}
