package sun.security.smartcardio;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;

final class PCSCTerminals extends CardTerminals {
   private static long contextId;
   private Map<String, PCSCTerminals.ReaderState> stateMap;
   private static final Map<String, Reference<TerminalImpl>> terminals = new HashMap();

   static synchronized void initContext() throws PCSCException {
      if (contextId == 0L) {
         contextId = PCSC.SCardEstablishContext(0);
      }

   }

   private static synchronized TerminalImpl implGetTerminal(String var0) {
      Reference var1 = (Reference)terminals.get(var0);
      TerminalImpl var2 = var1 != null ? (TerminalImpl)var1.get() : null;
      if (var2 != null) {
         return var2;
      } else {
         var2 = new TerminalImpl(contextId, var0);
         terminals.put(var0, new WeakReference(var2));
         return var2;
      }
   }

   public synchronized List<CardTerminal> list(CardTerminals.State var1) throws CardException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         try {
            String[] var2 = PCSC.SCardListReaders(contextId);
            ArrayList var3 = new ArrayList(var2.length);
            if (this.stateMap == null) {
               if (var1 == CardTerminals.State.CARD_INSERTION) {
                  var1 = CardTerminals.State.CARD_PRESENT;
               } else if (var1 == CardTerminals.State.CARD_REMOVAL) {
                  var1 = CardTerminals.State.CARD_ABSENT;
               }
            }

            String[] var4 = var2;
            int var5 = var2.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String var7 = var4[var6];
               TerminalImpl var8 = implGetTerminal(var7);
               PCSCTerminals.ReaderState var9;
               switch(var1) {
               case ALL:
                  var3.add(var8);
                  break;
               case CARD_PRESENT:
                  if (var8.isCardPresent()) {
                     var3.add(var8);
                  }
                  break;
               case CARD_ABSENT:
                  if (!var8.isCardPresent()) {
                     var3.add(var8);
                  }
                  break;
               case CARD_INSERTION:
                  var9 = (PCSCTerminals.ReaderState)this.stateMap.get(var7);
                  if (var9 != null && var9.isInsertion()) {
                     var3.add(var8);
                  }
                  break;
               case CARD_REMOVAL:
                  var9 = (PCSCTerminals.ReaderState)this.stateMap.get(var7);
                  if (var9 != null && var9.isRemoval()) {
                     var3.add(var8);
                  }
                  break;
               default:
                  throw new CardException("Unknown state: " + var1);
               }
            }

            return Collections.unmodifiableList(var3);
         } catch (PCSCException var10) {
            throw new CardException("list() failed", var10);
         }
      }
   }

   public synchronized boolean waitForChange(long var1) throws CardException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Timeout must not be negative: " + var1);
      } else {
         if (this.stateMap == null) {
            this.stateMap = new HashMap();
            this.waitForChange(0L);
         }

         if (var1 == 0L) {
            var1 = -1L;
         }

         try {
            String[] var3 = PCSC.SCardListReaders(contextId);
            int var4 = var3.length;
            if (var4 == 0) {
               throw new IllegalStateException("No terminals available");
            } else {
               int[] var5 = new int[var4];
               PCSCTerminals.ReaderState[] var6 = new PCSCTerminals.ReaderState[var4];

               int var7;
               for(var7 = 0; var7 < var3.length; ++var7) {
                  String var8 = var3[var7];
                  PCSCTerminals.ReaderState var9 = (PCSCTerminals.ReaderState)this.stateMap.get(var8);
                  if (var9 == null) {
                     var9 = new PCSCTerminals.ReaderState();
                  }

                  var6[var7] = var9;
                  var5[var7] = var9.get();
               }

               var5 = PCSC.SCardGetStatusChange(contextId, var1, var5, var3);
               this.stateMap.clear();

               for(var7 = 0; var7 < var4; ++var7) {
                  PCSCTerminals.ReaderState var11 = var6[var7];
                  var11.update(var5[var7]);
                  this.stateMap.put(var3[var7], var11);
               }

               return true;
            }
         } catch (PCSCException var10) {
            if (var10.code == -2146435062) {
               return false;
            } else {
               throw new CardException("waitForChange() failed", var10);
            }
         }
      }
   }

   static List<CardTerminal> waitForCards(List<? extends CardTerminal> var0, long var1, boolean var3) throws CardException {
      long var4;
      if (var1 == 0L) {
         var1 = -1L;
         var4 = -1L;
      } else {
         var4 = 0L;
      }

      String[] var6 = new String[var0.size()];
      int var7 = 0;

      TerminalImpl var10;
      for(Iterator var8 = var0.iterator(); var8.hasNext(); var6[var7++] = var10.name) {
         CardTerminal var9 = (CardTerminal)var8.next();
         if (!(var9 instanceof TerminalImpl)) {
            throw new IllegalArgumentException("Invalid terminal type: " + var9.getClass().getName());
         }

         var10 = (TerminalImpl)var9;
      }

      int[] var12 = new int[var6.length];
      Arrays.fill((int[])var12, (int)0);

      try {
         ArrayList var13;
         do {
            var12 = PCSC.SCardGetStatusChange(contextId, var4, var12, var6);
            var4 = var1;
            var13 = null;

            for(var7 = 0; var7 < var6.length; ++var7) {
               boolean var14 = (var12[var7] & 32) != 0;
               if (var14 == var3) {
                  if (var13 == null) {
                     var13 = new ArrayList();
                  }

                  var13.add(implGetTerminal(var6[var7]));
               }
            }
         } while(var13 == null);

         return Collections.unmodifiableList(var13);
      } catch (PCSCException var11) {
         if (var11.code == -2146435062) {
            return Collections.emptyList();
         } else {
            throw new CardException("waitForCard() failed", var11);
         }
      }
   }

   private static class ReaderState {
      private int current = 0;
      private int previous = 0;

      ReaderState() {
      }

      int get() {
         return this.current;
      }

      void update(int var1) {
         this.previous = this.current;
         this.current = var1;
      }

      boolean isInsertion() {
         return !present(this.previous) && present(this.current);
      }

      boolean isRemoval() {
         return present(this.previous) && !present(this.current);
      }

      static boolean present(int var0) {
         return (var0 & 32) != 0;
      }
   }
}
