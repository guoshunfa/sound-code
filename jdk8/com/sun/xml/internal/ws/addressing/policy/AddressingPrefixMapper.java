package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.policy.spi.PrefixMapper;
import java.util.HashMap;
import java.util.Map;

public class AddressingPrefixMapper implements PrefixMapper {
   private static final Map<String, String> prefixMap = new HashMap();

   public Map<String, String> getPrefixMap() {
      return prefixMap;
   }

   static {
      prefixMap.put(AddressingVersion.MEMBER.policyNsUri, "wsap");
      prefixMap.put(AddressingVersion.MEMBER.nsUri, "wsa");
      prefixMap.put("http://www.w3.org/2007/05/addressing/metadata", "wsam");
   }
}
