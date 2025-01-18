package project.floe.domain.record_save.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import project.floe.domain.record.entity.Record;
import project.floe.domain.user.entity.User;
import project.floe.entity.BaseEntity;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "record_id"})
})
public class RecordSave extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    public RecordSave(User user,Record record){
        this.user = user;
        this.record = record;
    }
}
