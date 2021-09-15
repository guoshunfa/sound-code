package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.transport.CorbaContactInfoListImpl;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;

public class SocketFactoryContactInfoListImpl extends CorbaContactInfoListImpl {
   public SocketFactoryContactInfoListImpl(ORB var1) {
      super(var1);
   }

   public SocketFactoryContactInfoListImpl(ORB var1, IOR var2) {
      super(var1, var2);
   }

   public Iterator iterator() {
      return new SocketFactoryContactInfoListIteratorImpl(this.orb, this);
   }
}
