package com.sun.xml.internal.bind.v2.model.core;

public enum WildcardMode {
   STRICT(false, true),
   SKIP(true, false),
   LAX(true, true);

   public final boolean allowDom;
   public final boolean allowTypedObject;

   private WildcardMode(boolean allowDom, boolean allowTypedObject) {
      this.allowDom = allowDom;
      this.allowTypedObject = allowTypedObject;
   }
}
