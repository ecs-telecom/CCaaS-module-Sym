package com.ecstel.sym.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "events_agent")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class realTimeAgent {

    private Date timestamp;
    @Id
    private String id;
    private int companyId;
    private int contactId;
    private String contactStartHandleTime;
    private Boolean isOutbound;
    private String fromAddress;
    private String lastUpdateTime;
    private String outStateDescription;
    private String outStateId;
    private String sessionStartTime;
    private int skillId;
    private String skillName;
    private String startDate;
    private String TEAM_NAME;
    private int LOGIN_ID;
    private String AGT_NAME;
    private String EXTENSION;
    private String ACD_STATE;
    private String SUB_STATE;
    private int ACD_SKILL;
    private String DIRECTION;
    private String CALL_SUB_STATE;
    private Long STATE_DURATION;
    private int ANS;
    private int ABD;
    private int OBCONN;
    private int LOGIN_TIME;
    private int TALKTIME_IN;
    private int TALKTIME_OUT;
    private int NOTREADY_TIME;
    private Double AVGTALK_TIME;
    private int READY_TIME;

    // toDocument 메서드 추가
    public org.bson.Document toDocument() {
        org.bson.Document doc = new org.bson.Document();
        doc.append("timestamp", this.timestamp);
        doc.append("id", this.id);
        doc.append("companyId", this.companyId);
        doc.append("contactId", this.contactId);
        doc.append("contactStartHandleTime", this.contactStartHandleTime);
        doc.append("isOutbound", this.isOutbound);
        doc.append("fromAddress", this.fromAddress);
        doc.append("lastUpdateTime", this.lastUpdateTime);
        doc.append("outStateDescription", this.outStateDescription);
        doc.append("outStateId", this.outStateId);
        doc.append("sessionStartTime", this.sessionStartTime);
        doc.append("skillId", this.skillId);
        doc.append("skillName", this.skillName);
        doc.append("startDate", this.startDate);
        doc.append("TEAM_NAME", this.TEAM_NAME);
        doc.append("LOGIN_ID", this.LOGIN_ID);
        doc.append("AGT_NAME", this.AGT_NAME);
        doc.append("EXTENSION", this.EXTENSION);
        doc.append("ACD_STATE", this.ACD_STATE);
        doc.append("SUB_STATE", this.SUB_STATE);
        doc.append("ACD_SKILL", this.ACD_SKILL);
        doc.append("DIRECTION", this.DIRECTION);
        doc.append("CALL_SUB_STATE", this.CALL_SUB_STATE);
        doc.append("STATE_DURATION", this.STATE_DURATION);
        doc.append("ANS", this.ANS);
        doc.append("ABD", this.ABD);
        doc.append("OBCONN", this.OBCONN);
        doc.append("LOGIN_TIME", this.LOGIN_TIME);
        doc.append("TALKTIME_IN", this.TALKTIME_IN);
        doc.append("TALKTIME_OUT", this.TALKTIME_OUT);
        doc.append("NOTREADY_TIME", this.NOTREADY_TIME);
        doc.append("AVGTALK_TIME", this.AVGTALK_TIME);
        doc.append("READY_TIME", this.READY_TIME);

        return doc;
    }
}