/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.project;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author matthew
 */
public class TestProject {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws IOException {
		
//		Path start = Paths.get("/lsc/liferay-portal-ee/");
//		
//		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
//			
//			@Override
//			public FileVisitResult visitFile(
//				Path file, BasicFileAttributes attr) throws IOException {
//				
//				String path = file.toString();
//				
//				String name = file.getFileName().toString();
//				name = name.replace(".java", "");
//				
//				if (!(file.toString().endsWith(".java") && name.endsWith("Test"))){
//					return CONTINUE;
//				}
//
//				String content = readFile(path, Charset.defaultCharset());
//				
//				if (!content.contains("import com.liferay.portal.test.MainServletExecutionTestListener")) {
//					return CONTINUE;
//				}
//				
//				System.out.println("." + path);
//				
//				String newContent = content.replace("import com.liferay.portal.test.MainServletExecutionTestListener;", "import com.liferay.portal.test.MainServletTestRule;\nimport org.junit.ClassRule;");
//				newContent = newContent.replace("\t\tMainServletExecutionTestListener.class,\n", "");
//				newContent = newContent.replace("MainServletExecutionTestListener.class", "");
//				newContent = newContent.replace("@ExecutionTestListeners(listeners = {})\n", "");
//				
//				int nameIndex = newContent.indexOf(name);
//				int insertIndex = newContent.indexOf("{", nameIndex);
//				newContent = newContent.substring(0, insertIndex+1) +  "\n\n\t@ClassRule\n\tpublic static final MainServletTestRule mainServletTestRule =\n\t\tnew MainServletTestRule();\n" + newContent.substring(insertIndex+2);
//				
////				System.out.println(newContent);
//				
//				try (Writer writer =
//					new BufferedWriter(
//						new OutputStreamWriter(
//							new FileOutputStream(path), "utf-8"))){
//					
//					writer.write(newContent);
//				}
//				
//				return CONTINUE;
//		}
//			 
//			@Override
//			public FileVisitResult visitFileFailed(
//				Path file, IOException exc) {
//			System.err.println(exc);
//			return CONTINUE;
//			}
//			
//		});
//		
////		boolean test = false;
////		System.out.println((test && (false || test)));
//	}
//	
//		public static String readFile(String path, Charset encoding) 
//			throws IOException {
//
//			byte[] encoded = Files.readAllBytes(Paths.get(path));
//			return new String(encoded, encoding);
//		}
		
//		Pattern pattern = Pattern.compile("\\w{3}\\d{3}(?R)?");
//		
//		String content = "aaa111bbb222";
//		
//		Matcher matcher = pattern.matcher(content);
//		
//		while (matcher.find()) {
//			System.out.print("Found: ");
//			System.out.println(matcher.group());
//		}
		
//		int[] number = new int[1];
//		Class cls = null;
//		try {
//			cls = Class.forName("int");
//		}
//		catch (Exception e) {
//			System.out.println("Something went wrong: "+ e.getCause());
//		}
//			System.out.println(char.class.getName());
//			System.out.println();
//		
//		String hi = "hi";
//		String hiProxy = (String)TracingIH.createProxy(hi);
//		
//		System.out.println(hiProxy.toString());
//		
//		boolean test = true;
//		
//		assert (test || false):"This is printing";
//
//		System.out.println("Good");
//		
//		ClassLoader cl = ClassLoader.getSystemClassLoader();
		
//		try {
//			cls = cl.loadClass("java.lang.String");
//			System.out.println((cls.newInstance()).class.getName());
//		}
//		catch (Exception e) {
//		}
		
//		try {
//			Counter.test();
//		}
//		catch (Exception e) {
//			
//		}
		
//		Map<String, String> map = new HashMap<>();
//		String key = "key";
//		String value = "value1";
//		map.put(key, value);
//		value = map.get(key);
//		System.out.println("Value");
//		value = "value2";
//		System.out.println(map.get(key));
//
//		try {
//			Class clz = Class.forName("[Ljava.lang.String;");
//			System.out.println("Class: " + clz.getName());
//		}
//		catch (ClassNotFoundException cnfe) {
//			System.out.println("Uh-oh");
//		}
//		
//		System.out.println(MyCounter.class.getName());
//		System.out.println(MyCounter.class.getCanonicalName());
//		System.out.println(MyCounter.class.getSimpleName());
		
