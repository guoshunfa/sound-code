package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Level;

final class SnmpRequestTree {
   private Hashtable<Object, SnmpRequestTree.Handler> hashtable = null;
   private SnmpMibRequest request = null;
   private int version = 0;
   private boolean creationflag = false;
   private boolean getnextflag = false;
   private int type = 0;
   private boolean setreqflag = false;

   SnmpRequestTree(SnmpMibRequest var1, boolean var2, int var3) {
      this.request = var1;
      this.version = var1.getVersion();
      this.creationflag = var2;
      this.hashtable = new Hashtable();
      this.setPduType(var3);
   }

   public static int mapSetException(int var0, int var1) throws SnmpStatusException {
      if (var1 == 0) {
         return var0;
      } else {
         int var3 = var0;
         if (var0 == 225) {
            var3 = 17;
         } else if (var0 == 224) {
            var3 = 17;
         }

         return var3;
      }
   }

   public static int mapGetException(int var0, int var1) throws SnmpStatusException {
      if (var1 == 0) {
         return var0;
      } else {
         int var3 = var0;
         if (var0 == 225) {
            var3 = var0;
         } else if (var0 == 224) {
            var3 = var0;
         } else if (var0 == 6) {
            var3 = 224;
         } else if (var0 == 18) {
            var3 = 224;
         } else if (var0 >= 7 && var0 <= 12) {
            var3 = 224;
         } else if (var0 == 4) {
            var3 = 224;
         } else if (var0 != 16 && var0 != 5) {
            var3 = 225;
         }

         return var3;
      }
   }

   public Object getUserData() {
      return this.request.getUserData();
   }

   public boolean isCreationAllowed() {
      return this.creationflag;
   }

   public boolean isSetRequest() {
      return this.setreqflag;
   }

   public int getVersion() {
      return this.version;
   }

   public int getRequestPduVersion() {
      return this.request.getRequestPduVersion();
   }

   public SnmpMibNode getMetaNode(SnmpRequestTree.Handler var1) {
      return var1.meta;
   }

   public int getOidDepth(SnmpRequestTree.Handler var1) {
      return var1.depth;
   }

   public Enumeration<SnmpMibSubRequest> getSubRequests(SnmpRequestTree.Handler var1) {
      return new SnmpRequestTree.Enum(this, var1);
   }

   public Enumeration<SnmpRequestTree.Handler> getHandlers() {
      return this.hashtable.elements();
   }

   public void add(SnmpMibNode var1, int var2, SnmpVarBind var3) throws SnmpStatusException {
      this.registerNode(var1, var2, (SnmpOid)null, var3, false, (SnmpVarBind)null);
   }

   public void add(SnmpMibNode var1, int var2, SnmpOid var3, SnmpVarBind var4, boolean var5) throws SnmpStatusException {
      this.registerNode(var1, var2, var3, var4, var5, (SnmpVarBind)null);
   }

   public void add(SnmpMibNode var1, int var2, SnmpOid var3, SnmpVarBind var4, boolean var5, SnmpVarBind var6) throws SnmpStatusException {
      this.registerNode(var1, var2, var3, var4, var5, var6);
   }

   void setPduType(int var1) {
      this.type = var1;
      this.setreqflag = var1 == 253 || var1 == 163;
   }

   void setGetNextFlag() {
      this.getnextflag = true;
   }

   void switchCreationFlag(boolean var1) {
      this.creationflag = var1;
   }

   SnmpMibSubRequest getSubRequest(SnmpRequestTree.Handler var1) {
      return var1 == null ? null : new SnmpRequestTree.SnmpMibSubRequestImpl(this.request, var1.getSubList(), (SnmpOid)null, false, this.getnextflag, (SnmpVarBind)null);
   }

   SnmpMibSubRequest getSubRequest(SnmpRequestTree.Handler var1, SnmpOid var2) {
      if (var1 == null) {
         return null;
      } else {
         int var3 = var1.getEntryPos(var2);
         return var3 == -1 ? null : new SnmpRequestTree.SnmpMibSubRequestImpl(this.request, var1.getEntrySubList(var3), var1.getEntryOid(var3), var1.isNewEntry(var3), this.getnextflag, var1.getRowStatusVarBind(var3));
      }
   }

   SnmpMibSubRequest getSubRequest(SnmpRequestTree.Handler var1, int var2) {
      return var1 == null ? null : new SnmpRequestTree.SnmpMibSubRequestImpl(this.request, var1.getEntrySubList(var2), var1.getEntryOid(var2), var1.isNewEntry(var2), this.getnextflag, var1.getRowStatusVarBind(var2));
   }

