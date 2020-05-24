package lab8;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.lang.reflect.Method;

public class MyClassLoader extends SecureClassLoader {

	public static List<Method> methodsList = new ArrayList<>();

	public static Boolean loadJar(String path)
			throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException {
		List<Class> listClass = new ArrayList<>();

		JarFile jarFile = new JarFile(path, true); // JarFile(path to jar, verify)
		Enumeration<JarEntry> enumerationJarEntry = jarFile.entries();
		while (enumerationJarEntry.hasMoreElements()) {
			JarEntry entry = enumerationJarEntry.nextElement();
			try {
				jarFile.getInputStream(entry);
			} catch (Exception e) {
				System.out.println("Cannot load " + path);
				System.out.println(e.getMessage());
				return false;
			}
		}

        Manifest manifest = jarFile.getManifest();
        Map<String, Attributes> map = manifest.getEntries();
        boolean signed = false;
        for(Map.Entry<String, Attributes> entry : map.entrySet()){
            String name = entry.getValue().entrySet().toString();
            if(name.contains("-Digest")){
                signed = true;
            }
        }
        if(!signed) {
            System.err.println("ERROR: JAR is not signed.\n");
            return false;
        }

		enumerationJarEntry = jarFile.entries();
		URL[] urls = { new URL("jar:file:" + path + "!/") };
		URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls);

		while (enumerationJarEntry.hasMoreElements()) {
			JarEntry jarEntry = enumerationJarEntry.nextElement();

			if (jarEntry.isDirectory() == true || jarEntry.getName().endsWith(".class") == false) {
				continue;
			}

			String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
			className = className.replace('/', '.');
			Class classObj = null;
			try {
				classObj = urlClassLoader.loadClass(className);
			} catch (SecurityException s) {
				System.out.println("Cannot load class " + className);
				System.out.println(s.getMessage());
				continue;
			}

			listClass.add(urlClassLoader.loadClass(className));
		}

		//
		// Load methods.
		//
		for (Class cl : listClass) {
			Method method = null;

			if(extractClasssName(cl.getName()).equals("FloatSorterQuick"))
				method = cl.getMethod("solve2", List.class);

			if(extractClasssName(cl.getName()).equals("IntSorter"))
				method = cl.getMethod("solve", int[].class);

			if (method != null)
				methodsList.add(method);
		}
		return true;
	}

	private static String extractClasssName(String path) {
		String[] parts;
		parts = path.split("\\.");
		return parts[parts.length - 1];
	}

	public List<Method> getMethodsList() {
		return methodsList;
	}
}
