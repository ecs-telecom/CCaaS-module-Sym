package com.ecstel.sym.service;

import com.ecstel.sym.vo.BatchInfo;

public interface RealTimeService {
    void getRealTimeAgt(BatchInfo batchInfo) throws Exception;

    void getRealTimeService(BatchInfo batchInfo) throws Exception;

    void getAgtServiceMap(BatchInfo batchInfo) throws Exception;

    void getRealTimeCampaign(BatchInfo batchInfo) throws Exception;

}
