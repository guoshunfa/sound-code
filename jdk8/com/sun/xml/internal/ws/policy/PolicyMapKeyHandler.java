package com.sun.xml.internal.ws.policy;

interface PolicyMapKeyHandler {
   boolean areEqual(PolicyMapKey var1, PolicyMapKey var2);

   int generateHashCode(PolicyMapKey var1);
}
