package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound;
import org.omg.CORBA.Object;

public interface InitialNameServiceOperations {
   void bind(String var1, Object var2, boolean var3) throws NameAlreadyBound;
}
