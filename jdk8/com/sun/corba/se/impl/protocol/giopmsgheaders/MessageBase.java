package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
import com.sun.corba.se.impl.encoding.CDRInputStream_1_0;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.AddressingDispositionException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.RequestPartitioningComponent;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Principal;
import org.omg.CORBA.SystemException;
import org.omg.IOP.TaggedProfile;
import sun.corba.SharedSecrets;

public abstract class MessageBase implements Message {
   public byte[] giopHeader;
   private ByteBuffer byteBuffer;
   private int threadPoolToUse;
   byte encodingVersion = 0;
   private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.protocol");

   public static String typeToString(int var0) {
      return typeToString((byte)var0);
   }

   public static String typeToString(byte var0) {
      String var1 = var0 + "/";
      switch(var0) {
      case 0:
         var1 = var1 + "GIOPRequest";
         break;
      case 1:
         var1 = var1 + "GIOPReply";
         break;
      case 2:
         var1 = var1 + "GIOPCancelRequest";
         break;
      case 3:
         var1 = var1 + "GIOPLocateRequest";
         break;
      case 4:
         var1 = var1 + "GIOPLocateReply";
         break;
      case 5:
         var1 = var1 + "GIOPCloseConnection";
         break;
      case 6:
         var1 = var1 + "GIOPMessageError";
         break;
      case 7:
         var1 = var1 + "GIOPFragment";
         break;
      default:
         var1 = var1 + "Unknown";
      }

      return var1;
   }

   public static MessageBase readGIOPMessage(ORB var0, CorbaConnection var1) {
      MessageBase var2 = readGIOPHeader(var0, var1);
      var2 = (MessageBase)readGIOPBody(var0, var1, var2);
      return var2;
   }

