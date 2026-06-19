package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND " +
            "b.status = 'APPROVED' AND b.end < :now " +
            "ORDER BY b.end DESC")
    Optional<Booking> findLastApprovedBookingByItemId(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND " +
            "b.status = 'APPROVED' AND b.start > :now " +
            "ORDER BY b.start ASC")
    Optional<Booking> findNextApprovedBookingByItemId(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.item.id = :itemId AND " +
            "b.booker.id = :userId AND b.status = 'APPROVED' AND b.end < :now")
    boolean existsApprovedPastBooking(@Param("itemId") Long itemId,
                                      @Param("userId") Long userId,
                                      @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND " +
            "b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByBooker(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < :now")
    List<Booking> findPastByBooker(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :now")
    List<Booking> findFutureByBooker(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = 'WAITING'")
    List<Booking> findWaitingByBooker(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = 'REJECTED'")
    List<Booking> findRejectedByBooker(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND " +
            "b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < :now")
    List<Booking> findPastByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > :now")
    List<Booking> findFutureByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = 'WAITING'")
    List<Booking> findWaitingByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = 'REJECTED'")
    List<Booking> findRejectedByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND " +
            "b.status = 'APPROVED' AND b.end < :now")
    List<Booking> findLastApprovedBookingsByItemIds(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND " +
            "b.status = 'APPROVED' AND b.start > :now")
    List<Booking> findNextApprovedBookingsByItemIds(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now);
}