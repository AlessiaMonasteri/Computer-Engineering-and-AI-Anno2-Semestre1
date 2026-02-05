package AlessiaMonasteri.BagECommerce.entities;

import AlessiaMonasteri.BagECommerce.entities.enums.Subject;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subject subject;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Contact() {}

    public Contact(User user, String message, Subject subject) {
        this.user = user;
        this.message = message;
        this.subject = subject;
    }

    public UUID getId() {
            return id;
        }

        public Subject getSubject() {
            return subject;
        }

        public void setSubject(Subject subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "Contact{" +
                    "id=" + id +
                    ", subject=" + subject +
                    ", message='" + message + '\'' +
                    ", createdAt=" + createdAt +
                    ", user=" + user +
                    '}';
        }
}

