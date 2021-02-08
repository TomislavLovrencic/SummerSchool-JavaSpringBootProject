package com.agency04.project.service;


import com.agency04.project.api.HelperController;
import com.agency04.project.config.AsyncConfig;
import com.agency04.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;


@Service
@EnableScheduling
public class ScheduledTasksService {


    @Autowired
    private HeistService heistService;

    @Autowired
    private MailSendingService mailSendingService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private RequirementSkillService requirementSkillService;

    private Timer timer = new Timer();

    @Value("${levelUpTime}")
    private Long levelUpTime;


    public void updateStartAndEndHeist(Heist heist) throws ParseException {
        if (heist.getHeistStatus().equals(HeistStatus.FINISHED)) return;

        Runnable endHeist = new Runnable() {
            @Override
            public void run() {
                heist.setHeistStatus(HeistStatus.FINISHED);
                heistService.addHeist(heist);
                mailSendingService.notifyMembers(heist, "The heist has ended!!");
            }
        };
        AsyncConfig.executeTaskT(endHeist, HelperController.parseISOtimeToDate(heist.getEndTime()));

        Runnable startHeist = new Runnable() {
            @Override
            public void run() {
                heist.setHeistStatus(HeistStatus.IN_PROGRESS);
                heistService.addHeist(heist);
                mailSendingService.notifyMembers(heist, "The heist has begun!");

                Runnable startSkillTimer = new Runnable() {
                    @Override
                    public void run() {
                        timer(heist);
                    }
                };
                try {
                    Date date = HelperController.parseISOtimeToDate(heist.getStartTime());
                    date.setSeconds((int) (date.getSeconds() + levelUpTime));
                    AsyncConfig.executeTaskT(startSkillTimer, date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        };
        AsyncConfig.executeTaskT(startHeist, HelperController.parseISOtimeToDate(heist.getStartTime()));

    }

    public void updateStarsOnSkills(Heist heist) {

        if (heist.getHeistStatus().equals(HeistStatus.FINISHED)) {
            timer.cancel();
        }

        List<HeistMember> members = heist.getMembers();

        List<RequirementSkill> requirementSkills = requirementSkillService.findAllSkills(heist.getId());

        List<String> updatedSkills = new ArrayList<>();

        for (HeistMember member : members) {
            List<Skill> skills = skillService.findAllSkills(member.getId());
            for (Skill skill : skills) {
                for (RequirementSkill requirementSkill : requirementSkills) {
                    if (skill.getName().equals(requirementSkill.getName())) {
                        if (skill.getLevel().length() >= requirementSkill.getLevel().length()) {
                            if (!updatedSkills.contains(skill.getName())) {
                                if (skill.getLevel().length() < 10) {
                                    skill.setLevel(skill.getLevel() + "*");
                                    updatedSkills.add(skill.getName());
                                    skillService.addSkill(skill);
                                }

                            }
                        }
                    }
                }
            }
        }
    }


    public void timer(Heist heist) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateStarsOnSkills(heist);
            }
        }, 0, levelUpTime * 1000L);
    }

}

