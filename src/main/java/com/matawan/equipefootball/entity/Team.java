package com.matawan.equipefootball.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String acronym;

    @Column(nullable = false)
    private Double budget;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Player> players;

    public Team() {
    }

    public Team(Long id, String name, String acronym, Double budget, List<Player> players) {
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

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", acronym='" + acronym + '\'' +
                ", budget=" + budget +
                ", players=" + players +
                '}';
    }

}
