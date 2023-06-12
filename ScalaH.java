import java.io.*;
import java.util.StringTokenizer;
import java.util.Optional;
import java.util.spi.ToolProvider;

public class ScalaH {
	/*
	   Takes as input "public native void foo(int,java.util.Vector);"
	   and returns "public native void foo(int p1,java.util.Vector p2);"
	   */
	private static String addParameterNames(String fun) {
		int b = fun.indexOf('(');
		int e = fun.indexOf(')');
		StringTokenizer st = new StringTokenizer(fun.substring(b+1, e), ",");
		String res = "";
		int pn = 1;
		while(st.hasMoreTokens()) {
			res += st.nextToken() + " p" + pn;
			if (st.hasMoreTokens())
				res += ", ";
			pn++;
		}
		return fun.substring(0, b+1) + res + ");";
	}

	public static void main(String... args)  throws FileNotFoundException, IOException {
		if (args.length != 1) {
			System.out.println("Usage: java ScalaH className");
			System.exit(1);	
		} 
		Optional<ToolProvider> javap = ToolProvider.findFirst("javap");
		if (!javap.isPresent()) {
			System.err.println("javap is not present");
			System.exit(1);
		}
		ByteArrayOutputStream baus = new ByteArrayOutputStream(); 
		javap.get().run(
				//new PrintStream(new FileOutputStream("T.java")), 
				new PrintStream(baus),
				System.err,
				args[0]
			       );
		//System.out.println(new String(baus.toByteArray()));
		String javaSource = new String(baus.toByteArray());
		String[] lines = javaSource.split("\n");
		String javaFile = "";
		String className = "";
		for (String line:lines) {
			if (line.contains(" class ")) {
				javaFile += line.trim() + "\n";
				int idx = line.indexOf(" class ") + 7;
				className = line.substring(idx, line.indexOf(" ", idx)).trim();
			}
			if (line.contains(" native "))
				javaFile += addParameterNames(line) + "\n";
		}
		javaFile += "}";
                FileWriter fw = new FileWriter(className + ".java");
		fw.write(javaFile);	
		fw.close();
		//javac -h c_headers -cp .;scala-library.jar example.java
		Optional<ToolProvider> javac = ToolProvider.findFirst("javac");
		if (javac.isPresent()) {
			javac.get().run(
					System.out,
					System.err,
					"-h",
					".",
					"-cp",
					".",
					className + ".java");
			System.out.println("Please, find your header, " + className + ".h, in the current directory");
			new java.io.File(className + ".java").delete();	
		}
	}
}
