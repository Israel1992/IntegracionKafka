package com.devs4j.kafka;

import java.util.concurrent.CompletableFuture;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class CursoKafkaSpringApplication implements CommandLineRunner {
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired
	private RestHighLevelClient client;
	
	private static final Logger log =  LoggerFactory.getLogger(CursoKafkaSpringApplication.class);

	
	@KafkaListener(topics = "debugeando-ideas", groupId="devs4j-group")
	public void listen(String message) {
		log.info("Message received{} ", message);
		IndexRequest indexRequest = buildIndexRequest("key", message);
		client.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
			
			@Override
			public void onResponse(IndexResponse response) {
				log.debug("Successful request");
			}
			
			@Override
			public void onFailure(Exception e) {
				log.debug("Error storing the message {} ", e);
				
			}
			
		});
	}
	
	private IndexRequest buildIndexRequest(String key, String value) {
		IndexRequest request = new IndexRequest("devs4j-transactions");
		request.id(key);
		request.source(value);
		return request;
	}
		
	public static void main(String[] args) {
		SpringApplication.run(CursoKafkaSpringApplication.class, args);
	}


	
	@Override
	public void run(String... args) throws Exception {
		CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("debugeando-ideas","Ejemplo de un mensaje simple");
		future.whenComplete((result, ex) -> {
		    log.info("Message sent", result.getRecordMetadata().offset());
		});
	}
	

	/*
	@Autowired
	private RestHighLevelClient client;
	@Override
	public void run(String... args) throws Exception {
		IndexRequest indexRequest = new IndexRequest("devs4j-transactions");
		indexRequest.id("44");
		indexRequest.source("{\"nombre\":\"Sammie\",\"apellido\":\"Goldner\",\"username\":\"hugh.vonrueden\",\"monto\":9622235.2009}", XContentType.JSON);
		
		IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
		log.info("Response id = {} ", response.getId());
		
		
	}
	*/
	
}
