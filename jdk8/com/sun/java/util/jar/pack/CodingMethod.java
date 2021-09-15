package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

interface CodingMethod {
   void readArrayFrom(InputStream var1, int[] var2, int var3, int var4) throws IOException;

   void writeArrayTo(OutputStream var1, int[] var2, int var3, int var4) throws IOException;

   byte[] getMetaCoding(Coding var1);
}
