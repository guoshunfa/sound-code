package com.sun.org.omg.SendingContext;

import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.SendingContext.RunTimeOperations;

public interface CodeBaseOperations extends RunTimeOperations {
   Repository get_ir();

   String implementation(String var1);

   String[] implementations(String[] var1);

   FullValueDescription meta(String var1);

   FullValueDescription[] metas(String[] var1);

   String[] bases(String var1);
}
