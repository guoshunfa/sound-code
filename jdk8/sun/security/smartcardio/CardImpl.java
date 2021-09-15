package sun.security.smartcardio;

import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardPermission;
import sun.security.action.GetPropertyAction;

final class CardImpl extends Card {
   private final TerminalImpl terminal;
   final long cardId;
   private final ATR atr;
   final int protocol;
   private final ChannelImpl basicChannel;
   private volatile CardImpl.State state;
   private volatile Thread exclusiveThread;
   private static final boolean isWindows;
   private static byte[] commandOpenChannel;
   private static final boolean invertReset;

   CardImpl(TerminalImpl var1, String var2) throws PCSCException {
      this.terminal = var1;
      byte var3 = 2;
      int var4;
      if (var2.equals("*")) {
         var4 = 3;
      } else if (var2.equalsIgnoreCase("T=0")) {
         var4 = 1;
      } else if (var2.equalsIgnoreCase("T=1")) {
         var4 = 2;
      } else {
         if (!var2.equalsIgnoreCase("direct")) {
            throw new IllegalArgumentException("Unsupported protocol " + var2);
         }

         var4 = isWindows ? 0 : 4;
         var3 = 3;
      }

      this.cardId = PCSC.SCardConnect(var1.contextId, var1.name, var3, var4);
      byte[] var5 = new byte[2];
      byte[] var6 = PCSC.SCardStatus(this.cardId, var5);
      this.atr = new ATR(var6);
      this.protocol = var5[1] & 255;
      this.basicChannel = new ChannelImpl(this, 0);
      this.state = CardImpl.State.OK;
   }

   void checkState() {
      CardImpl.State var1 = this.state;
      if (var1 == CardImpl.State.DISCONNECTED) {
         throw new IllegalStateException("Card has been disconnected");
      } else if (var1 == CardImpl.State.REMOVED) {
         throw new IllegalStateException("Card has been removed");
      }
   }

   boolean isValid() {
      if (this.state != CardImpl.State.OK) {
         return false;
      } else {
         try {
            PCSC.SCardStatus(this.cardId, new byte[2]);
            return true;
         } catch (PCSCException var2) {
            this.state = CardImpl.State.REMOVED;
            return false;
         }
      }
   }

   private void checkSecurity(String var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(new CardPermission(this.terminal.name, var1));
      }

   }

   void handleError(PCSCException var1) {
      if (var1.code == -2146434967) {
         this.state = CardImpl.State.REMOVED;
      }

   }

   public ATR getATR() {
      return this.atr;
   }

   public String getProtocol() {
      switch(this.protocol) {
      case 1:
         return "T=0";
      case 2:
         return "T=1";
      default:
         return "Unknown protocol " + this.protocol;
      }
   }

   public CardChannel getBasicChannel() {
      this.checkSecurity("getBasicChannel");
      this.checkState();
      return this.basicChannel;
   }

   private static int getSW(byte[] var0) {
      if (var0.length < 2) {
         return -1;
      } else {
         int var1 = var0[var0.length - 2] & 255;
         int var2 = var0[var0.length - 1] & 255;
         return var1 << 8 | var2;
      }
   }

   public CardChannel openLogicalChannel() throws CardException {
      this.checkSecurity("openLogicalChannel");
      this.checkState();
      this.checkExclusive();

      try {
         byte[] var1 = PCSC.SCardTransmit(this.cardId, this.protocol, commandOpenChannel, 0, commandOpenChannel.length);
         if (var1.length == 3 && getSW(var1) == 36864) {
            return new ChannelImpl(this, var1[0]);
         } else {
            throw new CardException("openLogicalChannel() failed, card response: " + PCSC.toString(var1));
         }
      } catch (PCSCException var2) {
         this.handleError(var2);
         throw new CardException("openLogicalChannel() failed", var2);
      }
   }

   void checkExclusive() throws CardException {
      Thread var1 = this.exclusiveThread;
      if (var1 != null) {
         if (var1 != Thread.currentThread()) {
            throw new CardException("Exclusive access established by another Thread");
         }
      }
   }

   public synchronized void beginExclusive() throws CardException {
      this.checkSecurity("exclusive");
      this.checkState();
      if (this.exclusiveThread != null) {
         throw new CardException("Exclusive access has already been assigned to Thread " + this.exclusiveThread.getName());
      } else {
         try {
            PCSC.SCardBeginTransaction(this.cardId);
         } catch (PCSCException var2) {
            this.handleError(var2);
            throw new CardException("beginExclusive() failed", var2);
         }

         this.exclusiveThread = Thread.currentThread();
      }
   }

   public synchronized void endExclusive() throws CardException {
      this.checkState();
      if (this.exclusiveThread != Thread.currentThread()) {
         throw new IllegalStateException("Exclusive access not assigned to current Thread");
      } else {
         try {
            PCSC.SCardEndTransaction(this.cardId, 0);
         } catch (PCSCException var5) {
            this.handleError(var5);
            throw new CardException("endExclusive() failed", var5);
         } finally {
            this.exclusiveThread = null;
         }

      }
   }

   public byte[] transmitControlCommand(int var1, byte[] var2) throws CardException {
      this.checkSecurity("transmitControl");
      this.checkState();
      this.checkExclusive();
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         try {
            byte[] var3 = PCSC.SCardControl(this.cardId, var1, var2);
            return var3;
         } catch (PCSCException var4) {
            this.handleError(var4);
            throw new CardException("transmitControlCommand() failed", var4);
         }
      }
   }

   public void disconnect(boolean var1) throws CardException {
      if (var1) {
         this.checkSecurity("reset");
      }

      if (this.state == CardImpl.State.OK) {
         this.checkExclusive();
         if (invertReset) {
            var1 = !var1;
         }

         try {
            PCSC.SCardDisconnect(this.cardId, var1 ? 1 : 0);
         } catch (PCSCException var6) {
            throw new CardException("disconnect() failed", var6);
         } finally {
            this.state = CardImpl.State.DISCONNECTED;
            this.exclusiveThread = null;
         }

      }
   }

   public String toString() {
      return "PC/SC card in " + this.terminal.name + ", protocol " + this.getProtocol() + ", state " + this.state;
   }

   protected void finalize() throws Throwable {
      try {
         if (this.state == CardImpl.State.OK) {
            this.state = CardImpl.State.DISCONNECTED;
            PCSC.SCardDisconnect(this.cardId, 0);
         }
      } finally {
         super.finalize();
      }

   }

   static {
      String var0 = (String)AccessController.doPrivileged(() -> {
         return System.getProperty("os.name");
      });
      isWindows = var0.startsWith("Windows");
      commandOpenChannel = new byte[]{0, 112, 0, 0, 1};
      invertReset = Boolean.parseBoolean((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.security.smartcardio.invertCardReset", "false"))));
   }

   private static enum State {
      OK,
      REMOVED,
      DISCONNECTED;
   }
}
