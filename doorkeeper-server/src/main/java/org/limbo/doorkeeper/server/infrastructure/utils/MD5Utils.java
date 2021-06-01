/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.limbo.doorkeeper.server.infrastructure.utils;

import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.util.Random;

public class MD5Utils {

	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	private static String toHex(byte bytes[]) {
		StringBuilder hex = new StringBuilder();
        for (byte b : bytes) hex.append(toHex(b));

		return hex.toString();
	}

	private static String toHex(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 对入参进行MD5加密，并返回转16进制后的字符串
	 * @param origin	原始字符串
	 * @param charset	原始字符串字符集
	 * @return			MD5加密后的16进制数字的字符串形式
	 */
	public static String md5AndHex(String origin, String charset) {
		String resultString;
		charset = charset == null ? "UTF-8" : charset;

		try {
			resultString = origin;
			MessageDigest md = MessageDigest.getInstance("MD5");
			if (StringUtils.isBlank(charset))
				resultString = toHex(md.digest(resultString.getBytes()));
			else
				resultString = toHex(md.digest(resultString.getBytes(charset)));
		} catch (Exception e) {
			throw new IllegalStateException("加密失败！", e);
		}
		return resultString;
	}

	/**
	 * 生成含有随机盐的密码
	 * @param input     要加密的密码
	 * @return String   含有随机盐的密码
	 */
	public static String md5WithSalt(String input) {
		// 生成一个16位的随机数
		Random random = new Random();
		StringBuilder saltBuilder = new StringBuilder(16);
		saltBuilder.append(random.nextInt(99999999)).append(random.nextInt(99999999));
		int len = saltBuilder.length();
		if (len < 16) {
			for (int i = 0; i < 16 - len; i++) {
				saltBuilder.append("0");
			}
		}

		// 生成最终的加密盐
		String salt = saltBuilder.toString();
		input = md5AndHex(input + salt, null);
		char[] cs = new char[48];
		for (int i = 0; i < 48; i += 3) {
			cs[i] = input.charAt(i / 3 * 2);
			char c = salt.charAt(i / 3);
			cs[i + 1] = c;
			cs[i + 2] = input.charAt(i / 3 * 2 + 1);
		}
		return String.valueOf(cs);
	}



	/**
	 * 验证加盐后是否和原密码一致
	 *
	 * @param input 未加密的原密码
	 * @param input 加密之后的密码
	 */
	public static boolean verify(String input, String encrypted) {
		char[] cs1 = new char[32];
		char[] cs2 = new char[16];
		for (int i = 0; i < 48; i += 3) {
			cs1[i / 3 * 2] = encrypted.charAt(i);
			cs1[i / 3 * 2 + 1] = encrypted.charAt(i + 2);
			cs2[i / 3] = encrypted.charAt(i + 1);
		}
		String Salt = new String(cs2);
		return md5AndHex(input + Salt, null).equals(String.valueOf(cs1));
	}

	public static void main(String[] args) {
		// 原密码
		String plaintext = "admin";

		// 获取加盐后的MD5值
		String ciphertext = md5WithSalt(plaintext);
		System.out.println("加盐后MD5：" + ciphertext);
		System.out.println("是否是同一字符串:" + verify(plaintext, ciphertext));

		String test = "f71e7186fa91c90436e3905436685e197674e24d7b99105d";
		System.out.println("是否是同一字符串:" + verify(plaintext, test));
	}

}
