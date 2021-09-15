package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedProfileTemplate;

public interface IIOPProfileTemplate extends TaggedProfileTemplate {
   GIOPVersion getGIOPVersion();

   IIOPAddress getPrimaryAddress();
}
