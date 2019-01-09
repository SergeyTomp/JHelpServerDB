package jhelp.orm;

import javax.persistence.*;

@Entity
@Table(name = "definition")
public class Definition {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "definition")
    private String definition;

//    @Column(name = "term_id")
//    private Integer term_id;

    @ManyToOne
    @JoinColumn(name = "term_id")
    private Term term;

    public Integer getId() {
        return id;
    }

    public String getDefinition() {
        return definition;
    }

//    public Integer getTerm_id() {
//        return term_id;
//    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public Definition() {}

    public Definition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return definition;
    }
}
