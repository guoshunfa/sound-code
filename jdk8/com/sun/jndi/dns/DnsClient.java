package com.sun.jndi.dns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.naming.CommunicationException;
import javax.naming.ConfigurationException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.ServiceUnavailableException;
import sun.security.jca.JCAUtil;

public class DnsClient {
   private static final int IDENT_OFFSET = 0;
   private static final int FLAGS_OFFSET = 2;
   private static final int NUMQ_OFFSET = 4;
   private static final int NUMANS_OFFSET = 6;
   private static final int NUMAUTH_OFFSET = 8;
   private static final int NUMADD_OFFSET = 10;
   private static final int DNS_HDR_SIZE = 12;
   private static final int NO_ERROR = 0;
   private static final int FORMAT_ERROR = 1;
   private static final int SERVER_FAILURE = 2;
   private static final int NAME_ERROR = 3;
   private static final int NOT_IMPL = 4;
   private static final int REFUSED = 5;
   private static final String[] rcodeDescription = new String[]{"No error", "DNS format error", "DNS server failure", "DNS name not found", "DNS operation not supported", "DNS service refused"};
   private static final int DEFAULT_PORT = 53;
   private static final int TRANSACTION_ID_BOUND = 65536;
   private static final SecureRandom random = JCAUtil.getSecureRandom();
   private InetAddress[] servers;
   private int[] serverPorts;
   private int timeout;
   private int retries;
   private final Object udpSocketLock = new Object();
   private static final DNSDatagramSocketFactory factory;
   private Map<Integer, ResourceRecord> reqs;
   private Map<Integer, byte[]> resps;
   private Object queuesLock = new Object();
   private static final boolean debug = false;

   public DnsClient(String[] var1, int var2, int var3) throws NamingException {
      this.timeout = var2;
      this.retries = var3;
      this.servers = new InetAddress[var1.length];
      this.serverPorts = new int[var1.length];

      for(int var4 = 0; var4 < var1.length; ++var4) {
         int var5 = var1[var4].indexOf(58, var1[var4].indexOf(93) + 1);
         this.serverPorts[var4] = var5 < 0 ? 53 : Integer.parseInt(var1[var4].substring(var5 + 1));
         String var6 = var5 < 0 ? var1[var4] : var1[var4].substring(0, var5);

         try {
            this.servers[var4] = InetAddress.getByName(var6);
         } catch (UnknownHostException var9) {
            ConfigurationException var8 = new ConfigurationException("Unknown DNS server: " + var6);
            var8.setRootCause(var9);
            throw var8;
         }
      }

      this.reqs = Collections.synchronizedMap(new HashMap());
      this.resps = Collections.synchronizedMap(new HashMap());
   }

   DatagramSocket getDatagramSocket() throws NamingException {
      try {
         return factory.open();
      } catch (SocketException var3) {
         ConfigurationException var2 = new ConfigurationException();
         var2.setRootCause(var3);
         throw var2;
      }
   }

   protected void finalize() {
      this.close();
   }

   public void close() {
      synchronized(this.queuesLock) {
         this.reqs.clear();
         this.resps.clear();
      }
   }

   ResourceRecords query(DnsName var1, int var2, int var3, boolean var4, boolean var5) throws NamingException {
      int var6;
      Packet var7;
      ResourceRecord var8;
      do {
         var6 = random.nextInt(65536);
         var7 = this.makeQueryPacket(var1, var6, var2, var3, var4);
         var8 = (ResourceRecord)this.reqs.putIfAbsent(var6, new ResourceRecord(var7.getData(), var7.length(), 12, true, false));
      } while(var8 != null);

      Object var9 = null;
      boolean[] var10 = new boolean[this.servers.length];

      try {
         for(int var11 = 0; var11 < this.retries; ++var11) {
            for(int var12 = 0; var12 < this.servers.length; ++var12) {
               if (!var10[var12]) {
                  try {
                     Object var13 = null;
                     byte[] var43 = this.doUdpQuery(var7, this.servers[var12], this.serverPorts[var12], var11, var6);
                     if (var43 == null) {
                        if (this.resps.size() > 0) {
                           var43 = this.lookupResponse(var6);
                        }

                        if (var43 == null) {
                           continue;
                        }
                     }

                     Header var14 = new Header(var43, var43.length);
                     if (!var5 || var14.authoritative) {
                        if (var14.truncated) {
                           for(int var15 = 0; var15 < this.servers.length; ++var15) {
                              int var16 = (var12 + var15) % this.servers.length;
                              if (!var10[var16]) {
                                 try {
                                    Tcp var17 = new Tcp(this.servers[var16], this.serverPorts[var16]);

                                    byte[] var18;
                                    try {
                                       var18 = this.doTcpQuery(var17, var7);
                                    } finally {
                                       var17.close();
                                    }

                                    Header var19 = new Header(var18, var18.length);
                                    if (var19.query) {
                                       throw new CommunicationException("DNS error: expecting response");
                                    }

                                    this.checkResponseCode(var19);
                                    if (!var5 || var19.authoritative) {
                                       var14 = var19;
                                       var43 = var18;
                                       break;
                                    }

                                    var10[var16] = true;
                                 } catch (Exception var36) {
                                 }
                              }
                           }
                        }

                        ResourceRecords var44 = new ResourceRecords(var43, var43.length, var14, false);
                        return var44;
                     }

                     var9 = new NameNotFoundException("DNS response not authoritative");
                     var10[var12] = true;
                  } catch (IOException var37) {
                     if (var9 == null) {
                        var9 = var37;
                     }

                     if (var37.getClass().getName().equals("java.net.PortUnreachableException")) {
                        var10[var12] = true;
                     }
                  } catch (NameNotFoundException var38) {
                     throw var38;
                  } catch (CommunicationException var39) {
                     if (var9 == null) {
                        var9 = var39;
                     }
                  } catch (NamingException var40) {
                     if (var9 == null) {
                        var9 = var40;
                     }

                     var10[var12] = true;
                  }
               }
            }
         }
      } finally {
         this.reqs.remove(var6);
      }

      if (var9 instanceof NamingException) {
         throw (NamingException)var9;
      } else {
         CommunicationException var42 = new CommunicationException("DNS error");
         var42.setRootCause((Throwable)var9);
         throw var42;
      }
   }

