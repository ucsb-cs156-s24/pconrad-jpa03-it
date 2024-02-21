package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBDatesController.class)
@Import(TestConfig.class)
public class UCSBDatesControllerTests extends ControllerTestCase {

    @MockBean
    UCSBDateRepository ucsbDateRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsbdates/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsbdates/all"))
                            .andExpect(status().is(200)); // logged
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

            // arrange
            LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

            UCSBDate ucsbDate = UCSBDate.builder()
                            .name("firstDayOfClasses")
                            .quarterYYYYQ("20222")
                            .localDateTime(ldt)
                            .build();

            when(ucsbDateRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbDate));

            // act
            MvcResult response = mockMvc.perform(get("/api/ucsbdates?id=7"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(ucsbDateRepository, times(1)).findById(eq(7L));
            String expectedJson = mapper.writeValueAsString(ucsbDate);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }
}
