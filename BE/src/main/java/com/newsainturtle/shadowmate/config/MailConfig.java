package com.newsainturtle.shadowmate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.port}")
    private String serverPort;

    @Value("${spring.mail.host}")
    private String serverHost;

    @Value("${spring.mail.username}")
    private String serverEmail;

    @Value("${spring.mail.password}")
    private String serverPassword;

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(serverHost);
        javaMailSender.setUsername(serverEmail);
        javaMailSender.setPassword(serverPassword);

        javaMailSender.setPort(Integer.parseInt(serverPort));

        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.smtp.ssl.trust","smtp.google.com");
        properties.setProperty("mail.smtp.ssl.enable","true");
        return properties;
    }
}
