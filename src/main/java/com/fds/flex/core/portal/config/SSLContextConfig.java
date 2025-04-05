package com.fds.flex.core.portal.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SSLContextConfig {
	@Value("${flexcore.portal.trust.store.path}")
	private Resource trustStore;

	@Value("${flexcore.portal.trust.store.password}")
	private String trustStorePassword;

	@Value("${flexcore.portal.request.ssl.noop-hostname-verifier}")
	boolean noopHostnameVerifie;

	@Bean
	public SSLContext initSSLContext() {

		SSLContext sslContext = null;

		FileInputStream keyStoreStream = null;

		try {
			
			sslContext = SSLContext.getInstance("TLSv1.2");
			
			File file = trustStore.getFile();

			keyStoreStream = new FileInputStream(file);

			KeyStore keyStore = KeyStore.getInstance("JKS");

			keyStore.load(keyStoreStream, trustStorePassword.toCharArray());

			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);

			sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			if (keyStoreStream != null) {
				try {
					keyStoreStream.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
		}

		return sslContext;
	}

	@Bean
	public SSLConnectionSocketFactory initConnectionSocketFactory() {
		SSLConnectionSocketFactory connectionSocketFactory = null;

		if (noopHostnameVerifie) {
			try {
				connectionSocketFactory = new SSLConnectionSocketFactory(
						SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
						NoopHostnameVerifier.INSTANCE);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		} else {
			SSLContext sslContext = initSSLContext();
			if (sslContext != null) {
				connectionSocketFactory = new SSLConnectionSocketFactory(sslContext);

			}
		}

		return connectionSocketFactory;
	}
}
