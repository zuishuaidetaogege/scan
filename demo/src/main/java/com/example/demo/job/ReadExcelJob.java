package com.example.demo.job;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.file.excel.listener.ReadExcelListener;
import com.diboot.file.util.ExcelHelper;
import com.diboot.file.util.FileHelper;
import com.diboot.scheduler.annotation.CollectThisJob;
import com.example.demo.excel.listener.IamUserImportExcelListener;
import com.example.demo.excel.model.IamUserImportModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;

/**
 * 数据同步任务
 */
@Slf4j
@DisallowConcurrentExecution
@CollectThisJob(name = "Excel数据读取", cron = "0 0 2 * * ?")
public class ReadExcelJob extends QuartzJobBean {

    @Autowired(required = false)
    private RestTemplate restTemplate;



    @Transactional(rollbackFor = Exception.class)
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {


        readExcel("user.excel",new IamUserImportExcelListener());

    }

    private boolean readExcel(String path,ReadExcelListener<?> listener){
        try {
            EasyExcel.read(path).registerReadListener(listener).head(listener.getExcelModelClass()).sheet().doRead();
        } catch (Exception e) {
            log.warn("解析excel文件失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw e;
        }

        String errorDataFilePath = listener.getErrorDataFilePath();
        if (V.isEmpty(errorDataFilePath)) {
            return true;
        }
        // copy 错误·文件 errorDataFilePath


        return false;
    }

}
