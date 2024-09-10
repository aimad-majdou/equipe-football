package com.matawan.equipefootball.service;

import com.matawan.equipefootball.dto.PlayerDto;
import com.matawan.equipefootball.entity.Player;
import org.springframework.stereotype.Service;


@Service
public class PlayerService {

    public PlayerDto convertToDto(Player player) {
        return new PlayerDto(player.getId(), player.getName(), player.getPosition());
    }

    public Player convertToEntity(PlayerDto playerDto) {
        return new Player(playerDto.getId(), playerDto.getName(), playerDto.getPosition());
    }
}
