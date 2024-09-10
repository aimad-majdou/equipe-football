package com.matawan.equipefootball;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matawan.equipefootball.dto.TeamDto;
import com.matawan.equipefootball.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test") // Activates 'test' profile for this test
public class TeamControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TeamRepository teamRepository;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); // Set up MockMvc with the full application context
        teamRepository.deleteAll(); // Clean the database before each test
    }

    /**
     * Test for successfully adding a valid team through the controller
     */
    @Test
    void testAddValidTeam() throws Exception {
        // Arrange: create a valid TeamDto object
        TeamDto teamDto = new TeamDto();
        teamDto.setName("OGC Nice");
        teamDto.setAcronym("OGCN");
        teamDto.setBudget(10000000.0);

        // Act & Assert: perform the POST request and verify the result
        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDto))) // xonvert teamDto to JSON
                .andExpect(status().isCreated()) // expect 201 Created status
                .andExpect(jsonPath("$.name").value("OGC Nice"))
                .andExpect(jsonPath("$.acronym").value("OGCN"))
                .andExpect(jsonPath("$.budget").value(10000000.0));

        // verify that the team was saved in the database
        assertEquals(1, teamRepository.count());
    }

    /**
     * Test for adding a team with missing required fields
     */
    @Test
    void testAddTeamWithMissingFields() throws Exception {
        // Arrange: create a TeamDto object with missing fields (budget and name)
        TeamDto teamDto = new TeamDto();
        teamDto.setAcronym("OGCN");

        // Act & Assert: perform the POST request and expect validation errors
        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDto)))
                .andExpect(status().isBadRequest()) // Expect 400 Bad Request
                .andExpect(jsonPath("$.errors.name").value("Team name is required")) // Missing 'name'
                .andExpect(jsonPath("$.errors.budget").value("Budget is required")); // Missing 'budget'

        // verify that the team was not saved in the database
        assertEquals(0, teamRepository.count());
    }

    /**
     * Test for adding a team with a negative budget
     */
    @Test
    void testAddTeamWithNegativeBudget() throws Exception {
        // Arrange: create a TeamDto object with a negative budget
        TeamDto teamDto = new TeamDto();
        teamDto.setName("OGC Nice");
        teamDto.setAcronym("OGCN");
        teamDto.setBudget(-5000.0); // Negative budget

        // Act & Assert: perform the POST request and expect a validation error
        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDto)))
                .andExpect(status().isBadRequest()) // Expect 400 Bad Request
                .andExpect(jsonPath("$.errors.budget").value("Budget must be a positive value")); // Expect validation error for budget

        // Verify that the team was not saved in the database
        assertEquals(0, teamRepository.count());
    }

    /**
     * Test for fetching a team by ID
     */
    @Test
    void testGetTeamById() throws Exception {
        // Arrange: add a team directly to the repository
        TeamDto teamDto = new TeamDto();
        teamDto.setName("OGC Nice");
        teamDto.setAcronym("OGCN");
        teamDto.setBudget(10000000.0);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDto)))
                .andExpect(status().isCreated());

        // Act & Assert: perform a GET request to fetch the team by ID
        mockMvc.perform(get("/api/teams/{id}", 1L)) // Assuming the ID is 1
                .andExpect(status().isOk()) // Expect 200 OK status
                .andExpect(jsonPath("$.name").value("OGC Nice"))
                .andExpect(jsonPath("$.acronym").value("OGCN"))
                .andExpect(jsonPath("$.budget").value(10000000.0));
    }

    /**
     * Test for fetching a team by an invalid ID
     */
    @Test
    void testGetTeamByInvalidId() throws Exception {
        // Act & Assert: perform a GET request to fetch a team by an invalid ID
        mockMvc.perform(get("/api/teams/{id}", 9999L)) // invalid ID
                .andExpect(status().isNotFound()) // expect 404 Not Found status
                .andExpect(jsonPath("$.message").value("Team with id 9999 not found"));
    }
}
