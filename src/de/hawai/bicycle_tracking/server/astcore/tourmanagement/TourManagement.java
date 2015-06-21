package de.hawai.bicycle_tracking.server.astcore.tourmanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hawai.bicycle_tracking.server.astcore.bikemanagement.IBike;
import de.hawai.bicycle_tracking.server.astcore.bikemanagement.IBikeManagement;
import de.hawai.bicycle_tracking.server.astcore.customermanagement.IUser;
import de.hawai.bicycle_tracking.server.utility.value.GPS;

@Component
public class TourManagement implements ITourManagement{

    @Autowired
    private ITourDao tourDao;

    @Autowired
    private IBikeManagement bikeManagement;

    @Override
    public Optional<ITour> getTourById(UUID id) {
        return tourDao.getById(id);
    }

    @Override
    public List<ITour> getToursByUser(IUser user) {
        List<ITour> tours = new ArrayList<>();
        for (IBike bike: bikeManagement.findByOwner(user)){
            tours.addAll(tourDao.findByBikeId(bike.getId()));
        }
        return tours;
    }

    @Override
    public ITour addTour(String name, IBike bike, Date startAt, Date finishedAt, List<GPS> waypoints, double lengthInKm)
            throws AddTourFailedException {
        try {
            Tour tour = new Tour(name, bike.getId(), startAt, finishedAt, waypoints, lengthInKm);
            System.out.println("New Tour: " + tour.toString());
            tourDao.save(tour);
            System.out.println("New Tour after save: " + tour.toString());
            updateMileageOfBike(bike);
            return tour;
        } catch (RuntimeException e){
            e.printStackTrace();
            throw new AddTourFailedException(e.getMessage());
        }
    }

    @Override
    public void updateTour(ITour inTour, String name, IBike bike, Date startAt, Date finishedAt, List<GPS> waypoints, double lengthInKm)
            throws UpdateTourFailedException {
        try {
            IBike oldBike = bikeManagement.getBikeById(inTour.getBike()).get();
            Tour tour = (Tour) inTour;
            tour.setName(name);
            tour.setBike(bike.getId());
            tour.setStartAt(startAt);
            tour.setFinishedAt(finishedAt);
            tour.updateWay(waypoints, lengthInKm);
            tour.setUpdatedAt(new Date());
            tourDao.save(tour);
            updateMileageOfBike(bike);
            if(!oldBike.equals(bike)){
                updateMileageOfBike(oldBike);
            }
        } catch (RuntimeException e){
            throw new UpdateTourFailedException(e.getMessage());
        }
    }

    @Override
    public void deleteTour(ITour tour) {
        try {
            tourDao.delete((Tour) tour);
        } catch (RuntimeException e){}
    }

    /*
     * Setzt die Laufleistung eines Fahrrads neu, anhand aller gefahrenen Strecken.
     * Eine einfache addition langt nicht, da das dann nicht ThreadSafe wäre.
     * So ist es auch nicht ganz ThreadSafe, ist aber selbstkorrigierend.
     */
    private void updateMileageOfBike(IBike bike){
        double mileAge = 0.0;
        for (ITour tour: tourDao.findByBikeId(bike.getId())) {
            mileAge += tour.getLengthInKm();
        }
        bikeManagement.updateBikesMileAge(bike, mileAge);
    }
}
