package com.sun.corba.se.spi.legacy.interceptor;

import com.sun.corba.se.spi.oa.ObjectAdapter;

public interface IORInfoExt {
   int getServerPort(String var1) throws UnknownType;

   ObjectAdapter getObjectAdapter();
}
