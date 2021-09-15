package com.sun.org.apache.xerces.internal.xs.datatypes;

import java.util.List;

public interface ObjectList extends List {
   int getLength();

   boolean contains(Object var1);

   Object item(int var1);
}
