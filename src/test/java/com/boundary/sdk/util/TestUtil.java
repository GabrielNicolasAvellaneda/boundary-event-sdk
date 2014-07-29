package com.boundary.sdk.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtil {
	/**
	 * Helper function for reading JSON examples
	 * 
	 * @param path Location of the file
	 * @param encoding Encoding used to read file
	 * @return {@String} Contents of file
	 * @throws IOException
	 */
	static public String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}
