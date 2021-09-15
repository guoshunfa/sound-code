package com.sun.org.apache.xerces.internal.xs.datatypes;

import com.sun.org.apache.xerces.internal.xs.XSException;
import java.util.List;

public interface ByteList extends List {
   int getLength();

   boolean contains(byte var1);

   byte item(int var1) throws XSException;
}
