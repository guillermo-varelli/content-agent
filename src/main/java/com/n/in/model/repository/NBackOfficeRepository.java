package com.n.in.model.repository;


import com.n.in.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NBackOfficeRepository extends JpaRepository<Content, Long> {

    List<Content> findByStatusInOrderByCreatedDesc(List<String> statuses);
}

