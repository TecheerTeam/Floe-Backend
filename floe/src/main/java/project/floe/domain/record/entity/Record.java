package project.floe.domain.record.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import project.floe.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE record SET is_deleted = true WHERE record_id = ?")
public class Record extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    /*
    게시글 작성자의 정보를 담는 필드
    추후 User개발 완료시 해당 코드로 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    */

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false)
    private RecordType recordType;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> medias;

    @Embedded
    private RecordTags recordTags;

    public void updateRecord(String title, String content, RecordType recordType, Tags tags, List<Media> medias) {
        this.title = title;
        this.content = content;
        this.recordType = recordType;
        recordTags.clear();
        addTag(tags);
        addMedia(medias);
    }

    public void addTag(Tags tags) {
        recordTags.add(this, tags);
    }

    public List<Long> getMediaIds(){
        return medias.stream()
                .map(Media::getId)
                .toList();
    }

    public void addMedia(List<Media> updatedMedias) {
        medias.clear();
        medias.addAll(updatedMedias);
    }

    public List<String> getTagNames() {
        return this.recordTags.getTagNames();
    }
}
