package com.agency04.project.api;

import com.agency04.project.model.*;
import com.agency04.project.model.DTO.HeistDTO;
import com.agency04.project.model.DTO.HeistMemberDTO;
import com.agency04.project.model.DTO.RequirementSkillDTO;
import com.agency04.project.model.DTO.SkillDTO;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class HelperController {


    public static HeistMember fromDTOtoModel(HeistMemberDTO heistMemberDTO) {
        if (!(heistMemberDTO.getSex().equals("F") || heistMemberDTO.getSex().equals("M"))) {
            throw new IllegalArgumentException();
        }
        if (heistMemberDTO.getMainSkill() != null) {
            if (heistMemberDTO.getSkills().stream().noneMatch(p -> p.getName().equals(heistMemberDTO.getMainSkill()))) {
                throw new IllegalArgumentException();
            }
        }

        //check for duplicate skills
        List<Skill> skillsHeistMember = heistMemberDTO.getSkills().stream().map(HelperController::skillDTOtoSkill).collect(Collectors.toList());

        for (Skill skill : skillsHeistMember) {
            if (skill.getLevel().length() > 10 || skill.getLevel().length() < 1) {
                throw new IllegalArgumentException();
            }
            for (Character a : skill.getLevel().toCharArray()) {
                if (a != '*') throw new IllegalArgumentException();
            }
        }

        if (checkForSameNameSkills(skillsHeistMember)) {
            throw new IllegalArgumentException();
        }

        HeistMember member = new HeistMember(
                heistMemberDTO.getName(),
                heistMemberDTO.getSex(),
                heistMemberDTO.getEmail(),
                heistMemberDTO.getSkills().stream().map(HelperController::skillDTOtoSkill).collect(Collectors.toList()),
                heistMemberDTO.getMainSkill(),
                switch (heistMemberDTO.getStatus()) {
                    case ("AVAILABLE") -> RobberStatus.AVAILABLE;
                    case ("EXPIRED") -> RobberStatus.EXPIRED;
                    case ("INCARCERATED") -> RobberStatus.INCARCERATED;
                    case ("RETIRED") -> RobberStatus.RETIRED;
                    default -> throw new IllegalArgumentException();
                }
        );

        return member;
    }

    public static List<HeistMember> getRandomArray(int x, List<HeistMember> memberList) {

        int brojac = 0;

        List<HeistMember> members = new ArrayList<>();

        while (brojac < x) {
            for (HeistMember member : memberList) {
                int tmp = (int) (Math.random() * 2 + 1);
                if (tmp == 1) {
                    if (!members.contains(member)) {
                        members.add(member);
                        brojac++;
                    }
                }
                if (brojac == x) {
                    return members;
                }
            }
        }
        throw new RuntimeException();
    }


    public static HeistDTO heistToHeistDTO(Heist heist) {
        HeistDTO heistDTO = new HeistDTO();
        heistDTO.setName(heist.getName());
        heistDTO.setLocation(heist.getLocation());
        heistDTO.setStartTime(heist.getStartTime());
        heistDTO.setEndTime(heist.getEndTime());
        heistDTO.setSkills(heist.getSkills().stream().map(HelperController::requirementSkillToDTO).collect(Collectors.toList()));
        heistDTO.setStatus(
                switch (heist.getHeistStatus()) {
                    case PLANING -> "PLANING";
                    case READY -> "READY";
                    case IN_PROGRESS -> "IN_PROGRESS";
                    case FINISHED -> "FINISHED";
                    default -> throw new IllegalArgumentException();
                });

        return heistDTO;
    }

    public static HeistMemberDTO heistMemberToDTO(HeistMember heistMember) {
        HeistMemberDTO heistMemberDTO = new HeistMemberDTO();
        heistMemberDTO.setName(heistMember.getName());
        heistMemberDTO.setEmail(heistMember.getEmail());
        heistMemberDTO.setMainSkill(heistMember.getMainSkill());
        heistMemberDTO.setSex(heistMember.getSex());
        heistMemberDTO.setSkills(heistMember.getSkills().stream().map(HelperController::skillToSkillDTO).collect(Collectors.toList()));
        heistMemberDTO.setStatus(
                switch (heistMember.getStatus()) {
                    case RETIRED -> "RETIRED";
                    case AVAILABLE -> "AVAILABLE";
                    case INCARCERATED -> "INCARCERATED";
                    case EXPIRED -> "EXPIRED";
                    default -> throw new IllegalArgumentException();
                }
        );

        return heistMemberDTO;
    }

    public static SkillDTO skillToSkillDTO(Skill skill) {
        SkillDTO skillDTO = new SkillDTO();
        skillDTO.setName(skill.getName());
        skillDTO.setLevel(skill.getLevel());
        return skillDTO;
    }

    public static Skill skillDTOtoSkill(SkillDTO skillDTO) {
        Skill skill = new Skill();
        skill.setLevel(skillDTO.getLevel());
        skill.setName(skillDTO.getName());
        return skill;
    }


    public static RequirementSkillDTO requirementSkillToDTO(RequirementSkill requirementSkill) {
        RequirementSkillDTO requirementSkillDTO = new RequirementSkillDTO();
        requirementSkillDTO.setLevel(requirementSkill.getLevel());
        requirementSkillDTO.setMembers(requirementSkill.getMembers());
        requirementSkillDTO.setName(requirementSkill.getName());
        return requirementSkillDTO;
    }

    public static Heist heistDTOtoHeist(HeistDTO heistDTO) {
        Heist heist = new Heist(
                heistDTO.getName(),
                heistDTO.getLocation(),
                heistDTO.getStartTime(),
                heistDTO.getEndTime(),
                heistDTO.getSkills().stream().map(HelperController::DTOtoRequirementSkill).collect(Collectors.toList()),
                switch (heistDTO.getStatus()) {
                    case "PLANING" -> HeistStatus.PLANING;
                    case "READY" -> HeistStatus.READY;
                    case "IN_PROGRESS" -> HeistStatus.IN_PROGRESS;
                    case "FINISHED" -> HeistStatus.FINISHED;
                    default -> throw new IllegalArgumentException();
                }
        );

        return heist;
    }

    public static RequirementSkill DTOtoRequirementSkill(RequirementSkillDTO requirementSkillDTO) {
        RequirementSkill requirementSkill = new RequirementSkill(
                requirementSkillDTO.getName(),
                requirementSkillDTO.getLevel(),
                requirementSkillDTO.getMembers()
        );
        return requirementSkill;
    }


    public static boolean checkForSameNameSkills(List<Skill> skills) {
        //check for duplicate skills
        for (int i = 0; i < skills.size(); i++) {
            for (int j = 0; j < skills.size(); j++) {
                if (i != j) {
                    if (skills.get(i).getName()
                            .equals(skills.get(j).getName())) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public static boolean checkForSameNameAndLevelSkills(List<RequirementSkill> skills) {
        for (int i = 0; i < skills.size(); i++) {
            for (int j = 0; j < skills.size(); j++) {
                if (i != j) {
                    if (skills.get(i).getName().equals(skills.get(j).getName())) {
                        if (skills.get(i).getLevel().equals(skills.get(j).getLevel())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static Date parseISOtimeToDate(String isoTime) throws ParseException {
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");

        Date time = df1.parse(isoTime);
        return time;
    }

    public static String parseDateTimeToISO(Date date) {

        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        String text = sdf.format(date);
        return text;
    }

    public static boolean checkIfSkillsMatch(HeistMember member, List<RequirementSkill> reqSkills) {
        if (member.getStatus().equals(RobberStatus.AVAILABLE) || member.getStatus().equals(RobberStatus.RETIRED)) {
            for (RequirementSkill skill : reqSkills) {
                for (Skill memberSkill : member.getSkills()) {
                    if (memberSkill.getName().equals(skill.getName())) {
                        if (memberSkill.getLevel().length() >= skill.getLevel().length()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkIfMemberHasBeenConfirmed(HeistMember heistMember, Heist heist) throws ParseException {
        for (Heist onGoingHeist : heistMember.getHeist()) {
            Date startTimeOnGoingHeist = HelperController.parseISOtimeToDate(onGoingHeist.getStartTime());
            Date startTimeHeist = HelperController.parseISOtimeToDate(heist.getStartTime());
            Date endTimeOnGoingHeist = HelperController.parseISOtimeToDate(onGoingHeist.getEndTime());
            Date endTimeHeist = HelperController.parseISOtimeToDate(heist.getEndTime());
            if (startTimeHeist.compareTo(startTimeOnGoingHeist) >= 0 && startTimeHeist.compareTo(endTimeOnGoingHeist) <= 0) {
                return true;
            }
            if (endTimeHeist.compareTo(endTimeOnGoingHeist) <= 0 && endTimeHeist.compareTo(startTimeOnGoingHeist) >= 0) {
                return true;
            }
            if (startTimeHeist.compareTo(startTimeOnGoingHeist) < 0 && endTimeHeist.compareTo(endTimeOnGoingHeist) > 0) {
                return true;
            }
        }
        return false;
    }
}
