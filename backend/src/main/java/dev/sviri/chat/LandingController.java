package dev.sviri.chat;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.UUID;

@Controller
public class LandingController {
    private static final String UID_COOKIE = "uid";
    private static final Log log = LogFactory.getLog(LandingController.class);

    private final UserService userService;
    private final RoomService roomService;

    public LandingController(UserService userService, RoomService roomService) {
        this.userService = userService;
        this.roomService = roomService;
    }

    @GetMapping("/")
    public void redirectToRoom(@CookieValue(value = UID_COOKIE, required = false) String uid, HttpServletResponse httpServletResponse) throws IOException {
        User initiator = getUserAndDropCookie(uid, httpServletResponse);
        Room room = roomService.createRoom(initiator);
        httpServletResponse.sendRedirect("/room/" + room.uuid());
    }


    @GetMapping("/room/{room_uid}")
    public String room(@PathVariable("room_uid") String roomUid, @CookieValue(value = UID_COOKIE, required = false) String uid, HttpServletResponse response) {
        UUID parsedRoomUid = null;
        try {
            parsedRoomUid = UUID.fromString(roomUid);
        } catch (Exception e) {
            log.warn(String.format("'%s' is not a valid room UUID", parsedRoomUid));
        }
        Room room = roomService.findRoom(parsedRoomUid);
        if (room == null) {
            return "/static/error.html";
        }
        getUserAndDropCookie(uid, response);
        return "/static/chat.html";
    }

    private User getUserAndDropCookie(String uid, HttpServletResponse httpServletResponse) {
        UUID userId = null;
        if (uid != null) {
            try {
                userId = UUID.fromString(uid);
            } catch (Exception e) {
                log.warn(String.format("'%s' is not a valid user UUID", uid));
            }
        } else {
            userId = UUID.randomUUID();
        }
        var initiator = new User(userId);
        Cookie userIdCookie = new Cookie(UID_COOKIE, initiator.uid().toString());
        userIdCookie.setPath("/");
        httpServletResponse.addCookie(userIdCookie);
        return initiator;
    }
}
