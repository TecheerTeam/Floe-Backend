package project.floe.domain.notification.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import project.floe.domain.notification.dto.response.ConnectSuccessResponseDto;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.entity.UserRole;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.auth.jwt.service.JwtService;
import project.floe.global.config.TestSecurityConfig;
import project.floe.global.result.ResultCode;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class SseConnectionTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        Optional<User> existingUser = userRepository.findByEmail("test@test.com");
        user = existingUser.orElseGet(() -> userRepository.save(User.builder()
                .email("test@test.com")
                .nickname("nickname")
                .password("password")
                .role(UserRole.USER)
                .build()));
    }

    @Test
    void sse구독() throws Exception {
        String url = "http://localhost:" + port + "/api/v1/notification/subscribe";
        String userEmail = user.getEmail();
        String accessToken = jwtService.createAccessToken(userEmail);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "text/event-stream");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setDoInput(true);

        assertThat(connection.getResponseCode()).isEqualTo(200);
        assertThat(connection.getContentType()).isEqualTo("text/event-stream");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

            String line = reader.readLine();
            if (line.startsWith("data:")) {
                line = line.substring(5).trim(); // 'data:' 제거 후 공백 제거
            }

            ObjectMapper objectMapper = new ObjectMapper();
            ConnectSuccessResponseDto response = objectMapper.readValue(line, ConnectSuccessResponseDto.class);

            assertThat(response.getCode()).isEqualTo(ResultCode.NOTIFICATION_CONNECT_SUCCESS.getCode());
            assertThat(response.getMessage()).isEqualTo(ResultCode.NOTIFICATION_CONNECT_SUCCESS.getMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

