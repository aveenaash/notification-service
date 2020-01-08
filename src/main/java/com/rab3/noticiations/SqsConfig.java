package com.rab3.noticiations;

import javax.jms.Session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

@Configuration
public class SqsConfig {
	
	@Value("${sqs.queue.endpoint}")
	private String endPoint;
	
	@Value("${aws.accesskey}")
	private String accessKey;
	
	@Value("${aws.secretkey}")
	private String secretKey;

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(sqsConnectionFactory());
		factory.setDestinationResolver(new DynamicDestinationResolver());
		factory.setConcurrency("3-10");
		factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		// factory.setMessageConverter(messageConverter());
		return factory;
	}

	@Bean
	public SQSConnectionFactory sqsConnectionFactory() {

		SQSConnectionFactory sqsConnectionFactory = SQSConnectionFactory.builder()
				.withAWSCredentialsProvider(awsCredentialsProvider()).withEndpoint(endPoint)
				.withNumberOfMessagesToPrefetch(10).build();
		
		return sqsConnectionFactory;

	}
	
	@Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
		AWSCredentialsProvider credentials = new AWSCredentialsProvider() {
			
			public void refresh() {
				
			}
			
			public AWSCredentials getCredentials() {
				return new BasicAWSCredentials(accessKey, secretKey);
			}
		};
		
		return credentials;
    }
	
	@Bean
    public MessageConverter messageConverter() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        builder.dateFormat(new ISO8601DateFormat());

        MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();

        mappingJackson2MessageConverter.setObjectMapper(builder.build());
        mappingJackson2MessageConverter.setTargetType(MessageType.TEXT);
        mappingJackson2MessageConverter.setTypeIdPropertyName("documentType");
        return mappingJackson2MessageConverter;
    }

}
