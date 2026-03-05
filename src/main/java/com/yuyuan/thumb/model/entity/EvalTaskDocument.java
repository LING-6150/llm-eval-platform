package com.yuyuan.thumb.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Document(indexName = "eval_tasks")


public class EvalTaskDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String promptText;

    @Field(type = FieldType.Keyword)
    private String modelName;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Integer)
    private Integer tokenCount;

    @Field(type = FieldType.Long)
    private Long latency;

    @Field(type = FieldType.Text)
    private String resultText;


    @Field(type = FieldType.Keyword)
    private String createTime;
}