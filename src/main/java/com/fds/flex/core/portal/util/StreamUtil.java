package com.fds.flex.core.portal.util;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StreamUtil {

	public static final int BUFFER_SIZE = 8192;

	public static final boolean FORCE_TIO = false;

	public static void transfer(InputStream inputStream, OutputStream outputStream, int bufferSize, boolean cleanUp,
			long length) throws IOException {

		if (inputStream == null) {
			throw new IllegalArgumentException("Input stream is null");
		}

		if (outputStream == null) {
			throw new IllegalArgumentException("Output stream is null");
		}

		if (bufferSize <= 0) {
			bufferSize = BUFFER_SIZE;
		}

		try {
			if (!FORCE_TIO && (inputStream instanceof FileInputStream) && (outputStream instanceof FileOutputStream)) {

				FileInputStream fileInputStream = (FileInputStream) inputStream;
				FileOutputStream fileOutputStream = (FileOutputStream) outputStream;

				transferFileChannel(fileInputStream.getChannel(), fileOutputStream.getChannel(), length);
			} else {
				transferByteArray(inputStream, outputStream, bufferSize, length);
			}
		} finally {
			if (cleanUp) {
				cleanUp(false, inputStream, outputStream);
			}
		}
	}

	public static void cleanUp(boolean quiet, Closeable... closeables) {
		IOException ioException1 = null;

		for (Closeable closeable : closeables) {
			if (closeable != null) {
				try {
					closeable.close();
				} catch (IOException ioException2) {
					if (ioException1 == null) {
						ioException1 = ioException2;
					} else {
						ioException1.addSuppressed(ioException2);
					}
				}
			}
		}

		if (ioException1 == null) {
			return;
		}

		if (quiet) {
			log.warn(ioException1.getMessage());
		} else {
			ReflectionUtil.throwException(ioException1);
		}
	}

	public static void transfer(InputStream inputStream, OutputStream outputStream, long length) throws IOException {

		transfer(inputStream, outputStream, BUFFER_SIZE, true, length);
	}

	protected static void transferByteArray(InputStream inputStream, OutputStream outputStream, int bufferSize,
			long length) throws IOException {

		byte[] bytes = new byte[bufferSize];

		if (length > 0) {
			long remainingLength = length;

			while (remainingLength > 0) {
				int readBytes = inputStream.read(bytes, 0, (int) Math.min(remainingLength, bufferSize));

				if (readBytes == -1) {
					break;
				}

				outputStream.write(bytes, 0, readBytes);

				remainingLength -= readBytes;
			}
		} else {
			int value = -1;

			while ((value = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, value);
			}
		}
	}

	protected static void transferFileChannel(FileChannel inputFileChannel, FileChannel outputFileChannel, long length)
			throws IOException {

		if (length <= 0) {
			length = inputFileChannel.size() - inputFileChannel.position();
		}

		long count = 0;

		while (count < length) {
			count += inputFileChannel.transferTo(inputFileChannel.position() + count, length - count,
					outputFileChannel);
		}
	}

}
