package com.matawan.equipefootball;

import com.matawan.equipefootball.dto.TeamDto;
import com.matawan.equipefootball.entity.Team;
import com.matawan.equipefootball.repository.TeamRepository;
import com.matawan.equipefootball.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test") // Activates 'test' profile for this test
public class TeamServiceIntegrationTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        teamRepository.deleteAll(); // clean the database before each test
    }

    @Test
    void testAddTeam() {
        // Arrange: create a valid TeamDto object
        TeamDto teamDto = new TeamDto();
        teamDto.setName("OGC Nice");
        teamDto.setAcronym("OGCN");
        teamDto.setBudget(10000000.0); // Set budget

        // Act: add the team through the service
        TeamDto savedTeam = teamService.addTeam(teamDto);

        // Assert: verify the team was added correctly
        Optional<Team> teamOptional = teamRepository.findById(savedTeam.getId());
        assertTrue(teamOptional.isPresent()); // Verify team was saved in the database
        assertEquals("OGC Nice", teamOptional.get().getName()); // Check team name
        assertEquals("OGCN", teamOptional.get().getAcronym()); // Check acronym
        assertEquals(10000000.0, teamOptional.get().getBudget()); // Check budget
    }

    @Test
    void testGetTeamById() {
        // Arrange: create and save a team
        Team team = new Team();
        team.setName("Olympique Lyon");
        team.setAcronym("OL");
        team.setBudget(20000000.0);
        team = teamRepository.save(team);

        // Act: retrieve the team by ID
        TeamDto teamDto = teamService.getTeamById(team.getId());

        // Assert: verify the team was retrieved correctly
        assertNotNull(teamDto);
        assertEquals("Olympique Lyon", teamDto.getName());
        assertEquals("OL", teamDto.getAcronym());
        assertEquals(20000000.0, teamDto.getBudget());
    }

    @Test
    void testGetAllTeamsWithPagination() {
        // Arrange: add multiple teams
        TeamDto teamDto1 = new TeamDto();
        teamDto1.setName("OGC Nice");
        teamDto1.setAcronym("OGCN");
        teamDto1.setBudget(10000000.0);

        TeamDto teamDto2 = new TeamDto();
        teamDto2.setName("PSG");
        teamDto2.setAcronym("PSG");
        teamDto2.setBudget(20000000.0);

        TeamDto teamDto3 = new TeamDto();
        teamDto3.setName("Olympique Lyon");
        teamDto3.setAcronym("OL");
        teamDto3.setBudget(15000000.0);

        // save the teams
        teamService.addTeam(teamDto1);
        teamService.addTeam(teamDto2);
        teamService.addTeam(teamDto3);

        // Act: retrieve all teams with pagination (page 0, size 2)
        Page<TeamDto> pageResult = teamService.getTeams(0, 2, null);

        // Assert: verify that the first page contains two teams
        assertEquals(2, pageResult.getContent().size()); // verify we got 2 teams in the first page
        assertEquals(3, pageResult.getTotalElements()); // verify total number of teams is 3
        assertEquals(2, pageResult.getTotalPages()); // verify that there are 2 pages
    }

    @Test
    void testGetAllTeamsWithSorting() {
        // Arrange: add multiple teams
        TeamDto teamDto1 = new TeamDto();
        teamDto1.setName("OGC Nice");
        teamDto1.setAcronym("OGCN");
        teamDto1.setBudget(10000000.0);

        TeamDto teamDto2 = new TeamDto();
        teamDto2.setName("Paris Saint-Germain");
        teamDto2.setAcronym("PSG");
        teamDto2.setBudget(20000000.0);

        TeamDto teamDto3 = new TeamDto();
        teamDto3.setName("Olympique Lyon");
        teamDto3.setAcronym("OL");
        teamDto3.setBudget(15000000.0);

        // Save the teams
        teamService.addTeam(teamDto1);
        teamService.addTeam(teamDto2);
        teamService.addTeam(teamDto3);

        // Act: retrieve all teams sorted by name in descending order
        Page<TeamDto> sortedResult = teamService.getTeams(0, 3, List.of("-name"));

        // Assert: verify sorting order
        assertEquals("Paris Saint-Germain", sortedResult.getContent().get(0).getName()); // PSG should come first
        assertEquals("Olympique Lyon", sortedResult.getContent().get(1).getName()); // then OL
        assertEquals("OGC Nice", sortedResult.getContent().get(2).getName()); // then OGC Nice
    }
}
