package com.dwt.photovoltaic.Photovoltaic.System.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender javaMailSender;
    
    @Value("${spring.mail.username}")
    private String senderEmail;
    
    public void sendEmailWithAttachment(String recipientEmail, String subject, String body, String attachmentFilePath) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(senderEmail);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body);
            
            FileSystemResource file = new FileSystemResource(attachmentFilePath);
            helper.addAttachment(file.getFilename(), file);
            
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
