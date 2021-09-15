package sun.security.smartcardio;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardNotPresentException;
import javax.smartcardio.CardPermission;
import javax.smartcardio.CardTerminal;

final class TerminalImpl extends CardTerminal {
   final long contextId;
   final String name;
   private CardImpl card;

   TerminalImpl(long var1, String var3) {
      this.contextId = var1;
      this.name = var3;
   }

   public String getName() {
      return this.name;
   }

   public synchronized Card connect(String var1) throws CardException {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(new CardPermission(this.name, "connect"));
      }

      if (this.card != null) {
         if (this.card.isValid()) {
            String var3 = this.card.getProtocol();
            if (!var1.equals("*") && !var1.equalsIgnoreCase(var3)) {
               throw new CardException("Cannot connect using " + var1 + ", connection already established using " + var3);
            }

            return this.card;
         }

         this.card = null;
      }

      try {
         this.card = new CardImpl(this, var1);
         return this.card;
      } catch (PCSCException var4) {
         if (var4.code != -2146434967 && var4.code != -2146435060) {
            throw new CardException("connect() failed", var4);
         } else {
            throw new CardNotPresentException("No card present", var4);
         }
      }
   }

   public boolean isCardPresent() throws CardException {
      try {
         int[] var1 = PCSC.SCardGetStatusChange(this.contextId, 0L, new int[]{0}, new String[]{this.name});
         return (var1[0] & 32) != 0;
      } catch (PCSCException var2) {
         throw new CardException("isCardPresent() failed", var2);
      }
   }

   private boolean waitForCard(boolean var1, long var2) throws CardException {
      if (var2 < 0L) {
         throw new IllegalArgumentException("timeout must not be negative");
      } else {
         if (var2 == 0L) {
            var2 = -1L;
         }

         int[] var4 = new int[]{0};
         String[] var5 = new String[]{this.name};

         try {
            var4 = PCSC.SCardGetStatusChange(this.contextId, 0L, var4, var5);
            boolean var6 = (var4[0] & 32) != 0;
            if (var1 == var6) {
               return true;
            } else {
               for(long var7 = System.currentTimeMillis() + var2; var1 != var6 && var2 != 0L; var6 = (var4[0] & 32) != 0) {
                  if (var2 != -1L) {
                     var2 = Math.max(var7 - System.currentTimeMillis(), 0L);
                  }

                  var4 = PCSC.SCardGetStatusChange(this.contextId, var2, var4, var5);
               }

               return var1 == var6;
            }
         } catch (PCSCException var9) {
            if (var9.code == -2146435062) {
               return false;
            } else {
               throw new CardException("waitForCard() failed", var9);
            }
         }
      }
   }

   public boolean waitForCardPresent(long var1) throws CardException {
      return this.waitForCard(true, var1);
   }

   public boolean waitForCardAbsent(long var1) throws CardException {
      return this.waitForCard(false, var1);
   }

   public String toString() {
      return "PC/SC terminal " + this.name;
   }
}
