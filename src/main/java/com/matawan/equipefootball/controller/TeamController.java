package com.matawan.equipefootball.controller;

import com.matawan.equipefootball.dto.TeamDto;
import com.matawan.equipefootball.service.TeamService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private static final Logger logger = LoggerFactory.getLogger(TeamController.class); // logger instance
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Get a team by its id
     *
     * @param id the id of the team
     * @return the team with the given id
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable Long id) {
        logger.info("Received request to fetch team with id: {}", id);
        TeamDto teamDTO = teamService.getTeamById(id);
        logger.info("Returning team: {}", teamDTO.getName());
        return ResponseEntity.ok(teamDTO);
    }

    /**
     * Get a paginated list of teams with optional sorting criteria
     *
     * @param page the page number, default is 0
     * @param size the number of items per page, default is 10
     * @param sortBy an optional list of fields to sort by (prefix with (-) for descending order)
     * @return a paginated list of teams
     */
    @GetMapping
    public ResponseEntity<?> getTeams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> sortBy
    ) {
        logger.info("Received request to fetch teams with page: {}, size: {}, sortBy: {}", page, size, sortBy);
        try {
            Page<TeamDto> teams = teamService.getTeams(page, size, sortBy);
            logger.info("Returning {} teams", teams.getTotalElements());
            return ResponseEntity.ok(teams);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid sort field provided: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Add a new team
     * @param teamDto the team to add
     * @return the added team
     */
    @PostMapping
    public ResponseEntity<TeamDto> addTeam(@RequestBody @Valid TeamDto teamDto) {
        logger.info("Received request to add new team: {}", teamDto.getName());
        TeamDto savedTeam = teamService.addTeam(teamDto);
        logger.info("Added team with id: {}", savedTeam.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTeam);
    }
}
