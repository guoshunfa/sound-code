package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.xs.ShortList;

public interface ValueStore {
   void addValue(Field var1, Object var2, short var3, ShortList var4);

   void reportError(String var1, Object[] var2);
}
