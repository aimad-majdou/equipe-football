package com.matawan.equipefootball.repository;

import com.matawan.equipefootball.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository  extends JpaRepository<Team, Long> {
}
