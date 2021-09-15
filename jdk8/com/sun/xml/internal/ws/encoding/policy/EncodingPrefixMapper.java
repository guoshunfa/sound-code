package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.policy.spi.PrefixMapper;
import java.util.HashMap;
import java.util.Map;

public class EncodingPrefixMapper implements PrefixMapper {
   private static final Map<String, String> prefixMap = new HashMap();

   public Map<String, String> getPrefixMap() {
      return prefixMap;
   }

   static {
      prefixMap.put("http://schemas.xmlsoap.org/ws/2004/09/policy/encoding", "wspe");
      prefixMap.put("http://schemas.xmlsoap.org/ws/2004/09/policy/optimizedmimeserialization", "wsoma");
      prefixMap.put("http://java.sun.com/xml/ns/wsit/2006/09/policy/encoding/client", "cenc");
      prefixMap.put("http://java.sun.com/xml/ns/wsit/2006/09/policy/fastinfoset/service", "fi");
   }
}
