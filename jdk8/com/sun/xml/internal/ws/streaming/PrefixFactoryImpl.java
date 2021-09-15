package com.sun.xml.internal.ws.streaming;

import java.util.HashMap;
import java.util.Map;

public class PrefixFactoryImpl implements PrefixFactory {
   private String _base;
   private int _next;
   private Map _cachedUriToPrefixMap;

   public PrefixFactoryImpl(String base) {
      this._base = base;
      this._next = 1;
   }

   public String getPrefix(String uri) {
      String prefix = null;
      if (this._cachedUriToPrefixMap == null) {
         this._cachedUriToPrefixMap = new HashMap();
      } else {
         prefix = (String)this._cachedUriToPrefixMap.get(uri);
      }

      if (prefix == null) {
         prefix = this._base + Integer.toString(this._next++);
         this._cachedUriToPrefixMap.put(uri, prefix);
      }

      return prefix;
   }
}
