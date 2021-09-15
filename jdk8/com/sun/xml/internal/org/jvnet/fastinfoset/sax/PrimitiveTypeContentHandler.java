package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public interface PrimitiveTypeContentHandler {
   void booleans(boolean[] var1, int var2, int var3) throws SAXException;

   void bytes(byte[] var1, int var2, int var3) throws SAXException;

   void shorts(short[] var1, int var2, int var3) throws SAXException;

   void ints(int[] var1, int var2, int var3) throws SAXException;

   void longs(long[] var1, int var2, int var3) throws SAXException;

   void floats(float[] var1, int var2, int var3) throws SAXException;

   void doubles(double[] var1, int var2, int var3) throws SAXException;

   void uuids(long[] var1, int var2, int var3) throws SAXException;
}
