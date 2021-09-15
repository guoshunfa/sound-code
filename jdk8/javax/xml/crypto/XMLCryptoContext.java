package javax.xml.crypto;

public interface XMLCryptoContext {
   String getBaseURI();

   void setBaseURI(String var1);

   KeySelector getKeySelector();

   void setKeySelector(KeySelector var1);

   URIDereferencer getURIDereferencer();

   void setURIDereferencer(URIDereferencer var1);

   String getNamespacePrefix(String var1, String var2);

   String putNamespacePrefix(String var1, String var2);

   String getDefaultNamespacePrefix();

   void setDefaultNamespacePrefix(String var1);

   Object setProperty(String var1, Object var2);

   Object getProperty(String var1);

   Object get(Object var1);

   Object put(Object var1, Object var2);
}
