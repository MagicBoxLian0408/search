package kr.magicbox.search.adapter.out.elasticsearch.document;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@Document(indexName = "creator-index")
@Setting(settingPath = "elasticsearch/creator-settings.json")
public class CreatorDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long, name = "creator_id")
    private Long creatorId;

    @Field(type = FieldType.Text, name = "nickname", analyzer = "nori")
    private String nickname;

    @Field(type = FieldType.Text, name = "tagline", analyzer = "nori")
    private String tagline;

    @Field(type = FieldType.Keyword, name = "profile_image_url")
    private String profileImageUrl;

    @Field(type = FieldType.Keyword, name = "genres")
    private List<String> genres;

    @Field(type = FieldType.Date, name = "created_at")
    private Instant createdAt;
}
