package com.matawan.equipefootball.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matawan.equipefootball.dto.TeamDto;
import com.matawan.equipefootball.exception.GlobalExceptionHandler;
import com.matawan.equipefootball.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TeamControllerTest {

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(teamController)
                .setControllerAdvice(new GlobalExceptionHandler()) // include the GlobalExceptionHandler in the text context
                .build();
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test for retrieving a team by id
     */
    @Test
    void testGetTeamById() throws Exception {
        TeamDto teamDto = new TeamDto();
        teamDto.setId(1L);
        teamDto.setName("OGC Nice");

        when(teamService.getTeamById(1L)).thenReturn(teamDto);

        mockMvc.perform(get("/api/teams/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("OGC Nice"));

        verify(teamService, times(1)).getTeamById(1L);
    }

    /**
     * Test for fetching a paginated list of teams
     */
    @Test
    void testGetTeams() throws Exception {
        TeamDto team1 = new TeamDto();
        team1.setId(1L);
        team1.setName("Team A");

        TeamDto team2 = new TeamDto();
        team2.setId(2L);
        team2.setName("Team B");

        List<TeamDto> teams = Arrays.asList(team1, team2);
        Page<TeamDto> page = new PageImpl<>(teams, PageRequest.of(0, 10), teams.size());

        when(teamService.getTeams(0, 10, null)).thenReturn(page);

        mockMvc.perform(get("/api/teams")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Team A"))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].name").value("Team B"));

        verify(teamService, times(1)).getTeams(0, 10, null);
    }

    /**
     * Test for fetching teams with invalid sorting field
     */
    @Test
    void testGetTeamsWithInvalidSortField() throws Exception {
        when(teamService.getTeams(0, 10, Collections.singletonList("invalidField"))).thenThrow(new IllegalArgumentException("Invalid field name for sorting: invalidField"));

        mockMvc.perform(get("/api/teams")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "invalidField"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid field name for sorting: invalidField"));

        verify(teamService, times(1)).getTeams(0, 10, Collections.singletonList("invalidField"));
    }

    /**
     * Test case for successfully adding a team
     */
    @Test
    void testAddTeamSuccess() throws Exception {
        // Creating a valid team object
        TeamDto teamDto = new TeamDto();
        teamDto.setId(1L);
        teamDto.setName("OGC Nice");
        teamDto.setAcronym("OGCN");
        teamDto.setBudget(50000000.0);
        // Optional 'players' field is left empty

        // mocking the service to return the same team when it's saved
        when(teamService.addTeam(any(TeamDto.class))).thenReturn(teamDto);

        // performing the POST request and verifying the response
        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(teamDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("OGC Nice"))
                .andExpect(jsonPath("$.acronym").value("OGCN"))
                .andExpect(jsonPath("$.budget").value(50000000.0));

        verify(teamService, times(1)).addTeam(any(TeamDto.class));
    }

    /**
     * Test case for adding a team with missing required fields
     * This test ensures that the validation works and returns a 400 Bad Request when required fields are missing
     */
    @Test
    void testAddTeamWithMissingFields() throws Exception {
        TeamDto teamDto = new TeamDto();
        teamDto.setAcronym("OGCN"); // Missing 'name' and 'budget'

        // performing the POST request with missing fields and verifying the response
        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(teamDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Team name is required"))
                .andExpect(jsonPath("$.errors.budget").value("Budget is required"));

        verify(teamService, times(0)).addTeam(any(TeamDto.class));
    }

    /**
     * Test case for adding a team with invalid budget (negative value)
     * This test ensures that validation catches the invalid budget value and returns an appropriate error message
     */
    @Test
    void testAddTeamWithInvalidBudget() throws Exception {
        TeamDto teamDto = new TeamDto();
        teamDto.setName("OGC Nice");
        teamDto.setAcronym("OGCN");
        teamDto.setBudget(-1000.0); // Invalid negative budget

        // performing the POST request with invalid budget and verifying the response
        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(teamDto))) // pass the teamDto as JSON
                .andExpect(status().isBadRequest()) // expect 400 Bad Request status
                .andExpect(jsonPath("$.errors.budget").value("Budget must be a positive value")); // Expect validation error for budget

        verify(teamService, times(0)).addTeam(any(TeamDto.class));
    }

}
