package sun.awt.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DropTargetContextPeer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
import sun.security.util.SecurityConstants;
import sun.util.logging.PlatformLogger;

public abstract class SunDropTargetContextPeer implements DropTargetContextPeer, Transferable {
   public static final boolean DISPATCH_SYNC = true;
   private DropTarget currentDT;
   private DropTargetContext currentDTC;
   private long[] currentT;
   private int currentA;
   private int currentSA;
   private int currentDA;
   private int previousDA;
   private long nativeDragContext;
   private Transferable local;
   private boolean dragRejected = false;
   protected int dropStatus = 0;
   protected boolean dropComplete = false;
   boolean dropInProcess = false;
   protected static final Object _globalLock = new Object();
   private static final PlatformLogger dndLog = PlatformLogger.getLogger("sun.awt.dnd.SunDropTargetContextPeer");
   protected static Transferable currentJVMLocalSourceTransferable = null;
   protected static final int STATUS_NONE = 0;
   protected static final int STATUS_WAIT = 1;
   protected static final int STATUS_ACCEPT = 2;
   protected static final int STATUS_REJECT = -1;

   public static void setCurrentJVMLocalSourceTransferable(Transferable var0) throws InvalidDnDOperationException {
      synchronized(_globalLock) {
         if (var0 != null && currentJVMLocalSourceTransferable != null) {
            throw new InvalidDnDOperationException();
         } else {
            currentJVMLocalSourceTransferable = var0;
         }
      }
   }

   private static Transferable getJVMLocalSourceTransferable() {
      return currentJVMLocalSourceTransferable;
   }

   public DropTarget getDropTarget() {
      return this.currentDT;
   }

   public synchronized void setTargetActions(int var1) {
      this.currentA = var1 & 1073741827;
   }

   public int getTargetActions() {
      return this.currentA;
   }

   public Transferable getTransferable() {
      return this;
   }

