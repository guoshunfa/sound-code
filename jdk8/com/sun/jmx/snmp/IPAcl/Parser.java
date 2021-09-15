package com.sun.jmx.snmp.IPAcl;

import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Vector;

class Parser implements ParserTreeConstants, ParserConstants {
   protected JJTParserState jjtree = new JJTParserState();
   public ParserTokenManager token_source;
   ASCII_CharStream jj_input_stream;
   public Token token;
   public Token jj_nt;
   private int jj_ntk;
   private Token jj_scanpos;
   private Token jj_lastpos;
   private int jj_la;
   public boolean lookingAhead = false;
   private boolean jj_semLA;
   private int jj_gen;
   private final int[] jj_la1 = new int[22];
   private final int[] jj_la1_0 = new int[]{256, 524288, 1048576, 8192, 0, 393216, 0, Integer.MIN_VALUE, 285212672, 0, 0, 0, 0, 8192, 8192, 0, -1862270976, 0, 32768, 8192, 0, -1862270976};
   private final int[] jj_la1_1 = new int[]{0, 0, 0, 0, 16, 0, 16, 0, 0, 32, 32, 64, 32, 0, 0, 16, 0, 16, 0, 0, 16, 0};
   private final Parser.JJCalls[] jj_2_rtns = new Parser.JJCalls[3];
   private boolean jj_rescan = false;
   private int jj_gc = 0;
   private Vector<int[]> jj_expentries = new Vector();
   private int[] jj_expentry;
   private int jj_kind = -1;
   private int[] jj_lasttokens = new int[100];
   private int jj_endpos;

