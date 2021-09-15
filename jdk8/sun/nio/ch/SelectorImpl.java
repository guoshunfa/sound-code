package sun.nio.ch;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.IllegalSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class SelectorImpl extends AbstractSelector {
   protected Set<SelectionKey> selectedKeys = new HashSet();
   protected HashSet<SelectionKey> keys = new HashSet();
   private Set<SelectionKey> publicKeys;
   private Set<SelectionKey> publicSelectedKeys;

   protected SelectorImpl(SelectorProvider var1) {
      super(var1);
      if (Util.atBugLevel("1.4")) {
         this.publicKeys = this.keys;
         this.publicSelectedKeys = this.selectedKeys;
      } else {
         this.publicKeys = Collections.unmodifiableSet(this.keys);
         this.publicSelectedKeys = Util.ungrowableSet(this.selectedKeys);
      }

   }

   public Set<SelectionKey> keys() {
      if (!this.isOpen() && !Util.atBugLevel("1.4")) {
         throw new ClosedSelectorException();
      } else {
         return this.publicKeys;
      }
   }

   public Set<SelectionKey> selectedKeys() {
      if (!this.isOpen() && !Util.atBugLevel("1.4")) {
         throw new ClosedSelectorException();
      } else {
         return this.publicSelectedKeys;
      }
   }

   protected abstract int doSelect(long var1) throws IOException;

   private int lockAndDoSelect(long var1) throws IOException {
      synchronized(this) {
         if (!this.isOpen()) {
            throw new ClosedSelectorException();
         } else {
            int var10000;
            synchronized(this.publicKeys) {
               synchronized(this.publicSelectedKeys) {
                  var10000 = this.doSelect(var1);
               }
            }

            return var10000;
         }
      }
   }

   public int select(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Negative timeout");
      } else {
         return this.lockAndDoSelect(var1 == 0L ? -1L : var1);
      }
   }

   public int select() throws IOException {
      return this.select(0L);
   }

   public int selectNow() throws IOException {
      return this.lockAndDoSelect(0L);
   }

   public void implCloseSelector() throws IOException {
      this.wakeup();
      synchronized(this) {
         synchronized(this.publicKeys) {
            synchronized(this.publicSelectedKeys) {
               this.implClose();
            }
         }

      }
   }

   protected abstract void implClose() throws IOException;

   public void putEventOps(SelectionKeyImpl var1, int var2) {
   }

   protected final SelectionKey register(AbstractSelectableChannel var1, int var2, Object var3) {
      if (!(var1 instanceof SelChImpl)) {
         throw new IllegalSelectorException();
      } else {
         SelectionKeyImpl var4 = new SelectionKeyImpl((SelChImpl)var1, this);
         var4.attach(var3);
         synchronized(this.publicKeys) {
            this.implRegister(var4);
         }

         var4.interestOps(var2);
         return var4;
      }
   }

   protected abstract void implRegister(SelectionKeyImpl var1);

   void processDeregisterQueue() throws IOException {
      Set var1 = this.cancelledKeys();
      synchronized(var1) {
         if (!var1.isEmpty()) {
            Iterator var3 = var1.iterator();

            while(var3.hasNext()) {
               SelectionKeyImpl var4 = (SelectionKeyImpl)var3.next();

               try {
                  this.implDereg(var4);
               } catch (SocketException var11) {
                  throw new IOException("Error deregistering key", var11);
               } finally {
                  var3.remove();
               }
            }
         }

      }
   }

   protected abstract void implDereg(SelectionKeyImpl var1) throws IOException;

   public abstract Selector wakeup();
}
