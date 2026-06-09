package kr.magicbox.search.adapter.out.elasticsearch.document;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@Document(indexName = "release-index", createIndex = false)
public class ReleaseDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private Long releaseId;

    @Field(type = FieldType.Keyword)
    private Long creatorId;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String description;

    @Field(type = FieldType.Keyword)
    private String level;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Long)
    private Long price;

    @Field(type = FieldType.Integer)
    private Integer limitedQuantity;

    @Field(type = FieldType.Keyword)
    private List<String> mediaUrls;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant scheduledAt;

    @Field(type = FieldType.Long)
    private Long likeCount;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant createdAt;
}
