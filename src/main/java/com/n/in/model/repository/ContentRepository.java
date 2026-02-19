package com.n.in.model.repository;


import com.n.in.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    // Devuelve todos con status = 'initiated' y imageUrl = NULL
    List<Content> findByStatusAndImageUrlIsNull(String status);
}

