package com.matawan.equipefootball;

import com.matawan.equipefootball.dto.TeamDto;
import com.matawan.equipefootball.entity.Team;
import com.matawan.equipefootball.exception.ResourceNotFoundException;
import com.matawan.equipefootball.repository.TeamRepository;
import com.matawan.equipefootball.service.PlayerService;
import com.matawan.equipefootball.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private TeamService teamService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test for fetching a team by a valid id
     */
    @Test
    void testGetTeamByIdValid() {
        Team team = new Team();
        team.setId(1L);
        team.setName("OGC Nice");
        team.setPlayers(Collections.emptyList());

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        TeamDto result = teamService.getTeamById(1L);

        assertNotNull(result);
        assertEquals("OGC Nice", result.getName());
        verify(teamRepository, times(1)).findById(1L);
    }

    /**
     * Test for fetching a team by an invalid id
     */
    @Test
    void testGetTeamByIdInvalid() {
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            teamService.getTeamById(1L);
        });

        assertEquals("Team with id 1 not found", exception.getMessage());
        verify(teamRepository, times(1)).findById(1L);
    }

    /**
     * Test for fetching teams without sorting
     */
    @Test
    void testGetTeamsWithoutSorting() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team A");
        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team B");

        List<Team> teams = Arrays.asList(team1, team2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Team> teamPage = new PageImpl<>(teams, pageable, teams.size());

        when(teamRepository.findAll(any(Pageable.class))).thenReturn(teamPage);

        Page<TeamDto> result = teamService.getTeams(0, 10, null);

        assertEquals(2, result.getTotalElements());
        verify(teamRepository, times(1)).findAll(any(Pageable.class));
    }

    /**
     * Test for fetching teams with valid sorting
     */
    @Test
    void testGetTeamsWithValidSorting() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team A");
        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team B");

        List<Team> teams = Arrays.asList(team1, team2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Team> teamPage = new PageImpl<>(teams, pageable, teams.size());

        when(teamRepository.findAll(any(Pageable.class))).thenReturn(teamPage);

        Page<TeamDto> result = teamService.getTeams(0, 10, List.of("name"));

        assertEquals(2, result.getTotalElements());
        verify(teamRepository, times(1)).findAll(any(Pageable.class));
    }

    /**
     * Test for invalid sorting field
     */
    @Test
    void testGetTeamsWithInvalidSortingField() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            teamService.getTeams(0, 10, Collections.singletonList("invalidField"));
        });

        assertEquals("Invalid field name for sorting: invalidField", exception.getMessage());
    }

    /**
     * Test for adding a new team
     */
    @Test
    void testAddTeam() {
        Team team = new Team();
        team.setId(1L);
        team.setName("OGC Nice");

        when(teamRepository.save(any(Team.class))).thenReturn(team);

        TeamDto teamDto = new TeamDto();
        teamDto.setName("OGC Nice");

        TeamDto result = teamService.addTeam(teamDto);

        assertNotNull(result);
        assertEquals("OGC Nice", result.getName());
        verify(teamRepository, times(1)).save(any(Team.class));
    }
}
