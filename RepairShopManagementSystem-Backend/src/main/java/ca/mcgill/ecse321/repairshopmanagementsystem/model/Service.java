package ca.mcgill.ecse321.repairshopmanagementsystem.model;

import javax.persistence.Entity;
import java.util.Set;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

@Entity
public class Service {
    private ServiceType serviceType;

    private void setServiceType(ServiceType value) {
        this.serviceType = value;
    }

    private ServiceType getServiceType() {
        return this.serviceType;
    }

    private Integer serviceId;

    private void setServiceId(Integer value) {
        this.serviceId = value;
    }

    @Id
    private Integer getServiceId() {
        return this.serviceId;
    }

    private Set<Assistant> assistant;

    @ManyToMany(mappedBy = "service")
    public Set<Assistant> getAssistant() {
        return this.assistant;
    }

    public void setAssistant(Set<Assistant> assistants) {
        this.assistant = assistants;
    }

    private Appointment appointment;

    @OneToOne(optional = false)
    public Appointment getAppointment() {
        return this.appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

}
