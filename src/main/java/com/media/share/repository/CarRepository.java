package com.media.share.repository;

import com.media.share.model.CarModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CarRepository extends ElasticsearchRepository<CarModel, String> {
}
