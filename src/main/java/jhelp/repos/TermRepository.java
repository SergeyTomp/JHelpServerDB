package jhelp.repos;

import jhelp.orm.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Integer> {
    Optional<Term> findById(Integer i);
    Optional<Term> findByTerm(String term);
}
