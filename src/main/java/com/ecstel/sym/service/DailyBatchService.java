package com.ecstel.sym.service;

import com.ecstel.sym.vo.BatchInfo;

public interface DailyBatchService {
    void getDailyBatch(BatchInfo batchInfo,String ymd) throws Exception;

}
