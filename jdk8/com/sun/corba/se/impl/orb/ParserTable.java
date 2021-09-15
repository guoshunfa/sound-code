package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.impl.transport.DefaultIORToSocketInfoImpl;
import com.sun.corba.se.impl.transport.DefaultSocketFactoryImpl;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.OperationFactory;
import com.sun.corba.se.spi.orb.ParserData;
import com.sun.corba.se.spi.orb.ParserDataFactory;
import com.sun.corba.se.spi.orb.StringPair;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.spi.transport.TransportDefault;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitializer;
import sun.corba.SharedSecrets;

public class ParserTable {
   private static String MY_CLASS_NAME = ParserTable.class.getName();
   private static ParserTable myInstance = new ParserTable();
   private ORBUtilSystemException wrapper = ORBUtilSystemException.get("orb.lifecycle");
   private ParserData[] parserData;

   public static ParserTable get() {
      return myInstance;
   }

   public ParserData[] getParserData() {
      ParserData[] var1 = new ParserData[this.parserData.length];
      System.arraycopy(this.parserData, 0, var1, 0, this.parserData.length);
      return var1;
   }

   private ParserTable() {
      String var1 = "65537,65801,65568";
      String[] var2 = new String[]{"subcontract", "poa", "transport"};
      USLPort[] var3 = new USLPort[]{new USLPort("FOO", 2701), new USLPort("BAR", 3333)};
      ReadTimeouts var4 = TransportDefault.makeReadTimeoutsFactory().create(100, 3000, 300, 20);
      ORBInitializer[] var5 = new ORBInitializer[]{null, new ParserTable.TestORBInitializer1(), new ParserTable.TestORBInitializer2()};
      StringPair[] var6 = new StringPair[]{new StringPair("foo.bar.blech.NonExistent", "dummy"), new StringPair(MY_CLASS_NAME + "$TestORBInitializer1", "dummy"), new StringPair(MY_CLASS_NAME + "$TestORBInitializer2", "dummy")};
      Acceptor[] var7 = new Acceptor[]{new ParserTable.TestAcceptor2(), new ParserTable.TestAcceptor1(), null};
      StringPair[] var8 = new StringPair[]{new StringPair("foo.bar.blech.NonExistent", "dummy"), new StringPair(MY_CLASS_NAME + "$TestAcceptor1", "dummy"), new StringPair(MY_CLASS_NAME + "$TestAcceptor2", "dummy")};
      StringPair[] var9 = new StringPair[]{new StringPair("Foo", "ior:930492049394"), new StringPair("Bar", "ior:3453465785633576")};
      Object var10 = null;
      String var11 = "corbaloc::camelot/NameService";

      try {
         new URL(var11);
      } catch (Exception var13) {
      }

      ParserData[] var12 = new ParserData[]{ParserDataFactory.make("com.sun.CORBA.ORBDebug", OperationFactory.listAction(",", OperationFactory.stringAction()), "debugFlags", new String[0], var2, "subcontract,poa,transport"), ParserDataFactory.make("org.omg.CORBA.ORBInitialHost", OperationFactory.stringAction(), "ORBInitialHost", "", "Foo", "Foo"), ParserDataFactory.make("org.omg.CORBA.ORBInitialPort", OperationFactory.integerAction(), "ORBInitialPort", new Integer(900), new Integer(27314), "27314"), ParserDataFactory.make("com.sun.CORBA.ORBServerHost", OperationFactory.stringAction(), "ORBServerHost", "", "camelot", "camelot"), ParserDataFactory.make("com.sun.CORBA.ORBServerPort", OperationFactory.integerAction(), "ORBServerPort", new Integer(0), new Integer(38143), "38143"), ParserDataFactory.make("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", OperationFactory.stringAction(), "listenOnAllInterfaces", "com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", "foo", "foo"), ParserDataFactory.make("org.omg.CORBA.ORBId", OperationFactory.stringAction(), "orbId", "", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.ORBid", OperationFactory.stringAction(), "orbId", "", "foo", "foo"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.integerAction(), "persistentServerId", new Integer(-1), new Integer(1234), "1234"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.setFlagAction(), "persistentServerIdInitialized", Boolean.FALSE, Boolean.TRUE, "1234"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.setFlagAction(), "orbServerIdPropertySpecified", Boolean.FALSE, Boolean.TRUE, "1234"), ParserDataFactory.make("com.sun.CORBA.connection.ORBHighWaterMark", OperationFactory.integerAction(), "highWaterMark", new Integer(240), new Integer(3745), "3745"), ParserDataFactory.make("com.sun.CORBA.connection.ORBLowWaterMark", OperationFactory.integerAction(), "lowWaterMark", new Integer(100), new Integer(12), "12"), ParserDataFactory.make("com.sun.CORBA.connection.ORBNumberToReclaim", OperationFactory.integerAction(), "numberToReclaim", new Integer(5), new Integer(231), "231"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOPVersion", this.makeGVOperation(), "giopVersion", GIOPVersion.DEFAULT_VERSION, new GIOPVersion(2, 3), "2.3"), ParserDataFactory.make("com.sun.CORBA.giop.ORBFragmentSize", this.makeFSOperation(), "giopFragmentSize", new Integer(1024), new Integer(65536), "65536"), ParserDataFactory.make("com.sun.CORBA.giop.ORBBufferSize", OperationFactory.integerAction(), "giopBufferSize", new Integer(1024), new Integer(234000), "234000"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOP11BuffMgr", this.makeBMGROperation(), "giop11BuffMgr", new Integer(0), new Integer(1), "CLCT"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOP12BuffMgr", this.makeBMGROperation(), "giop12BuffMgr", new Integer(2), new Integer(0), "GROW"), ParserDataFactory.make("com.sun.CORBA.giop.ORBTargetAddressing", OperationFactory.compose(OperationFactory.integerRangeAction(0, 3), OperationFactory.convertIntegerToShort()), "giopTargetAddressPreference", new Short((short)3), new Short((short)2), "2"), ParserDataFactory.make("com.sun.CORBA.giop.ORBTargetAddressing", this.makeADOperation(), "giopAddressDisposition", new Short((short)0), new Short((short)2), "2"), ParserDataFactory.make("com.sun.CORBA.codeset.AlwaysSendCodeSetCtx", OperationFactory.booleanAction(), "alwaysSendCodeSetCtx", Boolean.TRUE, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.UseByteOrderMarkers", OperationFactory.booleanAction(), "useByteOrderMarkers", true, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.UseByteOrderMarkersInEncaps", OperationFactory.booleanAction(), "useByteOrderMarkersInEncaps", false, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.charsets", this.makeCSOperation(), "charData", CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.getCharComponent(), CodeSetComponentInfo.createFromString(var1), var1), ParserDataFactory.make("com.sun.CORBA.codeset.wcharsets", this.makeCSOperation(), "wcharData", CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.getWCharComponent(), CodeSetComponentInfo.createFromString(var1), var1), ParserDataFactory.make("com.sun.CORBA.ORBAllowLocalOptimization", OperationFactory.booleanAction(), "allowLocalOptimization", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.legacy.connection.ORBSocketFactoryClass", this.makeLegacySocketFactoryOperation(), "legacySocketFactory", (Object)null, new ParserTable.TestLegacyORBSocketFactory(), MY_CLASS_NAME + "$TestLegacyORBSocketFactory"), ParserDataFactory.make("com.sun.CORBA.transport.ORBSocketFactoryClass", this.makeSocketFactoryOperation(), "socketFactory", new DefaultSocketFactoryImpl(), new ParserTable.TestORBSocketFactory(), MY_CLASS_NAME + "$TestORBSocketFactory"), ParserDataFactory.make("com.sun.CORBA.transport.ORBListenSocket", this.makeUSLOperation(), "userSpecifiedListenPorts", new USLPort[0], var3, "FOO:2701,BAR:3333"), ParserDataFactory.make("com.sun.CORBA.transport.ORBIORToSocketInfoClass", this.makeIORToSocketInfoOperation(), "iorToSocketInfo", new DefaultIORToSocketInfoImpl(), new ParserTable.TestIORToSocketInfo(), MY_CLASS_NAME + "$TestIORToSocketInfo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBIIOPPrimaryToContactInfoClass", this.makeIIOPPrimaryToContactInfoOperation(), "iiopPrimaryToContactInfo", (Object)null, new ParserTable.TestIIOPPrimaryToContactInfo(), MY_CLASS_NAME + "$TestIIOPPrimaryToContactInfo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBContactInfoList", this.makeContactInfoListFactoryOperation(), "corbaContactInfoListFactory", (Object)null, new ParserTable.TestContactInfoListFactory(), MY_CLASS_NAME + "$TestContactInfoListFactory"), ParserDataFactory.make("com.sun.CORBA.POA.ORBPersistentServerPort", OperationFactory.integerAction(), "persistentServerPort", new Integer(0), new Integer(2743), "2743"), ParserDataFactory.make("com.sun.CORBA.POA.ORBPersistentServerPort", OperationFactory.setFlagAction(), "persistentPortInitialized", Boolean.FALSE, Boolean.TRUE, "2743"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.integerAction(), "persistentServerId", new Integer(0), new Integer(294), "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.setFlagAction(), "persistentServerIdInitialized", Boolean.FALSE, Boolean.TRUE, "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.setFlagAction(), "orbServerIdPropertySpecified", Boolean.FALSE, Boolean.TRUE, "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBActivated", OperationFactory.booleanAction(), "serverIsORBActivated", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.POA.ORBBadServerIdHandlerClass", OperationFactory.classAction(), "badServerIdHandlerClass", (Object)null, ParserTable.TestBadServerIdHandler.class, MY_CLASS_NAME + "$TestBadServerIdHandler"), ParserDataFactory.make("org.omg.PortableInterceptor.ORBInitializerClass.", this.makeROIOperation(), "orbInitializers", new ORBInitializer[0], var5, var6, ORBInitializer.class), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptor", this.makeAcceptorInstantiationOperation(), "acceptors", new Acceptor[0], var7, var8, Acceptor.class), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptorSocketType", OperationFactory.stringAction(), "acceptorSocketType", "SocketChannel", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBUseNIOSelectToWait", OperationFactory.booleanAction(), "acceptorSocketUseSelectThreadToWait", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptorSocketUseWorkerThreadForEvent", OperationFactory.booleanAction(), "acceptorSocketUseWorkerThreadForEvent", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBConnectionSocketType", OperationFactory.stringAction(), "connectionSocketType", "SocketChannel", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBUseNIOSelectToWait", OperationFactory.booleanAction(), "connectionSocketUseSelectThreadToWait", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBConnectionSocketUseWorkerThreadForEvent", OperationFactory.booleanAction(), "connectionSocketUseWorkerThreadForEvent", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBDisableDirectByteBufferUse", OperationFactory.booleanAction(), "disableDirectByteBufferUse", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBTCPReadTimeouts", this.makeTTCPRTOperation(), "readTimeouts", TransportDefault.makeReadTimeoutsFactory().create(100, 3000, 300, 20), var4, "100:3000:300:20"), ParserDataFactory.make("com.sun.CORBA.encoding.ORBEnableJavaSerialization", OperationFactory.booleanAction(), "enableJavaSerialization", Boolean.FALSE, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.ORBUseRepId", OperationFactory.booleanAction(), "useRepId", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("org.omg.CORBA.ORBInitRef", OperationFactory.identityAction(), "orbInitialReferences", new StringPair[0], var9, var9, StringPair.class)};
      this.parserData = var12;
   }

   private Operation makeTTCPRTOperation() {
      Operation[] var1 = new Operation[]{OperationFactory.integerAction(), OperationFactory.integerAction(), OperationFactory.integerAction(), OperationFactory.integerAction()};
      Operation var2 = OperationFactory.sequenceAction(":", var1);
      Operation var3 = new Operation() {
         public Object operate(Object var1) {
            Object[] var2 = (Object[])((Object[])var1);
            Integer var3 = (Integer)((Integer)var2[0]);
            Integer var4 = (Integer)((Integer)var2[1]);
            Integer var5 = (Integer)((Integer)var2[2]);
            Integer var6 = (Integer)((Integer)var2[3]);
            return TransportDefault.makeReadTimeoutsFactory().create(var3, var4, var5, var6);
         }
      };
      Operation var4 = OperationFactory.compose(var2, var3);
      return var4;
   }

   private Operation makeUSLOperation() {
      Operation[] var1 = new Operation[]{OperationFactory.stringAction(), OperationFactory.integerAction()};
      Operation var2 = OperationFactory.sequenceAction(":", var1);
      Operation var3 = new Operation() {
         public Object operate(Object var1) {
            Object[] var2 = (Object[])((Object[])var1);
            String var3 = (String)((String)var2[0]);
            Integer var4 = (Integer)((Integer)var2[1]);
            return new USLPort(var3, var4);
         }
      };
      Operation var4 = OperationFactory.compose(var2, var3);
      Operation var5 = OperationFactory.listAction(",", var4);
      return var5;
   }

   private Operation makeMapOperation(final Map var1) {
      return new Operation() {
         public Object operate(Object var1x) {
            return var1.get(var1x);
         }
      };
   }

   private Operation makeBMGROperation() {
      HashMap var1 = new HashMap();
      var1.put("GROW", new Integer(0));
      var1.put("CLCT", new Integer(1));
      var1.put("STRM", new Integer(2));
      return this.makeMapOperation(var1);
   }

   private Operation makeLegacySocketFactoryOperation() {
      Operation var1 = new Operation() {
         public Object operate(Object var1) {
            String var2 = (String)var1;

            try {
               Class var3 = SharedSecrets.getJavaCorbaAccess().loadClass(var2);
               if (ORBSocketFactory.class.isAssignableFrom(var3)) {
                  return var3.newInstance();
               } else {
                  throw ParserTable.this.wrapper.illegalSocketFactoryType(var3.toString());
               }
            } catch (Exception var4) {
               throw ParserTable.this.wrapper.badCustomSocketFactory((Throwable)var4, var2);
            }
         }
      };
      return var1;
   }

   private Operation makeSocketFactoryOperation() {
      Operation var1 = new Operation() {
         public Object operate(Object var1) {
            String var2 = (String)var1;

            try {
               Class var3 = SharedSecrets.getJavaCorbaAccess().loadClass(var2);
               if (com.sun.corba.se.spi.transport.ORBSocketFactory.class.isAssignableFrom(var3)) {
                  return var3.newInstance();
               } else {
                  throw ParserTable.this.wrapper.illegalSocketFactoryType(var3.toString());
               }
            } catch (Exception var4) {
               throw ParserTable.this.wrapper.badCustomSocketFactory((Throwable)var4, var2);
            }
         }
      };
      return var1;
   }

   private Operation makeIORToSocketInfoOperation() {
      Operation var1 = new Operation() {
         public Object operate(Object var1) {
            String var2 = (String)var1;

            try {
               Class var3 = SharedSecrets.getJavaCorbaAccess().loadClass(var2);
               if (IORToSocketInfo.class.isAssignableFrom(var3)) {
                  return var3.newInstance();
               } else {
                  throw ParserTable.this.wrapper.illegalIorToSocketInfoType(var3.toString());
               }
            } catch (Exception var4) {
               throw ParserTable.this.wrapper.badCustomIorToSocketInfo((Throwable)var4, var2);
            }
         }
      };
      return var1;
   }

   private Operation makeIIOPPrimaryToContactInfoOperation() {
      Operation var1 = new Operation() {
         public Object operate(Object var1) {
            String var2 = (String)var1;

            try {
               Class var3 = SharedSecrets.getJavaCorbaAccess().loadClass(var2);
               if (IIOPPrimaryToContactInfo.class.isAssignableFrom(var3)) {
                  return var3.newInstance();
               } else {
                  throw ParserTable.this.wrapper.illegalIiopPrimaryToContactInfoType(var3.toString());
               }
            } catch (Exception var4) {
               throw ParserTable.this.wrapper.badCustomIiopPrimaryToContactInfo((Throwable)var4, var2);
            }
         }
      };
      return var1;
   }

   private Operation makeContactInfoListFactoryOperation() {
      Operation var1 = new Operation() {
         public Object operate(Object var1) {
            String var2 = (String)var1;

            try {
               Class var3 = SharedSecrets.getJavaCorbaAccess().loadClass(var2);
               if (CorbaContactInfoListFactory.class.isAssignableFrom(var3)) {
                  return var3.newInstance();
               } else {
                  throw ParserTable.this.wrapper.illegalContactInfoListFactoryType(var3.toString());
               }
            } catch (Exception var4) {
               throw ParserTable.this.wrapper.badContactInfoListFactory((Throwable)var4, var2);
            }
         }
      };
      return var1;
   }

   private Operation makeCSOperation() {
      Operation var1 = new Operation() {
         public Object operate(Object var1) {
            String var2 = (String)var1;
            return CodeSetComponentInfo.createFromString(var2);
         }
      };
      return var1;
   }

   private Operation makeADOperation() {
      Operation var1 = new Operation() {
         private Integer[] map = new Integer[]{new Integer(0), new Integer(1), new Integer(2), new Integer(0)};

         public Object operate(Object var1) {
            int var2 = (Integer)var1;
            return this.map[var2];
         }
      };
      Operation var2 = OperationFactory.integerRangeAction(0, 3);
      Operation var3 = OperationFactory.compose(var2, var1);
      Operation var4 = OperationFactory.compose(var3, OperationFactory.convertIntegerToShort());
      return var4;
   }

   private Operation makeFSOperation() {
      Operation var1 = new Operation() {
         public Object operate(Object var1) {
            int var2 = (Integer)var1;
            if (var2 < 32) {
               throw ParserTable.this.wrapper.fragmentSizeMinimum(new Integer(var2), new Integer(32));
            } else if (var2 % 8 != 0) {
               throw ParserTable.this.wrapper.fragmentSizeDiv(new Integer(var2), new Integer(8));
            } else {
               return var1;
            }
         }
      };
      Operation var2 = OperationFactory.compose(OperationFactory.integerAction(), var1);
      return var2;
   }

   private Operation makeGVOperation() {
      Operation var1 = OperationFactory.listAction(".", OperationFactory.integerAction());
      Operation var2 = new Operation() {
         public Object operate(Object var1) {
            Object[] var2 = (Object[])((Object[])var1);
            int var3 = (Integer)((Integer)var2[0]);
            int var4 = (Integer)((Integer)var2[1]);
            return new GIOPVersion(var3, var4);
         }
      };
      Operation var3 = OperationFactory.compose(var1, var2);
      return var3;
   }

   private Operation makeROIOperation() {
      Operation var1 = OperationFactory.classAction();
      Operation var2 = OperationFactory.suffixAction();
      Operation var3 = OperationFactory.compose(var2, var1);
      Operation var4 = OperationFactory.maskErrorAction(var3);
      Operation var5 = new Operation() {
         public Object operate(Object var1) {
            final Class var2 = (Class)var1;
            if (var2 == null) {
               return null;
            } else if (ORBInitializer.class.isAssignableFrom(var2)) {
               ORBInitializer var3 = null;

               try {
                  var3 = (ORBInitializer)AccessController.doPrivileged(new PrivilegedExceptionAction() {
                     public Object run() throws InstantiationException, IllegalAccessException {
                        return var2.newInstance();
                     }
                  });
                  return var3;
               } catch (PrivilegedActionException var5) {
                  throw ParserTable.this.wrapper.orbInitializerFailure((Throwable)var5.getException(), var2.getName());
               } catch (Exception var6) {
                  throw ParserTable.this.wrapper.orbInitializerFailure((Throwable)var6, var2.getName());
               }
            } else {
               throw ParserTable.this.wrapper.orbInitializerType(var2.getName());
            }
         }
      };
      Operation var6 = OperationFactory.compose(var4, var5);
      return var6;
   }

   private Operation makeAcceptorInstantiationOperation() {
      Operation var1 = OperationFactory.classAction();
      Operation var2 = OperationFactory.suffixAction();
      Operation var3 = OperationFactory.compose(var2, var1);
      Operation var4 = OperationFactory.maskErrorAction(var3);
      Operation var5 = new Operation() {
         public Object operate(Object var1) {
            final Class var2 = (Class)var1;
            if (var2 == null) {
               return null;
            } else if (Acceptor.class.isAssignableFrom(var2)) {
               Acceptor var3 = null;

               try {
                  var3 = (Acceptor)AccessController.doPrivileged(new PrivilegedExceptionAction() {
                     public Object run() throws InstantiationException, IllegalAccessException {
                        return var2.newInstance();
                     }
                  });
                  return var3;
               } catch (PrivilegedActionException var5) {
                  throw ParserTable.this.wrapper.acceptorInstantiationFailure((Throwable)var5.getException(), var2.getName());
               } catch (Exception var6) {
                  throw ParserTable.this.wrapper.acceptorInstantiationFailure((Throwable)var6, var2.getName());
               }
            } else {
               throw ParserTable.this.wrapper.acceptorInstantiationTypeFailure(var2.getName());
            }
         }
      };
      Operation var6 = OperationFactory.compose(var4, var5);
      return var6;
   }

   private Operation makeInitRefOperation() {
      return new Operation() {
         public Object operate(Object var1) {
            String[] var2 = (String[])((String[])var1);
            if (var2.length != 2) {
               throw ParserTable.this.wrapper.orbInitialreferenceSyntax();
            } else {
               return var2[0] + "=" + var2[1];
            }
         }
      };
   }

   public static final class TestAcceptor2 implements Acceptor {
      public boolean equals(Object var1) {
         return var1 instanceof ParserTable.TestAcceptor2;
      }

      public int hashCode() {
         return 1;
      }

      public boolean initialize() {
         return true;
      }

      public boolean initialized() {
         return true;
      }

      public String getConnectionCacheType() {
         return "FOO";
      }

      public void setConnectionCache(InboundConnectionCache var1) {
      }

      public InboundConnectionCache getConnectionCache() {
         return null;
      }

      public boolean shouldRegisterAcceptEvent() {
         return true;
      }

      public void setUseSelectThreadForConnections(boolean var1) {
      }

      public boolean shouldUseSelectThreadForConnections() {
         return true;
      }

      public void setUseWorkerThreadForConnections(boolean var1) {
      }

      public boolean shouldUseWorkerThreadForConnections() {
         return true;
      }

      public void accept() {
      }

      public void close() {
      }

      public EventHandler getEventHandler() {
         return null;
      }

      public MessageMediator createMessageMediator(Broker var1, Connection var2) {
         return null;
      }

      public MessageMediator finishCreatingMessageMediator(Broker var1, Connection var2, MessageMediator var3) {
         return null;
      }

      public InputObject createInputObject(Broker var1, MessageMediator var2) {
         return null;
      }

      public OutputObject createOutputObject(Broker var1, MessageMediator var2) {
         return null;
      }
   }

   public static final class TestAcceptor1 implements Acceptor {
      public boolean equals(Object var1) {
         return var1 instanceof ParserTable.TestAcceptor1;
      }

      public int hashCode() {
         return 1;
      }

      public boolean initialize() {
         return true;
      }

      public boolean initialized() {
         return true;
      }

      public String getConnectionCacheType() {
         return "FOO";
      }

      public void setConnectionCache(InboundConnectionCache var1) {
      }

      public InboundConnectionCache getConnectionCache() {
         return null;
      }

      public boolean shouldRegisterAcceptEvent() {
         return true;
      }

      public void setUseSelectThreadForConnections(boolean var1) {
      }

      public boolean shouldUseSelectThreadForConnections() {
         return true;
      }

      public void setUseWorkerThreadForConnections(boolean var1) {
      }

      public boolean shouldUseWorkerThreadForConnections() {
         return true;
      }

      public void accept() {
      }

      public void close() {
      }

      public EventHandler getEventHandler() {
         return null;
      }

      public MessageMediator createMessageMediator(Broker var1, Connection var2) {
         return null;
      }

      public MessageMediator finishCreatingMessageMediator(Broker var1, Connection var2, MessageMediator var3) {
         return null;
      }

      public InputObject createInputObject(Broker var1, MessageMediator var2) {
         return null;
      }

      public OutputObject createOutputObject(Broker var1, MessageMediator var2) {
         return null;
      }
   }

   public static final class TestORBInitializer2 extends LocalObject implements ORBInitializer {
      public boolean equals(Object var1) {
         return var1 instanceof ParserTable.TestORBInitializer2;
      }

      public int hashCode() {
         return 1;
      }

      public void pre_init(ORBInitInfo var1) {
      }

      public void post_init(ORBInitInfo var1) {
      }
   }

   public static final class TestORBInitializer1 extends LocalObject implements ORBInitializer {
      public boolean equals(Object var1) {
         return var1 instanceof ParserTable.TestORBInitializer1;
      }

      public int hashCode() {
         return 1;
      }

      public void pre_init(ORBInitInfo var1) {
      }

      public void post_init(ORBInitInfo var1) {
      }
   }

   public static final class TestContactInfoListFactory implements CorbaContactInfoListFactory {
      public boolean equals(Object var1) {
         return var1 instanceof ParserTable.TestContactInfoListFactory;
      }

      public int hashCode() {
         return 1;
      }

      public void setORB(ORB var1) {
      }

      public CorbaContactInfoList create(IOR var1) {
         return null;
      }
   }

   public static final class TestIIOPPrimaryToContactInfo implements IIOPPrimaryToContactInfo {
      public void reset(ContactInfo var1) {
      }

      public boolean hasNext(ContactInfo var1, ContactInfo var2, List var3) {
         return true;
      }

      public ContactInfo next(ContactInfo var1, ContactInfo var2, List var3) {
         return null;
      }
   }

   public static final class TestIORToSocketInfo implements IORToSocketInfo {
      public boolean equals(Object var1) {
         return var1 instanceof ParserTable.TestIORToSocketInfo;
      }

      public int hashCode() {
         return 1;
      }

      public List getSocketInfo(IOR var1) {
         return null;
      }
   }

   public static final class TestORBSocketFactory implements com.sun.corba.se.spi.transport.ORBSocketFactory {
      public boolean equals(Object var1) {
         return var1 instanceof ParserTable.TestORBSocketFactory;
      }

      public int hashCode() {
         return 1;
      }

      public void setORB(ORB var1) {
      }

      public ServerSocket createServerSocket(String var1, InetSocketAddress var2) {
         return null;
      }

      public Socket createSocket(String var1, InetSocketAddress var2) {
         return null;
      }

      public void setAcceptedSocketOptions(Acceptor var1, ServerSocket var2, Socket var3) {
      }
   }

   public static final class TestLegacyORBSocketFactory implements ORBSocketFactory {
      public boolean equals(Object var1) {
         return var1 instanceof ParserTable.TestLegacyORBSocketFactory;
      }

      public int hashCode() {
         return 1;
      }

      public ServerSocket createServerSocket(String var1, int var2) {
         return null;
      }

      public SocketInfo getEndPointInfo(org.omg.CORBA.ORB var1, IOR var2, SocketInfo var3) {
         return null;
      }

      public Socket createSocket(SocketInfo var1) {
         return null;
      }
   }

   public final class TestBadServerIdHandler implements BadServerIdHandler {
      public boolean equals(Object var1) {
         return var1 instanceof ParserTable.TestBadServerIdHandler;
      }

      public int hashCode() {
         return 1;
      }

      public void handle(ObjectKey var1) {
      }
   }
}