		byte[] data = {74, 97, 118, 97, 32, 68, 101, 98, 117, 103, 32, 87, 105, 114, 101, 32, 80, 114, 111, 116, 111, 99, 111, 108, 32, 40, 82, 101, 102, 101, 114, 101, 110, 99, 101, 32, 73, 109, 112, 108, 101, 109, 101, 110, 116, 97, 116, 105, 111, 110, 41, 32, 118, 101, 114, 115, 105, 111, 110, 32, 49, 46, 54, 10, 74, 86, 77, 32, 68, 101, 98, 117, 103, 32, 73, 110, 116, 101, 114, 102, 97, 99, 101, 32, 118, 101, 114, 115, 105, 111, 110, 32, 49, 46, 50, 10, 74, 86, 77, 32, 118, 101, 114, 115, 105, 111, 110, 32, 49, 46, 55, 46, 48, 95, 54, 48, 32, 40, 74, 97, 118, 97, 32, 72, 111, 116, 83, 112, 111, 116, 40, 84, 77, 41, 32, 54, 52, 45, 66, 105, 116, 32, 83, 101, 114, 118, 101, 114, 32, 86, 77, 44, 32, 109, 105, 120, 101, 100, 32, 109, 111, 100, 101, 44, 32, 115, 104, 97, 114, 105, 110, 103, 41, 0, 0, 0, 1, 0, 0, 0, 6, 0, 0, 0, 8, 49, 46, 55, 46, 48, 95, 54, 48, 0, 0, 0, 33, 74, 97, 118, 97, 32, 72, 111, 116, 83, 112, 111, 116, 40, 84, 77, 41, 32, 54, 52, 45, 66, 105, 116, 32, 83, 101, 114, 118, 101, 114, 32, 86, 77};
		
		byte[] convert = new byte[173];

		byte[] stuff = {74, 68, 87, 80, 45, 72, 97, 110, 100, 115, 104, 97, 107, 101};

		for (int i = 0;i<173;i++) {
			convert[i] = data[i];
		}
		System.out.println(new String(stuff));
		
//		byte[] sized = {0, 0, 0, -11, 0, 0, 22, -84, -128, 0, 0, 0, 0, 0, -83, 74, 97, 118, 97, 32, 68, 101, 98, 117, 103, 32, 87, 105, 114, 101, 32, 80, 114, 111, 116, 111, 99, 111, 108, 32, 40, 82, 101, 102, 101, 114, 101, 110, 99, 101, 32, 73, 109, 112, 108, 101, 109, 101, 110, 116, 97, 116, 105, 111, 110, 41, 32, 118, 101, 114, 115, 105, 111, 110, 32, 49, 46, 54, 10, 74, 86, 77, 32, 68, 101, 98, 117, 103, 32, 73, 110, 116, 101, 114, 102, 97, 99, 101, 32, 118, 101, 114, 115, 105, 111, 110, 32, 49, 46, 50, 10, 74, 86, 77, 32, 118, 101, 114, 115, 105, 111, 110, 32, 49, 46, 55, 46, 48, 95, 54, 48, 32, 40, 74, 97, 118, 97, 32, 72, 111, 116, 83, 112, 111, 116, 40, 84, 77, 41, 32, 54, 52, 45, 66, 105, 116, 32, 83, 101, 114, 118, 101, 114, 32, 86, 77, 44, 32, 109, 105, 120, 101, 100, 32, 109, 111, 100, 101, 44, 32, 115, 104, 97, 114, 105, 110, 103, 41, 0, 0, 0, 1, 0, 0, 0, 6, 0, 0, 0, 8, 49, 46, 55, 46, 48, 95, 54, 48, 0, 0, 0, 33, 74, 97, 118, 97, 32, 72, 111, 116, 83, 112, 111, 116, 40, 84, 77, 41, 32, 54, 52, 45, 66, 105, 116, 32, 83, 101, 114, 118, 101, 114, 32, 86, 77};
//		System.out.println("Size: "+sized.length);
		
		byte[] test = {0, 2};
		
		Set<byte[]> set = new HashSet<>();
		
		set.add(test);
		byte[] test2 = {0, 2};
		System.out.println(set.contains(test2));
		
		List<ByteBuffer> testset = new ArrayList<>();
	
		byte[] key = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8};

		short s = 0x80;
		
		System.out.println(Arrays.toString(Arrays.copyOfRange(key, 4, 8)));
	}
	
}
