package kr.magicbox.search.adapter.out.elasticsearch.document;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@Document(indexName = "general-goods-index")
@Setting(settingPath = "elasticsearch/general-goods-settings.json")
public class GeneralGoodsDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long, name = "general_goods_id")
    private Long generalGoodsId;

    @Field(type = FieldType.Long, name = "creator_id")
    private Long creatorId;

    @Field(type = FieldType.Text, name = "name", analyzer = "nori")
    private String name;

    @Field(type = FieldType.Long, name = "price")
    private Long price;

    @Field(type = FieldType.Long, name = "stock")
    private Long stock;

    @Field(type = FieldType.Keyword, name = "media_urls")
    private List<String> mediaUrls;

    @Field(type = FieldType.Date, name = "created_at")
    private Instant createdAt;
}
