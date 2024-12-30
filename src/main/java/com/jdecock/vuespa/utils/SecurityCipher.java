package com.jdecock.vuespa.utils;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

public abstract class SecurityCipher {
	@Value("${jdecock.security.cookie-secret-key}")
	private static String secretKey;

	@Value("${jdecock.security.cookie-salt}")
	private static String salt;

	public static String encrypt(String value) {
		if (StringUtils.isEmpty(value))
			return null;

		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

			KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(),
				65536, 256);
			SecretKey secretKey = keyFactory.generateSecret(keySpec);
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

			byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

			return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			return null;
		}
	}

	public static String decrypt(String value) {
		if (StringUtils.isEmpty(value))
			return null;

		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

			KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(),
				65536, 256);
			SecretKey secretKey = keyFactory.generateSecret(keySpec);
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

			byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

			return new String(cipher.doFinal(Base64.getDecoder().decode(value)));
		} catch (Exception e) {
			return null;
		}
	}
}
