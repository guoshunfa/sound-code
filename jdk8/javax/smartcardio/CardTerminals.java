package javax.smartcardio;

import java.util.Iterator;
import java.util.List;

public abstract class CardTerminals {
   protected CardTerminals() {
   }

   public List<CardTerminal> list() throws CardException {
      return this.list(CardTerminals.State.ALL);
   }

   public abstract List<CardTerminal> list(CardTerminals.State var1) throws CardException;

   public CardTerminal getTerminal(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         try {
            Iterator var2 = this.list().iterator();

            CardTerminal var3;
            do {
               if (!var2.hasNext()) {
                  return null;
               }

               var3 = (CardTerminal)var2.next();
            } while(!var3.getName().equals(var1));

            return var3;
         } catch (CardException var4) {
            return null;
         }
      }
   }

   public void waitForChange() throws CardException {
      this.waitForChange(0L);
   }

   public abstract boolean waitForChange(long var1) throws CardException;

   public static enum State {
      ALL,
      CARD_PRESENT,
      CARD_ABSENT,
      CARD_INSERTION,
      CARD_REMOVAL;
   }
}
