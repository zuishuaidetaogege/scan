package com.example.demo.job;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.parser.EntityInfoCache;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.D;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.iam.entity.*;
import com.diboot.iam.mapper.IamUserPositionMapper;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.vo.IamUserVO;
import com.diboot.scheduler.annotation.CollectThisJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据同步任务
 */
@DisallowConcurrentExecution
@CollectThisJob(name = "数据同步", cron = "0 0 2 * * ?")
public class DataSyncJob extends QuartzJobBean {


    @Value("${diboot.iam.data-url}")
    private String dataUrl;
    @Value("${diboot.iam.oauth2-client.client-id}")
    private String clientId;
    @Value("${diboot.iam.oauth2-client.client-secret}")
    private String clientSecret;

    @Autowired(required = false)
    private RestTemplate restTemplate;

    @Autowired
    private IamUserRoleService userRoleService;

    @Autowired
    private IamUserPositionMapper userPositionMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        String afterDate = null;
        Trigger trigger = jobExecutionContext.getTrigger();
        long startTime = trigger.getStartTime().getTime();
        Date nextFireTime = trigger.getNextFireTime();
        if (nextFireTime != null) {
            long lastDate = startTime - (nextFireTime.getTime() - startTime);
            afterDate = D.convert2FormatString(new Date(lastDate), D.FORMAT_DATETIME_Y4MDHMS);
        }

        // 组织数据
        saveData(IamOrg.class, getData("org", afterDate).getData());
        // 角色数据
        saveData(IamRole.class, getData("role", afterDate).getData());
        // 岗位数据
        saveData(IamPosition.class, getData("position", afterDate).getData());

        // 用户数据
        Map<String, Object> data = getData("user", afterDate).getData();
        Map<String, List> userData = (Map<String, List>) data.get("user");
        saveData(IamUser.class, userData);
        List<IamUserVO> userVOList = JSON.parseArray(JSON.toJSONString(userData.get("update")), IamUserVO.class);
        for (IamUserVO iamUserVO : userVOList) {
            // 批量更新角色关联关系
            List<IamRole> roleList = iamUserVO.getRoleList();
            if (V.isEmpty(roleList)) {
                continue;
            }
            List<Long> roleIds = roleList.stream().map(BaseEntity::getId).collect(Collectors.toList());
            String userTypeName = IamUser.class.getSimpleName();
            userRoleService.createOrUpdateN2NRelations(IamUserRole::getUserId, iamUserVO.getId(), IamUserRole::getRoleId, roleIds,
                    q -> q.lambda().eq(IamUserRole::getUserType, userTypeName), e -> e.setUserType(userTypeName));
        }

        // 用户账号数据
        Map<String, List> accountData = (Map<String, List>) data.get("account");
        saveData(IamAccount.class, accountData);

        // 用户岗位数据
        Map<String, List> userPositionData = (Map<String, List>) data.get("userPosition");
        saveData(IamUserPosition.class, userPositionData,
                query -> userPositionMapper.selectList(query), list -> userPositionMapper.deleteBatchIds(list),
                list -> list.forEach(userPositionMapper::insert), list -> list.forEach(userPositionMapper::updateById));

    }

    /**
     * 保持数据
     *
     * @param typeClass
     * @param data
     * @param <T>
     */
    private <T extends BaseEntity> void saveData(Class<T> typeClass, Map<String, List> data) {
        EntityInfoCache entityInfoByClass = BindingCacheManager.getEntityInfoByClass(typeClass);
        IService<T> service = entityInfoByClass.getService();
        saveData(typeClass, data,
                query -> service.list(query), list -> service.removeBatchByIds(list),
                list -> service.saveBatch(list), list -> service.updateBatchById(list));
    }

    /**
     * 保存数据
     *
     * @param typeClass
     * @param data
     * @param getList
     * @param removeBatchByIds
     * @param saveBatch
     * @param updatedBatch
     * @param <T>
     */
    private <T extends BaseEntity> void saveData(Class<T> typeClass, Map<String, List> data,
                                                 Function<Wrapper<T>, List<T>> getList, Consumer<List<Long>> removeBatchByIds,
                                                 Consumer<List<T>> saveBatch, Consumer<List<T>> updatedBatch) {
        // 删除数据
        List<Object> delIds = (List<Object>) data.get("delete");
        if (V.notEmpty(delIds)) {
            removeBatchByIds.accept(delIds.stream().map(e -> Long.valueOf(e.toString())).collect(Collectors.toList()));
        }
        // 更新数据
        List<T> dataList = JSON.parseArray(JSON.toJSONString(data.get("update")), typeClass);
        if (V.isEmpty(dataList)) {
            return;
        }
        // 数据id列表
        List<Long> ids = dataList.stream().map(BaseEntity::getId).collect(Collectors.toList());
        // 已存在数据id列表
        List<T> oldList = getList.apply(Wrappers.lambdaQuery(typeClass).select(BaseEntity::getId).in(BaseEntity::getId, ids));
        List<Long> oldIds = oldList.stream().map(BaseEntity::getId).collect(Collectors.toList());

        if (ids.size() == oldIds.size()) {
            updatedBatch.accept(dataList);
        } else if (oldIds.isEmpty()) {
            saveBatch.accept(dataList);
        } else {
            // 分离出新增与更新数据
            List<T> insertDatas = new ArrayList<>();
            List<T> updatedDatas = new ArrayList<>();
            for (T obj : dataList) {
                if (oldIds.contains(obj.getId())) {
                    updatedDatas.add(obj);
                } else {
                    insertDatas.add(obj);
                }
            }
            saveBatch.accept(insertDatas);
            updatedBatch.accept(updatedDatas);
        }
    }

    /**
     * 获取数据
     *
     * @param type
     * @param afterDate
     * @return
     */
    private JsonResult<Map> getData(String type, String afterDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        byte[] authorization = (clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8);
        String base64Auth = Base64.getEncoder().encodeToString(authorization);
        headers.add("auth-client", "Basic " + base64Auth);

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("afterDate", afterDate);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
        return restTemplate.postForEntity(dataUrl + type, request, JsonResult.class).getBody();
    }

}
