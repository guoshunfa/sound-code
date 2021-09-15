package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;

public interface EncryptionProperties {
   String getId();

   void setId(String var1);

   Iterator<EncryptionProperty> getEncryptionProperties();

   void addEncryptionProperty(EncryptionProperty var1);

   void removeEncryptionProperty(EncryptionProperty var1);
}
