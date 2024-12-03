package com.ecstel.sym.service;

import com.ecstel.sym.vo.BatchInfo;

public interface InfoService {
    void getU_Service(BatchInfo batchInfo) throws Exception;

    void getU_Campagin(BatchInfo batchInfo) throws Exception;

    void getU_Acw(BatchInfo batchInfo) throws Exception;

    void getU_Agent(BatchInfo batchInfo) throws Exception;

    void getU_Nrsn(BatchInfo batchInfo) throws Exception;

    void getU_WorkingHour(BatchInfo batchInfo) throws Exception;

    void getU_Poc(BatchInfo batchInfo) throws Exception;
}
