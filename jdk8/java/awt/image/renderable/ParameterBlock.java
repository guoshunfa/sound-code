package java.awt.image.renderable;

import java.awt.image.RenderedImage;
import java.io.Serializable;
import java.util.Vector;

public class ParameterBlock implements Cloneable, Serializable {
   protected Vector<Object> sources = new Vector();
   protected Vector<Object> parameters = new Vector();

   public ParameterBlock() {
   }

   public ParameterBlock(Vector<Object> var1) {
      this.setSources(var1);
   }

   public ParameterBlock(Vector<Object> var1, Vector<Object> var2) {
      this.setSources(var1);
      this.setParameters(var2);
   }

   public Object shallowClone() {
      try {
         return super.clone();
      } catch (Exception var2) {
         return null;
      }
   }

   public Object clone() {
      ParameterBlock var1;
      try {
         var1 = (ParameterBlock)super.clone();
      } catch (Exception var3) {
         return null;
      }

      if (this.sources != null) {
         var1.setSources((Vector)this.sources.clone());
      }

      if (this.parameters != null) {
         var1.setParameters((Vector)this.parameters.clone());
      }

      return var1;
   }

   public ParameterBlock addSource(Object var1) {
      this.sources.addElement(var1);
      return this;
   }

   public Object getSource(int var1) {
      return this.sources.elementAt(var1);
   }

   public ParameterBlock setSource(Object var1, int var2) {
      int var3 = this.sources.size();
      int var4 = var2 + 1;
      if (var3 < var4) {
         this.sources.setSize(var4);
      }

      this.sources.setElementAt(var1, var2);
      return this;
   }

   public RenderedImage getRenderedSource(int var1) {
      return (RenderedImage)this.sources.elementAt(var1);
   }

   public RenderableImage getRenderableSource(int var1) {
      return (RenderableImage)this.sources.elementAt(var1);
   }

   public int getNumSources() {
      return this.sources.size();
   }

   public Vector<Object> getSources() {
      return this.sources;
   }

   public void setSources(Vector<Object> var1) {
      this.sources = var1;
   }

   public void removeSources() {
      this.sources = new Vector();
   }

   public int getNumParameters() {
      return this.parameters.size();
   }

   public Vector<Object> getParameters() {
      return this.parameters;
   }

   public void setParameters(Vector<Object> var1) {
      this.parameters = var1;
   }

   public void removeParameters() {
      this.parameters = new Vector();
   }

   public ParameterBlock add(Object var1) {
      this.parameters.addElement(var1);
      return this;
   }

   public ParameterBlock add(byte var1) {
      return this.add(new Byte(var1));
   }

   public ParameterBlock add(char var1) {
      return this.add(new Character(var1));
   }

   public ParameterBlock add(short var1) {
      return this.add(new Short(var1));
   }

   public ParameterBlock add(int var1) {
      return this.add(new Integer(var1));
   }

   public ParameterBlock add(long var1) {
      return this.add(new Long(var1));
   }

   public ParameterBlock add(float var1) {
      return this.add(new Float(var1));
   }

   public ParameterBlock add(double var1) {
      return this.add(new Double(var1));
   }

   public ParameterBlock set(Object var1, int var2) {
      int var3 = this.parameters.size();
      int var4 = var2 + 1;
      if (var3 < var4) {
         this.parameters.setSize(var4);
      }

      this.parameters.setElementAt(var1, var2);
      return this;
   }

   public ParameterBlock set(byte var1, int var2) {
      return this.set(new Byte(var1), var2);
   }

   public ParameterBlock set(char var1, int var2) {
      return this.set(new Character(var1), var2);
   }

   public ParameterBlock set(short var1, int var2) {
      return this.set(new Short(var1), var2);
   }

   public ParameterBlock set(int var1, int var2) {
      return this.set(new Integer(var1), var2);
   }

   public ParameterBlock set(long var1, int var3) {
      return this.set(new Long(var1), var3);
   }

   public ParameterBlock set(float var1, int var2) {
      return this.set(new Float(var1), var2);
   }

   public ParameterBlock set(double var1, int var3) {
      return this.set(new Double(var1), var3);
   }

   public Object getObjectParameter(int var1) {
      return this.parameters.elementAt(var1);
   }

   public byte getByteParameter(int var1) {
      return (Byte)this.parameters.elementAt(var1);
   }

   public char getCharParameter(int var1) {
      return (Character)this.parameters.elementAt(var1);
   }

   public short getShortParameter(int var1) {
      return (Short)this.parameters.elementAt(var1);
   }

   public int getIntParameter(int var1) {
      return (Integer)this.parameters.elementAt(var1);
   }

   public long getLongParameter(int var1) {
      return (Long)this.parameters.elementAt(var1);
   }

   public float getFloatParameter(int var1) {
      return (Float)this.parameters.elementAt(var1);
   }

   public double getDoubleParameter(int var1) {
      return (Double)this.parameters.elementAt(var1);
   }

   public Class[] getParamClasses() {
      int var1 = this.getNumParameters();
      Class[] var2 = new Class[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         Object var4 = this.getObjectParameter(var3);
         if (var4 instanceof Byte) {
            var2[var3] = Byte.TYPE;
         } else if (var4 instanceof Character) {
            var2[var3] = Character.TYPE;
         } else if (var4 instanceof Short) {
            var2[var3] = Short.TYPE;
         } else if (var4 instanceof Integer) {
            var2[var3] = Integer.TYPE;
         } else if (var4 instanceof Long) {
            var2[var3] = Long.TYPE;
         } else if (var4 instanceof Float) {
            var2[var3] = Float.TYPE;
         } else if (var4 instanceof Double) {
            var2[var3] = Double.TYPE;
         } else {
            var2[var3] = var4.getClass();
         }
      }

      return var2;
   }
}
