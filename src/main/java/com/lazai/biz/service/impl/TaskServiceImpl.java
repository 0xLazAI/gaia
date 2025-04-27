package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.ActionHandler;
import com.lazai.biz.service.TaskService;
import com.lazai.biz.service.TgSingleService;
import com.lazai.entity.*;
import com.lazai.entity.dto.*;
import com.lazai.entity.vo.TaskRecordVO;
import com.lazai.entity.vo.TaskTemplateVO;
import com.lazai.enums.TaskActionStatusEnum;
import com.lazai.enums.TaskRecordNodeStatusEnum;
import com.lazai.enums.TaskStatusEnum;
import com.lazai.enums.TaskTemplateProcessTypeEnum;
import com.lazai.exception.DomainException;
import com.lazai.repostories.*;
import com.lazai.request.TaskCreateRequest;
import com.lazai.request.TaskQueryRequest;
import com.lazai.request.TriggerTaskRequest;
import com.lazai.utils.DateUtils;
import com.lazai.utils.JsonUtils;
import com.lazai.utils.SnowFlake;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @see TaskService
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRecordRepository taskRecordRepository;

    @Autowired
    private TaskActionRepository taskActionRepository;

    @Autowired
    private TransactionTemplate transactionTemplateCommon;

    @Autowired
    private TaskTemplateRepository taskTemplateRepository;

    @Autowired
    private TgSingleService tgSingleService;

    @Autowired
    private SnowFlake snowFlake;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ScoreBalanceRepository scoreBalanceRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERROR_LOG");

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * @see TaskService#createTask(TaskCreateRequest)
     */
    @Override
    public String createTask(TaskCreateRequest request){
        return transactionTemplateCommon.execute(transactionStatus -> {
            TaskTemplate taskTemplate = taskTemplateRepository.selectByCode(request.getTemplateCode());
            if(taskTemplate == null){
                throw new DomainException("template not fund", 404);
            }
            JSONObject templateContent = JSON.parseObject(taskTemplate.getContent());
            if(TaskTemplateProcessTypeEnum.ONCE.value().equals(taskTemplate.getProcessType())){
                TaskRecordQueryParam taskRecordQueryParamTmp = new TaskRecordQueryParam();
                taskRecordQueryParamTmp.setInnerUser(request.getInnerUserId());
                taskRecordQueryParamTmp.setOuterUser(request.getOuterUserId());
                taskRecordQueryParamTmp.setApp(request.getApp());
                taskRecordQueryParamTmp.setStatusList(Arrays.asList(TaskStatusEnum.INIT.value(), TaskStatusEnum.PROCESSING.value(),TaskStatusEnum.FINISH.value()));
                taskRecordQueryParamTmp.setTaskTemplateId(taskTemplate.getTemplateCode());
                List<TaskRecord> existsTask = taskRecordRepository.queryList(taskRecordQueryParamTmp);
                if(!CollectionUtils.isEmpty(existsTask)){
                    return existsTask.get(0).getTaskNo();
                }
            }

            if(TaskTemplateProcessTypeEnum.DAILY.value().equals(taskTemplate.getProcessType())){
                TaskRecordQueryParam taskRecordQueryParamTmp = new TaskRecordQueryParam();
                taskRecordQueryParamTmp.setInnerUser(request.getInnerUserId());
                taskRecordQueryParamTmp.setOuterUser(request.getOuterUserId());
                taskRecordQueryParamTmp.setApp(request.getApp());
                taskRecordQueryParamTmp.setStatusList(Arrays.asList(TaskStatusEnum.INIT.value(), TaskStatusEnum.PROCESSING.value(),TaskStatusEnum.FINISH.value()));
                taskRecordQueryParamTmp.setTaskTemplateId(taskTemplate.getTemplateCode());
                taskRecordQueryParamTmp.setCreatedStart(DateUtils.getStartOfToday());
                taskRecordQueryParamTmp.setCreatedEnd(DateUtils.getEndOfToday());
                List<TaskRecord> existsTask = taskRecordRepository.queryList(taskRecordQueryParamTmp);
                if(!CollectionUtils.isEmpty(existsTask)){
                    return existsTask.get(0).getTaskNo();
                }
            }

            if(TaskTemplateProcessTypeEnum.DAILY_TIMES.value().equals(taskTemplate.getProcessType())){
                TaskRecordQueryParam taskRecordQueryParamTmp = new TaskRecordQueryParam();
                taskRecordQueryParamTmp.setInnerUser(request.getInnerUserId());
                taskRecordQueryParamTmp.setOuterUser(request.getOuterUserId());
                taskRecordQueryParamTmp.setApp(request.getApp());
                taskRecordQueryParamTmp.setStatusList(Arrays.asList(TaskStatusEnum.INIT.value(), TaskStatusEnum.PROCESSING.value(),TaskStatusEnum.FINISH.value()));
                taskRecordQueryParamTmp.setTaskTemplateId(taskTemplate.getTemplateCode());
                taskRecordQueryParamTmp.setCreatedStart(DateUtils.getStartOfToday());
                taskRecordQueryParamTmp.setCreatedEnd(DateUtils.getEndOfToday());
                List<TaskRecord> existsTask = taskRecordRepository.queryList(taskRecordQueryParamTmp);
                if(!CollectionUtils.isEmpty(existsTask) && existsTask.size() >= templateContent.getJSONObject("context").getInteger("dailyTimes")){
                   List<TaskRecord> processingTask = existsTask.stream().filter(a->!a.getStatus().equals(TaskStatusEnum.FINISH.value())).toList();
                   if(CollectionUtils.isEmpty(processingTask)){
                       throw new DomainException("This task can only be triggered " + templateContent.getJSONObject("context").getInteger("dailyTimes") + " times a day. ",403);
                   }else{
                       return processingTask.get(0).getTaskNo();
                   }
                }
            }

            JSONArray nodesJSONArray = templateContent.getJSONArray("nodes");
            JSONObject contextTemp = templateContent.getJSONObject("context");
            int i = 0;
            String taskNo = "task_" + snowFlake.nextId();
            List<TaskRecordNodeDTO> taskRecordNodeDTOS = new ArrayList<>();
            for (Object nodeSingle:nodesJSONArray){
                TaskTemplateNodeDTO templateNodeDTO = JSON.parseObject(JSON.toJSONString(nodeSingle), TaskTemplateNodeDTO.class);
                TaskRecordNodeDTO taskRecordNodeDTO = convertToTaskRecordNodeDTO(templateNodeDTO);
                for(JSONObject actionSingle: taskRecordNodeDTO.getActions()){
                    TaskAction taskActionSingle = new TaskAction();
                    taskActionSingle.setActionName("");
                    taskActionSingle.setBizNo(taskNo);
                    taskActionSingle.setBizType("task");
                    taskActionSingle.setStatus(TaskActionStatusEnum.INIT.value());
                    taskActionSingle.setRunType(actionSingle.getString("runType"));
                    taskActionSingle.setActionHandler(actionSingle.getString("actionHandler"));
                    taskActionSingle.setOperator(request.getOperator().getId());
                    taskActionSingle.setCreator(request.getOperator().getId());
                    taskActionSingle.setUser(StringUtils.isBlank(request.getInnerUserId())?"":request.getInnerUserId());
                    taskActionSingle.setContent(JSON.toJSONString(actionSingle));
                    taskActionRepository.insert(taskActionSingle);
                }
                if(i == 0){
                    taskRecordNodeDTO.setStatus("processing");
                }
                i++;
                taskRecordNodeDTOS.add(taskRecordNodeDTO);
            }
            var taskRecord = convertToTaskRecord(taskNo, request, taskRecordNodeDTOS, contextTemp, taskTemplate);
            taskRecordRepository.insert(taskRecord);
            return taskRecord.getTaskNo();
        });
    }

    /**
     * @see TaskService#triggerTask
     */
    @Override
    public JSONObject triggerTask(TriggerTaskRequest request){
        JSONObject result = new JSONObject();
        result.put("isTaskFinish", false);
        final TaskRecord[] taskRecord = {taskRecordRepository.selectByTaskNo(request.getTaskNo(), false)};
        if(taskRecord[0] == null){
            throw new DomainException("task not found",404);
        }
        if(taskRecord[0].getStatus().equals(TaskStatusEnum.FINISH.value())){
            result.put("isTaskFinish", true);
            return result;
        }
        JSONObject taskRecordContent = JSON.parseObject(taskRecord[0].getContent());
        List<TaskRecordNodeDTO> taskRecordNodeDTOList = JSON.parseArray(taskRecordContent.getString("nodes"), TaskRecordNodeDTO.class);
        List<TaskRecordNodeDTO> notProcessNodes = taskRecordNodeDTOList.stream().filter(a->!a.getStatus().equals(TaskRecordNodeStatusEnum.FINISH.value())).toList();
        Boolean isFinalNode = false;
        if(notProcessNodes.size() == 1){
            isFinalNode = true;
        }
        TaskRecordNodeDTO currentNode;
        if(!CollectionUtils.isEmpty(notProcessNodes)){
            currentNode = notProcessNodes.get(0);
        } else {
            currentNode = null;
        }
        if(currentNode != null){
            transactionTemplateCommon.executeWithoutResult(transactionStatus -> {
                taskRecord[0] = taskRecordRepository.selectByTaskNo(request.getTaskNo(), true);
                currentNode.setStatus(TaskRecordNodeStatusEnum.PROCESSING.value());
                taskRecordContent.put("nodes", taskRecordNodeDTOList);
                taskRecord[0].setContent(JSON.toJSONString(taskRecordContent));
                taskRecordRepository.updateByTaskNo(taskRecord[0]);
            });
            if(!CollectionUtils.isEmpty(notProcessNodes)){
                if(CollectionUtils.isEmpty(currentNode.getSubNodes())){
                    //trigger actions
                    List<TaskAction> taskActions = taskActionRepository.queryByBizNoAndBizType(taskRecord[0].getTaskNo(), "task");
                    JSONObject taskContent = JSON.parseObject(taskRecord[0].getContent());
                    for(TaskAction actionSingle:taskActions){
                        final ActionHandler[] actionHandler = {null};
                        if(actionSingle.getStatus().equals(TaskActionStatusEnum.FINISH.value())){
                            continue;
                        }
                        if("local".equals(actionSingle.getRunType())){
                            try{
                                transactionTemplateCommon.executeWithoutResult(transactionStatus -> {
                                    try{
                                        taskRecord[0] = taskRecordRepository.selectByTaskNo(request.getTaskNo(), true);
                                        Class<?> clazz = Class.forName(actionSingle.getActionHandler());
                                        actionHandler[0] =  (ActionHandler)applicationContext.getBean(clazz);
                                        JSONObject triggerResult =  actionHandler[0].handle(JSON.parseObject(taskRecord[0].getContext()));
                                        if(triggerResult!=null){
                                            JsonUtils.mergeJsonObjects(taskContent, triggerResult);
                                        }
                                        actionSingle.setStatus(TaskActionStatusEnum.FINISH.value());
                                        taskActionRepository.updateById(actionSingle);
                                    }catch (Throwable e){
                                        ERROR_LOGGER.error("trigger action error", e);
                                        throw new DomainException("trigger action error", 500);
                                    }
                                });
                            }catch (Throwable e){
                                actionSingle.setStatus(TaskActionStatusEnum.ERROR.value());
                                taskActionRepository.updateById(actionSingle);
                                throw e;
                            }
                        }else {
                            //TODO
                        }
                    }
                    transactionTemplateCommon.executeWithoutResult(transactionStatus -> {
                        taskRecord[0] = taskRecordRepository.selectByTaskNo(request.getTaskNo(), true);
                        currentNode.setStatus(TaskRecordNodeStatusEnum.FINISH.value());
                        taskRecordContent.put("nodes", taskRecordNodeDTOList);
                        taskRecord[0].setContent(JSON.toJSONString(taskRecordContent));
                        taskRecordRepository.updateByTaskNo(taskRecord[0]);
                    });
                }else{//TODO

                }
            }
        }

        if(isFinalNode){
            result.put("isTaskFinish", true);
            transactionTemplateCommon.executeWithoutResult(transactionStatus -> {
                taskRecord[0] = taskRecordRepository.selectByTaskNo(request.getTaskNo(), true);
                taskRecord[0].setStatus(TaskStatusEnum.FINISH.value());
                taskRecordRepository.updateByTaskNo(taskRecord[0]);
            });
        }
        return result;
    }

    /**
     * verify task is done
     * @param user
     * @param templateContext
     */
    public void verifyTaskDone(User user, JSONObject templateContext){

        if("tgGroupJoin".equals(templateContext.getString("scoreType")) && StringUtils.isNotBlank(user.getTgId())){
            Boolean isJoin = false;
            isJoin = tgSingleService.ifUserInGroup(user.getTgId(), templateContext.getString("botToken"), templateContext.getString("groupId"));
            if(!isJoin){
                throw new DomainException("not join tg group!",403);
            }
        }
    }

    /**
     * @see TaskService#claimTask
     */
    @Override
    public void claimTask(TaskCreateRequest request){
        TaskTemplate taskTemplate = taskTemplateRepository.selectByCode(request.getTemplateCode());
        if(taskTemplate == null){
            throw new DomainException("template not fund", 404);
        }
        User user = userRepository.findById(request.getInnerUserId(), false);
        JSONObject templateContent = JSON.parseObject(taskTemplate.getContent());
        JSONObject templateContext = templateContent.getJSONObject("context");
        verifyTaskDone(user, templateContext);
        createAndTriggerTask(request);
    }

    /**
     * @see TaskService#createAndTriggerTask
     */
    @Override
    public void createAndTriggerTask(TaskCreateRequest request){
            String taskNo = createTask(request);
            TriggerTaskRequest triggerTaskRequest = new TriggerTaskRequest();
            triggerTaskRequest.setTaskNo(taskNo);
            triggerTaskRequest.setOperator(request.getOperator());
            triggerTask(triggerTaskRequest);
    }

    /**
     * @see TaskService#userTaskRecords
     */
    @Override
    public List<TaskRecordVO> userTaskRecords(TaskQueryRequest taskQueryRequest){
        TaskRecordQueryParam taskRecordQueryParam = toQueryParam(taskQueryRequest);
        List<TaskRecord> taskRecords = taskRecordRepository.queryList(taskRecordQueryParam);
        List<TaskRecordVO> result = toTaskRecordVOs(taskRecords);
        if(!CollectionUtils.isEmpty(result)){
            List<String> taskNos = result.stream().map(TaskRecordVO::getTaskNo).toList();
            ScoreBalanceQueryParam scoreBalanceQueryParam = new ScoreBalanceQueryParam();
            scoreBalanceQueryParam.setBizIds(taskNos);
            //scoreBalanceQueryParam.setBizType("task");
            List<ScoreBalance> scoreBalanceList = scoreBalanceRepository.queryList(scoreBalanceQueryParam);
            Map<String, ScoreBalance> scoreBalanceMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(scoreBalanceList)){
                scoreBalanceMap = scoreBalanceList.stream().collect(Collectors.toMap(ScoreBalance::getBizId, b->b));
            }
            for(TaskRecordVO taskRecordVOSingle:result){
                List<JSONObject> scoreInfoList = taskRecordVOSingle.getScoreInfo();
                if(scoreBalanceMap.containsKey(taskRecordVOSingle.getTaskNo())){
                    scoreInfoList.add(JSON.parseObject(JSON.toJSONString(scoreBalanceMap.get(taskRecordVOSingle.getTaskNo()))));
                }
            }
        }
        return result;
    }

    /**
     * @see TaskService#userTaskTemplatesUse
     */
    @Override
    public List<TaskTemplateVO> userTaskTemplatesUse(TaskQueryRequest taskQueryRequest){
        List<TaskTemplateVO> result = new ArrayList<>();
        TaskRecordQueryParam taskRecordQueryParam = toQueryParam(taskQueryRequest);
        List<TaskRecord> taskRecords = taskRecordRepository.queryList(taskRecordQueryParam);
        TaskTemplateQueryParam taskTemplateQueryParam = new TaskTemplateQueryParam();
        taskTemplateQueryParam.setTemplateCodes(taskQueryRequest.getTemplateCodes());
        List<TaskTemplate> taskTemplateList = taskTemplateRepository.queryList(taskTemplateQueryParam);
        Map<String, TaskTemplateVO> taskTemplateVOMap = taskTemplateList.stream().collect(Collectors.toMap(TaskTemplate::getTemplateCode, this::toTaskTemplateVO));
        if(!CollectionUtils.isEmpty(taskRecords)){
            for(TaskRecord taskRecord:taskRecords){
                TaskTemplateVO taskTemplateVOSingle = taskTemplateVOMap.get(taskRecord.getTaskTemplateId());;
                if(TaskTemplateProcessTypeEnum.ONCE.value().equals(taskTemplateVOSingle.getTaskType())){
                    taskTemplateVOSingle.setTaskCount(taskTemplateVOSingle.getTaskCount()+1);
                    if(TaskStatusEnum.FINISH.value().equals(taskRecord.getStatus())){
                        taskTemplateVOSingle.setTaskFinishCount(taskTemplateVOSingle.getTaskFinishCount()+1);
                    }
                }else if(TaskTemplateProcessTypeEnum.DAILY.value().equals(taskTemplateVOSingle.getTaskType()) || TaskTemplateProcessTypeEnum.DAILY_TIMES.value().equals(taskTemplateVOSingle.getTaskType())){
                   Date endOfToday = DateUtils.getEndOfToday();
                   Date startOfToday = DateUtils.getStartOfToday();
                   if(taskRecord.getCreatedAt().getTime() <= endOfToday.getTime() && taskRecord.getCreatedAt().getTime() >= startOfToday.getTime()){
                       taskTemplateVOSingle.setTaskCount(taskTemplateVOSingle.getTaskCount()+1);
                       if(TaskStatusEnum.FINISH.value().equals(taskRecord.getStatus())){
                           taskTemplateVOSingle.setTaskFinishCount(taskTemplateVOSingle.getTaskFinishCount()+1);
                       }
                   }
                }
            }

        }
        result = taskTemplateVOMap.values().stream().toList();
        return result;
    }

    /**
     * to TaskTemplateVO
     * @param taskTemplate
     * @return
     */
    public TaskTemplateVO toTaskTemplateVO(TaskTemplate taskTemplate){
        TaskTemplateVO taskTemplateVO = new TaskTemplateVO();

        taskTemplateVO.setTaskType(taskTemplate.getProcessType());
        taskTemplateVO.setTaskName(taskTemplate.getTemplateName());
        taskTemplateVO.setTaskTemplateId(taskTemplate.getTemplateCode());
        taskTemplateVO.setTaskFinishCount(0);
        taskTemplateVO.setTaskCount(0);
        JSONObject taskTemplateVOSingleContent = new JSONObject();
        JSONObject templateContent = JSON.parseObject(taskTemplate.getContent());
        if(TaskTemplateProcessTypeEnum.DAILY.value().equals(taskTemplate.getProcessType()) || TaskTemplateProcessTypeEnum.DAILY_TIMES.value().equals(taskTemplate.getProcessType())){
            if(templateContent.getJSONObject("context").getInteger("dailyTimes") != null){
                taskTemplateVOSingleContent.put("dailyTimesLimit", templateContent.getJSONObject("context").getInteger("dailyTimes"));
            }
        }
        if(StringUtils.isNotBlank(templateContent.getJSONObject("context").getString("twitterName"))){
            taskTemplateVOSingleContent.put("twitterName", templateContent.getJSONObject("context").getString("twitterName"));
        }
        taskTemplateVO.setContent(taskTemplateVOSingleContent);

        return taskTemplateVO;

    }

    /**
     * convert to TaskRecordQueryParam
     * @param request
     * @return
     */
    public TaskRecordQueryParam toQueryParam(TaskQueryRequest request){
        TaskRecordQueryParam taskRecordQueryParam = new TaskRecordQueryParam();
        taskRecordQueryParam.setCreatedStart(request.getCreatedStartAt());
        taskRecordQueryParam.setCreatedEnd(request.getCreatedEndAt());
        taskRecordQueryParam.setTaskTemplateIds(request.getTemplateCodes());
        taskRecordQueryParam.setInnerUser(request.getInnerPlatformUserId());
        taskRecordQueryParam.setOuterUser(request.getBizUserId());
        taskRecordQueryParam.setApp(request.getAppToken());
        taskRecordQueryParam.setStatusList(request.getStatus());
        taskRecordQueryParam.setTaskNos(request.getTaskNos());
        return taskRecordQueryParam;

    }

    /**
     * convert to TaskRecord
     * @param taskNo
     * @param taskCreateRequest
     * @param taskRecordNodeDTOList
     * @param contextTemp
     * @param taskTemplate
     * @return
     */
    public TaskRecord convertToTaskRecord(String taskNo, TaskCreateRequest taskCreateRequest, List<TaskRecordNodeDTO> taskRecordNodeDTOList, JSONObject contextTemp, TaskTemplate taskTemplate){
        TaskRecord taskRecord = new TaskRecord();
        taskRecord.setStatus(TaskStatusEnum.PROCESSING.value());
        taskRecord.setTaskNo(taskNo);
        taskRecord.setTaskTemplateId(taskCreateRequest.getTemplateCode());
        JSONObject content = new JSONObject();
        content.put("nodes", taskRecordNodeDTOList);
        taskRecord.setContent(JSON.toJSONString(content));
        contextTemp.put("taskName", taskTemplate.getTemplateName());
        contextTemp.put("innerUserId", taskCreateRequest.getInnerUserId());
        contextTemp.put("outerUserId", taskCreateRequest.getOuterUserId());
        contextTemp.put("taskNo", taskNo);
        taskRecord.setContext(JSON.toJSONString(contextTemp));
        taskRecord.setOuterUser(taskCreateRequest.getOuterUserId());
        taskRecord.setInnerUser(taskCreateRequest.getInnerUserId());
        taskRecord.setApp(taskCreateRequest.getApp());
        taskRecord.setOperator(taskCreateRequest.getOperator().getId());
        taskRecord.setCreator(taskCreateRequest.getOperator().getId());
        taskRecord.setVersion(0);
        return taskRecord;
    }

    /**
     * to TaskRecordVO list
     * @param taskRecords
     * @return
     */
    public List<TaskRecordVO> toTaskRecordVOs(List<TaskRecord> taskRecords){
        List<TaskRecordVO> result = new ArrayList<>();
        if(!CollectionUtils.isEmpty(taskRecords)){
            for(TaskRecord taskRecord:taskRecords){
                TaskRecordVO taskRecordVO = toTaskRecordVO(taskRecord);
                result.add(taskRecordVO);
            }
        }
        return result;
    }

    /**
     * to TaskRecordVO
     * @param taskRecord
     * @return
     */
    public  TaskRecordVO toTaskRecordVO(TaskRecord taskRecord){
        TaskRecordVO taskRecordVO = new TaskRecordVO();
        taskRecordVO.setId(taskRecord.getId());
        taskRecordVO.setTaskNo(taskRecord.getTaskNo());
        taskRecordVO.setTaskTemplateId(taskRecord.getTaskTemplateId());
        taskRecordVO.setContent(taskRecord.getContent());
        taskRecordVO.setContext(taskRecord.getContext());
        taskRecordVO.setCreator(taskRecord.getCreator());
        taskRecordVO.setOperator(taskRecord.getOperator());
        taskRecordVO.setVersion(taskRecord.getVersion());
        taskRecordVO.setCreatedAt(simpleDateFormat.format(taskRecord.getCreatedAt()));
        taskRecordVO.setStatus(taskRecord.getStatus());
        taskRecordVO.setUpdatedAt(simpleDateFormat.format(taskRecord.getUpdatedAt()));
        taskRecordVO.setInnerUser(taskRecord.getInnerUser());
        taskRecordVO.setOuterUser(taskRecord.getOuterUser());
        taskRecordVO.setApp(taskRecord.getApp());
        JSONObject contextObj = JSON.parseObject(taskRecord.getContext());
        taskRecordVO.setTaskName(contextObj.getString("taskName"));
        return taskRecordVO;

    }

    /**
     * convert to TaskRecordNodeDTO
     * @param taskTemplateNodeDTO
     * @return
     */
    public static TaskRecordNodeDTO convertToTaskRecordNodeDTO(TaskTemplateNodeDTO taskTemplateNodeDTO){
        TaskRecordNodeDTO taskRecordNodeDTO = new TaskRecordNodeDTO();
        taskRecordNodeDTO.setOrgType(taskTemplateNodeDTO.getOrgType());
        taskRecordNodeDTO.setRelation(taskTemplateNodeDTO.getRelation());
        taskRecordNodeDTO.setSubNodes(convertToTaskRecordNodeDTOList(taskTemplateNodeDTO.getSubNodes()));
        taskRecordNodeDTO.setNodeId(taskTemplateNodeDTO.getNodeId());
        taskRecordNodeDTO.setTriggerType(taskTemplateNodeDTO.getTriggerType());
        taskRecordNodeDTO.setPlatform(taskTemplateNodeDTO.getPlatform());
        taskRecordNodeDTO.setBizType(taskTemplateNodeDTO.getBizType());
        taskRecordNodeDTO.setContent(taskTemplateNodeDTO.getContent());
        List<JSONObject> actions = taskTemplateNodeDTO.getActions();
        for(JSONObject actionSingle: actions){
            actionSingle.put("status",TaskActionStatusEnum.INIT.value());
        }
        taskRecordNodeDTO.setActions(actions);
        JSONObject context = new JSONObject();
        taskRecordNodeDTO.setContext(context);
        taskRecordNodeDTO.setStatus(TaskRecordNodeStatusEnum.INIT.value());
        return taskRecordNodeDTO;
    }

    /**
     * convert to TaskRecordNodeDTO list
     * @param taskTemplateNodeDTOList
     * @return
     */
    public static List<TaskRecordNodeDTO> convertToTaskRecordNodeDTOList(List<TaskTemplateNodeDTO> taskTemplateNodeDTOList){
        List<TaskRecordNodeDTO> result = new ArrayList<>();
        if(CollectionUtils.isEmpty(taskTemplateNodeDTOList)){
            return result;
        }
        for(TaskTemplateNodeDTO taskTemplateNodeDTO:taskTemplateNodeDTOList){
            TaskRecordNodeDTO single = convertToTaskRecordNodeDTO(taskTemplateNodeDTO);
            result.add(single);
        }

        return result;
    }

}
