package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;

interface Handler {
   ObjectKeyTemplate handle(int var1, int var2, InputStream var3, OctetSeqHolder var4);
}
