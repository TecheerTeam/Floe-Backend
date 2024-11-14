package project.floe.domain.record.service;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.floe.domain.record.entity.Tag;
import project.floe.domain.record.entity.Tags;
import project.floe.domain.record.repository.TagJpaRepository;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TagService {

    private final TagJpaRepository tagRepository;

    @Transactional
    public Tag createTag(String tagName){
        Tag newTag = Tag.builder()
                .tagName(tagName)
                .build();
        return tagRepository.save(newTag);
    }

    public Tags createTags(List<String> tagNames){
        List<Tag> newTags = new ArrayList<>();
        for (String tagName: tagNames){
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> createTag(tagName));
            newTags.add(tag);
        }
        return new Tags(newTags);
    }

    public Tags findTagListByTagName(List<String> tagNames){
        List<Tag> foundTags = tagNames.stream()
                .map(this::findTagByTagName)
                .toList();
        return new Tags(foundTags);
    }

    public Tag findTagByTagName(String tagName){
        return tagRepository.findTagByTagName(tagName);
    }
}
