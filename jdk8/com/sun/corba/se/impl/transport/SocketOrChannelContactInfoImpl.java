package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.SocketInfo;

public class SocketOrChannelContactInfoImpl extends CorbaContactInfoBase implements SocketInfo {
   protected boolean isHashCodeCached;
   protected int cachedHashCode;
   protected String socketType;
   protected String hostname;
   protected int port;

   protected SocketOrChannelContactInfoImpl() {
      this.isHashCodeCached = false;
   }

   protected SocketOrChannelContactInfoImpl(ORB var1, CorbaContactInfoList var2) {
      this.isHashCodeCached = false;
      this.orb = var1;
      this.contactInfoList = var2;
   }

   public SocketOrChannelContactInfoImpl(ORB var1, CorbaContactInfoList var2, String var3, String var4, int var5) {
      this(var1, var2);
      this.socketType = var3;
      this.hostname = var4;
      this.port = var5;
   }

   public SocketOrChannelContactInfoImpl(ORB var1, CorbaContactInfoList var2, IOR var3, short var4, String var5, String var6, int var7) {
      this(var1, var2, var5, var6, var7);
      this.effectiveTargetIOR = var3;
      this.addressingDisposition = var4;
   }

   public boolean isConnectionBased() {
      return true;
   }

   public boolean shouldCacheConnection() {
      return true;
   }

   public String getConnectionCacheType() {
      return "SocketOrChannelConnectionCache";
   }

   public Connection createConnection() {
      SocketOrChannelConnectionImpl var1 = new SocketOrChannelConnectionImpl(this.orb, this, this.socketType, this.hostname, this.port);
      return var1;
   }

   public String getMonitoringName() {
      return "SocketConnections";
   }

   public String getType() {
      return this.socketType;
   }

   public String getHost() {
      return this.hostname;
   }

   public int getPort() {
      return this.port;
   }

   public int hashCode() {
      if (!this.isHashCodeCached) {
         this.cachedHashCode = this.socketType.hashCode() ^ this.hostname.hashCode() ^ this.port;
         this.isHashCodeCached = true;
      }

      return this.cachedHashCode;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof SocketOrChannelContactInfoImpl)) {
         return false;
      } else {
         SocketOrChannelContactInfoImpl var2 = (SocketOrChannelContactInfoImpl)var1;
         if (this.port != var2.port) {
            return false;
         } else if (!this.hostname.equals(var2.hostname)) {
            return false;
         } else {
            if (this.socketType == null) {
               if (var2.socketType != null) {
                  return false;
               }
            } else if (!this.socketType.equals(var2.socketType)) {
               return false;
            }

            return true;
         }
      }
   }

   public String toString() {
      return "SocketOrChannelContactInfoImpl[" + this.socketType + " " + this.hostname + " " + this.port + "]";
   }

   protected void dprint(String var1) {
      ORBUtility.dprint("SocketOrChannelContactInfoImpl", var1);
   }
}
