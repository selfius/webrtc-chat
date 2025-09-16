package dev.sviri.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VolleyApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        var response = restTemplate.withRedirects(ClientHttpRequestFactorySettings.Redirects.DONT_FOLLOW).getForEntity("http://localhost:{0}", String.class, port);
        assertThat(Objects.requireNonNull(response.getHeaders().get("set-cookie")).getFirst()).startsWith("uid=");
        assertThat(response.getStatusCode().value()).isEqualTo(302);
    }
}
