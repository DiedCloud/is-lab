package org.example.islab.repository;

import org.example.islab.entity.AdminRequest;
import org.example.islab.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRequestRepository extends JpaRepository<AdminRequest, Long> {
    boolean existsByUser(User user);
}
