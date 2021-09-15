package javax.swing.plaf.nimbus;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;

public abstract class State<T extends JComponent> {
   static final Map<String, State.StandardState> standardStates = new HashMap(7);
   static final State Enabled = new State.StandardState(1);
   static final State MouseOver = new State.StandardState(2);
   static final State Pressed = new State.StandardState(4);
   static final State Disabled = new State.StandardState(8);
   static final State Focused = new State.StandardState(256);
   static final State Selected = new State.StandardState(512);
   static final State Default = new State.StandardState(1024);
   private String name;

   protected State(String var1) {
      this.name = var1;
   }

   public String toString() {
      return this.name;
   }

   boolean isInState(T var1, int var2) {
      return this.isInState(var1);
   }

   protected abstract boolean isInState(T var1);

   String getName() {
      return this.name;
   }

   static boolean isStandardStateName(String var0) {
      return standardStates.containsKey(var0);
   }

   static State.StandardState getStandardState(String var0) {
      return (State.StandardState)standardStates.get(var0);
   }

   static final class StandardState extends State<JComponent> {
      private int state;

      private StandardState(int var1) {
         super(toString(var1));
         this.state = var1;
         standardStates.put(this.getName(), this);
      }

      public int getState() {
         return this.state;
      }

      boolean isInState(JComponent var1, int var2) {
         return (var2 & this.state) == this.state;
      }

      protected boolean isInState(JComponent var1) {
         throw new AssertionError("This method should never be called");
      }

      private static String toString(int var0) {
         StringBuffer var1 = new StringBuffer();
         if ((var0 & 1024) == 1024) {
            var1.append("Default");
         }

         if ((var0 & 8) == 8) {
            if (var1.length() > 0) {
               var1.append("+");
            }

            var1.append("Disabled");
         }

         if ((var0 & 1) == 1) {
            if (var1.length() > 0) {
               var1.append("+");
            }

            var1.append("Enabled");
         }

         if ((var0 & 256) == 256) {
            if (var1.length() > 0) {
               var1.append("+");
            }

            var1.append("Focused");
         }

         if ((var0 & 2) == 2) {
            if (var1.length() > 0) {
               var1.append("+");
            }

            var1.append("MouseOver");
         }

         if ((var0 & 4) == 4) {
            if (var1.length() > 0) {
               var1.append("+");
            }

            var1.append("Pressed");
         }

         if ((var0 & 512) == 512) {
            if (var1.length() > 0) {
               var1.append("+");
            }

            var1.append("Selected");
         }

         return var1.toString();
      }

      // $FF: synthetic method
      StandardState(int var1, Object var2) {
         this(var1);
      }
   }
}
