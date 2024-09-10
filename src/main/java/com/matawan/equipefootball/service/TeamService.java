package com.matawan.equipefootball.service;

import com.matawan.equipefootball.dto.PlayerDto;
import com.matawan.equipefootball.dto.TeamDto;
import com.matawan.equipefootball.entity.Player;
import com.matawan.equipefootball.entity.Team;
import com.matawan.equipefootball.exception.ResourceNotFoundException;
import com.matawan.equipefootball.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class); // logger instance for TeamService


    private final TeamRepository teamRepository;
    private final PlayerService playerService;

    private static final List<String> SORT_FIELDS = List.of("name", "acronym", "budget");

    public TeamService(TeamRepository teamRepository, PlayerService playerService) {
        this.teamRepository = teamRepository;
        this.playerService = playerService;
    }

    /**
     * Get a team by its id
     *
     * @param id the id of the team
     * @return the team with the given id
     * @throws ResourceNotFoundException if the team with the given id is not found
     */
    public TeamDto getTeamById(Long id) {
        logger.info("Fetching team with id {}...", id);
        Team team = teamRepository.findById(id).orElseThrow(() -> {
            logger.error("Team with id {} not found", id);
            return new ResourceNotFoundException(String.format("Team with id %d not found", id));
        });
        logger.info("Team with id {} found: {}", id, team.toString());
        return convertToDto(team);
    }

    /**
     * Get a paginated list of teams with optional sorting criteria
     *
     * @param page the page number
     * @param size the number of items per page
     * @param sortBy an optional list of fields to sort by (prefix with (-) for descending order)
     * @return a paginated list of teams
     * @throws IllegalArgumentException if an invalid field is provided for sorting
     */
    public Page<TeamDto> getTeams(int page, int size, List<String> sortBy) {
        logger.info("Fetching teams with page {}, size {}, sortBy: {}...", page, size, sortBy);
        Pageable pageable;
        // handle the case when sortBy is null by providing a default sorting criterion
        if (sortBy == null || sortBy.isEmpty()) {
            // no sorting applied if sortBy is null or empty
            logger.info("No sorting criteria provided, fetching teams without sorting.");
            pageable = PageRequest.of(page, size); // No sorting
        } else {
            // apply sorting criteria if sortBy is provided
            pageable = PageRequest.of(page, size, Sort.by(getSortOrders(sortBy)));
        }
        Page<TeamDto> result = teamRepository.findAll(pageable).map(this::convertToDto);
        logger.info("Fetched {} teams", result.getTotalElements());
        return result;
    }

    /**
     * Add a new team
     *
     * @param teamDto the team to add
     * @return the added team
     */
    public TeamDto addTeam(TeamDto teamDto) {
        logger.info("Adding new team: {}", teamDto);
        Team team = convertToEntity(teamDto);
        team = teamRepository.save(team);
        logger.info("Team added with id: {}", team.getId());
        return convertToDto(team);
    }

    /**
     * Get a list of sorting fields that are valid for sorting
     * This method makes sure that the field names provided in the sortBy list are valid fields for sorting, to avoid exceptions
     * @return list of valid sort fields
     */
    private List<String> getValidSortFields() {
        // get all declared fields of the Team entity class
        Field[] fields = Team.class.getDeclaredFields();

        // make sure that the declared fields include the allowed fields for sorting we have defined in the allowedFields list
        return Arrays.stream(fields).map(Field::getName).filter(SORT_FIELDS::contains).toList();
    }

    /**
     * Convert a list of sorting fields to a list of Sort.Order
     * The field names prefixed with a minus sign (-) indicate descending order, while those without the minus sign indicate ascending order
     * Example: ["name", "-budget"] will be converted to [Sort.Order.asc("name"), Sort.Order.desc("budget")]
     *
     * @param sortBy list of strings with either a field name or a field name prefixed with a minus sign (-) to indicate descending order
     * @return list of Sort.Order that represents the sorting criteria
     * @throws IllegalArgumentException if an invalid field is provided for sorting.
     */
    private List<Sort.Order> getSortOrders(List<String> sortBy) {
        return sortBy.stream().map(field -> {
            boolean isDescending = field.startsWith("-");
            String fieldName = isDescending ? field.substring(1) : field;

            // validate the field name
            // if a sorting criterion is provided in the SORT_FIELDS list but it doesn't match any field in the Team entity, Spring Data JPA will throw an exception
            // to avoid this, we validate the field name first using getValidSortFields
            if (!getValidSortFields().contains(fieldName)) {
                logger.error("Invalid sorting field: {}", fieldName);
                throw new IllegalArgumentException("Invalid field name for sorting: " + fieldName);
            }

            logger.info("Sorting by field: {}, direction: {}", fieldName, isDescending ? "DESC" : "ASC");

            // Apply descending or ascending order
            return isDescending ? Sort.Order.desc(fieldName) : Sort.Order.asc(fieldName);
        }).collect(Collectors.toList());
    }

    private TeamDto convertToDto(Team team) {
        TeamDto teamDto = new TeamDto();
        teamDto.setId(team.getId());
        teamDto.setName(team.getName());
        teamDto.setAcronym(team.getAcronym());
        teamDto.setBudget(team.getBudget());

        // handle the case where players might be null
        List<PlayerDto> playerDTOs = team.getPlayers() != null ?
                team.getPlayers().stream().map(playerService::convertToDto).toList() :
                Collections.emptyList();  // return an empty list if players are null
        teamDto.setPlayers(playerDTOs);

        logger.info("Converted Team entity to TeamDto: {}", teamDto.getName());

        return teamDto;
    }

    private Team convertToEntity(TeamDto teamDto) {
        Team team = new Team();
        team.setId(teamDto.getId());
        team.setName(teamDto.getName());
        team.setAcronym(teamDto.getAcronym());

        // ensure we are only setting a non-null budget
        if (teamDto.getBudget() != null) {
            team.setBudget(teamDto.getBudget());
        }

        // handle the case where players might be null
        List<Player> players = teamDto.getPlayers() != null ?
                teamDto.getPlayers().stream().map(playerService::convertToEntity).toList() :
                Collections.emptyList();  // return an empty list if players are null
        team.setPlayers(players);

        logger.info("Converted TeamDto to Team entity: {}", team.getName());

        return team;
    }

}
