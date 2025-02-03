package org.example.islab.repository;

import org.example.islab.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository  extends JpaRepository<Location, Long> {
}
