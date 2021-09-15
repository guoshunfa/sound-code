package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.RefreshFailedException;
import javax.security.auth.Refreshable;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Credentials;
import sun.security.krb5.KrbException;

public class KerberosTicket implements Destroyable, Refreshable, Serializable {
   private static final long serialVersionUID = 7395334370157380539L;
   private static final int FORWARDABLE_TICKET_FLAG = 1;
   private static final int FORWARDED_TICKET_FLAG = 2;
   private static final int PROXIABLE_TICKET_FLAG = 3;
   private static final int PROXY_TICKET_FLAG = 4;
   private static final int POSTDATED_TICKET_FLAG = 6;
   private static final int RENEWABLE_TICKET_FLAG = 8;
   private static final int INITIAL_TICKET_FLAG = 9;
   private static final int NUM_FLAGS = 32;
   private byte[] asn1Encoding;
   private KeyImpl sessionKey;
   private boolean[] flags;
   private Date authTime;
   private Date startTime;
   private Date endTime;
   private Date renewTill;
   private KerberosPrincipal client;
   private KerberosPrincipal server;
   private InetAddress[] clientAddresses;
   private transient boolean destroyed = false;

   public KerberosTicket(byte[] var1, KerberosPrincipal var2, KerberosPrincipal var3, byte[] var4, int var5, boolean[] var6, Date var7, Date var8, Date var9, Date var10, InetAddress[] var11) {
      this.init(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   private void init(byte[] var1, KerberosPrincipal var2, KerberosPrincipal var3, byte[] var4, int var5, boolean[] var6, Date var7, Date var8, Date var9, Date var10, InetAddress[] var11) {
      if (var4 == null) {
         throw new IllegalArgumentException("Session key for ticket cannot be null");
      } else {
         this.init(var1, var2, var3, new KeyImpl(var4, var5), var6, var7, var8, var9, var10, var11);
      }
   }

   private void init(byte[] var1, KerberosPrincipal var2, KerberosPrincipal var3, KeyImpl var4, boolean[] var5, Date var6, Date var7, Date var8, Date var9, InetAddress[] var10) {
      if (var1 == null) {
         throw new IllegalArgumentException("ASN.1 encoding of ticket cannot be null");
      } else {
         this.asn1Encoding = (byte[])var1.clone();
         if (var2 == null) {
            throw new IllegalArgumentException("Client name in ticket cannot be null");
         } else {
            this.client = var2;
            if (var3 == null) {
               throw new IllegalArgumentException("Server name in ticket cannot be null");
            } else {
               this.server = var3;
               this.sessionKey = var4;
               if (var5 != null) {
                  if (var5.length >= 32) {
                     this.flags = (boolean[])var5.clone();
                  } else {
                     this.flags = new boolean[32];

                     for(int var11 = 0; var11 < var5.length; ++var11) {
                        this.flags[var11] = var5[var11];
                     }
                  }
               } else {
                  this.flags = new boolean[32];
               }

               if (this.flags[8]) {
                  if (var9 == null) {
                     throw new IllegalArgumentException("The renewable period end time cannot be null for renewable tickets.");
                  }

                  this.renewTill = new Date(var9.getTime());
               }

               if (var6 != null) {
                  this.authTime = new Date(var6.getTime());
               }

               if (var7 != null) {
                  this.startTime = new Date(var7.getTime());
               } else {
                  this.startTime = this.authTime;
               }

               if (var8 == null) {
                  throw new IllegalArgumentException("End time for ticket validity cannot be null");
               } else {
                  this.endTime = new Date(var8.getTime());
                  if (var10 != null) {
                     this.clientAddresses = (InetAddress[])var10.clone();
                  }

               }
            }
         }
      }
   }

   public final KerberosPrincipal getClient() {
      return this.client;
   }

   public final KerberosPrincipal getServer() {
      return this.server;
   }

   public final SecretKey getSessionKey() {
      if (this.destroyed) {
         throw new IllegalStateException("This ticket is no longer valid");
      } else {
         return this.sessionKey;
      }
   }

   public final int getSessionKeyType() {
      if (this.destroyed) {
         throw new IllegalStateException("This ticket is no longer valid");
      } else {
         return this.sessionKey.getKeyType();
      }
   }

   public final boolean isForwardable() {
      return this.flags == null ? false : this.flags[1];
   }

   public final boolean isForwarded() {
      return this.flags == null ? false : this.flags[2];
   }

   public final boolean isProxiable() {
      return this.flags == null ? false : this.flags[3];
   }

   public final boolean isProxy() {
      return this.flags == null ? false : this.flags[4];
   }

   public final boolean isPostdated() {
      return this.flags == null ? false : this.flags[6];
   }

   public final boolean isRenewable() {
      return this.flags == null ? false : this.flags[8];
   }

   public final boolean isInitial() {
      return this.flags == null ? false : this.flags[9];
   }

   public final boolean[] getFlags() {
      return this.flags == null ? null : (boolean[])this.flags.clone();
   }

   public final Date getAuthTime() {
      return this.authTime == null ? null : (Date)this.authTime.clone();
   }

   public final Date getStartTime() {
      return this.startTime == null ? null : (Date)this.startTime.clone();
   }

   public final Date getEndTime() {
      return this.endTime == null ? null : (Date)this.endTime.clone();
   }

   public final Date getRenewTill() {
      return this.renewTill == null ? null : (Date)this.renewTill.clone();
   }

   public final InetAddress[] getClientAddresses() {
      return this.clientAddresses == null ? null : (InetAddress[])this.clientAddresses.clone();
   }

   public final byte[] getEncoded() {
      if (this.destroyed) {
         throw new IllegalStateException("This ticket is no longer valid");
      } else {
         return (byte[])this.asn1Encoding.clone();
      }
   }

   public boolean isCurrent() {
      return this.endTime == null ? false : System.currentTimeMillis() <= this.endTime.getTime();
   }

   public void refresh() throws RefreshFailedException {
      if (this.destroyed) {
         throw new RefreshFailedException("A destroyed ticket cannot be renewd.");
      } else if (!this.isRenewable()) {
         throw new RefreshFailedException("This ticket is not renewable");
      } else if (System.currentTimeMillis() > this.getRenewTill().getTime()) {
         throw new RefreshFailedException("This ticket is past its last renewal time.");
      } else {
         Object var1 = null;
         Credentials var2 = null;

         try {
            var2 = new Credentials(this.asn1Encoding, this.client.toString(), this.server.toString(), this.sessionKey.getEncoded(), this.sessionKey.getKeyType(), this.flags, this.authTime, this.startTime, this.endTime, this.renewTill, this.clientAddresses);
            var2 = var2.renew();
         } catch (KrbException var8) {
            var1 = var8;
         } catch (IOException var9) {
            var1 = var9;
         }

         if (var1 != null) {
            RefreshFailedException var3 = new RefreshFailedException("Failed to renew Kerberos Ticket for client " + this.client + " and server " + this.server + " - " + ((Throwable)var1).getMessage());
            var3.initCause((Throwable)var1);
            throw var3;
         } else {
            synchronized(this) {
               try {
                  this.destroy();
               } catch (DestroyFailedException var6) {
               }

               this.init(var2.getEncoded(), new KerberosPrincipal(var2.getClient().getName()), new KerberosPrincipal(var2.getServer().getName(), 2), var2.getSessionKey().getBytes(), var2.getSessionKey().getEType(), var2.getFlags(), var2.getAuthTime(), var2.getStartTime(), var2.getEndTime(), var2.getRenewTill(), var2.getClientAddresses());
               this.destroyed = false;
            }
         }
      }
   }

   public void destroy() throws DestroyFailedException {
      if (!this.destroyed) {
         Arrays.fill((byte[])this.asn1Encoding, (byte)0);
         this.client = null;
         this.server = null;
         this.sessionKey.destroy();
         this.flags = null;
         this.authTime = null;
         this.startTime = null;
         this.endTime = null;
         this.renewTill = null;
         this.clientAddresses = null;
         this.destroyed = true;
      }

   }

   public boolean isDestroyed() {
      return this.destroyed;
   }

   public String toString() {
      if (this.destroyed) {
         return "Destroyed KerberosTicket";
      } else {
         StringBuffer var1 = new StringBuffer();
         if (this.clientAddresses != null) {
            for(int var2 = 0; var2 < this.clientAddresses.length; ++var2) {
               var1.append("clientAddresses[" + var2 + "] = " + this.clientAddresses[var2].toString());
            }
         }

         return "Ticket (hex) = \n" + (new HexDumpEncoder()).encodeBuffer(this.asn1Encoding) + "\nClient Principal = " + this.client.toString() + "\nServer Principal = " + this.server.toString() + "\nSession Key = " + this.sessionKey.toString() + "\nForwardable Ticket " + this.flags[1] + "\nForwarded Ticket " + this.flags[2] + "\nProxiable Ticket " + this.flags[3] + "\nProxy Ticket " + this.flags[4] + "\nPostdated Ticket " + this.flags[6] + "\nRenewable Ticket " + this.flags[8] + "\nInitial Ticket " + this.flags[8] + "\nAuth Time = " + this.authTime + "\nStart Time = " + this.startTime + "\nEnd Time = " + this.endTime.toString() + "\nRenew Till = " + this.renewTill + "\nClient Addresses " + (this.clientAddresses == null ? " Null " : var1.toString() + "\n");
      }
   }

   public int hashCode() {
      byte var1 = 17;
      if (this.isDestroyed()) {
         return var1;
      } else {
         int var2 = var1 * 37 + Arrays.hashCode(this.getEncoded());
         var2 = var2 * 37 + this.endTime.hashCode();
         var2 = var2 * 37 + this.client.hashCode();
         var2 = var2 * 37 + this.server.hashCode();
         var2 = var2 * 37 + this.sessionKey.hashCode();
         if (this.authTime != null) {
            var2 = var2 * 37 + this.authTime.hashCode();
         }

         if (this.startTime != null) {
            var2 = var2 * 37 + this.startTime.hashCode();
         }

         if (this.renewTill != null) {
            var2 = var2 * 37 + this.renewTill.hashCode();
         }

         var2 = var2 * 37 + Arrays.hashCode((Object[])this.clientAddresses);
         return var2 * 37 + Arrays.hashCode(this.flags);
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof KerberosTicket)) {
         return false;
      } else {
         KerberosTicket var2 = (KerberosTicket)var1;
         if (!this.isDestroyed() && !var2.isDestroyed()) {
            if (Arrays.equals(this.getEncoded(), var2.getEncoded()) && this.endTime.equals(var2.getEndTime()) && this.server.equals(var2.getServer()) && this.client.equals(var2.getClient()) && this.sessionKey.equals(var2.getSessionKey()) && Arrays.equals((Object[])this.clientAddresses, (Object[])var2.getClientAddresses()) && Arrays.equals(this.flags, var2.getFlags())) {
               if (this.authTime == null) {
                  if (var2.getAuthTime() != null) {
                     return false;
                  }
               } else if (!this.authTime.equals(var2.getAuthTime())) {
                  return false;
               }

               if (this.startTime == null) {
                  if (var2.getStartTime() != null) {
                     return false;
                  }
               } else if (!this.startTime.equals(var2.getStartTime())) {
                  return false;
               }

               if (this.renewTill == null) {
                  if (var2.getRenewTill() != null) {
                     return false;
                  }
               } else if (!this.renewTill.equals(var2.getRenewTill())) {
                  return false;
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.sessionKey == null) {
         throw new InvalidObjectException("Session key cannot be null");
      } else {
         try {
            this.init(this.asn1Encoding, this.client, this.server, this.sessionKey, this.flags, this.authTime, this.startTime, this.endTime, this.renewTill, this.clientAddresses);
         } catch (IllegalArgumentException var3) {
            throw (InvalidObjectException)(new InvalidObjectException(var3.getMessage())).initCause(var3);
         }
      }
   }
}
