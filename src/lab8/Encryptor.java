package lab8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class Encryptor {

	private PublicKey publicKey;
	private PrivateKey privateKey;
	private Cipher cipher;
	private KeyFactory keyFactory;

	public Encryptor() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

		keyFactory = KeyFactory.getInstance("RSA");

		try {
			cipher = Cipher.getInstance("RSA");
		} catch (Exception e) {
			e.printStackTrace();
		}

		X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(Files.readAllBytes(Paths.get("public.der")));
		publicKey = keyFactory.generatePublic(publicSpec);
		PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(Files.readAllBytes(Paths.get("private.der")));
		privateKey = keyFactory.generatePrivate(privateSpec);
	}

	public Boolean encriptFileContent(String inputFile, String outputFile) {

		try {
			byte[] inputData = Files.readAllBytes(Paths.get(inputFile));
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] outputData = cipher.doFinal(inputData);
			Files.write(Paths.get(outputFile), outputData, StandardOpenOption.CREATE);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public String decriptFileContent(String inputFile, String outputFile) {

		try {
			byte[] inputData = Files.readAllBytes(Paths.get(inputFile));
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] outputData = cipher.doFinal(inputData);
			Files.write(Paths.get(outputFile), outputData, StandardOpenOption.CREATE);
			return new String(outputData);
		} catch (Exception e) {
			return null;
		}
	}
}
