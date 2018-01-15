package hello;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import hello.User;

import java.util.List;
import java.util.Optional;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ToolsRepository extends JpaRepository<Tools, Long> {
    Optional<Tools> findByName(String name);
    Tools findByNfc(String nfc);

}
