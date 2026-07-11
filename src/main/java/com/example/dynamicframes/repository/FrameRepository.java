package com.example.dynamicframes.repository;

import com.example.dynamicframes.model.FrameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrameRepository extends JpaRepository<FrameEntity, Long> {

    @Query("SELECT f FROM FrameEntity f WHERE f.parentFrame IS NULL ORDER BY f.displayOrder ASC")
    List<FrameEntity> findRootFrames();
}
