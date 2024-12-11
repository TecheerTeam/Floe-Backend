package project.floe.domain.record.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import project.floe.domain.record.entity.Tag;
import project.floe.domain.record.entity.Tags;
import project.floe.domain.record.repository.TagJpaRepository;
import project.floe.global.config.TestSecurityConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class TagServiceTest {

    @Autowired
    private TagService tagService;

    @MockBean
    private TagJpaRepository tagJpaRepository;

    @DisplayName("태그를 생성합니다")
    @Test
    void 태그_생성() {
        String test = "test";
        Tag mockTag = Tag.builder().tagName(test).build();

        Tag tag = tagService.createTag(test);

        assertThat(tag).isInstanceOf(Tag.class);
        assertThat(tag.getTagName()).isEqualTo(test);
    }

    @DisplayName("태그들을 String으로 넘겨주면 이미 존재하는지 여부를 판별해 새로 생성하거나 기존걸 가져와 리스트에 담습니다")
    @Test
    void 태그_들_생성() {
        List<String> tagNames = List.of("test1", "test2", "test3");

        Tags tags = tagService.createTags(tagNames);
        // 각 태그에 대해 findByTagName이 Optional.empty()를 반환 (존재하지 않음)
        when(tagJpaRepository.findByTagName(any(String.class))).thenReturn(Optional.empty());

        // save 메서드는 받은 Tag 객체를 그대로 반환하도록 설정
        when(tagJpaRepository.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            return Tag.builder().tagName(tag.getTagName()).build();
        });
        assertThat(tags).isInstanceOf(Tags.class);
        assertThat(tags.getTagNames()).containsExactlyElementsOf(tagNames);
    }
}