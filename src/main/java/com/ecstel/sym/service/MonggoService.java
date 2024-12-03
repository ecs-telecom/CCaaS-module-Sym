package com.ecstel.sym.service;

import com.ecstel.sym.collection.realTimeAgent;
import com.ecstel.sym.collection.realTimeService;
import com.ecstel.sym.repository.MonggoRealTimeAgentRepository;
import com.ecstel.sym.repository.MonggoRealTimeServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MonggoService {

    @Autowired
    private MonggoRealTimeServiceRepository serviceRepository;

    @Autowired
    private MonggoRealTimeAgentRepository agentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public realTimeService saveService(realTimeService user) {
        return serviceRepository.save(user); // 데이터 저장
    }

    public realTimeAgent saveAgent(realTimeAgent agent) {
        return agentRepository.save(agent); // 데이터 저장
    }

    public List<realTimeService> getAllService() {
        return serviceRepository.findAll(); // 모든 사용자 조회
    }

    public List<realTimeAgent> getAllAgent() {
        return agentRepository.findAll(); // 모든 사용자 조회
    }

    public List<realTimeService> findService(int companyId, int skillId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("companyId").is(companyId));  // companyId 조건
        query.addCriteria(Criteria.where("skillId").is(skillId));      // skillId 조건

        return mongoTemplate.find(query, realTimeService.class);
    }

    public List<realTimeAgent> findAgent(int companyId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("companyId").is(companyId));  // companyId 조건

        return mongoTemplate.find(query, realTimeAgent.class);
    }




}