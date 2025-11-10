package com.example.SkillSwap.service.impl;

import com.example.SkillSwap.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
public class JitsiMeetService {

    private static final String JITSI_BASE_URL = "https://meet.jit.si/";
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    public String createMeetingLink(Long bookingId) {
        String roomId = generateRoomId(bookingId);
        String meetUrl = JITSI_BASE_URL + roomId;

        log.info("Jitsi Meet room created for booking {}: {}", bookingId, meetUrl);
        return meetUrl;
    }

    private String generateRoomId(Long bookingId) {
        String baseId = "SkillSwap-" + bookingId;
        String randomCode = generateRandomCode(bookingId);

        return baseId + "-" + randomCode;
    }

    private String generateRandomCode(Long bookingId) {
        Random random = new Random(bookingId);
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return code.toString();
    }


    public boolean canJoinMeeting(LocalDateTime scheduledTime, int durationMinutes) {
        if (scheduledTime == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime meetingStart = scheduledTime;
        LocalDateTime meetingEnd = meetingStart.plusMinutes(durationMinutes);

        return now.isAfter(meetingStart.minusMinutes(15)) &&
                now.isBefore(meetingEnd.plusMinutes(15));
    }
}
