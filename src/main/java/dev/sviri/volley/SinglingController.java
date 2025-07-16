package dev.sviri.volley;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SinglingController {
    private final Log log = LogFactory.getLog(SinglingController.class);

    private static final Object OFFER_LOCK = new Object();
    private String offer;

    @PostMapping("/offer")
    public void postOffer(@RequestBody String offer) {
        log.warn("received offer" + offer);
        synchronized (OFFER_LOCK) {
            this.offer = offer;
        }
    }

    @GetMapping("/offer")
    public String getOffer() {
        synchronized (OFFER_LOCK) {
            return this.offer;
        }
    }
}
