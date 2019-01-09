package jhelp.orm;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "term")
public class Term {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "term")
    private String term;

    public Term() {}

    public Term(String term) {
        this.term = term;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "term", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Definition> definitions = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public String getTerm() {
        return term;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public void addDefinition(Definition definition) {
        definitions.add(definition);
        definition.setTerm(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term that = (Term) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Term{" +
                "term='" + term + '\'' +
                '}';
    }
}
