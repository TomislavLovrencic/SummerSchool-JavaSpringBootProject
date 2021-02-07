package com.agency04.project.service;

import com.agency04.project.model.Heist;
import com.agency04.project.model.HeistMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailSendingService {

    @Autowired
    private JavaMailSender javaMailSender;


    public void  sendEmail(String email,String message) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);

        msg.setSubject("Hello fellow heist member!");
        msg.setText(message);

        javaMailSender.send(msg);

    }

    public void notifyMembers(Heist heist,String message){
        List<HeistMember> members = heist.getMembers();
        if(members == null) return;
        for(HeistMember heistMember : members){
            String email = heistMember.getEmail();
            sendEmail(email,message);
        }
    }
}
