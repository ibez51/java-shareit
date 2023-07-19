package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByStartDesc(Integer userId);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Integer userId,
                                                                LocalDateTime dateTimeNow);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Integer userId);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Integer userId,
                                                                   LocalDateTime dateTimeNow);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Integer userId,
                                                                  LocalDateTime dateTimeNow);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Integer userId,
                                                                               LocalDateTime dateTimeNow,
                                                                               LocalDateTime localDateTimeRightNow);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Integer userId,
                                                             BookingStatus status);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Integer userId,
                                                               LocalDateTime dateTimeNow);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Integer userId,
                                                                            LocalDateTime dateTimeNow,
                                                                            LocalDateTime localDateTimeRightNow);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Integer userId,
                                                          BookingStatus status);

    @Query(value = "WITH cte AS (SELECT *, ROW_NUMBER() OVER (PARTITION BY ITEM_ID ORDER BY START_DATE DESC) AS rn FROM Bookings b " +
            "WHERE b.ITEM_ID IN :items AND status NOT IN :statusExclude AND START_DATE < :dateTimeNow)" +
            "SELECT * FROM cte WHERE rn = 1;", nativeQuery = true)
    List<Booking> findLastBooking(@Param("items") Set<Integer> itemId,
                                  @Param("statusExclude") Set<String> statusExclude,
                                  @Param("dateTimeNow") LocalDateTime dateTimeNow);

    @Query(value = "WITH cte AS (SELECT *, ROW_NUMBER() OVER (PARTITION BY ITEM_ID ORDER BY START_DATE ASC) AS rn FROM Bookings b " +
            "WHERE b.ITEM_ID IN :items AND status NOT IN :statusExclude AND START_DATE > :dateTimeNow)" +
            "SELECT * FROM cte WHERE rn = 1;", nativeQuery = true)
    List<Booking> findNextBooking(@Param("items") Set<Integer> itemId,
                                  @Param("statusExclude") Set<String> statusExclude,
                                  @Param("dateTimeNow") LocalDateTime dateTimeNow);

    @Query(value = "select case when count(*) > 0 then true else false end from bookings booking " +
            "where booking.item_id = :itemId " +
            "and booking.booker_id = :userId " +
            "and booking.status NOT IN :excludeStatus " +
            "and booking.end_date < :dateTimeNow", nativeQuery = true)
    boolean existsApprovedBookingInPast(@Param("itemId") Integer itemId,
                                        @Param("userId") Integer userId,
                                        @Param("excludeStatus") Set<String> status,
                                        @Param("dateTimeNow") LocalDateTime dateTimeNow);
}
