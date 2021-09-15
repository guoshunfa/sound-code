package com.sun.corba.se.impl.naming.namingutil;

import java.util.ArrayList;
import java.util.List;

public abstract class INSURLBase implements INSURL {
   protected boolean rirFlag = false;
   protected ArrayList theEndpointInfo = null;
   protected String theKeyString = "NameService";
   protected String theStringifiedName = null;

   public boolean getRIRFlag() {
      return this.rirFlag;
   }

   public List getEndpointInfo() {
      return this.theEndpointInfo;
   }

   public String getKeyString() {
      return this.theKeyString;
   }

   public String getStringifiedName() {
      return this.theStringifiedName;
   }

   public abstract boolean isCorbanameURL();

   public void dPrint() {
      System.out.println("URL Dump...");
      System.out.println("Key String = " + this.getKeyString());
      System.out.println("RIR Flag = " + this.getRIRFlag());
      System.out.println("isCorbanameURL = " + this.isCorbanameURL());

      for(int var1 = 0; var1 < this.theEndpointInfo.size(); ++var1) {
         ((IIOPEndpointInfo)this.theEndpointInfo.get(var1)).dump();
      }

      if (this.isCorbanameURL()) {
         System.out.println("Stringified Name = " + this.getStringifiedName());
      }

   }
}
