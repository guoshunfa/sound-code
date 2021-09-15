package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public class XSNamedMapImpl extends AbstractMap implements XSNamedMap {
   public static final XSNamedMapImpl EMPTY_MAP = new XSNamedMapImpl(new XSObject[0], 0);
   final String[] fNamespaces;
   final int fNSNum;
   final SymbolHash[] fMaps;
   XSObject[] fArray = null;
   int fLength = -1;
   private Set fEntrySet = null;

   public XSNamedMapImpl(String namespace, SymbolHash map) {
      this.fNamespaces = new String[]{namespace};
      this.fMaps = new SymbolHash[]{map};
      this.fNSNum = 1;
   }

   public XSNamedMapImpl(String[] namespaces, SymbolHash[] maps, int num) {
      this.fNamespaces = namespaces;
      this.fMaps = maps;
      this.fNSNum = num;
   }

   public XSNamedMapImpl(XSObject[] array, int length) {
      if (length == 0) {
         this.fNamespaces = null;
         this.fMaps = null;
         this.fNSNum = 0;
         this.fArray = array;
         this.fLength = 0;
      } else {
         this.fNamespaces = new String[]{array[0].getNamespace()};
         this.fMaps = null;
         this.fNSNum = 1;
         this.fArray = array;
         this.fLength = length;
      }
   }

   public synchronized int getLength() {
      if (this.fLength == -1) {
         this.fLength = 0;

         for(int i = 0; i < this.fNSNum; ++i) {
            this.fLength += this.fMaps[i].getLength();
         }
      }

      return this.fLength;
   }

   public XSObject itemByName(String namespace, String localName) {
      for(int i = 0; i < this.fNSNum; ++i) {
         if (isEqual(namespace, this.fNamespaces[i])) {
            if (this.fMaps != null) {
               return (XSObject)this.fMaps[i].get(localName);
            }

            for(int j = 0; j < this.fLength; ++j) {
               XSObject ret = this.fArray[j];
               if (ret.getName().equals(localName)) {
                  return ret;
               }
            }

            return null;
         }
      }

      return null;
   }

   public synchronized XSObject item(int index) {
      if (this.fArray == null) {
         this.getLength();
         this.fArray = new XSObject[this.fLength];
         int pos = 0;

         for(int i = 0; i < this.fNSNum; ++i) {
            pos += this.fMaps[i].getValues(this.fArray, pos);
         }
      }

      return index >= 0 && index < this.fLength ? this.fArray[index] : null;
   }

   static boolean isEqual(String one, String two) {
      return one != null ? one.equals(two) : two == null;
   }

   public boolean containsKey(Object key) {
      return this.get(key) != null;
   }

   public Object get(Object key) {
      if (key instanceof QName) {
         QName name = (QName)key;
         String namespaceURI = name.getNamespaceURI();
         if ("".equals(namespaceURI)) {
            namespaceURI = null;
         }

         String localPart = name.getLocalPart();
         return this.itemByName(namespaceURI, localPart);
      } else {
         return null;
      }
   }

   public int size() {
      return this.getLength();
   }

   public synchronized Set entrySet() {
      if (this.fEntrySet == null) {
         final int length = this.getLength();
         final XSNamedMapImpl.XSNamedMapEntry[] entries = new XSNamedMapImpl.XSNamedMapEntry[length];

         for(int i = 0; i < length; ++i) {
            XSObject xso = this.item(i);
            entries[i] = new XSNamedMapImpl.XSNamedMapEntry(new QName(xso.getNamespace(), xso.getName()), xso);
         }

         this.fEntrySet = new AbstractSet() {
            public Iterator iterator() {
               return new Iterator() {
                  private int index = 0;

                  public boolean hasNext() {
                     return this.index < length;
                  }

                  public Object next() {
                     if (this.index < length) {
                        return entries[this.index++];
                     } else {
                        throw new NoSuchElementException();
                     }
                  }

                  public void remove() {
                     throw new UnsupportedOperationException();
                  }
               };
            }

            public int size() {
               return length;
            }
         };
      }

      return this.fEntrySet;
   }

   private static final class XSNamedMapEntry implements Map.Entry {
      private final QName key;
      private final XSObject value;

      public XSNamedMapEntry(QName key, XSObject value) {
         this.key = key;
         this.value = value;
      }

      public Object getKey() {
         return this.key;
      }

      public Object getValue() {
         return this.value;
      }

      public Object setValue(Object value) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object o) {
         if (!(o instanceof Map.Entry)) {
            return false;
         } else {
            boolean var10000;
            label38: {
               label27: {
                  Map.Entry e = (Map.Entry)o;
                  Object otherKey = e.getKey();
                  Object otherValue = e.getValue();
                  if (this.key == null) {
                     if (otherKey != null) {
                        break label27;
                     }
                  } else if (!this.key.equals(otherKey)) {
                     break label27;
                  }

                  if (this.value == null) {
                     if (otherValue == null) {
                        break label38;
                     }
                  } else if (this.value.equals(otherValue)) {
                     break label38;
                  }
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         StringBuffer buffer = new StringBuffer();
         buffer.append(String.valueOf((Object)this.key));
         buffer.append('=');
         buffer.append(String.valueOf((Object)this.value));
         return buffer.toString();
      }
   }
}
