package lab8;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {
	private final static String jarPath = ".\\src\\resources\\SortingLibSigned.jar";
	private static List<Method> listMethods = new ArrayList<>();
	private static Boolean loadedJar = false;

	public static void main(String[] args) {

//		if (args.length == 0) {
//			testSortingLib();
//			return;
//		}

		Scanner scanner;
		while (true) {
			System.out.println("---------------------------------------------\n");
			System.out.println("Choose option");
			System.out.println("1) Encrypt the txt file");
			System.out.println("2) Decrypt the txt file");
			System.out.println("3) Sort");
			// System.out.println("1) Encrypt the txt file");

			System.out.println("0) exit");

			int opt = 0;
			scanner = new Scanner(System.in);
			opt = scanner.nextInt();

			try {
				switch (opt) {
				case 1:
					encriptFile();
					break;
				case 2:
					decriptFile();
					break;
				case 3:
					testSortingLib();
					break;
				case 0:
					System.exit(0);
				default:
					continue;
				}

			} catch (Exception e) {
				System.out.println("EXCEPTION");
				System.out.println(e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	private static Boolean encriptFile() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		String orginalFileName = "file.txt";
		String encriptedFileName = "encripted.txt";

		System.out.print("Type name of created file(default is file.txt):");
		Scanner scanner = new Scanner(System.in);
		String inputfilename = scanner.nextLine();
		if (inputfilename.isEmpty() == false)
			orginalFileName = inputfilename;

		System.out.print("Type name of encripted file(default is encripted.txt):");
		String outputfilename = scanner.nextLine();
		if (outputfilename.isEmpty() == false)
			encriptedFileName = outputfilename;

		System.out.println("Type text to save:");
		String inputString = scanner.nextLine();

		PrintWriter outputFile = new PrintWriter(orginalFileName);
		outputFile.println(inputString);

		outputFile.close();

		Boolean result = new Encryptor().encriptFileContent(orginalFileName, encriptedFileName);

		if (result == true)
			System.out.print("ENCRIPTED TEXT: " + inputString);

		return result;
	}

	private static Boolean decriptFile() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		String orginalFileName = "encripted.txt";
		String decriptedFileName = "decripted.txt";

		System.out.print("Type name of encrypted file(default is encripted.txt):");
		Scanner scanner = new Scanner(System.in);
		String inputfilename = scanner.nextLine();
		if (inputfilename.isEmpty() == false)
			orginalFileName = inputfilename;

		System.out.print("Type name of encripted file(default is decripted.txt):");
		String outputfilename = scanner.nextLine();
		if (outputfilename.isEmpty() == false)
			decriptedFileName = outputfilename;

		String result = new Encryptor().decriptFileContent(orginalFileName, decriptedFileName);

		if (result != null)
			System.out.print("DECRIPTED TEXT: " + result);

		return result != null;
	}

	private static void testSortingLib() {
		MyClassLoader myClassLoader = new MyClassLoader();

		if (loadedJar == false) {
			try {
				loadedJar = myClassLoader.loadJar(jarPath);
				listMethods = myClassLoader.getMethodsList();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			if (loadedJar == false) {
				System.out.println("ERROR: Jar has not been loaded.");
				return;
			}

			if (listMethods.size() == 0) {
				System.out.println("ERROR: Method list is empty.");
				return;
			}
		}

		//
		// Chose algorithm.
		//

		Scanner scanner = new Scanner(System.in);
		int methodID = 0;

		while (true) {

			System.out.println("Available methods:");
			int counter = 0;
			for (Method method : listMethods) {
				System.out.println(counter + ": " + method.getDeclaringClass());
				counter++;
			}

			System.out.print("Your chose: ");

			try {
				methodID = scanner.nextInt();
			} catch (Exception e) {
				System.out.println("---------------------------------------------\n");
				System.out.println("Wrong parameter\n");
			}

			if (methodID >= 0 && methodID < counter)
				break;
			else {
				System.out.println("---------------------------------------------\n");
				System.out.println("Wrong parameter\n");
			}
		}

		//
		// Read size of array to sort.
		//

		int arraySize = 0;

		while (true) {
			System.out.print("Size of array to generate: ");

			try {
				arraySize = scanner.nextInt();
			} catch (Exception e) {
				System.out.println("---------------------------------------------\n");
				System.out.println("Wrong parameter1\n");
			}

			if (arraySize > 1)
				break;
			else {
				System.out.println("---------------------------------------------\n");
				System.out.println("Wrong parameter2\n");
			}
		}

		//
		// Generate array.
		//

		Random rand = new Random();
		int array[] = new int[arraySize];

		for (int i = 0; i < arraySize; i++) {
			array[i] = rand.nextInt(10000);
		}

		//
		// Print array before sorting.
		//
		{
			final int linesize = 15;
			int currentPossitionInLine = 0;

			System.out.print("\n");
			for (int i = 0; i < arraySize; i++) {

				System.out.print(array[i] + "  ");
				currentPossitionInLine++;
				if (currentPossitionInLine >= linesize) {
					System.out.println("");
					currentPossitionInLine = 0;
				}
			}
		}

		//
		// Sort array
		//
		Method method = listMethods.get(methodID);
        try {
        	Object obj = method.invoke(method.getDeclaringClass().newInstance(), array);
			array = (int[])obj;

		} catch (Exception e) {
			System.out.println("Something went wrong...");
			System.out.println(e.getMessage());
			return;
		}
        System.out.print("\n");

		//
		// Print array after sorting.
		//
		{
			final int linesize = 15;
			int currentPossitionInLine = 0;

			System.out.print("\n");
			for (int i = 0; i < arraySize; i++) {

				System.out.print(array[i] + "  ");
				currentPossitionInLine++;
				if (currentPossitionInLine >= linesize) {
					System.out.println("");
					currentPossitionInLine = 0;
				}
			}
		}
		System.out.print("\n");
	}
}
