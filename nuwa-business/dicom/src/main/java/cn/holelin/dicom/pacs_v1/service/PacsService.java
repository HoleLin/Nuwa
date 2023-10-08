package cn.holelin.dicom.pacs_v1.service;

import cn.holelin.dicom.domain.SimpleUserInfo;
import cn.holelin.dicom.pacs_v1.config.DefaultPacsServerProperties;
import cn.holelin.dicom.pacs_v1.config.PacsProperties;
import cn.holelin.dicom.pacs_v1.config.PacsServerProperties;
import cn.holelin.dicom.pacs_v1.consts.StringConstants;
import cn.holelin.dicom.pacs_v1.domain.PacsBaseConfig;
import cn.holelin.dicom.pacs_v1.domain.PacsPullTaskProperties;
import cn.holelin.dicom.pacs_v1.entity.PullTaskRecord;
import cn.holelin.dicom.pacs_v1.enums.InformationModelEnum;
import cn.holelin.dicom.pacs_v1.enums.PullTaskStateEnum;
import cn.holelin.dicom.pacs_v1.exception.BusinessException;
import cn.holelin.dicom.pacs_v1.request.PacsSearchRequest;
import cn.holelin.dicom.pacs_v1.request.PacsStoreCondition;
import cn.holelin.dicom.pacs_v1.request.PacsStoreRequest;
import cn.holelin.dicom.pacs_v1.response.PacsSearchResponse;
import cn.holelin.dicom.pacs_v1.support.PacsCEchoSupport;
import cn.holelin.dicom.pacs_v1.support.PacsCFindSupport;
import cn.holelin.dicom.pacs_v1.utils.GlobalPullTaskContext;
import cn.holelin.dicom.pacs_v1.utils.PacsHelper;
import cn.holelin.dicom.pacs_v1.utils.SnowflakeUtil;
import cn.holelin.dicom.pacs_v1.utils.ThreadLocalUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


/**
 * @author HoleLin
 */
@Slf4j
@Service
public class PacsService {

    private final PacsProperties pacsProperties;
//    private final MongoTemplate mongoTemplate;
//
//    private final RedissonClient redissonClient;
//    private final NewRedisUtils redisUtils;

//    public PacsService(PacsProperties pacsProperties, MongoTemplate mongoTemplate,
//                       RedissonClient redissonClient, NewRedisUtils redisUtils) {
//        this.pacsProperties = pacsProperties;
//        this.mongoTemplate = mongoTemplate;
//        this.redissonClient = redissonClient;
//        this.redisUtils = redisUtils;
//    }

    public PacsService(PacsProperties pacsProperties) {
        this.pacsProperties = pacsProperties;
    }

