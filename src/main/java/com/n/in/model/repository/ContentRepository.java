package com.n.in.model.repository;

import com.n.in.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByStatusAndImageUrlIsNull(String status);

    List<Content> findByStatusInOrderByCreatedDesc(List<String> statuses);
}
