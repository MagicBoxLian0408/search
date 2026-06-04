package kr.magicbox.search.adapter.out.elasticsearch.document;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@Document(indexName = "release-index")
@Setting(settingPath = "elasticsearch/release-settings.json")
public class ReleaseDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long, name = "release_id")
    private Long releaseId;

    @Field(type = FieldType.Long, name = "creator_id")
    private Long creatorId;

    @Field(type = FieldType.Text, name = "title", analyzer = "nori")
    private String title;

    @Field(type = FieldType.Text, name = "description", analyzer = "nori")
    private String description;

    @Field(type = FieldType.Keyword, name = "level")
    private String level;

    @Field(type = FieldType.Long, name = "price")
    private Long price;

    @Field(type = FieldType.Integer, name = "limited_quantity")
    private Integer limitedQuantity;

    @Field(type = FieldType.Keyword, name = "media_urls")
    private List<String> mediaUrls;

    @Field(type = FieldType.Date, name = "scheduled_at")
    private Instant scheduledAt;

    @Field(type = FieldType.Date, name = "created_at")
    private Instant createdAt;
}
