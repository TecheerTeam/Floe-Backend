package project.floe.domain.record_like.dto.item;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import project.floe.domain.user.entity.User;

@Getter
@Setter
public class RecordLikeItem {
    private Long userId;
    private String userName;
    private String profileImage;

    public RecordLikeItem(User user) {
        this.userId = user.getId();
        this.userName = user.getNickname();
        this.profileImage = user.getProfileImage();
    }

    public static List<RecordLikeItem> toList(List<User> userList) {

        return userList.stream()
                .map(RecordLikeItem::new)
                .collect(Collectors.toList());
    }
}
