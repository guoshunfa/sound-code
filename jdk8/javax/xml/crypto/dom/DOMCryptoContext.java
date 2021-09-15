package javax.xml.crypto.dom;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.Element;

public class DOMCryptoContext implements XMLCryptoContext {
   private HashMap<String, String> nsMap = new HashMap();
   private HashMap<String, Element> idMap = new HashMap();
   private HashMap<Object, Object> objMap = new HashMap();
   private String baseURI;
   private KeySelector ks;
   private URIDereferencer dereferencer;
   private HashMap<String, Object> propMap = new HashMap();
   private String defaultPrefix;

   protected DOMCryptoContext() {
   }

   public String getNamespacePrefix(String var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("namespaceURI cannot be null");
      } else {
         String var3 = (String)this.nsMap.get(var1);
         return var3 != null ? var3 : var2;
      }
   }

   public String putNamespacePrefix(String var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("namespaceURI is null");
      } else {
         return (String)this.nsMap.put(var1, var2);
      }
   }

   public String getDefaultNamespacePrefix() {
      return this.defaultPrefix;
   }

   public void setDefaultNamespacePrefix(String var1) {
      this.defaultPrefix = var1;
   }

   public String getBaseURI() {
      return this.baseURI;
   }

   public void setBaseURI(String var1) {
      if (var1 != null) {
         URI.create(var1);
      }

      this.baseURI = var1;
   }

   public URIDereferencer getURIDereferencer() {
      return this.dereferencer;
   }

   public void setURIDereferencer(URIDereferencer var1) {
      this.dereferencer = var1;
   }

   public Object getProperty(String var1) {
      if (var1 == null) {
         throw new NullPointerException("name is null");
      } else {
         return this.propMap.get(var1);
      }
   }

   public Object setProperty(String var1, Object var2) {
      if (var1 == null) {
         throw new NullPointerException("name is null");
      } else {
         return this.propMap.put(var1, var2);
      }
   }

   public KeySelector getKeySelector() {
      return this.ks;
   }

   public void setKeySelector(KeySelector var1) {
      this.ks = var1;
   }

   public Element getElementById(String var1) {
      if (var1 == null) {
         throw new NullPointerException("idValue is null");
      } else {
         return (Element)this.idMap.get(var1);
      }
   }

   public void setIdAttributeNS(Element var1, String var2, String var3) {
      if (var1 == null) {
         throw new NullPointerException("element is null");
      } else if (var3 == null) {
         throw new NullPointerException("localName is null");
      } else {
         String var4 = var1.getAttributeNS(var2, var3);
         if (var4 != null && var4.length() != 0) {
            this.idMap.put(var4, var1);
         } else {
            throw new IllegalArgumentException(var3 + " is not an attribute");
         }
      }
   }

   public Iterator iterator() {
      return Collections.unmodifiableMap(this.idMap).entrySet().iterator();
   }

   public Object get(Object var1) {
      return this.objMap.get(var1);
   }

   public Object put(Object var1, Object var2) {
      return this.objMap.put(var1, var2);
   }
}
