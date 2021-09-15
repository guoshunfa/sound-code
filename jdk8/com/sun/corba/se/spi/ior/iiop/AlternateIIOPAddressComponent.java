package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponent;

public interface AlternateIIOPAddressComponent extends TaggedComponent {
   IIOPAddress getAddress();
}
