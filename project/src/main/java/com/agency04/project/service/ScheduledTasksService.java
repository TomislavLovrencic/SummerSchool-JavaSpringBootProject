package com.agency04.project.service;


import com.agency04.project.api.HelperController;
import com.agency04.project.config.AsyncConfig;
import com.agency04.project.model.Heist;
import com.agency04.project.model.HeistMember;
import com.agency04.project.model.HeistStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;


@Service
@EnableScheduling
public class ScheduledTasksService {


    @Autowired
    private HeistService heistService;

    @Autowired
    private MailSendingService mailSendingService;


    public void updateStartAndEndHeist(Heist heist){
        if(heist.getHeistStatus().equals(HeistStatus.FINISHED)) return;

        Runnable startHeist = new Runnable() {
            @Override
            public void run() {
                heist.setHeistStatus(HeistStatus.IN_PROGRESS);
                heistService.addHeist(heist);
                mailSendingService.notifyMembers(heist,"The heist has begun!");
            }
        };
        try {
            AsyncConfig.executeTaskT(startHeist,HelperController.parseISOtimeToDate(heist.getStartTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Runnable endHeist = new Runnable() {
            @Override
            public void run() {
                heist.setHeistStatus(HeistStatus.FINISHED);
                heistService.addHeist(heist);
                mailSendingService.notifyMembers(heist,"The heist has ended!!");
            }
        };
        try {
            AsyncConfig.executeTaskT(endHeist,HelperController.parseISOtimeToDate(heist.getEndTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 5000)
    public void updateStarsOnSkills(Heist heist){

    }

}

