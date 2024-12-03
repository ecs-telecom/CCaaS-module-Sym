package com.ecstel.sym.repository;

import com.ecstel.sym.collection.realTimeAgent;
import com.ecstel.sym.collection.realTimeService;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonggoRealTimeAgentRepository extends MongoRepository<realTimeAgent, String> {
}

