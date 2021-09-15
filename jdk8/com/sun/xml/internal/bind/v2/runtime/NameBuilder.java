package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

public final class NameBuilder {
   private Map<String, Integer> uriIndexMap = new HashMap();
   private Set<String> nonDefaultableNsUris = new HashSet();
   private Map<String, Integer> localNameIndexMap = new HashMap();
   private QNameMap<Integer> elementQNameIndexMap = new QNameMap();
   private QNameMap<Integer> attributeQNameIndexMap = new QNameMap();

   public Name createElementName(QName name) {
      return this.createElementName(name.getNamespaceURI(), name.getLocalPart());
   }

   public Name createElementName(String nsUri, String localName) {
      return this.createName(nsUri, localName, false, this.elementQNameIndexMap);
   }

   public Name createAttributeName(QName name) {
      return this.createAttributeName(name.getNamespaceURI(), name.getLocalPart());
   }

   public Name createAttributeName(String nsUri, String localName) {
      assert nsUri.intern() == nsUri;

      assert localName.intern() == localName;

      if (nsUri.length() == 0) {
         return new Name(this.allocIndex(this.attributeQNameIndexMap, "", localName), -1, nsUri, this.allocIndex(this.localNameIndexMap, localName), localName, true);
      } else {
         this.nonDefaultableNsUris.add(nsUri);
         return this.createName(nsUri, localName, true, this.attributeQNameIndexMap);
      }
   }

   private Name createName(String nsUri, String localName, boolean isAttribute, QNameMap<Integer> map) {
      assert nsUri.intern() == nsUri;

      assert localName.intern() == localName;

      return new Name(this.allocIndex(map, nsUri, localName), this.allocIndex(this.uriIndexMap, nsUri), nsUri, this.allocIndex(this.localNameIndexMap, localName), localName, isAttribute);
   }

   private int allocIndex(Map<String, Integer> map, String str) {
      Integer i = (Integer)map.get(str);
      if (i == null) {
         i = map.size();
         map.put(str, i);
      }

      return i;
   }

   private int allocIndex(QNameMap<Integer> map, String nsUri, String localName) {
      Integer i = (Integer)map.get(nsUri, localName);
      if (i == null) {
         i = map.size();
         map.put(nsUri, localName, i);
      }

      return i;
   }

   public NameList conclude() {
      boolean[] nsUriCannotBeDefaulted = new boolean[this.uriIndexMap.size()];

      Map.Entry e;
      for(Iterator var2 = this.uriIndexMap.entrySet().iterator(); var2.hasNext(); nsUriCannotBeDefaulted[(Integer)e.getValue()] = this.nonDefaultableNsUris.contains(e.getKey())) {
         e = (Map.Entry)var2.next();
      }

      NameList r = new NameList(this.list(this.uriIndexMap), nsUriCannotBeDefaulted, this.list(this.localNameIndexMap), this.elementQNameIndexMap.size(), this.attributeQNameIndexMap.size());
      this.uriIndexMap = null;
      this.localNameIndexMap = null;
      return r;
   }

   private String[] list(Map<String, Integer> map) {
      String[] r = new String[map.size()];

      Map.Entry e;
      for(Iterator var3 = map.entrySet().iterator(); var3.hasNext(); r[(Integer)e.getValue()] = (String)e.getKey()) {
         e = (Map.Entry)var3.next();
      }

      return r;
   }
}