    public List<PacsSearchResponse> query(PacsSearchRequest request) {
        DefaultPacsServerProperties defaultConfig = pacsProperties.getDefaultConfig();
        PacsCFindSupport support = new PacsCFindSupport(getConfig(), defaultConfig.getNeedExtendedNegotiation());
        // 获取用户信息
        SimpleUserInfo userInfo = (SimpleUserInfo) ThreadLocalUtils.get(StringConstants.USER_INFO);
        Assert.isTrue(Objects.nonNull(userInfo), "获取用户信息失败");
        List<PacsSearchResponse> result = Lists.newArrayList();
        try {
            result = support.executeWithResult(InformationModelEnum.FIND, PacsHelper.buildFindAttributes(request));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
//        String roleId = userInfo.getRoleId();
//        if (CollUtil.isNotEmpty(result)) {
//            result = result.stream().peek(item -> {
//                String studyInstanceUid = item.getStudyInstanceUid();
//                String seriesInstanceUid = item.getSeriesInstanceUid();
//                Query query = new Query();
//                query.addCriteria(Criteria.where("uploader_role_id").in(roleId)
//                        .and("StudyInstanceUID").is(studyInstanceUid)
//                        .and("SeriesInstanceUID").is(seriesInstanceUid));
//                item.setExist(mongoTemplate.count(query, "dicom_archive") != 0);
//            }).collect(Collectors.toList());
//
//        }
        return result;
    }

    public String storeDicom(PacsStoreRequest request) {
        List<PacsStoreCondition> list = request.getList();
        Assert.isTrue(CollUtil.isNotEmpty(list), "请求列表不能为空");
        // 获取用户信息
        SimpleUserInfo userInfo = (SimpleUserInfo) ThreadLocalUtils.get(StringConstants.USER_INFO);
        Assert.isTrue(Objects.nonNull(userInfo), "获取用户信息失败");
        String roleId = userInfo.getRoleId();
        String username = userInfo.getUsername();
        String telephone = userInfo.getUserId();
        String currentIpAddress = userInfo.getCurrentIpAddress();
        String batchId = null;
        PacsPullTaskProperties pullTaskProperties = pacsProperties.getPullConfig();
        Integer singleUserPullTaskQueueThreshold = pullTaskProperties.getSingleUserPullTaskQueueThreshold();
//        RLock lock = redissonClient.getLock(roleId);
//        try {
//            if (lock.tryLock(1L, TimeUnit.SECONDS)) {
//                batchId = String.valueOf(SnowflakeUtil.genId());
//                Assert.isTrue(list.size() <= singleUserPullTaskQueueThreshold, "任务数量超过最大任务数");
//
//                Number number = (Number) redisUtils.get(USER_PULL_TASK_QUEUE_PREFIX_KEY + roleId);
//                long userTasks;
//                if (Objects.isNull(number)) {
//                    userTasks = 0L;
//                    redisUtils.setKeyWithExpirationTime(USER_PULL_TASK_QUEUE_PREFIX_KEY + roleId, userTasks, 10L, TimeUnit.MINUTES);
//                } else {
//                    userTasks = number.longValue();
//                }
//                Assert.isTrue(userTasks <= singleUserPullTaskQueueThreshold, "队列已满,请稍后再试");
                String finalBatchId = batchId;
                list.forEach(item -> {
                    String seriesInstanceUid = item.getSeriesInstanceUid();
                    String studyInstanceUid = item.getStudyInstanceUid();
                    Assert.isTrue(StringUtils.isNotEmpty(studyInstanceUid) &&
                            StringUtils.isNotEmpty(seriesInstanceUid), "拉取参数错误,必填参数确实");

                    joinPullTaskQueue(roleId, telephone, finalBatchId, item);
                });
//            }
//        } catch (InterruptedException e) {
//            throw new BusinessException("服务器繁忙,请稍后重试");
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }

        return batchId;
    }

//    public PullBatchTaskSummaryInfo hasTaskProcessing() {
//        // 获取用户信息
//        SimpleUserInfo userInfo = (SimpleUserInfo) ThreadLocalUtils.get(StringConstants.USER_INFO);
//        Assert.isTrue(Objects.nonNull(userInfo), "获取用户信息失败");
//        String telephone = userInfo.getUserId();
//        return (PullBatchTaskSummaryInfo) redisUtils.get(HAS_TASK_PREFIX_KEY + telephone);
//    }