   public DataFlavor[] getTransferDataFlavors() {
      Transferable var1 = this.local;
      return var1 != null ? var1.getTransferDataFlavors() : DataTransferer.getInstance().getFlavorsForFormatsAsArray(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap()));
   }

   public boolean isDataFlavorSupported(DataFlavor var1) {
      Transferable var2 = this.local;
      return var2 != null ? var2.isDataFlavorSupported(var1) : DataTransferer.getInstance().getFlavorsForFormats(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap())).containsKey(var1);
   }

   public Object getTransferData(DataFlavor var1) throws UnsupportedFlavorException, IOException, InvalidDnDOperationException {
      SecurityManager var2 = System.getSecurityManager();

      try {
         if (!this.dropInProcess && var2 != null) {
            var2.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
         }
      } catch (Exception var12) {
         Thread var4 = Thread.currentThread();
         var4.getUncaughtExceptionHandler().uncaughtException(var4, var12);
         return null;
      }

      Long var3 = null;
      Transferable var13 = this.local;
      if (var13 != null) {
         return var13.getTransferData(var1);
      } else if (this.dropStatus == 2 && !this.dropComplete) {
         Map var5 = DataTransferer.getInstance().getFlavorsForFormats(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap()));
         var3 = (Long)var5.get(var1);
         if (var3 == null) {
            throw new UnsupportedFlavorException(var1);
         } else if (var1.isRepresentationClassRemote() && this.currentDA != 1073741824) {
            throw new InvalidDnDOperationException("only ACTION_LINK is permissable for transfer of java.rmi.Remote objects");
         } else {
            long var6 = var3;
            Object var8 = this.getNativeData(var6);
            if (var8 instanceof byte[]) {
               try {
                  return DataTransferer.getInstance().translateBytes((byte[])((byte[])var8), var1, var6, this);
               } catch (IOException var10) {
                  throw new InvalidDnDOperationException(var10.getMessage());
               }
            } else if (var8 instanceof InputStream) {
               try {
                  return DataTransferer.getInstance().translateStream((InputStream)var8, var1, var6, this);
               } catch (IOException var11) {
                  throw new InvalidDnDOperationException(var11.getMessage());
               }
            } else {
               throw new IOException("no native data was transfered");
            }
         }
      } else {
         throw new InvalidDnDOperationException("No drop current");
      }
   }

   protected abstract Object getNativeData(long var1) throws IOException;

   public boolean isTransferableJVMLocal() {
      return this.local != null || getJVMLocalSourceTransferable() != null;
   }

   private int handleEnterMessage(Component var1, int var2, int var3, int var4, int var5, long[] var6, long var7) {
      return this.postDropTargetEvent(var1, var2, var3, var4, var5, var6, var7, 504, true);
   }

   protected void processEnterMessage(SunDropTargetEvent var1) {
      Component var2 = (Component)var1.getSource();
      DropTarget var3 = var2.getDropTarget();
      Point var4 = var1.getPoint();
      this.local = getJVMLocalSourceTransferable();
      if (this.currentDTC != null) {
         this.currentDTC.removeNotify();
         this.currentDTC = null;
      }

      if (var2.isShowing() && var3 != null && var3.isActive()) {
         this.currentDT = var3;
         this.currentDTC = this.currentDT.getDropTargetContext();
         this.currentDTC.addNotify(this);
         this.currentA = var3.getDefaultActions();

         try {
            var3.dragEnter(new DropTargetDragEvent(this.currentDTC, var4, this.currentDA, this.currentSA));
         } catch (Exception var6) {
            var6.printStackTrace();
            this.currentDA = 0;
         }
      } else {
         this.currentDT = null;
         this.currentDTC = null;
         this.currentDA = 0;
         this.currentSA = 0;
         this.currentA = 0;
      }

   }

   private void handleExitMessage(Component var1, long var2) {
      this.postDropTargetEvent(var1, 0, 0, 0, 0, (long[])null, var2, 505, true);
   }

   protected void processExitMessage(SunDropTargetEvent var1) {
      Component var2 = (Component)var1.getSource();
      DropTarget var3 = var2.getDropTarget();
      DropTargetContext var4 = null;
      if (var3 == null) {
         this.currentDT = null;
         this.currentT = null;
         if (this.currentDTC != null) {
            this.currentDTC.removeNotify();
         }

         this.currentDTC = null;
      } else {
         if (var3 != this.currentDT) {
            if (this.currentDTC != null) {
               this.currentDTC.removeNotify();
            }

            this.currentDT = var3;
            this.currentDTC = var3.getDropTargetContext();
            this.currentDTC.addNotify(this);
         }

         var4 = this.currentDTC;
         if (var3.isActive()) {
            try {
               var3.dragExit(new DropTargetEvent(var4));
            } catch (Exception var9) {
               var9.printStackTrace();
            } finally {
               this.currentA = 0;
               this.currentSA = 0;
               this.currentDA = 0;
               this.currentDT = null;
               this.currentT = null;
               this.currentDTC.removeNotify();
               this.currentDTC = null;
               this.local = null;
               this.dragRejected = false;
            }
         }

      }
   }

   private int handleMotionMessage(Component var1, int var2, int var3, int var4, int var5, long[] var6, long var7) {
      return this.postDropTargetEvent(var1, var2, var3, var4, var5, var6, var7, 506, true);
   }

   protected void processMotionMessage(SunDropTargetEvent var1, boolean var2) {
      Component var3 = (Component)var1.getSource();
      Point var4 = var1.getPoint();
      int var5 = var1.getID();
      DropTarget var6 = var3.getDropTarget();
      DropTargetContext var7 = null;
      if (var3.isShowing() && var6 != null && var6.isActive()) {
         if (this.currentDT != var6) {
            if (this.currentDTC != null) {
               this.currentDTC.removeNotify();
            }

            this.currentDT = var6;
            this.currentDTC = null;
         }

         var7 = this.currentDT.getDropTargetContext();
         if (var7 != this.currentDTC) {
            if (this.currentDTC != null) {
               this.currentDTC.removeNotify();
            }

            this.currentDTC = var7;
            this.currentDTC.addNotify(this);
         }

         this.currentA = this.currentDT.getDefaultActions();

         try {
            DropTargetDragEvent var8 = new DropTargetDragEvent(var7, var4, this.currentDA, this.currentSA);
            if (var2) {
               var6.dropActionChanged(var8);
            } else {
               var6.dragOver(var8);
            }

            if (this.dragRejected) {
               this.currentDA = 0;
            }
         } catch (Exception var10) {
            var10.printStackTrace();
            this.currentDA = 0;
         }
      } else {
         this.currentDA = 0;
      }

   }

   private void handleDropMessage(Component var1, int var2, int var3, int var4, int var5, long[] var6, long var7) {
      this.postDropTargetEvent(var1, var2, var3, var4, var5, var6, var7, 502, false);
   }

   protected void processDropMessage(SunDropTargetEvent var1) {
      Component var2 = (Component)var1.getSource();
      Point var3 = var1.getPoint();
      DropTarget var4 = var2.getDropTarget();
      this.dropStatus = 1;
      this.dropComplete = false;
      if (var2.isShowing() && var4 != null && var4.isActive()) {
         DropTargetContext var5 = var4.getDropTargetContext();
         this.currentDT = var4;
         if (this.currentDTC != null) {
            this.currentDTC.removeNotify();
         }

         this.currentDTC = var5;
         this.currentDTC.addNotify(this);
         this.currentA = var4.getDefaultActions();
         synchronized(_globalLock) {
            if ((this.local = getJVMLocalSourceTransferable()) != null) {
               setCurrentJVMLocalSourceTransferable((Transferable)null);
            }
         }

         this.dropInProcess = true;

         try {
            var4.drop(new DropTargetDropEvent(var5, var3, this.currentDA, this.currentSA, this.local != null));
         } finally {
            if (this.dropStatus == 1) {
               this.rejectDrop();
            } else if (!this.dropComplete) {
               this.dropComplete(false);
            }

            this.dropInProcess = false;
         }
      } else {
         this.rejectDrop();
      }

   }

   protected int postDropTargetEvent(Component var1, int var2, int var3, int var4, int var5, long[] var6, long var7, int var9, boolean var10) {
      AppContext var11 = SunToolkit.targetToAppContext(var1);
      SunDropTargetContextPeer.EventDispatcher var12 = new SunDropTargetContextPeer.EventDispatcher(this, var4, var5, var6, var7, var10);
      SunDropTargetEvent var13 = new SunDropTargetEvent(var1, var9, var2, var3, var12);
      if (var10) {
         DataTransferer.getInstance().getToolkitThreadBlockedHandler().lock();
      }

      SunToolkit.postEvent(var11, var13);
      this.eventPosted(var13);
      if (!var10) {
         return 0;
      } else {
         while(!var12.isDone()) {
            DataTransferer.getInstance().getToolkitThreadBlockedHandler().enter();
         }

         DataTransferer.getInstance().getToolkitThreadBlockedHandler().unlock();
         return var12.getReturnValue();
      }
   }

   public synchronized void acceptDrag(int var1) {
      if (this.currentDT == null) {
         throw new InvalidDnDOperationException("No Drag pending");
      } else {
         this.currentDA = this.mapOperation(var1);
         if (this.currentDA != 0) {
            this.dragRejected = false;
         }

      }
   }

   public synchronized void rejectDrag() {
      if (this.currentDT == null) {
         throw new InvalidDnDOperationException("No Drag pending");
      } else {
         this.currentDA = 0;
         this.dragRejected = true;
      }
   }

   public synchronized void acceptDrop(int var1) {
      if (var1 == 0) {
         throw new IllegalArgumentException("invalid acceptDrop() action");
      } else if (this.dropStatus != 1 && this.dropStatus != 2) {
         throw new InvalidDnDOperationException("invalid acceptDrop()");
      } else {
         this.currentDA = this.currentA = this.mapOperation(var1 & this.currentSA);
         this.dropStatus = 2;
         this.dropComplete = false;
      }
   }

   public synchronized void rejectDrop() {
      if (this.dropStatus != 1) {
         throw new InvalidDnDOperationException("invalid rejectDrop()");
      } else {
         this.dropStatus = -1;
         this.currentDA = 0;
         this.dropComplete(false);
      }
   }

   private int mapOperation(int var1) {
      int[] var2 = new int[]{2, 1, 1073741824};
      int var3 = 0;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         if ((var1 & var2[var4]) == var2[var4]) {
            var3 = var2[var4];
            break;
         }
      }

      return var3;
   }

   public synchronized void dropComplete(boolean var1) {
      if (this.dropStatus == 0) {
         throw new InvalidDnDOperationException("No Drop pending");
      } else {
         if (this.currentDTC != null) {
            this.currentDTC.removeNotify();
         }

         this.currentDT = null;
         this.currentDTC = null;
         this.currentT = null;
         this.currentA = 0;
         synchronized(_globalLock) {
            currentJVMLocalSourceTransferable = null;
         }

         this.dropStatus = 0;
         this.dropComplete = true;

         try {
            this.doDropDone(var1, this.currentDA, this.local != null);
         } finally {
            this.currentDA = 0;
            this.nativeDragContext = 0L;
         }

      }
   }

   protected abstract void doDropDone(boolean var1, int var2, boolean var3);

   protected synchronized long getNativeDragContext() {
      return this.nativeDragContext;
   }

   protected void eventPosted(SunDropTargetEvent var1) {
   }

   protected void eventProcessed(SunDropTargetEvent var1, int var2, boolean var3) {
   }

   protected static class EventDispatcher {
      private final SunDropTargetContextPeer peer;
      private final int dropAction;
      private final int actions;
      private final long[] formats;
      private long nativeCtxt;
      private final boolean dispatchType;
      private boolean dispatcherDone = false;
      private int returnValue = 0;
      private final HashSet eventSet = new HashSet(3);
      static final ToolkitThreadBlockedHandler handler = DataTransferer.getInstance().getToolkitThreadBlockedHandler();

      EventDispatcher(SunDropTargetContextPeer var1, int var2, int var3, long[] var4, long var5, boolean var7) {
         this.peer = var1;
         this.nativeCtxt = var5;
         this.dropAction = var2;
         this.actions = var3;
         this.formats = null == var4 ? null : Arrays.copyOf(var4, var4.length);
         this.dispatchType = var7;
      }

      void dispatchEvent(SunDropTargetEvent var1) {
         int var2 = var1.getID();
         switch(var2) {
         case 502:
            this.dispatchDropEvent(var1);
            break;
         case 503:
         default:
            throw new InvalidDnDOperationException();
         case 504:
            this.dispatchEnterEvent(var1);
            break;
         case 505:
            this.dispatchExitEvent(var1);
            break;
         case 506:
            this.dispatchMotionEvent(var1);
         }

      }

      private void dispatchEnterEvent(SunDropTargetEvent var1) {
         synchronized(this.peer) {
            this.peer.previousDA = this.dropAction;
            this.peer.nativeDragContext = this.nativeCtxt;
            this.peer.currentT = this.formats;
            this.peer.currentSA = this.actions;
            this.peer.currentDA = this.dropAction;
            this.peer.dropStatus = 2;
            this.peer.dropComplete = false;

            try {
               this.peer.processEnterMessage(var1);
            } finally {
               this.peer.dropStatus = 0;
            }

            this.setReturnValue(this.peer.currentDA);
         }
      }

      private void dispatchMotionEvent(SunDropTargetEvent var1) {
         synchronized(this.peer) {
            boolean var3 = this.peer.previousDA != this.dropAction;
            this.peer.previousDA = this.dropAction;
            this.peer.nativeDragContext = this.nativeCtxt;
            this.peer.currentT = this.formats;
            this.peer.currentSA = this.actions;
            this.peer.currentDA = this.dropAction;
            this.peer.dropStatus = 2;
            this.peer.dropComplete = false;

            try {
               this.peer.processMotionMessage(var1, var3);
            } finally {
               this.peer.dropStatus = 0;
            }

            this.setReturnValue(this.peer.currentDA);
         }
      }

      private void dispatchExitEvent(SunDropTargetEvent var1) {
         synchronized(this.peer) {
            this.peer.nativeDragContext = this.nativeCtxt;
            this.peer.processExitMessage(var1);
         }
      }

      private void dispatchDropEvent(SunDropTargetEvent var1) {
         synchronized(this.peer) {
            this.peer.nativeDragContext = this.nativeCtxt;
            this.peer.currentT = this.formats;
            this.peer.currentSA = this.actions;
            this.peer.currentDA = this.dropAction;
            this.peer.processDropMessage(var1);
         }
      }

      void setReturnValue(int var1) {
         this.returnValue = var1;
      }

      int getReturnValue() {
         return this.returnValue;
      }

      boolean isDone() {
         return this.eventSet.isEmpty();
      }

      void registerEvent(SunDropTargetEvent var1) {
         handler.lock();
         if (!this.eventSet.add(var1) && SunDropTargetContextPeer.dndLog.isLoggable(PlatformLogger.Level.FINE)) {
            SunDropTargetContextPeer.dndLog.fine("Event is already registered: " + var1);
         }

         handler.unlock();
      }

      void unregisterEvent(SunDropTargetEvent var1) {
         handler.lock();

         label128: {
            try {
               if (this.eventSet.remove(var1)) {
                  if (!this.eventSet.isEmpty()) {
                     break label128;
                  }

                  if (!this.dispatcherDone && this.dispatchType) {
                     handler.exit();
                  }

                  this.dispatcherDone = true;
                  break label128;
               }
            } finally {
               handler.unlock();
            }

            return;
         }

         try {
            this.peer.eventProcessed(var1, this.returnValue, this.dispatcherDone);
         } finally {
            if (this.dispatcherDone) {
               this.nativeCtxt = 0L;
               this.peer.nativeDragContext = 0L;
            }

         }

      }

      public void unregisterAllEvents() {
         Object[] var1 = null;
         handler.lock();

         try {
            var1 = this.eventSet.toArray();
         } finally {
            handler.unlock();
         }

         if (var1 != null) {
            for(int var2 = 0; var2 < var1.length; ++var2) {
               this.unregisterEvent((SunDropTargetEvent)var1[var2]);
            }
         }

      }
   }
}
