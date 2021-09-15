package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

interface Data {
   int size();

   byte[] read();

   long writeTo(DataFile var1);

   Data createNext(DataHead var1, ByteBuffer var2);
}
