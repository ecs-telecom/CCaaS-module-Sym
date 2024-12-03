package com.ecstel.sym.service.bp;

import com.ecstel.sym.base.define;
import com.ecstel.sym.config.NiceApiUrlConfig;
import com.ecstel.sym.httpClient.ApiService;
import com.ecstel.sym.mapper.ccaas.CCaaSDbMapper;
import com.ecstel.sym.mapper.ecp.EcpDbMapper;
import com.ecstel.sym.service.base.BaseStatService;
import com.ecstel.sym.utils.DataMap;
import com.ecstel.sym.utils.DateUtils;
import com.ecstel.sym.utils.StringUtil;
import com.ecstel.sym.vo.BatchInfo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component("bpStatService")
@RequiredArgsConstructor
public class StatService extends BaseStatService implements com.ecstel.sym.service.StatService {

    private static final Logger log = LoggerFactory.getLogger(StatService.class);

    @Autowired
    private ApiService apiService; // ApiService 주입

    @Autowired
    private NiceApiUrlConfig NiceApiUrlConfig; // ApiService 주입

    private final CCaaSDbMapper CCaaSMapper;
    private final EcpDbMapper EcpMapper;

    @Override
    @Transactional
    public boolean getEAgentStatus(BatchInfo batchInfo,int days) throws Exception {
        return true;
    }

    @Override
    @Transactional
    public boolean getStatHService(BatchInfo batchInfo,int days) throws Exception {

        return true;


    }

    @Override
    @Transactional
    public boolean getStatHAgentStatus(BatchInfo batchInfo,int days) throws Exception {
        return true;

    }

    @Override
    @Transactional
    public boolean getStatHAgent(BatchInfo batchInfo,int days) throws Exception {
            return true;
    }

    @Override
    @Transactional
    public boolean  getStatHAgentInbound(BatchInfo batchInfo,int days) throws Exception {
        return true;

    }

    @Override
    @Transactional
    public boolean getStatHAgentOutbound(BatchInfo batchInfo,int days) throws Exception {
        return true;

    }
}
