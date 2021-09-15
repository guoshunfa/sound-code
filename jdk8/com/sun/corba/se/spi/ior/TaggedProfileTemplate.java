package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import java.util.List;
import org.omg.CORBA_2_3.portable.OutputStream;

public interface TaggedProfileTemplate extends List, Identifiable, WriteContents, MakeImmutable {
   Iterator iteratorById(int var1);

   TaggedProfile create(ObjectKeyTemplate var1, ObjectId var2);

   void write(ObjectKeyTemplate var1, ObjectId var2, OutputStream var3);

   boolean isEquivalent(TaggedProfileTemplate var1);

   org.omg.IOP.TaggedComponent[] getIOPComponents(ORB var1, int var2);
}
