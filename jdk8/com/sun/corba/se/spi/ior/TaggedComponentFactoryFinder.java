package com.sun.corba.se.spi.ior;

import org.omg.CORBA.ORB;

public interface TaggedComponentFactoryFinder extends IdentifiableFactoryFinder {
   TaggedComponent create(ORB var1, org.omg.IOP.TaggedComponent var2);
}
