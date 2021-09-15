package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class SnmpIndex implements Serializable {
   private static final long serialVersionUID = 8712159739982192146L;
   private Vector<SnmpOid> oids = new Vector();
   private int size = 0;

   public SnmpIndex(SnmpOid[] var1) {
      this.size = var1.length;

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.oids.addElement(var1[var2]);
      }

   }

   public SnmpIndex(SnmpOid var1) {
      this.oids.addElement(var1);
      this.size = 1;
   }

   public int getNbComponents() {
      return this.size;
   }

   public Vector<SnmpOid> getComponents() {
      return this.oids;
   }

   public boolean equals(SnmpIndex var1) {
      if (this.size != var1.getNbComponents()) {
         return false;
      } else {
         Vector var4 = var1.getComponents();

         for(int var5 = 0; var5 < this.size; ++var5) {
            SnmpOid var2 = (SnmpOid)this.oids.elementAt(var5);
            SnmpOid var3 = (SnmpOid)var4.elementAt(var5);
            if (!var2.equals(var3)) {
               return false;
            }
         }

         return true;
      }
   }

   public int compareTo(SnmpIndex var1) {
      int var2 = var1.getNbComponents();
      Vector var3 = var1.getComponents();

      for(int var7 = 0; var7 < this.size; ++var7) {
         if (var7 > var2) {
            return 1;
         }

         SnmpOid var4 = (SnmpOid)this.oids.elementAt(var7);
         SnmpOid var5 = (SnmpOid)var3.elementAt(var7);
         int var6 = var4.compareTo(var5);
         if (var6 != 0) {
            return var6;
         }
      }

      return 0;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      Enumeration var2 = this.oids.elements();

      while(var2.hasMoreElements()) {
         SnmpOid var3 = (SnmpOid)var2.nextElement();
         var1.append("//").append(var3.toString());
      }

      return var1.toString();
   }
}
