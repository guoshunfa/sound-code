package org.omg.CosNaming;

import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public interface NamingContextExtOperations extends NamingContextOperations {
   String to_string(NameComponent[] var1) throws InvalidName;

   NameComponent[] to_name(String var1) throws InvalidName;

   String to_url(String var1, String var2) throws InvalidAddress, InvalidName;

   Object resolve_str(String var1) throws NotFound, CannotProceed, InvalidName;
}
