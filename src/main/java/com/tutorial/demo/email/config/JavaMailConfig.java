package com.tutorial.demo.email.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class JavaMailConfig {

    private static class EnvConfig {
        private final Dotenv dotenv;

        EnvConfig() {
            dotenv = Dotenv.configure().load();
        }

        public String getEmail() {
            return dotenv.get("GMAIL");
        }

        public String getPassword() {
            return dotenv.get("PASSWORD");
        }
    }

    public JavaMailConfig() {
        this.envConfig = new EnvConfig();
    }

    private final EnvConfig envConfig;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(envConfig.getEmail());
        mailSender.setPassword(envConfig.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.debug", "true");

        return mailSender;
    }
}
