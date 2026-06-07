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
@Document(indexName = "creator-index")
public class CreatorDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private Long creatorId;

    @Field(type = FieldType.Keyword)
    private Long userId;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String nickname;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String tagline;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String introduction;

    @Field(type = FieldType.Keyword)
    private String profileImageUrl;

    @Field(type = FieldType.Keyword)
    private List<String> genres;

    @Field(type = FieldType.Long)
    private Long followerCount;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant createdAt;
}
