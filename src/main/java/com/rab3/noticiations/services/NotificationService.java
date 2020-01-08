package com.rab3.noticiations.services;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.rab3.noticiations.dto.MessageDto;

@Service
public class NotificationService {

	@JmsListener(destination = "${sqs.queue.name}")
	public void listenMessage(MessageDto message) {
		System.out.println("Message received : " + message.toString());

	}
	
	

}
