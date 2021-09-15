package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.ArrayList;
import java.util.Collection;

final class FinalArrayList<T> extends ArrayList<T> {
   public FinalArrayList(int initialCapacity) {
      super(initialCapacity);
   }

   public FinalArrayList() {
   }

   public FinalArrayList(Collection<? extends T> ts) {
      super(ts);
   }
}
