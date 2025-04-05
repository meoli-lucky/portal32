package com.fds.flex.core.portal.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ReactiveResponseUtil {

	public static Mono<Void> write(ServerHttpResponse response, String s) {
		byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
		DataBufferFactory bufferFactory = response.bufferFactory();
		DataBuffer buffer = bufferFactory.wrap(bytes);
		
		return response.writeWith(Mono.just(buffer))
				.doOnError(error -> DataBufferUtils.release(buffer));
	}

	public static Mono<Void> write(ServerWebExchange exchange, String s) {
		return write(exchange.getResponse(), s);
	}
	
	public static Mono<Void> write(ServerHttpResponse response, ByteBuffer byteBuffer) {
		DataBufferFactory bufferFactory = response.bufferFactory();
		DataBuffer buffer = bufferFactory.wrap(byteBuffer);
		
		return response.writeWith(Mono.just(buffer))
				.doOnError(error -> DataBufferUtils.release(buffer));
	}

	public static Mono<Void> write(ServerWebExchange exchange, ByteBuffer byteBuffer) {
		return write(exchange.getResponse(), byteBuffer);
	}
	
	public static Mono<Void> write(ServerHttpResponse response, byte[] bytes, int offset, int contentLength) {
		DataBufferFactory bufferFactory = response.bufferFactory();
		
		// Tạo mảng mới từ một phần của mảng ban đầu
		byte[] subArray = new byte[contentLength];
		System.arraycopy(bytes, offset, subArray, 0, contentLength);
		
		DataBuffer buffer = bufferFactory.wrap(subArray);
		
		setContentLength(response, contentLength);
		
		return response.writeWith(Mono.just(buffer))
				.doOnError(error -> DataBufferUtils.release(buffer));
	}
	
	public static Mono<Void> write(ServerWebExchange exchange, byte[] bytes, int offset, int contentLength) {
		return write(exchange.getResponse(), bytes, offset, contentLength);
	}
	
	public static Mono<Void> write(ServerHttpResponse response, InputStream inputStream) {
		return write(response, inputStream, 0);
	}
	
	public static Mono<Void> write(ServerWebExchange exchange, InputStream inputStream) {
		return write(exchange.getResponse(), inputStream);
	}
	
	public static Mono<Void> write(ServerHttpResponse response, InputStream inputStream, long contentLength) {
		if (contentLength > 0) {
			response.getHeaders().set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
		}
		
		byte[] buffer = new byte[4096];
		try {
			byte[] allBytes = inputStream.readAllBytes();
			DataBuffer dataBuffer = response.bufferFactory().wrap(allBytes);
			return response.writeWith(Mono.just(dataBuffer))
					.doOnError(error -> DataBufferUtils.release(dataBuffer))
					.doFinally(signalType -> {
						try {
							if (inputStream != null) {
								inputStream.close();
							}
						} catch (IOException e) {
							log.error("Error closing input stream", e);
						}
					});
		} catch (IOException e) {
			log.error("Error reading from input stream", e);
			return Mono.error(e);
		}
	}
	
	public static Mono<Void> write(ServerWebExchange exchange, InputStream inputStream, long contentLength) {
		return write(exchange.getResponse(), inputStream, contentLength);
	}
	
	protected static void setContentLength(ServerHttpResponse response, long contentLength) {
		response.getHeaders().set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
	}
}
