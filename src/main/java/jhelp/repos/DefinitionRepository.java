package jhelp.repos;

import jhelp.orm.Definition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DefinitionRepository extends JpaRepository<Definition, Integer> {
    Optional<Definition> findDefinitionById(Integer i);
}