   public static MessageBase readGIOPHeader(ORB var0, CorbaConnection var1) {
      Object var2 = null;
      ReadTimeouts var3 = var0.getORBData().getTransportTCPReadTimeouts();
      ByteBuffer var4 = null;

      try {
         var4 = var1.read(12, 0, 12, (long)var3.get_max_giop_header_time_to_wait());
      } catch (IOException var14) {
         throw wrapper.ioexceptionWhenReadingConnection((Throwable)var14);
      }

      if (var0.giopDebugFlag) {
         dprint(".readGIOPHeader: " + typeToString(var4.get(7)));
         dprint(".readGIOPHeader: GIOP header is: ");
         ByteBuffer var5 = var4.asReadOnlyBuffer();
         var5.position(0).limit(12);
         ByteBufferWithInfo var6 = new ByteBufferWithInfo(var0, var5);
         var6.buflen = 12;
         CDRInputStream_1_0.printBuffer(var6);
      }

      int var15 = var4.get(0) << 24 & -16777216;
      int var16 = var4.get(1) << 16 & 16711680;
      int var7 = var4.get(2) << 8 & '\uff00';
      int var8 = var4.get(3) << 0 & 255;
      int var9 = var15 | var16 | var7 | var8;
      if (var9 != 1195986768) {
         throw wrapper.giopMagicError(CompletionStatus.COMPLETED_MAYBE);
      } else {
         byte var10 = 0;
         if (var4.get(4) == 13 && var4.get(5) <= 1 && var4.get(5) > 0 && var0.getORBData().isJavaSerializationEnabled()) {
            var10 = var4.get(5);
            var4.put(4, (byte)1);
            var4.put(5, (byte)2);
         }

         GIOPVersion var11 = var0.getORBData().getGIOPVersion();
         if (var0.giopDebugFlag) {
            dprint(".readGIOPHeader: Message GIOP version: " + var4.get(4) + '.' + var4.get(5));
            dprint(".readGIOPHeader: ORB Max GIOP Version: " + var11);
         }

         if ((var4.get(4) > var11.getMajor() || var4.get(4) == var11.getMajor() && var4.get(5) > var11.getMinor()) && var4.get(7) != 6) {
            throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
         } else {
            AreFragmentsAllowed(var4.get(4), var4.get(5), var4.get(6), var4.get(7));
            switch(var4.get(7)) {
            case 0:
               if (var0.giopDebugFlag) {
                  dprint(".readGIOPHeader: creating RequestMessage");
               }

               if (var4.get(4) == 1 && var4.get(5) == 0) {
                  var2 = new RequestMessage_1_0(var0);
               } else {
                  if (var4.get(4) == 1 && var4.get(5) == 1) {
                     var2 = new RequestMessage_1_1(var0);
                     break;
                  }

                  if (var4.get(4) != 1 || var4.get(5) != 2) {
                     throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
                  }

                  var2 = new RequestMessage_1_2(var0);
               }
               break;
            case 1:
               if (var0.giopDebugFlag) {
                  dprint(".readGIOPHeader: creating ReplyMessage");
               }

               if (var4.get(4) == 1 && var4.get(5) == 0) {
                  var2 = new ReplyMessage_1_0(var0);
               } else if (var4.get(4) == 1 && var4.get(5) == 1) {
                  var2 = new ReplyMessage_1_1(var0);
               } else {
                  if (var4.get(4) == 1 && var4.get(5) == 2) {
                     var2 = new ReplyMessage_1_2(var0);
                     break;
                  }

                  throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
               }
               break;
            case 2:
               if (var0.giopDebugFlag) {
                  dprint(".readGIOPHeader: creating CancelRequestMessage");
               }

               if (var4.get(4) == 1 && var4.get(5) == 0) {
                  var2 = new CancelRequestMessage_1_0();
               } else if (var4.get(4) == 1 && var4.get(5) == 1) {
                  var2 = new CancelRequestMessage_1_1();
               } else {
                  if (var4.get(4) == 1 && var4.get(5) == 2) {
                     var2 = new CancelRequestMessage_1_2();
                     break;
                  }

                  throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
               }
               break;
            case 3:
               if (var0.giopDebugFlag) {
                  dprint(".readGIOPHeader: creating LocateRequestMessage");
               }

               if (var4.get(4) == 1 && var4.get(5) == 0) {
                  var2 = new LocateRequestMessage_1_0(var0);
               } else if (var4.get(4) == 1 && var4.get(5) == 1) {
                  var2 = new LocateRequestMessage_1_1(var0);
               } else {
                  if (var4.get(4) == 1 && var4.get(5) == 2) {
                     var2 = new LocateRequestMessage_1_2(var0);
                     break;
                  }

                  throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
               }
               break;
            case 4:
               if (var0.giopDebugFlag) {
                  dprint(".readGIOPHeader: creating LocateReplyMessage");
               }

               if (var4.get(4) == 1 && var4.get(5) == 0) {
                  var2 = new LocateReplyMessage_1_0(var0);
               } else {
                  if (var4.get(4) == 1 && var4.get(5) == 1) {
                     var2 = new LocateReplyMessage_1_1(var0);
                     break;
                  }

                  if (var4.get(4) != 1 || var4.get(5) != 2) {
                     throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
                  }

                  var2 = new LocateReplyMessage_1_2(var0);
               }
               break;
            case 5:
            case 6:
               if (var0.giopDebugFlag) {
                  dprint(".readGIOPHeader: creating Message for CloseConnection or MessageError");
               }

               if (var4.get(4) == 1 && var4.get(5) == 0) {
                  var2 = new Message_1_0();
               } else if (var4.get(4) == 1 && var4.get(5) == 1) {
                  var2 = new Message_1_1();
               } else {
                  if (var4.get(4) == 1 && var4.get(5) == 2) {
                     var2 = new Message_1_1();
                     break;
                  }

                  throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
               }
               break;
            case 7:
               if (var0.giopDebugFlag) {
                  dprint(".readGIOPHeader: creating FragmentMessage");
               }

               if (var4.get(4) == 1 && var4.get(5) == 0) {
                  break;
               }

               if (var4.get(4) == 1 && var4.get(5) == 1) {
                  var2 = new FragmentMessage_1_1();
                  break;
               } else {
                  if (var4.get(4) == 1 && var4.get(5) == 2) {
                     var2 = new FragmentMessage_1_2();
                     break;
                  }

                  throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
               }
            default:
               if (var0.giopDebugFlag) {
                  dprint(".readGIOPHeader: UNKNOWN MESSAGE TYPE: " + var4.get(7));
               }

               throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
            }

            if (var4.get(4) == 1 && var4.get(5) == 0) {
               Message_1_0 var17 = (Message_1_0)var2;
               var17.magic = var9;
               var17.GIOP_version = new GIOPVersion(var4.get(4), var4.get(5));
               var17.byte_order = var4.get(6) == 1;
               ((MessageBase)var2).threadPoolToUse = 0;
               var17.message_type = var4.get(7);
               var17.message_size = readSize(var4.get(8), var4.get(9), var4.get(10), var4.get(11), var17.isLittleEndian()) + 12;
            } else {
               Message_1_1 var12 = (Message_1_1)var2;
               var12.magic = var9;
               var12.GIOP_version = new GIOPVersion(var4.get(4), var4.get(5));
               var12.flags = (byte)(var4.get(6) & 3);
               ((MessageBase)var2).threadPoolToUse = var4.get(6) >>> 2 & 63;
               var12.message_type = var4.get(7);
               var12.message_size = readSize(var4.get(8), var4.get(9), var4.get(10), var4.get(11), var12.isLittleEndian()) + 12;
            }

            if (var0.giopDebugFlag) {
               dprint(".readGIOPHeader: header construction complete.");
               ByteBuffer var18 = var4.asReadOnlyBuffer();
               byte[] var13 = new byte[12];
               var18.position(0).limit(12);
               var18.get(var13, 0, var13.length);
               ((MessageBase)var2).giopHeader = var13;
            }

            ((MessageBase)var2).setByteBuffer(var4);
            ((MessageBase)var2).setEncodingVersion(var10);
            return (MessageBase)var2;
         }
      }
   }

