package ru.branihin.fqw.models;



import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "t_doctor")
public class Doctor {

    public Doctor(){}

    @Id
    @Column(name = "doctor_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "doctor_id")
    private User user;

    @OneToMany(mappedBy = "doctor")
    private Set<Schedule> schedules;


    private String speciality;
    private Boolean isWork;

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", user=" + user +
                ", speciality='" + speciality + '\'' +
                ", isWork=" + isWork +
                '}';
    }

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = schedules;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public Boolean getWork() {
        return isWork;
    }

    public void setWork(Boolean work) {
        isWork = work;
    }
}
