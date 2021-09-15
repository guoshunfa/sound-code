package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream;

public interface ObjectKeyFactory {
   ObjectKey create(byte[] var1);

   ObjectKeyTemplate createTemplate(InputStream var1);
}