   ResourceRecords queryZone(DnsName var1, int var2, boolean var3) throws NamingException {
      int var4 = random.nextInt(65536);
      Packet var5 = this.makeQueryPacket(var1, var4, var2, 252, var3);
      Object var6 = null;

      for(int var7 = 0; var7 < this.servers.length; ++var7) {
         try {
            Tcp var8 = new Tcp(this.servers[var7], this.serverPorts[var7]);

            ResourceRecords var12;
            try {
               byte[] var9 = this.doTcpQuery(var8, var5);
               Header var10 = new Header(var9, var9.length);
               this.checkResponseCode(var10);
               ResourceRecords var11 = new ResourceRecords(var9, var9.length, var10, true);
               if (var11.getFirstAnsType() != 6) {
                  throw new CommunicationException("DNS error: zone xfer doesn't begin with SOA");
               }

               if (var11.answer.size() == 1 || var11.getLastAnsType() != 6) {
                  do {
                     var9 = this.continueTcpQuery(var8);
                     if (var9 == null) {
                        throw new CommunicationException("DNS error: incomplete zone transfer");
                     }

                     var10 = new Header(var9, var9.length);
                     this.checkResponseCode(var10);
                     var11.add(var9, var9.length, var10);
                  } while(var11.getLastAnsType() != 6);
               }

               var11.answer.removeElementAt(var11.answer.size() - 1);
               var12 = var11;
            } finally {
               var8.close();
            }

            return var12;
         } catch (IOException var19) {
            var6 = var19;
         } catch (NameNotFoundException var20) {
            throw var20;
         } catch (NamingException var21) {
            var6 = var21;
         }
      }

      if (var6 instanceof NamingException) {
         throw (NamingException)var6;
      } else {
         CommunicationException var22 = new CommunicationException("DNS error during zone transfer");
         var22.setRootCause((Throwable)var6);
         throw var22;
      }
   }

