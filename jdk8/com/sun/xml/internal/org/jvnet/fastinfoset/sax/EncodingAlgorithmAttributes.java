package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.Attributes;

public interface EncodingAlgorithmAttributes extends Attributes {
   String getAlgorithmURI(int var1);

   int getAlgorithmIndex(int var1);

   Object getAlgorithmData(int var1);

   String getAlpababet(int var1);

   boolean getToIndex(int var1);
}
