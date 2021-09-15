package javax.swing.plaf.nimbus;

import java.awt.Color;

abstract class ShadowEffect extends Effect {
   protected Color color;
   protected float opacity;
   protected int angle;
   protected int distance;
   protected int spread;
   protected int size;

   ShadowEffect() {
      this.color = Color.BLACK;
      this.opacity = 0.75F;
      this.angle = 135;
      this.distance = 5;
      this.spread = 0;
      this.size = 5;
   }

   Color getColor() {
      return this.color;
   }

   void setColor(Color var1) {
      Color var2 = this.getColor();
      this.color = var1;
   }

   float getOpacity() {
      return this.opacity;
   }

   void setOpacity(float var1) {
      float var2 = this.getOpacity();
      this.opacity = var1;
   }

   int getAngle() {
      return this.angle;
   }

   void setAngle(int var1) {
      int var2 = this.getAngle();
      this.angle = var1;
   }

   int getDistance() {
      return this.distance;
   }

   void setDistance(int var1) {
      int var2 = this.getDistance();
      this.distance = var1;
   }

   int getSpread() {
      return this.spread;
   }

   void setSpread(int var1) {
      int var2 = this.getSpread();
      this.spread = var1;
   }

   int getSize() {
      return this.size;
   }

   void setSize(int var1) {
      int var2 = this.getSize();
      this.size = var1;
   }
}
