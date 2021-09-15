package sun.security.smartcardio;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import sun.security.action.GetPropertyAction;

final class ChannelImpl extends CardChannel {
   private final CardImpl card;
   private final int channel;
   private volatile boolean isClosed;
   private static final boolean t0GetResponse = getBooleanProperty("sun.security.smartcardio.t0GetResponse", true);
   private static final boolean t1GetResponse = getBooleanProperty("sun.security.smartcardio.t1GetResponse", true);
   private static final boolean t1StripLe = getBooleanProperty("sun.security.smartcardio.t1StripLe", false);
   private static final byte[] B0 = new byte[0];

   ChannelImpl(CardImpl var1, int var2) {
      this.card = var1;
      this.channel = var2;
   }

   void checkClosed() {
      this.card.checkState();
      if (this.isClosed) {
         throw new IllegalStateException("Logical channel has been closed");
      }
   }

   public Card getCard() {
      return this.card;
   }

   public int getChannelNumber() {
      this.checkClosed();
      return this.channel;
   }

   private static void checkManageChannel(byte[] var0) {
      if (var0.length < 4) {
         throw new IllegalArgumentException("Command APDU must be at least 4 bytes long");
      } else if (var0[0] >= 0 && var0[1] == 112) {
         throw new IllegalArgumentException("Manage channel command not allowed, use openLogicalChannel()");
      }
   }

   public ResponseAPDU transmit(CommandAPDU var1) throws CardException {
      this.checkClosed();
      this.card.checkExclusive();
      byte[] var2 = var1.getBytes();
      byte[] var3 = this.doTransmit(var2);
      return new ResponseAPDU(var3);
   }

   public int transmit(ByteBuffer var1, ByteBuffer var2) throws CardException {
      this.checkClosed();
      this.card.checkExclusive();
      if (var1 != null && var2 != null) {
         if (var2.isReadOnly()) {
            throw new ReadOnlyBufferException();
         } else if (var1 == var2) {
            throw new IllegalArgumentException("command and response must not be the same object");
         } else if (var2.remaining() < 258) {
            throw new IllegalArgumentException("Insufficient space in response buffer");
         } else {
            byte[] var3 = new byte[var1.remaining()];
            var1.get(var3);
            byte[] var4 = this.doTransmit(var3);
            var2.put(var4);
            return var4.length;
         }
      } else {
         throw new NullPointerException();
      }
   }

   private static boolean getBooleanProperty(String var0, boolean var1) {
      String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var0)));
      if (var2 == null) {
         return var1;
      } else if (var2.equalsIgnoreCase("true")) {
         return true;
      } else if (var2.equalsIgnoreCase("false")) {
         return false;
      } else {
         throw new IllegalArgumentException(var0 + " must be either 'true' or 'false'");
      }
   }

   private byte[] concat(byte[] var1, byte[] var2, int var3) {
      int var4 = var1.length;
      if (var4 == 0 && var3 == var2.length) {
         return var2;
      } else {
         byte[] var5 = new byte[var4 + var3];
         System.arraycopy(var1, 0, var5, 0, var4);
         System.arraycopy(var2, 0, var5, var4, var3);
         return var5;
      }
   }

   private byte[] doTransmit(byte[] var1) throws CardException {
      try {
         checkManageChannel(var1);
         this.setChannel(var1);
         int var2 = var1.length;
         boolean var3 = this.card.protocol == 1;
         boolean var4 = this.card.protocol == 2;
         if (var3 && var2 >= 7 && var1[4] == 0) {
            throw new CardException("Extended length forms not supported for T=0");
         } else {
            if ((var3 || var4 && t1StripLe) && var2 >= 7) {
               int var5 = var1[4] & 255;
               if (var5 != 0) {
                  if (var2 == var5 + 6) {
                     --var2;
                  }
               } else {
                  var5 = (var1[5] & 255) << 8 | var1[6] & 255;
                  if (var2 == var5 + 9) {
                     var2 -= 2;
                  }
               }
            }

            boolean var11 = var3 && t0GetResponse || var4 && t1GetResponse;
            int var6 = 0;
            byte[] var7 = B0;

            byte[] var8;
            int var9;
            while(true) {
               ++var6;
               if (var6 >= 32) {
                  throw new CardException("Could not obtain response");
               }

               var8 = PCSC.SCardTransmit(this.card.cardId, this.card.protocol, var1, 0, var2);
               var9 = var8.length;
               if (!var11 || var9 < 2) {
                  break;
               }

               if (var9 == 2 && var8[0] == 108) {
                  var1[var2 - 1] = var8[1];
               } else {
                  if (var8[var9 - 2] != 97) {
                     break;
                  }

                  if (var9 > 2) {
                     var7 = this.concat(var7, var8, var9 - 2);
                  }

                  var1[1] = -64;
                  var1[2] = 0;
                  var1[3] = 0;
                  var1[4] = var8[var9 - 1];
                  var2 = 5;
               }
            }

            var7 = this.concat(var7, var8, var9);
            return var7;
         }
      } catch (PCSCException var10) {
         this.card.handleError(var10);
         throw new CardException(var10);
      }
   }

   private static int getSW(byte[] var0) throws CardException {
      if (var0.length < 2) {
         throw new CardException("Invalid response length: " + var0.length);
      } else {
         int var1 = var0[var0.length - 2] & 255;
         int var2 = var0[var0.length - 1] & 255;
         return var1 << 8 | var2;
      }
   }

   private static boolean isOK(byte[] var0) throws CardException {
      return var0.length == 2 && getSW(var0) == 36864;
   }

   private void setChannel(byte[] var1) {
      byte var2 = var1[0];
      if (var2 >= 0) {
         if ((var2 & 224) != 32) {
            if (this.channel <= 3) {
               var1[0] = (byte)(var1[0] & 188);
               var1[0] = (byte)(var1[0] | this.channel);
            } else {
               if (this.channel > 19) {
                  throw new RuntimeException("Unsupported channel number: " + this.channel);
               }

               var1[0] = (byte)(var1[0] & 176);
               var1[0] = (byte)(var1[0] | 64);
               var1[0] = (byte)(var1[0] | this.channel - 4);
            }

         }
      }
   }

   public void close() throws CardException {
      if (this.getChannelNumber() == 0) {
         throw new IllegalStateException("Cannot close basic logical channel");
      } else if (!this.isClosed) {
         this.card.checkExclusive();

         try {
            byte[] var1 = new byte[]{0, 112, -128, (byte)this.getChannelNumber()};
            this.setChannel(var1);
            byte[] var2 = PCSC.SCardTransmit(this.card.cardId, this.card.protocol, var1, 0, var1.length);
            if (!isOK(var2)) {
               throw new CardException("close() failed: " + PCSC.toString(var2));
            }
         } catch (PCSCException var6) {
            this.card.handleError(var6);
            throw new CardException("Could not close channel", var6);
         } finally {
            this.isClosed = true;
         }

      }
   }

   public String toString() {
      return "PC/SC channel " + this.channel;
   }
}
