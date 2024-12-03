package com.ecstel.sym.service;

import com.ecstel.sym.vo.BatchInfo;

public interface StatService {

    boolean getEAgentStatus(BatchInfo batchInfo,int days) throws Exception;

    boolean getStatHService(BatchInfo batchInfo,int days) throws Exception;

    boolean getStatHAgentStatus(BatchInfo batchInfo,int days) throws Exception;

    boolean getStatHAgent(BatchInfo batchInfo,int days) throws Exception;

    boolean getStatHAgentInbound(BatchInfo batchInfo,int days) throws Exception;

    boolean getStatHAgentOutbound(BatchInfo batchInfo,int days) throws Exception;

}
