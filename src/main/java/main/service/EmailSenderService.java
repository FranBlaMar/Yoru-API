package main.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderService {

	@Autowired
    private JavaMailSender emailSender;

	public int sendVerificationEmail(String to) throws MessagingException {
		
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper;
		
		helper = new MimeMessageHelper(message,true);
		
		int codVerify = (int)(Math.random()*9999 + 1000);
		
		helper.setSubject("Correo de Verificación");
		helper.setTo(to);
		helper.setText("¡Bienvenid@ a Yoru! Su código de verificación es : " + codVerify,true);
		
		emailSender.send(message);
		return codVerify;
	}
	
}
