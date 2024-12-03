package com.ecstel.sym.collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Document(collection = "events") // MongoDB의 컬렉션 이름
@Data                           // @Getter, @Setter, @ToString, @EqualsAndHashCode 포함
@NoArgsConstructor              // 기본 생성자 생성
@AllArgsConstructor             // 모든 필드를 포함한 생성자 생성
public class realTimeService {
    @Id
    private String id;          // MongoDB 기본 키
    private String event;
    private Date timestamp;
    private int companyId;
    private int tenantId;
    private int agentsLoggedIn;
    private int agentsAcw;
    private int agentsAvailable;
    private int agentsIdle;
    private int agentsUnavailable;
    private int agentsWorking;
    private int averageHandleTime;
    private int averageInqueueTime;
    private int averageSpeedToAnswer;
    private int avgtalktime;
    private int talktime;
    private int averageWrapTime;
    private int campaignId;
    private String campaignName;
    private int contactsActive;
    private int ans;
    private int offer;
    private int ansRate;
    private int contactsQueued;
    private int contactsOutOfSLA;
    private int contactsWithinSLA;
    private int holdTime;
    private Boolean isOutbound;
    private int maxWaitTime;
    private int avgWaitingTime;
    private int mediaTypeId;
    private String mediaTypeName;
    private int waiting;
    private int svcLv;
    private String name;
    private int skillId;
    private int totalContactTime;
    private int dials;
    private int connects;
    private int connectsAHT;
    private int abd;
    private int abandonRate;

}