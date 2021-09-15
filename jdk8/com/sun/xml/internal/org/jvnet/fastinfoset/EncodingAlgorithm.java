package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface EncodingAlgorithm {
   Object decodeFromBytes(byte[] var1, int var2, int var3) throws EncodingAlgorithmException;

   Object decodeFromInputStream(InputStream var1) throws EncodingAlgorithmException, IOException;

   void encodeToOutputStream(Object var1, OutputStream var2) throws EncodingAlgorithmException, IOException;

   Object convertFromCharacters(char[] var1, int var2, int var3) throws EncodingAlgorithmException;

   void convertToCharacters(Object var1, StringBuffer var2) throws EncodingAlgorithmException;
}
