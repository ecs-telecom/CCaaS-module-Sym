package com.ecstel.sym.service;

import com.ecstel.sym.vo.BatchInfo;

public interface EvtService {
    boolean getEContact(BatchInfo batchInfo,int days) throws Exception;
    boolean getCInbound(BatchInfo batchInfo,int days) throws Exception;
    boolean getCOutbound(BatchInfo batchInfo,int days) throws Exception;
    boolean getCCampaign(BatchInfo batchInfo,int days) throws Exception;

    boolean setComponentInfo(BatchInfo batchInfo,int days) throws Exception;
}
