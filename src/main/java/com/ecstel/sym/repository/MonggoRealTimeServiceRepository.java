package com.ecstel.sym.repository;

import com.ecstel.sym.collection.realTimeService;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonggoRealTimeServiceRepository extends MongoRepository<realTimeService, String> {
    realTimeService findByName(String name); // 이름으로 사용자 검색
}

