package com.sun.org.apache.regexp.internal;

public class recompile {
   public static void main(String[] arg) {
      RECompiler r = new RECompiler();
      if (arg.length <= 0 || arg.length % 2 != 0) {
         System.out.println("Usage: recompile <patternname> <pattern>");
         System.exit(0);
      }

      for(int i = 0; i < arg.length; i += 2) {
         try {
            String name = arg[i];
            String pattern = arg[i + 1];
            String instructions = name + "PatternInstructions";
            System.out.print("\n    // Pre-compiled regular expression '" + pattern + "'\n    private static char[] " + instructions + " = \n    {");
            REProgram program = r.compile(pattern);
            int numColumns = 7;
            char[] p = program.getInstructions();

            for(int j = 0; j < p.length; ++j) {
               if (j % numColumns == 0) {
                  System.out.print("\n        ");
               }

               String hex;
               for(hex = Integer.toHexString(p[j]); hex.length() < 4; hex = "0" + hex) {
               }

               System.out.print("0x" + hex + ", ");
            }

            System.out.println("\n    };");
            System.out.println("\n    private static RE " + name + "Pattern = new RE(new REProgram(" + instructions + "));");
         } catch (RESyntaxException var11) {
            System.out.println("Syntax error in expression \"" + arg[i] + "\": " + var11.toString());
         } catch (Exception var12) {
            System.out.println("Unexpected exception: " + var12.toString());
         } catch (Error var13) {
            System.out.println("Internal error: " + var13.toString());
         }
      }

   }
}
