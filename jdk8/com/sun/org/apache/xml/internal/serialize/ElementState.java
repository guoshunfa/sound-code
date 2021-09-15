package com.sun.org.apache.xml.internal.serialize;

import java.util.Map;

public class ElementState {
   public String rawName;
   public String localName;
   public String namespaceURI;
   public boolean preserveSpace;
   public boolean empty;
   public boolean afterElement;
   public boolean afterComment;
   public boolean doCData;
   public boolean unescaped;
   public boolean inCData;
   public Map<String, String> prefixes;
}
