package project.floe.domain.record.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.floe.domain.record.entity.Tag;
import project.floe.domain.record.entity.Tags;
import project.floe.domain.record.repository.TagJpaRepository;

@SpringBootTest
class TagServiceTest {

    private TagJpaRepository repository;
    @Autowired
    private TagService tagService;

    @DisplayName("태그를 생성합니다")
    @Test
    @Transactional
    void 태그_생성(){
        // given
        String test = "test";
        // when
        Tag tag = tagService.createTag(test);
        // then
        assertThat(tag).isInstanceOf(Tag.class);
        assertThat(tag.getTagName()).isEqualTo(test);
    }

    @DisplayName("태그들을 String으로 넘겨주면 이미 존재하는지 여부를 판별해 새로 생성하거나 기존걸 가져와 리스트에 담습니다")
    @Test
    @Transactional
    void 태그_들_생성(){
        // given
        List<String> tagNames = List.of("test1", "test2", "test3");
        // when
        Tags tags = tagService.createTags(tagNames);
        // then
        assertThat(tags).isInstanceOf(Tags.class);
        assertThat(tags.getTagNames()).containsExactlyElementsOf(tagNames);
    }

}