   private byte[] doUdpQuery(Packet var1, InetAddress var2, int var3, int var4, int var5) throws IOException, NamingException {
      byte var6 = 50;
      synchronized(this.udpSocketLock) {
         DatagramSocket var8 = this.getDatagramSocket();
         Throwable var9 = null;

         Object var43;
         try {
            DatagramPacket var10 = new DatagramPacket(var1.getData(), var1.length(), var2, var3);
            DatagramPacket var11 = new DatagramPacket(new byte[8000], 8000);
            var8.connect(var2, var3);
            int var12 = this.timeout * (1 << var4);

            try {
               var8.send(var10);
               int var13 = var12;
               boolean var14 = false;

               do {
                  var8.setSoTimeout(var13);
                  long var15 = System.currentTimeMillis();
                  var8.receive(var11);
                  long var17 = System.currentTimeMillis();
                  byte[] var19 = var11.getData();
                  if (this.isMatchResponse(var19, var5)) {
                     byte[] var20 = var19;
                     return var20;
                  }

                  var13 = var12 - (int)(var17 - var15);
               } while(var13 > var6);
            } finally {
               var8.disconnect();
            }

            var43 = null;
         } catch (Throwable var40) {
            var9 = var40;
            throw var40;
         } finally {
            if (var8 != null) {
               if (var9 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var38) {
                     var9.addSuppressed(var38);
                  }
               } else {
                  var8.close();
               }
            }

         }

         return (byte[])var43;
      }
   }

   private byte[] doTcpQuery(Tcp var1, Packet var2) throws IOException {
      int var3 = var2.length();
      var1.out.write(var3 >> 8);
      var1.out.write(var3);
      var1.out.write(var2.getData(), 0, var3);
      var1.out.flush();
      byte[] var4 = this.continueTcpQuery(var1);
      if (var4 == null) {
         throw new IOException("DNS error: no response");
      } else {
         return var4;
      }
   }

   private byte[] continueTcpQuery(Tcp var1) throws IOException {
      int var2 = var1.in.read();
      if (var2 == -1) {
         return null;
      } else {
         int var3 = var1.in.read();
         if (var3 == -1) {
            throw new IOException("Corrupted DNS response: bad length");
         } else {
            int var4 = var2 << 8 | var3;
            byte[] var5 = new byte[var4];

            int var7;
            for(int var6 = 0; var4 > 0; var6 += var7) {
               var7 = var1.in.read(var5, var6, var4);
               if (var7 == -1) {
                  throw new IOException("Corrupted DNS response: too little data");
               }

               var4 -= var7;
            }

            return var5;
         }
      }
   }

   private Packet makeQueryPacket(DnsName var1, int var2, int var3, int var4, boolean var5) {
      short var6 = var1.getOctets();
      int var7 = 12 + var6 + 4;
      Packet var8 = new Packet(var7);
      int var9 = var5 ? 256 : 0;
      var8.putShort(var2, 0);
      var8.putShort(var9, 2);
      var8.putShort(1, 4);
      var8.putShort(0, 6);
      var8.putInt(0, 8);
      this.makeQueryName(var1, var8, 12);
      var8.putShort(var4, 12 + var6);
      var8.putShort(var3, 12 + var6 + 2);
      return var8;
   }

   private void makeQueryName(DnsName var1, Packet var2, int var3) {
      for(int var4 = var1.size() - 1; var4 >= 0; --var4) {
         String var5 = var1.get(var4);
         int var6 = var5.length();
         var2.putByte(var6, var3++);

         for(int var7 = 0; var7 < var6; ++var7) {
            var2.putByte(var5.charAt(var7), var3++);
         }
      }

      if (!var1.hasRootLabel()) {
         var2.putByte(0, var3);
      }

   }

   private byte[] lookupResponse(Integer var1) throws NamingException {
      byte[] var2;
      if ((var2 = (byte[])this.resps.get(var1)) != null) {
         this.checkResponseCode(new Header(var2, var2.length));
         synchronized(this.queuesLock) {
            this.resps.remove(var1);
            this.reqs.remove(var1);
         }
      }

      return var2;
   }

   private boolean isMatchResponse(byte[] var1, int var2) throws NamingException {
      Header var3 = new Header(var1, var1.length);
      if (var3.query) {
         throw new CommunicationException("DNS error: expecting response");
      } else if (!this.reqs.containsKey(var2)) {
         return false;
      } else if (var3.xid != var2) {
         synchronized(this.queuesLock) {
            if (this.reqs.containsKey(var3.xid)) {
               this.resps.put(var3.xid, var1);
            }

            return false;
         }
      } else {
         this.checkResponseCode(var3);
         if (!var3.query && var3.numQuestions == 1) {
            ResourceRecord var4 = new ResourceRecord(var1, var1.length, 12, true, false);
            ResourceRecord var5 = (ResourceRecord)this.reqs.get(var2);
            int var6 = var5.getType();
            int var7 = var5.getRrclass();
            DnsName var8 = var5.getName();
            if ((var6 == 255 || var6 == var4.getType()) && (var7 == 255 || var7 == var4.getRrclass()) && var8.equals(var4.getName())) {
               synchronized(this.queuesLock) {
                  this.resps.remove(var2);
                  this.reqs.remove(var2);
                  return true;
               }
            }
         }

         return false;
      }
   }

   private void checkResponseCode(Header var1) throws NamingException {
      int var2 = var1.rcode;
      if (var2 != 0) {
         String var3 = var2 < rcodeDescription.length ? rcodeDescription[var2] : "DNS error";
         var3 = var3 + " [response code " + var2 + "]";
         switch(var2) {
         case 1:
         default:
            throw new NamingException(var3);
         case 2:
            throw new ServiceUnavailableException(var3);
         case 3:
            throw new NameNotFoundException(var3);
         case 4:
         case 5:
            throw new OperationNotSupportedException(var3);
         }
      }
   }

   private static void dprint(String var0) {
   }

   static {
      factory = new DNSDatagramSocketFactory(random);
   }
}
