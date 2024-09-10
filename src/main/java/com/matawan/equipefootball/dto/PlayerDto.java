package com.matawan.equipefootball.dto;

public class PlayerDto {
    private Long id;
    private String name;
    private String position;

    public PlayerDto(Long id, String name, String position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public PlayerDto() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "PlayerDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}
