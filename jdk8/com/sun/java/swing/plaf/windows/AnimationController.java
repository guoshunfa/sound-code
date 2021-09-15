package com.sun.java.swing.plaf.windows;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import sun.awt.AppContext;
import sun.security.action.GetBooleanAction;
import sun.swing.UIClientPropertyKey;

class AnimationController implements ActionListener, PropertyChangeListener {
   private static final boolean VISTA_ANIMATION_DISABLED = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("swing.disablevistaanimation")));
   private static final Object ANIMATION_CONTROLLER_KEY = new StringBuilder("ANIMATION_CONTROLLER_KEY");
   private final Map<JComponent, Map<TMSchema.Part, AnimationController.AnimationState>> animationStateMap = new WeakHashMap();
   private final Timer timer = new Timer(33, this);

   private static synchronized AnimationController getAnimationController() {
      AppContext var0 = AppContext.getAppContext();
      Object var1 = var0.get(ANIMATION_CONTROLLER_KEY);
      if (var1 == null) {
         var1 = new AnimationController();
         var0.put(ANIMATION_CONTROLLER_KEY, var1);
      }

      return (AnimationController)var1;
   }

   private AnimationController() {
      this.timer.setRepeats(true);
      this.timer.setCoalesce(true);
      UIManager.addPropertyChangeListener(this);
   }

   private static void triggerAnimation(JComponent var0, TMSchema.Part var1, TMSchema.State var2) {
      if (!(var0 instanceof JTabbedPane) && var1 != TMSchema.Part.TP_BUTTON) {
         AnimationController var3 = getAnimationController();
         TMSchema.State var4 = var3.getState(var0, var1);
         if (var4 != var2) {
            var3.putState(var0, var1, var2);
            if (var2 == TMSchema.State.DEFAULTED) {
               var4 = TMSchema.State.HOT;
            }

            if (var4 != null) {
               long var5;
               if (var2 == TMSchema.State.DEFAULTED) {
                  var5 = 1000L;
               } else {
                  XPStyle var7 = XPStyle.getXP();
                  var5 = var7 != null ? var7.getThemeTransitionDuration(var0, var1, normalizeState(var4), normalizeState(var2), TMSchema.Prop.TRANSITIONDURATIONS) : 1000L;
               }

               var3.startAnimation(var0, var1, var4, var2, var5);
            }
         }

      }
   }

   private static TMSchema.State normalizeState(TMSchema.State var0) {
      TMSchema.State var1;
      switch(var0) {
      case DOWNPRESSED:
      case LEFTPRESSED:
      case RIGHTPRESSED:
         var1 = TMSchema.State.UPPRESSED;
         break;
      case DOWNDISABLED:
      case LEFTDISABLED:
      case RIGHTDISABLED:
         var1 = TMSchema.State.UPDISABLED;
         break;
      case DOWNHOT:
      case LEFTHOT:
      case RIGHTHOT:
         var1 = TMSchema.State.UPHOT;
         break;
      case DOWNNORMAL:
      case LEFTNORMAL:
      case RIGHTNORMAL:
         var1 = TMSchema.State.UPNORMAL;
         break;
      default:
         var1 = var0;
      }

      return var1;
   }

   private synchronized TMSchema.State getState(JComponent var1, TMSchema.Part var2) {
      TMSchema.State var3 = null;
      Object var4 = var1.getClientProperty(AnimationController.PartUIClientPropertyKey.getKey(var2));
      if (var4 instanceof TMSchema.State) {
         var3 = (TMSchema.State)var4;
      }

      return var3;
   }

   private synchronized void putState(JComponent var1, TMSchema.Part var2, TMSchema.State var3) {
      var1.putClientProperty(AnimationController.PartUIClientPropertyKey.getKey(var2), var3);
   }

   private synchronized void startAnimation(JComponent var1, TMSchema.Part var2, TMSchema.State var3, TMSchema.State var4, long var5) {
      boolean var7 = false;
      if (var4 == TMSchema.State.DEFAULTED) {
         var7 = true;
      }

      Object var8 = (Map)this.animationStateMap.get(var1);
      if (var5 <= 0L) {
         if (var8 != null) {
            ((Map)var8).remove(var2);
            if (((Map)var8).size() == 0) {
               this.animationStateMap.remove(var1);
            }
         }

      } else {
         if (var8 == null) {
            var8 = new EnumMap(TMSchema.Part.class);
            this.animationStateMap.put(var1, var8);
         }

         ((Map)var8).put(var2, new AnimationController.AnimationState(var3, var5, var7));
         if (!this.timer.isRunning()) {
            this.timer.start();
         }

      }
   }

   static void paintSkin(JComponent var0, XPStyle.Skin var1, Graphics var2, int var3, int var4, int var5, int var6, TMSchema.State var7) {
      if (VISTA_ANIMATION_DISABLED) {
         var1.paintSkinRaw(var2, var3, var4, var5, var6, var7);
      } else {
         triggerAnimation(var0, var1.part, var7);
         AnimationController var8 = getAnimationController();
         synchronized(var8) {
            AnimationController.AnimationState var10 = null;
            Map var11 = (Map)var8.animationStateMap.get(var0);
            if (var11 != null) {
               var10 = (AnimationController.AnimationState)var11.get(var1.part);
            }

            if (var10 != null) {
               var10.paintSkin(var1, var2, var3, var4, var5, var6, var7);
            } else {
               var1.paintSkinRaw(var2, var3, var4, var5, var6, var7);
            }

         }
      }
   }

   public synchronized void propertyChange(PropertyChangeEvent var1) {
      if ("lookAndFeel" == var1.getPropertyName() && !(var1.getNewValue() instanceof WindowsLookAndFeel)) {
         this.dispose();
      }

   }

   public synchronized void actionPerformed(ActionEvent var1) {
      ArrayList var2 = null;
      ArrayList var3 = null;
      Iterator var4 = this.animationStateMap.keySet().iterator();

      JComponent var5;
      while(var4.hasNext()) {
         var5 = (JComponent)var4.next();
         var5.repaint();
         if (var3 != null) {
            var3.clear();
         }

         Map var6 = (Map)this.animationStateMap.get(var5);
         if (var5.isShowing() && var6 != null && var6.size() != 0) {
            Iterator var7 = var6.keySet().iterator();

            TMSchema.Part var8;
            while(var7.hasNext()) {
               var8 = (TMSchema.Part)var7.next();
               if (((AnimationController.AnimationState)var6.get(var8)).isDone()) {
                  if (var3 == null) {
                     var3 = new ArrayList();
                  }

                  var3.add(var8);
               }
            }

            if (var3 != null) {
               if (var3.size() == var6.size()) {
                  if (var2 == null) {
                     var2 = new ArrayList();
                  }

                  var2.add(var5);
               } else {
                  var7 = var3.iterator();

                  while(var7.hasNext()) {
                     var8 = (TMSchema.Part)var7.next();
                     var6.remove(var8);
                  }
               }
            }
         } else {
            if (var2 == null) {
               var2 = new ArrayList();
            }

            var2.add(var5);
         }
      }

      if (var2 != null) {
         var4 = var2.iterator();

         while(var4.hasNext()) {
            var5 = (JComponent)var4.next();
            this.animationStateMap.remove(var5);
         }
      }

      if (this.animationStateMap.size() == 0) {
         this.timer.stop();
      }

   }

   private synchronized void dispose() {
      this.timer.stop();
      UIManager.removePropertyChangeListener(this);
      Class var1 = AnimationController.class;
      synchronized(AnimationController.class) {
         AppContext.getAppContext().put(ANIMATION_CONTROLLER_KEY, (Object)null);
      }
   }

   private static class PartUIClientPropertyKey implements UIClientPropertyKey {
      private static final Map<TMSchema.Part, AnimationController.PartUIClientPropertyKey> map = new EnumMap(TMSchema.Part.class);
      private final TMSchema.Part part;

      static synchronized AnimationController.PartUIClientPropertyKey getKey(TMSchema.Part var0) {
         AnimationController.PartUIClientPropertyKey var1 = (AnimationController.PartUIClientPropertyKey)map.get(var0);
         if (var1 == null) {
            var1 = new AnimationController.PartUIClientPropertyKey(var0);
            map.put(var0, var1);
         }

         return var1;
      }

      private PartUIClientPropertyKey(TMSchema.Part var1) {
         this.part = var1;
      }

      public String toString() {
         return this.part.toString();
      }
   }

   private static class AnimationState {
      private final TMSchema.State startState;
      private final long duration;
      private long startTime;
      private boolean isForward = true;
      private boolean isForwardAndReverse;
      private float progress;

      AnimationState(TMSchema.State var1, long var2, boolean var4) {
         assert var1 != null && var2 > 0L;

         assert SwingUtilities.isEventDispatchThread();

         this.startState = var1;
         this.duration = var2 * 1000000L;
         this.startTime = System.nanoTime();
         this.isForwardAndReverse = var4;
         this.progress = 0.0F;
      }

      private void updateProgress() {
         assert SwingUtilities.isEventDispatchThread();

         if (!this.isDone()) {
            long var1 = System.nanoTime();
            this.progress = (float)(var1 - this.startTime) / (float)this.duration;
            this.progress = Math.max(this.progress, 0.0F);
            if (this.progress >= 1.0F) {
               this.progress = 1.0F;
               if (this.isForwardAndReverse) {
                  this.startTime = var1;
                  this.progress = 0.0F;
                  this.isForward = !this.isForward;
               }
            }

         }
      }

      void paintSkin(XPStyle.Skin var1, Graphics var2, int var3, int var4, int var5, int var6, TMSchema.State var7) {
         assert SwingUtilities.isEventDispatchThread();

         this.updateProgress();
         if (!this.isDone()) {
            Graphics2D var8 = (Graphics2D)var2.create();
            var1.paintSkinRaw(var8, var3, var4, var5, var6, this.startState);
            float var9;
            if (this.isForward) {
               var9 = this.progress;
            } else {
               var9 = 1.0F - this.progress;
            }

            var8.setComposite(AlphaComposite.SrcOver.derive(var9));
            var1.paintSkinRaw(var8, var3, var4, var5, var6, var7);
            var8.dispose();
         } else {
            var1.paintSkinRaw(var2, var3, var4, var5, var6, var7);
         }

      }

      boolean isDone() {
         assert SwingUtilities.isEventDispatchThread();

         return this.progress >= 1.0F;
      }
   }
}
