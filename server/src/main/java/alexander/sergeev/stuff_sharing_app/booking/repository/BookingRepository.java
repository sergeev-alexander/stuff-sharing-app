package alexander.sergeev.stuff_sharing_app.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    Optional<Booking> findById(Long bookingId);

    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(Long bookerId,
                                                              LocalDateTime now,
                                                              LocalDateTime sameNow,
                                                              Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId,
                                               LocalDateTime now,
                                               Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId,
                                                LocalDateTime now,
                                                Pageable pageable);

    List<Booking> findByBookerIdAndStatusIs(Long bookerId,
                                            BookingStatus status,
                                            Pageable pageable);

    List<Booking> findByItemIdIn(Collection<Long> ownerItemsIdList,
                                 Pageable pageable);

    List<Booking> findByItemIdInAndStartIsBeforeAndEndIsAfter(List<Long> ownerItemIdList,
                                                              LocalDateTime now,
                                                              LocalDateTime sameNow,
                                                              Pageable pageable);

    List<Booking> findByItemIdInAndEndIsBefore(List<Long> ownerItemIdList,
                                               LocalDateTime now,
                                               Pageable pageable);

    List<Booking> findByItemIdInAndStartIsAfter(List<Long> ownerItemIdList,
                                                LocalDateTime now,
                                                Pageable pageable);

    List<Booking> findByItemIdInAndStatusIs(List<Long> ownerItemIdList,
                                            BookingStatus status,
                                            Pageable pageable);

    Optional<Booking> findFirstByItemIdAndStartIsAfterAndStatusIs(Long itemId,
                                                                  LocalDateTime now,
                                                                  BookingStatus bookingStatus,
                                                                  Sort sort);

    Optional<Booking> findFirstByItemIdAndStartIsBeforeAndStatusIs(Long itemId,
                                                                   LocalDateTime now,
                                                                   BookingStatus bookingStatus,
                                                                   Sort sort);

    Boolean existsByBookerIdAndItemIdAndEndIsBeforeAndStatusIs(Long bookerId,
                                                               Long itemId,
                                                               LocalDateTime now,
                                                               BookingStatus bookingStatus);

}