   public final JDMSecurityDefs SecurityDefs() throws ParseException {
      JDMSecurityDefs var1 = new JDMSecurityDefs(0);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      JDMSecurityDefs var3;
      try {
         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 8:
            this.AclBlock();
            break;
         default:
            this.jj_la1[0] = this.jj_gen;
         }

         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 19:
            this.TrapBlock();
            break;
         default:
            this.jj_la1[1] = this.jj_gen;
         }

         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 20:
            this.InformBlock();
            break;
         default:
            this.jj_la1[2] = this.jj_gen;
         }

         this.jj_consume_token(0);
         this.jjtree.closeNodeScope(var1, true);
         var2 = false;
         var3 = var1;
      } catch (Throwable var7) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var7 instanceof RuntimeException) {
            throw (RuntimeException)var7;
         }

         if (var7 instanceof ParseException) {
            throw (ParseException)var7;
         }

         throw (Error)var7;
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

      return var3;
   }

   public final void AclBlock() throws ParseException {
      JDMAclBlock var1 = new JDMAclBlock(1);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         this.jj_consume_token(8);
         this.jj_consume_token(9);
         this.jj_consume_token(13);

         while(true) {
            this.AclItem();
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 13:
               break;
            default:
               this.jj_la1[3] = this.jj_gen;
               this.jj_consume_token(16);
               return;
            }
         }
      } catch (Throwable var7) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var7 instanceof RuntimeException) {
            throw (RuntimeException)var7;
         } else if (var7 instanceof ParseException) {
            throw (ParseException)var7;
         } else {
            throw (Error)var7;
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final void AclItem() throws ParseException {
      JDMAclItem var1 = new JDMAclItem(2);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         this.jj_consume_token(13);
         var1.com = this.Communities();
         var1.access = this.Access();
         this.Managers();
         this.jj_consume_token(16);
      } catch (Throwable var7) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var7 instanceof RuntimeException) {
            throw (RuntimeException)var7;
         }

         if (var7 instanceof ParseException) {
            throw (ParseException)var7;
         }

         throw (Error)var7;
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

   }

   public final JDMCommunities Communities() throws ParseException {
      JDMCommunities var1 = new JDMCommunities(3);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         this.jj_consume_token(10);
         this.jj_consume_token(9);
         this.Community();

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 36:
               this.jj_consume_token(36);
               this.Community();
               break;
            default:
               this.jj_la1[4] = this.jj_gen;
               this.jjtree.closeNodeScope(var1, true);
               var2 = false;
               JDMCommunities var3 = var1;
               return var3;
            }
         }
      } catch (Throwable var7) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var7 instanceof RuntimeException) {
            throw (RuntimeException)var7;
         } else if (var7 instanceof ParseException) {
            throw (ParseException)var7;
         } else {
            throw (Error)var7;
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final void Community() throws ParseException {
      JDMCommunity var1 = new JDMCommunity(4);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         Token var3 = this.jj_consume_token(31);
         this.jjtree.closeNodeScope(var1, true);
         var2 = false;
         var1.communityString = var3.image;
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

   }

   public final JDMAccess Access() throws ParseException {
      JDMAccess var1 = new JDMAccess(5);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      JDMAccess var3;
      try {
         this.jj_consume_token(7);
         this.jj_consume_token(9);
         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 17:
            this.jj_consume_token(17);
            var1.access = 17;
            break;
         case 18:
            this.jj_consume_token(18);
            var1.access = 18;
            break;
         default:
            this.jj_la1[5] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         }

         this.jjtree.closeNodeScope(var1, true);
         var2 = false;
         var3 = var1;
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

      return var3;
   }

   public final void Managers() throws ParseException {
      JDMManagers var1 = new JDMManagers(6);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         this.jj_consume_token(14);
         this.jj_consume_token(9);
         this.Host();

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 36:
               this.jj_consume_token(36);
               this.Host();
               break;
            default:
               this.jj_la1[6] = this.jj_gen;
               return;
            }
         }
      } catch (Throwable var7) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var7 instanceof RuntimeException) {
            throw (RuntimeException)var7;
         } else if (var7 instanceof ParseException) {
            throw (ParseException)var7;
         } else {
            throw (Error)var7;
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final void Host() throws ParseException {
      JDMHost var1 = new JDMHost(7);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 31:
            this.HostName();
            break;
         default:
            this.jj_la1[7] = this.jj_gen;
            if (this.jj_2_1(Integer.MAX_VALUE)) {
               this.NetMask();
            } else if (this.jj_2_2(Integer.MAX_VALUE)) {
               this.NetMaskV6();
            } else if (this.jj_2_3(Integer.MAX_VALUE)) {
               this.IpAddress();
            } else {
               switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
               case 24:
                  this.IpMask();
                  break;
               case 28:
                  this.IpV6Address();
                  break;
               default:
                  this.jj_la1[8] = this.jj_gen;
                  this.jj_consume_token(-1);
                  throw new ParseException();
               }
            }
         }
      } catch (Throwable var8) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var8 instanceof RuntimeException) {
            throw (RuntimeException)var8;
         }

         if (var8 instanceof ParseException) {
            throw (ParseException)var8;
         }

         throw (Error)var8;
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

   }

   public final void HostName() throws ParseException {
      JDMHostName var1 = new JDMHostName(8);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         Token var3 = this.jj_consume_token(31);
         var1.name.append(var3.image);

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 37:
               this.jj_consume_token(37);
               var3 = this.jj_consume_token(31);
               var1.name.append("." + var3.image);
               break;
            default:
               this.jj_la1[9] = this.jj_gen;
               return;
            }
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final void IpAddress() throws ParseException {
      JDMIpAddress var1 = new JDMIpAddress(9);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         Token var3 = this.jj_consume_token(24);
         var1.address.append(var3.image);

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 37:
               this.jj_consume_token(37);
               var3 = this.jj_consume_token(24);
               var1.address.append("." + var3.image);
               break;
            default:
               this.jj_la1[10] = this.jj_gen;
               return;
            }
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final void IpV6Address() throws ParseException {
      JDMIpV6Address var1 = new JDMIpV6Address(10);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         Token var3 = this.jj_consume_token(28);
         this.jjtree.closeNodeScope(var1, true);
         var2 = false;
         var1.address.append(var3.image);
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

   }

   public final void IpMask() throws ParseException {
      JDMIpMask var1 = new JDMIpMask(11);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         Token var3 = this.jj_consume_token(24);
         var1.address.append(var3.image);

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 38:
               this.jj_consume_token(38);
               var3 = this.jj_consume_token(24);
               var1.address.append("." + var3.image);
               break;
            default:
               this.jj_la1[11] = this.jj_gen;
               return;
            }
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final void NetMask() throws ParseException {
      JDMNetMask var1 = new JDMNetMask(12);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         Token var3 = this.jj_consume_token(24);
         var1.address.append(var3.image);

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 37:
               this.jj_consume_token(37);
               var3 = this.jj_consume_token(24);
               var1.address.append("." + var3.image);
               break;
            default:
               this.jj_la1[12] = this.jj_gen;
               this.jj_consume_token(39);
               var3 = this.jj_consume_token(24);
               this.jjtree.closeNodeScope(var1, true);
               var2 = false;
               var1.mask = var3.image;
               return;
            }
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final void NetMaskV6() throws ParseException {
      JDMNetMaskV6 var1 = new JDMNetMaskV6(13);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         Token var3 = this.jj_consume_token(28);
         var1.address.append(var3.image);
         this.jj_consume_token(39);
         var3 = this.jj_consume_token(24);
         this.jjtree.closeNodeScope(var1, true);
         var2 = false;
         var1.mask = var3.image;
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

   }

   public final void TrapBlock() throws ParseException {
      JDMTrapBlock var1 = new JDMTrapBlock(14);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         this.jj_consume_token(19);
         this.jj_consume_token(9);
         this.jj_consume_token(13);

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 13:
               this.TrapItem();
               break;
            default:
               this.jj_la1[13] = this.jj_gen;
               this.jj_consume_token(16);
               return;
            }
         }
      } catch (Throwable var7) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var7 instanceof RuntimeException) {
            throw (RuntimeException)var7;
         } else if (var7 instanceof ParseException) {
            throw (ParseException)var7;
         } else {
            throw (Error)var7;
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final void TrapItem() throws ParseException {
      JDMTrapItem var1 = new JDMTrapItem(15);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         this.jj_consume_token(13);
         var1.comm = this.TrapCommunity();
         this.TrapInterestedHost();

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 13:
               this.Enterprise();
               break;
            default:
               this.jj_la1[14] = this.jj_gen;
               this.jj_consume_token(16);
               return;
            }
         }
      } catch (Throwable var7) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var7 instanceof RuntimeException) {
            throw (RuntimeException)var7;
         } else if (var7 instanceof ParseException) {
            throw (ParseException)var7;
         } else {
            throw (Error)var7;
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final JDMTrapCommunity TrapCommunity() throws ParseException {
      JDMTrapCommunity var1 = new JDMTrapCommunity(16);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      JDMTrapCommunity var4;
      try {
         this.jj_consume_token(21);
         this.jj_consume_token(9);
         Token var3 = this.jj_consume_token(31);
         this.jjtree.closeNodeScope(var1, true);
         var2 = false;
         var1.community = var3.image;
         var4 = var1;
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

      return var4;
   }

   public final void TrapInterestedHost() throws ParseException {
      JDMTrapInterestedHost var1 = new JDMTrapInterestedHost(17);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         this.jj_consume_token(12);
         this.jj_consume_token(9);
         this.HostTrap();

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 36:
               this.jj_consume_token(36);
               this.HostTrap();
               break;
            default:
               this.jj_la1[15] = this.jj_gen;
               return;
            }
         }
      } catch (Throwable var7) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var7 instanceof RuntimeException) {
            throw (RuntimeException)var7;
         } else if (var7 instanceof ParseException) {
            throw (ParseException)var7;
         } else {
            throw (Error)var7;
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final void HostTrap() throws ParseException {
      JDMHostTrap var1 = new JDMHostTrap(18);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 24:
            this.IpAddress();
            break;
         case 28:
            this.IpV6Address();
            break;
         case 31:
            this.HostName();
            break;
         default:
            this.jj_la1[16] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         }
      } catch (Throwable var8) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var8 instanceof RuntimeException) {
            throw (RuntimeException)var8;
         }

         if (var8 instanceof ParseException) {
            throw (ParseException)var8;
         }

         throw (Error)var8;
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

   }

   public final void Enterprise() throws ParseException {
      JDMEnterprise var1 = new JDMEnterprise(19);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         this.jj_consume_token(13);
         this.jj_consume_token(11);
         this.jj_consume_token(9);
         Token var3 = this.jj_consume_token(35);
         var1.enterprise = var3.image;
         this.jj_consume_token(23);
         this.jj_consume_token(9);
         this.TrapNum();

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 36:
               this.jj_consume_token(36);
               this.TrapNum();
               break;
            default:
               this.jj_la1[17] = this.jj_gen;
               this.jj_consume_token(16);
               return;
            }
         }
      } catch (Throwable var8) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var8 instanceof RuntimeException) {
            throw (RuntimeException)var8;
         } else if (var8 instanceof ParseException) {
            throw (ParseException)var8;
         } else {
            throw (Error)var8;
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final void TrapNum() throws ParseException {
      JDMTrapNum var1 = new JDMTrapNum(20);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         Token var3 = this.jj_consume_token(24);
         var1.low = Integer.parseInt(var3.image);
         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 15:
            this.jj_consume_token(15);
            var3 = this.jj_consume_token(24);
            var1.high = Integer.parseInt(var3.image);
            break;
         default:
            this.jj_la1[18] = this.jj_gen;
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

   }

   public final void InformBlock() throws ParseException {
      JDMInformBlock var1 = new JDMInformBlock(21);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         this.jj_consume_token(20);
         this.jj_consume_token(9);
         this.jj_consume_token(13);

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 13:
               this.InformItem();
               break;
            default:
               this.jj_la1[19] = this.jj_gen;
               this.jj_consume_token(16);
               return;
            }
         }
      } catch (Throwable var7) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var7 instanceof RuntimeException) {
            throw (RuntimeException)var7;
         } else if (var7 instanceof ParseException) {
            throw (ParseException)var7;
         } else {
            throw (Error)var7;
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final void InformItem() throws ParseException {
      JDMInformItem var1 = new JDMInformItem(22);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         this.jj_consume_token(13);
         var1.comm = this.InformCommunity();
         this.InformInterestedHost();
         this.jj_consume_token(16);
      } catch (Throwable var7) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var7 instanceof RuntimeException) {
            throw (RuntimeException)var7;
         }

         if (var7 instanceof ParseException) {
            throw (ParseException)var7;
         }

         throw (Error)var7;
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

   }

   public final JDMInformCommunity InformCommunity() throws ParseException {
      JDMInformCommunity var1 = new JDMInformCommunity(23);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      JDMInformCommunity var4;
      try {
         this.jj_consume_token(22);
         this.jj_consume_token(9);
         Token var3 = this.jj_consume_token(31);
         this.jjtree.closeNodeScope(var1, true);
         var2 = false;
         var1.community = var3.image;
         var4 = var1;
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

      return var4;
   }

   public final void InformInterestedHost() throws ParseException {
      JDMInformInterestedHost var1 = new JDMInformInterestedHost(24);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         this.jj_consume_token(12);
         this.jj_consume_token(9);
         this.HostInform();

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 36:
               this.jj_consume_token(36);
               this.HostInform();
               break;
            default:
               this.jj_la1[20] = this.jj_gen;
               return;
            }
         }
      } catch (Throwable var7) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var7 instanceof RuntimeException) {
            throw (RuntimeException)var7;
         } else if (var7 instanceof ParseException) {
            throw (ParseException)var7;
         } else {
            throw (Error)var7;
         }
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }
   }

   public final void HostInform() throws ParseException {
      JDMHostInform var1 = new JDMHostInform(25);
      boolean var2 = true;
      this.jjtree.openNodeScope(var1);

      try {
         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 24:
            this.IpAddress();
            break;
         case 28:
            this.IpV6Address();
            break;
         case 31:
            this.HostName();
            break;
         default:
            this.jj_la1[21] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         }
      } catch (Throwable var8) {
         if (var2) {
            this.jjtree.clearNodeScope(var1);
            var2 = false;
         } else {
            this.jjtree.popNode();
         }

         if (var8 instanceof RuntimeException) {
            throw (RuntimeException)var8;
         }

         if (var8 instanceof ParseException) {
            throw (ParseException)var8;
         }

         throw (Error)var8;
      } finally {
         if (var2) {
            this.jjtree.closeNodeScope(var1, true);
         }

      }

   }

   private final boolean jj_2_1(int var1) {
      this.jj_la = var1;
      this.jj_lastpos = this.jj_scanpos = this.token;
      boolean var2 = !this.jj_3_1();
      this.jj_save(0, var1);
      return var2;
   }

   private final boolean jj_2_2(int var1) {
      this.jj_la = var1;
      this.jj_lastpos = this.jj_scanpos = this.token;
      boolean var2 = !this.jj_3_2();
      this.jj_save(1, var1);
      return var2;
   }

   private final boolean jj_2_3(int var1) {
      this.jj_la = var1;
      this.jj_lastpos = this.jj_scanpos = this.token;
      boolean var2 = !this.jj_3_3();
      this.jj_save(2, var1);
      return var2;
   }

   private final boolean jj_3_3() {
      if (this.jj_scan_token(24)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(37)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3_2() {
      if (this.jj_scan_token(28)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(39)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(24)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   private final boolean jj_3_1() {
      if (this.jj_scan_token(24)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else {
         do {
            Token var1 = this.jj_scanpos;
            if (this.jj_3R_14()) {
               this.jj_scanpos = var1;
               if (this.jj_scan_token(39)) {
                  return true;
               }

               if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }

               if (this.jj_scan_token(24)) {
                  return true;
               }

               if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
                  return false;
               }

               return false;
            }
         } while(this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos);

         return false;
      }
   }

   private final boolean jj_3R_14() {
      if (this.jj_scan_token(37)) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         return false;
      } else if (this.jj_scan_token(24)) {
         return true;
      } else {
         return this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos ? false : false;
      }
   }

   public Parser(InputStream var1) {
      this.jj_input_stream = new ASCII_CharStream(var1, 1, 1);
      this.token_source = new ParserTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int var2;
      for(var2 = 0; var2 < 22; ++var2) {
         this.jj_la1[var2] = -1;
      }

      for(var2 = 0; var2 < this.jj_2_rtns.length; ++var2) {
         this.jj_2_rtns[var2] = new Parser.JJCalls();
      }

   }

   public void ReInit(InputStream var1) {
      this.jj_input_stream.ReInit((InputStream)var1, 1, 1);
      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jjtree.reset();
      this.jj_gen = 0;

      int var2;
      for(var2 = 0; var2 < 22; ++var2) {
         this.jj_la1[var2] = -1;
      }

      for(var2 = 0; var2 < this.jj_2_rtns.length; ++var2) {
         this.jj_2_rtns[var2] = new Parser.JJCalls();
      }

   }

   public Parser(Reader var1) {
      this.jj_input_stream = new ASCII_CharStream(var1, 1, 1);
      this.token_source = new ParserTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int var2;
      for(var2 = 0; var2 < 22; ++var2) {
         this.jj_la1[var2] = -1;
      }

      for(var2 = 0; var2 < this.jj_2_rtns.length; ++var2) {
         this.jj_2_rtns[var2] = new Parser.JJCalls();
      }

   }

   public void ReInit(Reader var1) {
      this.jj_input_stream.ReInit((Reader)var1, 1, 1);
      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jjtree.reset();
      this.jj_gen = 0;

      int var2;
      for(var2 = 0; var2 < 22; ++var2) {
         this.jj_la1[var2] = -1;
      }

      for(var2 = 0; var2 < this.jj_2_rtns.length; ++var2) {
         this.jj_2_rtns[var2] = new Parser.JJCalls();
      }

   }

   public Parser(ParserTokenManager var1) {
      this.token_source = var1;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int var2;
      for(var2 = 0; var2 < 22; ++var2) {
         this.jj_la1[var2] = -1;
      }

      for(var2 = 0; var2 < this.jj_2_rtns.length; ++var2) {
         this.jj_2_rtns[var2] = new Parser.JJCalls();
      }

   }

   public void ReInit(ParserTokenManager var1) {
      this.token_source = var1;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jjtree.reset();
      this.jj_gen = 0;

      int var2;
      for(var2 = 0; var2 < 22; ++var2) {
         this.jj_la1[var2] = -1;
      }

      for(var2 = 0; var2 < this.jj_2_rtns.length; ++var2) {
         this.jj_2_rtns[var2] = new Parser.JJCalls();
      }

   }

   private final Token jj_consume_token(int var1) throws ParseException {
      Token var2;
      if ((var2 = this.token).next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
      if (this.token.kind != var1) {
         this.token = var2;
         this.jj_kind = var1;
         throw this.generateParseException();
      } else {
         ++this.jj_gen;
         if (++this.jj_gc > 100) {
            this.jj_gc = 0;

            for(int var3 = 0; var3 < this.jj_2_rtns.length; ++var3) {
               for(Parser.JJCalls var4 = this.jj_2_rtns[var3]; var4 != null; var4 = var4.next) {
                  if (var4.gen < this.jj_gen) {
                     var4.first = null;
                  }
               }
            }
         }

         return this.token;
      }
   }

   private final boolean jj_scan_token(int var1) {
      if (this.jj_scanpos == this.jj_lastpos) {
         --this.jj_la;
         if (this.jj_scanpos.next == null) {
            this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
         } else {
            this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
         }
      } else {
         this.jj_scanpos = this.jj_scanpos.next;
      }

      if (this.jj_rescan) {
         int var2 = 0;

         Token var3;
         for(var3 = this.token; var3 != null && var3 != this.jj_scanpos; var3 = var3.next) {
            ++var2;
         }

         if (var3 != null) {
            this.jj_add_error_token(var1, var2);
         }
      }

      return this.jj_scanpos.kind != var1;
   }

   public final Token getNextToken() {
      if (this.token.next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
      ++this.jj_gen;
      return this.token;
   }

   public final Token getToken(int var1) {
      Token var2 = this.lookingAhead ? this.jj_scanpos : this.token;

      for(int var3 = 0; var3 < var1; ++var3) {
         if (var2.next != null) {
            var2 = var2.next;
         } else {
            var2 = var2.next = this.token_source.getNextToken();
         }
      }

      return var2;
   }

   private final int jj_ntk() {
      return (this.jj_nt = this.token.next) == null ? (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind) : (this.jj_ntk = this.jj_nt.kind);
   }

   private void jj_add_error_token(int var1, int var2) {
      if (var2 < 100) {
         if (var2 == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = var1;
         } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];

            for(int var3 = 0; var3 < this.jj_endpos; ++var3) {
               this.jj_expentry[var3] = this.jj_lasttokens[var3];
            }

            boolean var7 = false;
            Enumeration var4 = this.jj_expentries.elements();

            label48:
            do {
               int[] var5;
               do {
                  if (!var4.hasMoreElements()) {
                     break label48;
                  }

                  var5 = (int[])var4.nextElement();
               } while(var5.length != this.jj_expentry.length);

               var7 = true;

               for(int var6 = 0; var6 < this.jj_expentry.length; ++var6) {
                  if (var5[var6] != this.jj_expentry[var6]) {
                     var7 = false;
                     break;
                  }
               }
            } while(!var7);

            if (!var7) {
               this.jj_expentries.addElement(this.jj_expentry);
            }

            if (var2 != 0) {
               this.jj_lasttokens[(this.jj_endpos = var2) - 1] = var1;
            }
         }

      }
   }

   public final ParseException generateParseException() {
      this.jj_expentries.removeAllElements();
      boolean[] var1 = new boolean[40];

      int var2;
      for(var2 = 0; var2 < 40; ++var2) {
         var1[var2] = false;
      }

      if (this.jj_kind >= 0) {
         var1[this.jj_kind] = true;
         this.jj_kind = -1;
      }

      int var3;
      for(var2 = 0; var2 < 22; ++var2) {
         if (this.jj_la1[var2] == this.jj_gen) {
            for(var3 = 0; var3 < 32; ++var3) {
               if ((this.jj_la1_0[var2] & 1 << var3) != 0) {
                  var1[var3] = true;
               }

               if ((this.jj_la1_1[var2] & 1 << var3) != 0) {
                  var1[32 + var3] = true;
               }
            }
         }
      }

      for(var2 = 0; var2 < 40; ++var2) {
         if (var1[var2]) {
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = var2;
            this.jj_expentries.addElement(this.jj_expentry);
         }
      }

      this.jj_endpos = 0;
      this.jj_rescan_token();
      this.jj_add_error_token(0, 0);
      int[][] var4 = new int[this.jj_expentries.size()][];

      for(var3 = 0; var3 < this.jj_expentries.size(); ++var3) {
         var4[var3] = (int[])this.jj_expentries.elementAt(var3);
      }

      return new ParseException(this.token, var4, tokenImage);
   }

   public final void enable_tracing() {
   }

   public final void disable_tracing() {
   }

   private final void jj_rescan_token() {
      this.jj_rescan = true;

      for(int var1 = 0; var1 < 3; ++var1) {
         Parser.JJCalls var2 = this.jj_2_rtns[var1];

         do {
            if (var2.gen > this.jj_gen) {
               this.jj_la = var2.arg;
               this.jj_lastpos = this.jj_scanpos = var2.first;
               switch(var1) {
               case 0:
                  this.jj_3_1();
                  break;
               case 1:
                  this.jj_3_2();
                  break;
               case 2:
                  this.jj_3_3();
               }
            }

            var2 = var2.next;
         } while(var2 != null);
      }

      this.jj_rescan = false;
   }

   private final void jj_save(int var1, int var2) {
      Parser.JJCalls var3;
      for(var3 = this.jj_2_rtns[var1]; var3.gen > this.jj_gen; var3 = var3.next) {
         if (var3.next == null) {
            var3 = var3.next = new Parser.JJCalls();
            break;
         }
      }

      var3.gen = this.jj_gen + var2 - this.jj_la;
      var3.first = this.token;
      var3.arg = var2;
   }

   static final class JJCalls {
      int gen;
      Token first;
      int arg;
      Parser.JJCalls next;
   }
}
