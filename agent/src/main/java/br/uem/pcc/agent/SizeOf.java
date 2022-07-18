package br.uem.pcc.agent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.openjdk.jol.info.ClassLayout;

/**
 * Calculate the size of an object, array, or primitive type
 * @author Luiz Carvalho <lmcarvalho@inf.puc-rio.br>
 *
 */
public class SizeOf {
	
	public static File sizeOfLog;
	
	public static File featureInfo;
	
	private static String featureInfoContent = "";
	
	public static long deep = 0l;

	public synchronized static void addFeatureInLog() {
		try (BufferedReader reader = new BufferedReader(new FileReader(featureInfo))) {
			String currentContent = "";
			boolean start = true;
			String line = reader.readLine();
			while (line != null) {
				if (!start) {
					currentContent += "#";
				} else {
					start = false;
				}
				currentContent += line;
				line = reader.readLine();
			}
			if (!featureInfoContent.equals(currentContent)) {
				String toLog = "SF:" + currentContent;
				featureInfoContent = currentContent;
				try(BufferedWriter writer = new BufferedWriter(new FileWriter(sizeOfLog, true));
						PrintWriter out = new PrintWriter(writer)) {
					out.println(toLog);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public synchronized static void decreaseDeep() {
		deep = deep - 1l;
	}
	
	public synchronized static void saveSizeOfLog(String fromClassName, String fromMethodName, long sizeOf) {
		++deep;
		String log = "Class:" + fromClassName + "#" + "Method:" + fromMethodName + 
				"#" + "SizeOf:" + sizeOf + '#' + "Deep:" + deep + '#' + "Thread:" + Thread.currentThread().getName();
		addFeatureInLog();
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(sizeOfLog, true));
				PrintWriter out = new PrintWriter(writer)) {
			out.println(log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Receive size (in bytes) and convert to megabyte
	 * @param bytes bytes
	 * @return megabyte
	 */
	public synchronized static double convertBytesToMegaByte(long bytes) {
		return ( (double)bytes ) / ( (double)1000000 );
	}
	
	/**
	 * Calculate an array size
	 * @param o array
	 * @return size in bytes
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public synchronized static long sizeOfArray(Object o, int deepRecursion) throws IllegalArgumentException, IllegalAccessException {
		Class<?> _class = o.getClass();
		Class<?> componentType = _class.getComponentType();
		
		int length = Array.getLength(o);
		if (length == 0) {
			return ClassLayout.parseInstance(o).instanceSize();
		}
	
		long size = length;
		final int firstElementIndex = 0;
		Object firstElement = null;
		while (componentType.isArray()) {
			firstElement = Array.get(o, firstElementIndex);
			if (firstElement == null) {
				break;
			}
			size *= Array.getLength(firstElement);
			o = firstElement;
			componentType = componentType.getComponentType();
		}
		firstElement = Array.get(o, firstElementIndex);
		long elementSize = 0;
		if (isPrimitive(firstElement)) {
			//elementSize = ClassLayout.parseInstance(firstElement).instanceSize();
			elementSize = sizeOfPrimtive(firstElement);
		} else {
			if (firstElement == null) {
				elementSize = 0;
			} else {
				elementSize = sizeOf(firstElement, deepRecursion);
			}
			/**
			 * 				System.out.println(o.toString());
				System.out.println(o.getClass());
				System.out.println("NULL");
				System.out.println(length);
			 */
		}
		return size * elementSize;
	}
	
	/**
	 * Calculate an primitive size
	 * @param o e.g: Character or Number
	 * @return size
	 */
	private synchronized static long sizeOfPrimtive(Object o) {
		long sum = 0;
		if (o instanceof Character) {
			Character c = (Character) o;
			sum = ClassLayout.parseInstance(c.charValue()).instanceSize();
		} else if (o instanceof Byte) {
			Byte n = (Byte) o;
			sum = ClassLayout.parseInstance(n.byteValue()).instanceSize();
		} else if (o instanceof Short) {
			Short n = (Short) o;
			sum = ClassLayout.parseInstance(n.shortValue()).instanceSize();
		} else if (o instanceof Integer) {
			Integer n = (Integer) o;
			sum = ClassLayout.parseInstance(n.intValue()).instanceSize();
		} else if (o instanceof Long) {
			Long n = (Long) o;
			sum = ClassLayout.parseInstance(n.longValue()).instanceSize();
		} else if (o instanceof Float) {
			Float n = (Float) o;
			sum = ClassLayout.parseInstance(n.floatValue()).instanceSize();
		} else if (o instanceof Double) {
			Double n = (Double) o;
			sum = ClassLayout.parseInstance(n.doubleValue()).instanceSize();
		} else if (o instanceof Boolean) { 
			Boolean n = (Boolean) o;
			sum = ClassLayout.parseInstance(n.booleanValue()).instanceSize();
		} else {
			sum = ClassLayout.parseInstance(new Double(Double.MAX_VALUE)).instanceSize() * 10;
		}
		return sum;
	}
	
	/**
	 * Verify a primitive type
	 * @param o object
	 * @return whether o is primitive type return true. Otherwise, return false.
	 */
	private synchronized static boolean isPrimitive(Object o) {
		return o instanceof Character || o instanceof Number ||
				o instanceof Boolean;
	}
	
	/**
	 * Calculate an object size
	 * @param o object
	 * @return size in bytes
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public synchronized static long sizeOf(Object o) {
		//System.out.println(o);
		try {
			return sizeOf(o, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static Map<Integer, Boolean> mObjects = new HashMap<Integer, Boolean>();
	
	
	public synchronized static boolean isIgnoredClass(String name) {
		if (name.startsWith("org.glassfish")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Calculate an object size
	 * @param o object
	 * @return size in bytes
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public synchronized static long sizeOf(Object o, int deepRecursion) throws IllegalArgumentException, IllegalAccessException {
		if (o == null) {
			return 0;
		}
		if (o.getClass() != null) {
			if (isIgnoredClass(o.getClass().getName())) {
				return 0;
			}
		}
		int hashCode = System.identityHashCode(o);
		//System.out.println(o.getClass().getName());
		//System.out.println(hashCode);
		if (deepRecursion == 1) {
			mObjects.clear();
		}
		if (mObjects.containsKey(new Integer(hashCode))) {
			return 0;
		} else {
			mObjects.put(new Integer(hashCode), true);
		}
		//System.out.println("DEEP>>>>>>>>>>>>>>>>>>>>>>>>>>  "+ deepRecursion);
		long sum = 0;
		Class<?> _class = o.getClass();
		if (isPrimitive(o)) {
			sum = sizeOfPrimtive(o);
		} else if (_class.isArray()) {
			sum = sizeOfArray(o, deepRecursion);
		} else {
			Field[] fields = _class.getDeclaredFields();
			Method[] methods = _class.getMethods();
			/**
			for (Method m: methods) {
				System.out.println(m.toString());
			}
			**/
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(o); 
				if (value != null) {
					if (field.getType().isPrimitive()) {
						sum += ClassLayout.parseInstance(value).instanceSize();
					} else {
						sum += sizeOf(value, deepRecursion + 1);
					}
				}
			}
		}
		return sum;
	}
	
	public synchronized static long sizeOf(int pt) {
		return sizeOfPrimtive(new Integer(pt));
	}
	
	public synchronized static long sizeOf(long pt) {
		return sizeOfPrimtive(new Long(pt));
	}
	
	public synchronized static long sizeOf(float pt) {
		return sizeOfPrimtive(new Float(pt));
	}
	
	public synchronized static long sizeOf(double pt) {
		return sizeOfPrimtive(new Double(pt));
	}
	
	public synchronized static long sizeOf(char pt) {
		return sizeOfPrimtive(new Character(pt));
	}
	
	public synchronized static long sizeOf(byte pt) {
		return sizeOfPrimtive(new Byte(pt));
	}
	
	public synchronized static long sizeOf(short pt) {
		return sizeOfPrimtive(new Short(pt));
	}
	
	public synchronized static long sizeOf(boolean pt) {
		return sizeOfPrimtive(new Boolean(pt));
	}

}
