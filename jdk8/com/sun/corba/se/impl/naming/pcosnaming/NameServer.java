package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.InitialNameService;
import com.sun.corba.se.spi.activation.InitialNameServiceHelper;
import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import java.util.Properties;
import org.omg.CosNaming.NamingContext;

public class NameServer {
   private ORB orb;
   private File dbDir;
   private static final String dbName = "names.db";

   public static void main(String[] var0) {
      NameServer var1 = new NameServer(var0);
      var1.run();
   }

   protected NameServer(String[] var1) {
      Properties var2 = System.getProperties();
      var2.put("com.sun.CORBA.POA.ORBServerId", "1000");
      var2.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
      this.orb = (ORB)org.omg.CORBA.ORB.init(var1, var2);
      String var3 = var2.getProperty("com.sun.CORBA.activation.DbDir") + var2.getProperty("file.separator") + "names.db" + var2.getProperty("file.separator");
      this.dbDir = new File(var3);
      if (!this.dbDir.exists()) {
         this.dbDir.mkdir();
      }

   }

   protected void run() {
      try {
         NameService var1 = new NameService(this.orb, this.dbDir);
         NamingContext var2 = var1.initialNamingContext();
         InitialNameService var3 = InitialNameServiceHelper.narrow(this.orb.resolve_initial_references("InitialNameService"));
         var3.bind("NameService", var2, true);
         System.out.println(CorbaResourceUtil.getText("pnameserv.success"));
         this.orb.run();
      } catch (Exception var4) {
         var4.printStackTrace(System.err);
      }

   }
}