   public static Message readGIOPBody(ORB var0, CorbaConnection var1, Message var2) {
      ReadTimeouts var3 = var0.getORBData().getTransportTCPReadTimeouts();
      ByteBuffer var4 = var2.getByteBuffer();
      var4.position(12);
      int var5 = var2.getSize() - 12;

      try {
         var4 = var1.read(var4, 12, var5, (long)var3.get_max_time_to_wait());
      } catch (IOException var8) {
         throw wrapper.ioexceptionWhenReadingConnection((Throwable)var8);
      }

      var2.setByteBuffer(var4);
      if (var0.giopDebugFlag) {
         dprint(".readGIOPBody: received message:");
         ByteBuffer var6 = var4.asReadOnlyBuffer();
         var6.position(0).limit(var2.getSize());
         ByteBufferWithInfo var7 = new ByteBufferWithInfo(var0, var6);
         CDRInputStream_1_0.printBuffer(var7);
      }

      return var2;
   }

   private static RequestMessage createRequest(ORB var0, GIOPVersion var1, byte var2, int var3, boolean var4, byte[] var5, String var6, ServiceContexts var7, Principal var8) {
      if (var1.equals(GIOPVersion.V1_0)) {
         return new RequestMessage_1_0(var0, var7, var3, var4, var5, var6, var8);
      } else if (var1.equals(GIOPVersion.V1_1)) {
         return new RequestMessage_1_1(var0, var7, var3, var4, new byte[]{0, 0, 0}, var5, var6, var8);
      } else if (var1.equals(GIOPVersion.V1_2)) {
         boolean var9 = true;
         byte var12;
         if (var4) {
            var12 = 3;
         } else {
            var12 = 0;
         }

         TargetAddress var10 = new TargetAddress();
         var10.object_key(var5);
         RequestMessage_1_2 var11 = new RequestMessage_1_2(var0, var3, var12, new byte[]{0, 0, 0}, var10, var6, var7);
         var11.setEncodingVersion(var2);
         return var11;
      } else {
         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
   }

   public static RequestMessage createRequest(ORB var0, GIOPVersion var1, byte var2, int var3, boolean var4, IOR var5, short var6, String var7, ServiceContexts var8, Principal var9) {
      Object var10 = null;
      IIOPProfile var11 = var5.getProfile();
      if (var6 == 0) {
         var11 = var5.getProfile();
         ObjectKey var12 = var11.getObjectKey();
         byte[] var13 = var12.getBytes(var0);
         var10 = createRequest(var0, var1, var2, var3, var4, var13, var7, var8, var9);
      } else {
         if (!var1.equals(GIOPVersion.V1_2)) {
            throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
         }

         boolean var15 = true;
         byte var16;
         if (var4) {
            var16 = 3;
         } else {
            var16 = 0;
         }

         TargetAddress var17 = new TargetAddress();
         if (var6 == 1) {
            var11 = var5.getProfile();
            var17.profile(var11.getIOPProfile());
         } else {
            if (var6 != 2) {
               throw wrapper.illegalTargetAddressDisposition(CompletionStatus.COMPLETED_NO);
            }

            IORAddressingInfo var14 = new IORAddressingInfo(0, var5.getIOPIOR());
            var17.ior(var14);
         }

         var10 = new RequestMessage_1_2(var0, var3, var16, new byte[]{0, 0, 0}, var17, var7, var8);
         ((RequestMessage)var10).setEncodingVersion(var2);
      }

      if (var1.supportsIORIIOPProfileComponents()) {
         int var18 = 0;
         IIOPProfileTemplate var19 = (IIOPProfileTemplate)var11.getTaggedProfileTemplate();
         Iterator var20 = var19.iteratorById(1398099457);
         if (var20.hasNext()) {
            var18 = ((RequestPartitioningComponent)var20.next()).getRequestPartitioningId();
         }

         if (var18 < 0 || var18 > 63) {
            throw wrapper.invalidRequestPartitioningId(new Integer(var18), new Integer(0), new Integer(63));
         }

         ((RequestMessage)var10).setThreadPoolToUse(var18);
      }

      return (RequestMessage)var10;
   }

   public static ReplyMessage createReply(ORB var0, GIOPVersion var1, byte var2, int var3, int var4, ServiceContexts var5, IOR var6) {
      if (var1.equals(GIOPVersion.V1_0)) {
         return new ReplyMessage_1_0(var0, var5, var3, var4, var6);
      } else if (var1.equals(GIOPVersion.V1_1)) {
         return new ReplyMessage_1_1(var0, var5, var3, var4, var6);
      } else if (var1.equals(GIOPVersion.V1_2)) {
         ReplyMessage_1_2 var7 = new ReplyMessage_1_2(var0, var3, var4, var5, var6);
         var7.setEncodingVersion(var2);
         return var7;
      } else {
         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
   }

   public static LocateRequestMessage createLocateRequest(ORB var0, GIOPVersion var1, byte var2, int var3, byte[] var4) {
      if (var1.equals(GIOPVersion.V1_0)) {
         return new LocateRequestMessage_1_0(var0, var3, var4);
      } else if (var1.equals(GIOPVersion.V1_1)) {
         return new LocateRequestMessage_1_1(var0, var3, var4);
      } else if (var1.equals(GIOPVersion.V1_2)) {
         TargetAddress var5 = new TargetAddress();
         var5.object_key(var4);
         LocateRequestMessage_1_2 var6 = new LocateRequestMessage_1_2(var0, var3, var5);
         var6.setEncodingVersion(var2);
         return var6;
      } else {
         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
   }

   public static LocateReplyMessage createLocateReply(ORB var0, GIOPVersion var1, byte var2, int var3, int var4, IOR var5) {
      if (var1.equals(GIOPVersion.V1_0)) {
         return new LocateReplyMessage_1_0(var0, var3, var4, var5);
      } else if (var1.equals(GIOPVersion.V1_1)) {
         return new LocateReplyMessage_1_1(var0, var3, var4, var5);
      } else if (var1.equals(GIOPVersion.V1_2)) {
         LocateReplyMessage_1_2 var6 = new LocateReplyMessage_1_2(var0, var3, var4, var5);
         var6.setEncodingVersion(var2);
         return var6;
      } else {
         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
   }

   public static CancelRequestMessage createCancelRequest(GIOPVersion var0, int var1) {
      if (var0.equals(GIOPVersion.V1_0)) {
         return new CancelRequestMessage_1_0(var1);
      } else if (var0.equals(GIOPVersion.V1_1)) {
         return new CancelRequestMessage_1_1(var1);
      } else if (var0.equals(GIOPVersion.V1_2)) {
         return new CancelRequestMessage_1_2(var1);
      } else {
         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
   }

   public static Message createCloseConnection(GIOPVersion var0) {
      if (var0.equals(GIOPVersion.V1_0)) {
         return new Message_1_0(1195986768, false, (byte)5, 0);
      } else if (var0.equals(GIOPVersion.V1_1)) {
         return new Message_1_1(1195986768, GIOPVersion.V1_1, (byte)0, (byte)5, 0);
      } else if (var0.equals(GIOPVersion.V1_2)) {
         return new Message_1_1(1195986768, GIOPVersion.V1_2, (byte)0, (byte)5, 0);
      } else {
         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
   }

   public static Message createMessageError(GIOPVersion var0) {
      if (var0.equals(GIOPVersion.V1_0)) {
         return new Message_1_0(1195986768, false, (byte)6, 0);
      } else if (var0.equals(GIOPVersion.V1_1)) {
         return new Message_1_1(1195986768, GIOPVersion.V1_1, (byte)0, (byte)6, 0);
      } else if (var0.equals(GIOPVersion.V1_2)) {
         return new Message_1_1(1195986768, GIOPVersion.V1_2, (byte)0, (byte)6, 0);
      } else {
         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      }
   }

   public static FragmentMessage createFragmentMessage(GIOPVersion var0) {
      return null;
   }

   public static int getRequestId(Message var0) {
      switch(var0.getType()) {
      case 0:
         return ((RequestMessage)var0).getRequestId();
      case 1:
         return ((ReplyMessage)var0).getRequestId();
      case 2:
         return ((CancelRequestMessage)var0).getRequestId();
      case 3:
         return ((LocateRequestMessage)var0).getRequestId();
      case 4:
         return ((LocateReplyMessage)var0).getRequestId();
      case 5:
      case 6:
      default:
         throw wrapper.illegalGiopMsgType(CompletionStatus.COMPLETED_MAYBE);
      case 7:
         return ((FragmentMessage)var0).getRequestId();
      }
   }

   public static void setFlag(ByteBuffer var0, int var1) {
      byte var2 = var0.get(6);
      var2 = (byte)(var2 | var1);
      var0.put(6, var2);
   }

   public static void clearFlag(byte[] var0, int var1) {
      var0[6] = (byte)(var0[6] & (255 ^ var1));
   }

   private static void AreFragmentsAllowed(byte var0, byte var1, byte var2, byte var3) {
      if (var0 == 1 && var1 == 0 && var3 == 7) {
         throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
      } else {
         if ((var2 & 2) == 2) {
            switch(var3) {
            case 2:
            case 5:
            case 6:
               throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
            case 3:
            case 4:
               if (var0 == 1 && var1 == 1) {
                  throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
               }
            }
         }

      }
   }

   static ObjectKey extractObjectKey(byte[] var0, ORB var1) {
      try {
         if (var0 != null) {
            ObjectKey var2 = var1.getObjectKeyFactory().create(var0);
            if (var2 != null) {
               return var2;
            }
         }
      } catch (Exception var3) {
      }

      throw wrapper.invalidObjectKey();
   }

   static ObjectKey extractObjectKey(TargetAddress var0, ORB var1) {
      short var2 = var1.getORBData().getGIOPTargetAddressPreference();
      short var3 = var0.discriminator();
      switch(var2) {
      case 0:
         if (var3 != 0) {
            throw new AddressingDispositionException((short)0);
         }
         break;
      case 1:
         if (var3 != 1) {
            throw new AddressingDispositionException((short)1);
         }
         break;
      case 2:
         if (var3 != 2) {
            throw new AddressingDispositionException((short)2);
         }
      case 3:
         break;
      default:
         throw wrapper.orbTargetAddrPreferenceInExtractObjectkeyInvalid();
      }

      try {
         IIOPProfile var5;
         TaggedProfile var6;
         switch(var3) {
         case 0:
            byte[] var4 = var0.object_key();
            if (var4 != null) {
               ObjectKey var10 = var1.getObjectKeyFactory().create(var4);
               if (var10 != null) {
                  return var10;
               }
            }
            break;
         case 1:
            var5 = null;
            var6 = var0.profile();
            if (var6 != null) {
               var5 = IIOPFactories.makeIIOPProfile(var1, var6);
               ObjectKey var11 = var5.getObjectKey();
               if (var11 != null) {
                  return var11;
               }
            }
            break;
         case 2:
            IORAddressingInfo var7 = var0.ior();
            if (var7 != null) {
               var6 = var7.ior.profiles[var7.selected_profile_index];
               var5 = IIOPFactories.makeIIOPProfile(var1, var6);
               ObjectKey var8 = var5.getObjectKey();
               if (var8 != null) {
                  return var8;
               }
            }
         }
      } catch (Exception var9) {
      }

      throw wrapper.invalidObjectKey();
   }

   private static int readSize(byte var0, byte var1, byte var2, byte var3, boolean var4) {
      int var5;
      int var6;
      int var7;
      int var8;
      if (!var4) {
         var5 = var0 << 24 & -16777216;
         var6 = var1 << 16 & 16711680;
         var7 = var2 << 8 & '\uff00';
         var8 = var3 << 0 & 255;
      } else {
         var5 = var3 << 24 & -16777216;
         var6 = var2 << 16 & 16711680;
         var7 = var1 << 8 & '\uff00';
         var8 = var0 << 0 & 255;
      }

      return var5 | var6 | var7 | var8;
   }

   static void nullCheck(Object var0) {
      if (var0 == null) {
         throw wrapper.nullNotAllowed();
      }
   }

   static SystemException getSystemException(String var0, int var1, CompletionStatus var2, String var3, ORBUtilSystemException var4) {
      SystemException var5 = null;

      try {
         Class var6 = SharedSecrets.getJavaCorbaAccess().loadClass(var0);
         if (var3 == null) {
            var5 = (SystemException)var6.newInstance();
         } else {
            Class[] var7 = new Class[]{String.class};
            Constructor var8 = var6.getConstructor(var7);
            Object[] var9 = new Object[]{var3};
            var5 = (SystemException)var8.newInstance(var9);
         }
      } catch (Exception var10) {
         throw var4.badSystemExceptionInReply(CompletionStatus.COMPLETED_MAYBE, var10);
      }

      var5.minor = var1;
      var5.completed = var2;
      return var5;
   }

   public void callback(MessageHandler var1) throws IOException {
      var1.handleInput((Message)this);
   }

   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }

   public void setByteBuffer(ByteBuffer var1) {
      this.byteBuffer = var1;
   }

   public int getThreadPoolToUse() {
      return this.threadPoolToUse;
   }

   public byte getEncodingVersion() {
      return this.encodingVersion;
   }

   public void setEncodingVersion(byte var1) {
      this.encodingVersion = var1;
   }

   private static void dprint(String var0) {
      ORBUtility.dprint("MessageBase", var0);
   }
}
