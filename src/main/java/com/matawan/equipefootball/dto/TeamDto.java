package com.matawan.equipefootball.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class TeamDto {
    private Long id;

    @NotBlank(message = "Team name is required")
    private String name;

    @NotBlank(message = "Acronym is required")
    private String acronym;

    @NotNull(message = "Budget is required")
    @Min(value = 0, message = "Budget must be a positive value")
    private Double budget;

    private List<PlayerDto> players;

    public TeamDto() {
    }

    public TeamDto(Long id, String name, String acronym, double budget, List<PlayerDto> players) {
        this.id = id;
        this.name = name;
        this.acronym = acronym;
        this.budget = budget;
        this.players = players;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public List<PlayerDto> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDto> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "TeamDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", acronym='" + acronym + '\'' +
                ", budget=" + budget +
                ", players=" + players +
                '}';
    }

}