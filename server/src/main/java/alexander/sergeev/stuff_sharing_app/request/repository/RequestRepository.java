package alexander.sergeev.stuff_sharing_app.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long requesterId, Pageable pageable);

    List<Request> findByRequesterIdIsNot(Long requesterId, Pageable pageable);

    default Request findRequestById(Long requestId) {
        return findById(requestId).orElseThrow(() -> new NotFoundException("There's no request with id " + requestId));
    }

}
