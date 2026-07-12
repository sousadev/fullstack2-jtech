package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskGroupRepository extends JpaRepository<TaskGroupEntity, UUID> {
    List<TaskGroupEntity> findByUserId(UUID userId);
}
