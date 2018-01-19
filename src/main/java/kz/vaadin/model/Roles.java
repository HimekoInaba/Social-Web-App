package kz.vaadin.model;


import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Roles")
public class Roles {

    @Id
    //@SequenceGenerator(name="hibernate_sequence",sequenceName="MY_SEQ_GEN", initialValue=205, allocationSize=12)
    //@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Roles() {
    }

    public Roles(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Roles{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
