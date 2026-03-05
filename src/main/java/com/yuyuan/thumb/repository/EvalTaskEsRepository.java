package com.yuyuan.thumb.repository;

import com.yuyuan.thumb.model.entity.EvalTaskDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvalTaskEsRepository extends ElasticsearchRepository<EvalTaskDocument, String> {
    List<EvalTaskDocument> findByModelName(String modelName);
    List<EvalTaskDocument> findByStatus(String status);
    List<EvalTaskDocument> findByUserId(Long userId);
}