   private void put(Object var1, SnmpRequestTree.Handler var2) {
      if (var2 != null) {
         if (var1 != null) {
            if (this.hashtable == null) {
               this.hashtable = new Hashtable();
            }

            this.hashtable.put(var1, var2);
         }
      }
   }

   private SnmpRequestTree.Handler get(Object var1) {
      if (var1 == null) {
         return null;
      } else {
         return this.hashtable == null ? null : (SnmpRequestTree.Handler)this.hashtable.get(var1);
      }
   }

   private static int findOid(SnmpOid[] var0, int var1, SnmpOid var2) {
      int var4 = 0;
      int var5 = var1 - 1;

      for(int var6 = var4 + (var5 - var4) / 2; var4 <= var5; var6 = var4 + (var5 - var4) / 2) {
         SnmpOid var7 = var0[var6];
         int var8 = var2.compareTo(var7);
         if (var8 == 0) {
            return var6;
         }

         if (var2.equals(var7)) {
            return var6;
         }

         if (var8 > 0) {
            var4 = var6 + 1;
         } else {
            var5 = var6 - 1;
         }
      }

      return -1;
   }

   private static int getInsertionPoint(SnmpOid[] var0, int var1, SnmpOid var2) {
      SnmpOid[] var3 = var0;
      int var5 = 0;
      int var6 = var1 - 1;

      int var7;
      for(var7 = var5 + (var6 - var5) / 2; var5 <= var6; var7 = var5 + (var6 - var5) / 2) {
         SnmpOid var8 = var3[var7];
         int var9 = var2.compareTo(var8);
         if (var9 == 0) {
            return var7;
         }

         if (var9 > 0) {
            var5 = var7 + 1;
         } else {
            var6 = var7 - 1;
         }
      }

      return var7;
   }