    /**
     * 创建任务并添加到拉图队列中
     *
     * @param roleId           角色ID
     * @param telephone        手机号
     * @param batchId          批次ID
     * @param condition        传输的条件
     */
    private void joinPullTaskQueue(String roleId, String telephone, String batchId,
                                   PacsStoreCondition condition) {
        // 构建拉图任务
        PullTaskRecord task = new PullTaskRecord();
        task.setCondition(condition);
        task.setTaskId(String.valueOf(SnowflakeUtil.genId()));
        task.setRoleId(roleId);
        task.setBatchId(batchId);
        task.setTelephone(telephone);
        task.setState(PullTaskStateEnum.WAITING.toString());

        GlobalPullTaskContext.offer(task);
//        mongoTemplate.insert(task);
//        redisUtils.increment(USER_PULL_TASK_QUEUE_PREFIX_KEY + roleId);
    }

//    public PullTaskProcessingResponse queryStoreProcess(QueryPacsStoreProcessingRequest request) {
//        SimpleUserInfo userInfo = (SimpleUserInfo) ThreadLocalUtils.get(StringConstants.USER_INFO);
//        Assert.isTrue(Objects.nonNull(userInfo), "获取用户信息失败");
//        String roleId = userInfo.getRoleId();
//        String batchId = request.getBatchId();
//        Query query = new Query().addCriteria(Criteria.where(PullTaskRecord.BATCH_ID).is(batchId).and(PullTaskRecord.ROLE_ID).is(roleId));
//        List<PullTaskRecord> records = mongoTemplate.find(query, PullTaskRecord.class);
//        Assert.isTrue(CollUtil.isNotEmpty(records), "未查询到传输信息");
//        List<PacsStoreCondition> succeedList = records.stream()
//                .filter(item -> PullTaskStateEnum.SUCCEED.toString().equals(item.getState()))
//                .map(PullTaskRecord::getCondition).collect(Collectors.toList());
//        List<PacsStoreCondition> failedList = records.stream()
//                .filter(item -> PullTaskStateEnum.FAILED.toString().equals(item.getState()))
//                .map(PullTaskRecord::getCondition).collect(Collectors.toList());
//        PullTaskProcessingResponse response = new PullTaskProcessingResponse();
//        response.setBatchSize(records.size());
//        response.setSucceed(succeedList);
//        response.setFailed(failedList);
//        return response;
//    }

    public boolean echoLocalPacs() {
        PacsBaseConfig config = new PacsBaseConfig();
        DefaultPacsServerProperties defaultConfig = pacsProperties.getDefaultConfig();
        config.setRemotePort(defaultConfig.getPort());
        config.setRemoteHostName(defaultConfig.getHostname());
        config.setRemoteAeTitle(defaultConfig.getAet());
        String testEcho = defaultConfig.getAet();
        config.setAeTitle(testEcho);
        config.setDeviceName(testEcho);
        PacsCEchoSupport support = new PacsCEchoSupport(config);
        boolean echo = false;
        try {
            echo = support.echo();
        } catch (Exception e) {
            log.error("连通性测试失败,配置为:{}", config);
            e.printStackTrace();
        }
        return echo;
    }

    public boolean echoRemotePacs() {
        PacsBaseConfig config = new PacsBaseConfig();
        PacsServerProperties remoteConfig = pacsProperties.getRemoteConfig();
        DefaultPacsServerProperties defaultConfig = pacsProperties.getDefaultConfig();
        config.setRemotePort(remoteConfig.getPort());
        config.setRemoteHostName(remoteConfig.getHostname());
        config.setRemoteAeTitle(remoteConfig.getAet());
        String testEcho = defaultConfig.getAet();
        config.setAeTitle(testEcho);
        config.setDeviceName(testEcho);
        PacsCEchoSupport support = new PacsCEchoSupport(config);
        boolean echo = false;
        try {
            echo = support.echo();
        } catch (Exception e) {
            log.error("连通性测试失败,配置为:{}", config);
            e.printStackTrace();
        }
        return echo;
    }

    private PacsBaseConfig getConfig() {
        PacsServerProperties remoteConfig = pacsProperties.getRemoteConfig();
        DefaultPacsServerProperties defaultConfig = pacsProperties.getDefaultConfig();
        PacsBaseConfig config = new PacsBaseConfig();
        config.setRemotePort(remoteConfig.getPort());
        config.setRemoteHostName(remoteConfig.getHostname());
        config.setRemoteAeTitle(remoteConfig.getAet());
        config.setAeTitle(defaultConfig.getAet());
        config.setDeviceName(defaultConfig.getAet());
        return config;
    }
}
