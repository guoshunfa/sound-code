package com.sun.xml.internal.messaging.saaj.util;

import java.util.ArrayList;
import java.util.Collection;

public final class FinalArrayList extends ArrayList {
   public FinalArrayList(int initialCapacity) {
      super(initialCapacity);
   }

   public FinalArrayList() {
   }

   public FinalArrayList(Collection collection) {
      super(collection);
   }
}