   private void registerNode(SnmpMibNode var1, int var2, SnmpOid var3, SnmpVarBind var4, boolean var5, SnmpVarBind var6) throws SnmpStatusException {
      if (var1 == null) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpRequestTree.class.getName(), "registerNode", "meta-node is null!");
      } else if (var4 == null) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpRequestTree.class.getName(), "registerNode", "varbind is null!");
      } else {
         SnmpRequestTree.Handler var8 = this.get(var1);
         if (var8 == null) {
            var8 = new SnmpRequestTree.Handler(this.type);
            var8.meta = var1;
            var8.depth = var2;
            this.put(var1, var8);
         }

         if (var3 == null) {
            var8.addVarbind(var4);
         } else {
            var8.addVarbind(var4, var3, var5, var6);
         }

      }
   }

   static final class Handler {
      SnmpMibNode meta;
      int depth;
      Vector<SnmpVarBind> sublist;
      SnmpOid[] entryoids = null;
      Vector<SnmpVarBind>[] entrylists = null;
      boolean[] isentrynew = null;
      SnmpVarBind[] rowstatus = null;
      int entrycount = 0;
      int entrysize = 0;
      final int type;
      private static final int Delta = 10;

      public Handler(int var1) {
         this.type = var1;
      }

      public void addVarbind(SnmpVarBind var1) {
         if (this.sublist == null) {
            this.sublist = new Vector();
         }

         this.sublist.addElement(var1);
      }

      void add(int var1, SnmpOid var2, Vector<SnmpVarBind> var3, boolean var4, SnmpVarBind var5) {
         if (this.entryoids == null) {
            this.entryoids = new SnmpOid[10];
            this.entrylists = (Vector[])(new Vector[10]);
            this.isentrynew = new boolean[10];
            this.rowstatus = new SnmpVarBind[10];
            this.entrysize = 10;
            var1 = 0;
         } else if (var1 < this.entrysize && this.entrycount != this.entrysize) {
            if (var1 < this.entrycount) {
               int var13 = var1 + 1;
               int var14 = this.entrycount - var1;
               System.arraycopy(this.entryoids, var1, this.entryoids, var13, var14);
               System.arraycopy(this.entrylists, var1, this.entrylists, var13, var14);
               System.arraycopy(this.isentrynew, var1, this.isentrynew, var13, var14);
               System.arraycopy(this.rowstatus, var1, this.rowstatus, var13, var14);
            }
         } else {
            SnmpOid[] var6 = this.entryoids;
            Vector[] var7 = this.entrylists;
            boolean[] var8 = this.isentrynew;
            SnmpVarBind[] var9 = this.rowstatus;
            this.entrysize += 10;
            this.entryoids = new SnmpOid[this.entrysize];
            this.entrylists = (Vector[])(new Vector[this.entrysize]);
            this.isentrynew = new boolean[this.entrysize];
            this.rowstatus = new SnmpVarBind[this.entrysize];
            if (var1 > this.entrycount) {
               var1 = this.entrycount;
            }

            if (var1 < 0) {
               var1 = 0;
            }

            int var11 = this.entrycount - var1;
            if (var1 > 0) {
               System.arraycopy(var6, 0, this.entryoids, 0, var1);
               System.arraycopy(var7, 0, this.entrylists, 0, var1);
               System.arraycopy(var8, 0, this.isentrynew, 0, var1);
               System.arraycopy(var9, 0, this.rowstatus, 0, var1);
            }

            if (var11 > 0) {
               int var12 = var1 + 1;
               System.arraycopy(var6, var1, this.entryoids, var12, var11);
               System.arraycopy(var7, var1, this.entrylists, var12, var11);
               System.arraycopy(var8, var1, this.isentrynew, var12, var11);
               System.arraycopy(var9, var1, this.rowstatus, var12, var11);
            }
         }

         this.entryoids[var1] = var2;
         this.entrylists[var1] = var3;
         this.isentrynew[var1] = var4;
         this.rowstatus[var1] = var5;
         ++this.entrycount;
      }

      public void addVarbind(SnmpVarBind var1, SnmpOid var2, boolean var3, SnmpVarBind var4) throws SnmpStatusException {
         Vector var5 = null;
         SnmpVarBind var6 = var4;
         if (this.entryoids == null) {
            var5 = new Vector();
            this.add(0, var2, var5, var3, var4);
         } else {
            int var7 = SnmpRequestTree.getInsertionPoint(this.entryoids, this.entrycount, var2);
            if (var7 > -1 && var7 < this.entrycount && var2.compareTo(this.entryoids[var7]) == 0) {
               var5 = this.entrylists[var7];
               var6 = this.rowstatus[var7];
            } else {
               var5 = new Vector();
               this.add(var7, var2, var5, var3, var4);
            }

            if (var4 != null) {
               if (var6 != null && var6 != var4 && (this.type == 253 || this.type == 163)) {
                  throw new SnmpStatusException(12);
               }

               this.rowstatus[var7] = var4;
            }
         }

         if (var4 != var1) {
            var5.addElement(var1);
         }

      }

      public int getSubReqCount() {
         int var1 = 0;
         if (this.sublist != null) {
            ++var1;
         }

         if (this.entryoids != null) {
            var1 += this.entrycount;
         }

         return var1;
      }

      public Vector<SnmpVarBind> getSubList() {
         return this.sublist;
      }

      public int getEntryPos(SnmpOid var1) {
         return SnmpRequestTree.findOid(this.entryoids, this.entrycount, var1);
      }

      public SnmpOid getEntryOid(int var1) {
         if (this.entryoids == null) {
            return null;
         } else {
            return var1 != -1 && var1 < this.entrycount ? this.entryoids[var1] : null;
         }
      }

      public boolean isNewEntry(int var1) {
         if (this.entryoids == null) {
            return false;
         } else {
            return var1 != -1 && var1 < this.entrycount ? this.isentrynew[var1] : false;
         }
      }

      public SnmpVarBind getRowStatusVarBind(int var1) {
         if (this.entryoids == null) {
            return null;
         } else {
            return var1 != -1 && var1 < this.entrycount ? this.rowstatus[var1] : null;
         }
      }

      public Vector<SnmpVarBind> getEntrySubList(int var1) {
         if (this.entrylists == null) {
            return null;
         } else {
            return var1 != -1 && var1 < this.entrycount ? this.entrylists[var1] : null;
         }
      }

      public Iterator<SnmpOid> getEntryOids() {
         return this.entryoids == null ? null : Arrays.asList(this.entryoids).iterator();
      }

      public int getEntryCount() {
         return this.entryoids == null ? 0 : this.entrycount;
      }
   }

   static final class SnmpMibSubRequestImpl implements SnmpMibSubRequest {
      private final Vector<SnmpVarBind> varbinds;
      private final SnmpMibRequest global;
      private final int version;
      private final boolean isnew;
      private final SnmpOid entryoid;
      private final boolean getnextflag;
      private final SnmpVarBind statusvb;

      SnmpMibSubRequestImpl(SnmpMibRequest var1, Vector<SnmpVarBind> var2, SnmpOid var3, boolean var4, boolean var5, SnmpVarBind var6) {
         this.global = var1;
         this.varbinds = var2;
         this.version = var1.getVersion();
         this.entryoid = var3;
         this.isnew = var4;
         this.getnextflag = var5;
         this.statusvb = var6;
      }

      public Enumeration<SnmpVarBind> getElements() {
         return this.varbinds.elements();
      }

      public Vector<SnmpVarBind> getSubList() {
         return this.varbinds;
      }

      public final int getSize() {
         return this.varbinds == null ? 0 : this.varbinds.size();
      }

      public void addVarBind(SnmpVarBind var1) {
         this.varbinds.addElement(var1);
         this.global.addVarBind(var1);
      }

      public boolean isNewEntry() {
         return this.isnew;
      }

      public SnmpOid getEntryOid() {
         return this.entryoid;
      }

      public int getVarIndex(SnmpVarBind var1) {
         return var1 == null ? 0 : this.global.getVarIndex(var1);
      }

      public Object getUserData() {
         return this.global.getUserData();
      }

      public void registerGetException(SnmpVarBind var1, SnmpStatusException var2) throws SnmpStatusException {
         if (this.version == 0) {
            throw new SnmpStatusException(var2, this.getVarIndex(var1) + 1);
         } else if (var1 == null) {
            throw var2;
         } else if (this.getnextflag) {
            var1.value = SnmpVarBind.endOfMibView;
         } else {
            int var3 = SnmpRequestTree.mapGetException(var2.getStatus(), this.version);
            if (var3 == 225) {
               var1.value = SnmpVarBind.noSuchObject;
            } else {
               if (var3 != 224) {
                  throw new SnmpStatusException(var3, this.getVarIndex(var1) + 1);
               }

               var1.value = SnmpVarBind.noSuchInstance;
            }

         }
      }

      public void registerSetException(SnmpVarBind var1, SnmpStatusException var2) throws SnmpStatusException {
         if (this.version == 0) {
            throw new SnmpStatusException(var2, this.getVarIndex(var1) + 1);
         } else {
            throw new SnmpStatusException(15, this.getVarIndex(var1) + 1);
         }
      }

      public void registerCheckException(SnmpVarBind var1, SnmpStatusException var2) throws SnmpStatusException {
         int var3 = var2.getStatus();
         int var4 = SnmpRequestTree.mapSetException(var3, this.version);
         if (var3 != var4) {
            throw new SnmpStatusException(var4, this.getVarIndex(var1) + 1);
         } else {
            throw new SnmpStatusException(var2, this.getVarIndex(var1) + 1);
         }
      }

      public int getVersion() {
         return this.version;
      }

      public SnmpVarBind getRowStatusVarBind() {
         return this.statusvb;
      }

      public SnmpPdu getPdu() {
         return this.global.getPdu();
      }

      public int getRequestPduVersion() {
         return this.global.getRequestPduVersion();
      }

      public SnmpEngine getEngine() {
         return this.global.getEngine();
      }

      public String getPrincipal() {
         return this.global.getPrincipal();
      }

      public int getSecurityLevel() {
         return this.global.getSecurityLevel();
      }

      public int getSecurityModel() {
         return this.global.getSecurityModel();
      }

      public byte[] getContextName() {
         return this.global.getContextName();
      }

      public byte[] getAccessContextName() {
         return this.global.getAccessContextName();
      }
   }

   static final class Enum implements Enumeration<SnmpMibSubRequest> {
      private final SnmpRequestTree.Handler handler;
      private final SnmpRequestTree hlist;
      private int entry = 0;
      private int iter = 0;
      private int size = 0;

      Enum(SnmpRequestTree var1, SnmpRequestTree.Handler var2) {
         this.handler = var2;
         this.hlist = var1;
         this.size = var2.getSubReqCount();
      }

      public boolean hasMoreElements() {
         return this.iter < this.size;
      }

      public SnmpMibSubRequest nextElement() throws NoSuchElementException {
         if (this.iter == 0 && this.handler.sublist != null) {
            ++this.iter;
            return this.hlist.getSubRequest(this.handler);
         } else {
            ++this.iter;
            if (this.iter > this.size) {
               throw new NoSuchElementException();
            } else {
               SnmpMibSubRequest var1 = this.hlist.getSubRequest(this.handler, this.entry);
               ++this.entry;
               return var1;
            }
         }
      }
   }
}
