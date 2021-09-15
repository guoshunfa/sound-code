package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.naming.pcosnaming.NameService;
import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import org.omg.CosNaming.NamingContext;

public class NameServiceStartThread extends Thread {
   private ORB orb;
   private File dbDir;

   public NameServiceStartThread(ORB var1, File var2) {
      this.orb = var1;
      this.dbDir = var2;
   }

   public void run() {
      try {
         NameService var1 = new NameService(this.orb, this.dbDir);
         NamingContext var2 = var1.initialNamingContext();
         this.orb.register_initial_reference("NameService", var2);
      } catch (Exception var3) {
         System.err.println("NameService did not start successfully");
         var3.printStackTrace();
      }

   }
}
