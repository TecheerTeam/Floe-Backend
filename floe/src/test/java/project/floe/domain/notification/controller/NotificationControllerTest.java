package project.floe.domain.notification.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import project.floe.domain.notification.dto.NotificationDto;
import project.floe.domain.notification.dto.response.GetNotificationListResponseDto;
import project.floe.domain.notification.dto.response.GetUnreadCountResponseDto;
import project.floe.domain.notification.service.NotificationService;
import project.floe.global.result.ResultCode;
import project.floe.global.result.ResultResponse;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    private MockMvc mockMvc;

    private final String BASE_URL = "/api/v1/notification";

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .build();
    }

    @Test
    void 알림조회() throws Exception {
        String url = BASE_URL;
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.NOTIFICATION_LIST_GET_SUCCESS);
        List<NotificationDto> notificationDtoList = new ArrayList<>();
        GetNotificationListResponseDto expectedDto = new GetNotificationListResponseDto(notificationDtoList);

        doReturn(expectedDto).when(notificationService).getNotificationList(any(HttpServletRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
                .andExpect(jsonPath("$.data.notificationList").value(expectedDto.getNotificationList()));
    }

    @Test
    void 알림읽음처리() throws Exception {
        String url = BASE_URL + "/{notificationId}/read";
        Long notificationId = 1L;
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.NOTIFICATION_READ_SUCCESS);

        doNothing().when(notificationService).readNotification(any(HttpServletRequest.class), eq(notificationId));

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(url, notificationId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));
    }

    @Test
    void 읽지않은알림전부읽음처리() throws Exception {
        String url = BASE_URL + "/read";
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.NOTIFICATION_ALL_READ_SUCCESS);

        doNothing().when(notificationService).readAllUnreadNotification(any(HttpServletRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(url)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));
    }

    @Test
    void 알림삭제() throws Exception {
        String url = BASE_URL + "/{notificationId}/delete";
        Long notificationId = 1L;
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.NOTIFICATION_DELETE_SUCCESS);

        doNothing().when(notificationService).deleteNotification(any(HttpServletRequest.class), eq(notificationId));

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, notificationId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));
    }

    @Test
    void 읽은알림전부삭제() throws Exception {
        String url = BASE_URL + "/unread/delete";
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.NOTIFICATION_ALL_DELETE_SUCCESS);

        doNothing().when(notificationService).deleteAllReadNotification(any(HttpServletRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.delete(url)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));
    }

    @Test
    void 읽지않은알림수조회() throws Exception {
        String url = BASE_URL + "/unread/count";
        ResultResponse expectedResponse = ResultResponse.of(ResultCode.NOTIFICATION_UNREAD_COUNT_GET_SUCCESS);
        GetUnreadCountResponseDto expectedDto = new GetUnreadCountResponseDto(1L);

        doReturn(expectedDto).when(notificationService).getUnreadNotificationCount(any(HttpServletRequest.class));

        mockMvc.perform(
                        MockMvcRequestBuilders.get(url)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(expectedResponse.getCode()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
                .andExpect(jsonPath("$.data.count").value(expectedDto.getCount()));
    }


}
