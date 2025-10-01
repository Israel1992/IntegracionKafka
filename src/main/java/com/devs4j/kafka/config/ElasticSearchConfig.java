package com.devs4j.kafka.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

	
	
	@Bean(destroyMethod = "close")
	public RestHighLevelClient createClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic","lcnYKBjFaBl8rR8F6rKV"));
		
	     // Crea un SSLContext que no verifique el hostname
        SSLContext sslContext = SSLContextBuilder
            .create()
            .loadTrustMaterial(null, (chain, authType) -> true) // Confía en todos los certificados (NO RECOMENDADO PARA PROD)
            .build();
        
		RestHighLevelClient client = new RestHighLevelClient(
				RestClient.builder(new HttpHost("localhost",9200, "https"))
				.setHttpClientConfigCallback((config) -> config.setDefaultCredentialsProvider(credentialsProvider)
				.setSSLContext(sslContext) // Establece el SSLContext
		        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				) // Deshabilita la verificación del hostname
			);
		
		
		return client;
	}
}
