package project.floe.domain.record_like.dto.response;

import java.util.List;
import lombok.Getter;
import project.floe.domain.record_like.dto.item.RecordLikeItem;
import project.floe.domain.user.entity.User;

@Getter
public class GetRecordLikeListResponseDto {
    private final List<RecordLikeItem> likeList;

    public GetRecordLikeListResponseDto(List<User> userList) {
        this.likeList = RecordLikeItem.toList(userList);
    }
}
