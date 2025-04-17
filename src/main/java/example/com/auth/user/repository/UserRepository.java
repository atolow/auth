package example.com.auth.user.repository;

import example.com.auth.user.domain.User;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    default User findByIdOrElseThrow(String username) {
        return findByUsername(username).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
