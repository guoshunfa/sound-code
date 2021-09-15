package com.sun.org.apache.xml.internal.security.signature;

import org.w3c.dom.Node;

public interface NodeFilter {
   int isNodeInclude(Node var1);

   int isNodeIncludeDO(Node var1, int var2);
}
