package de.hawai.bicycle_tracking.server.astcore.bikemanagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISellingLocationDao extends JpaRepository<SellingLocation, Long> {
}