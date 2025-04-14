package com.lazai.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lazai.biz.service.ActionHandler;
import com.lazai.biz.service.TaskService;
import com.lazai.entity.ScoreBalance;
import com.lazai.entity.TaskAction;
import com.lazai.entity.TaskRecord;
import com.lazai.entity.TaskTemplate;
import com.lazai.entity.dto.*;
import com.lazai.entity.vo.TaskRecordVO;
import com.lazai.entity.vo.TaskTemplateVO;
import com.lazai.exception.DomainException;
import com.lazai.repostories.ScoreBalanceRepository;
import com.lazai.repostories.TaskActionRepository;
import com.lazai.repostories.TaskRecordRepository;
import com.lazai.repostories.TaskTemplateRepository;
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

import java.util.*;
import java.util.stream.Collectors;

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
    private SnowFlake snowFlake;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ScoreBalanceRepository scoreBalanceRepository;

    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERROR_LOG");


    public String createTask(TaskCreateRequest request){
        return transactionTemplateCommon.execute(transactionStatus -> {
            TaskTemplate taskTemplate = taskTemplateRepository.selectByCode(request.getTemplateCode());
            if(taskTemplate == null){
                throw new DomainException("template not fund", 404);
            }
            JSONObject templateContent = JSON.parseObject(taskTemplate.getContent());
            if("ONCE".equals(taskTemplate.getProcessType())){
                TaskRecordQueryParam taskRecordQueryParamTmp = new TaskRecordQueryParam();
                taskRecordQueryParamTmp.setInnerUser(request.getInnerUserId());
                taskRecordQueryParamTmp.setOuterUser(request.getOuterUserId());
                taskRecordQueryParamTmp.setApp(request.getApp());
                taskRecordQueryParamTmp.setStatusList(Arrays.asList("processing","finish"));
                taskRecordQueryParamTmp.setTaskTemplateId(taskTemplate.getTemplateCode());
                List<TaskRecord> existsTask = taskRecordRepository.queryList(taskRecordQueryParamTmp);
                if(!CollectionUtils.isEmpty(existsTask)){
                    return existsTask.get(0).getTaskNo();
                }
            }

            if("DAILY".equals(taskTemplate.getProcessType())){
                TaskRecordQueryParam taskRecordQueryParamTmp = new TaskRecordQueryParam();
                taskRecordQueryParamTmp.setInnerUser(request.getInnerUserId());
                taskRecordQueryParamTmp.setOuterUser(request.getOuterUserId());
                taskRecordQueryParamTmp.setApp(request.getApp());
                taskRecordQueryParamTmp.setStatusList(Arrays.asList("processing","finish"));
                taskRecordQueryParamTmp.setTaskTemplateId(taskTemplate.getTemplateCode());
                taskRecordQueryParamTmp.setCreatedStart(DateUtils.getStartOfToday());
                taskRecordQueryParamTmp.setCreatedEnd(DateUtils.getEndOfToday());
                List<TaskRecord> existsTask = taskRecordRepository.queryList(taskRecordQueryParamTmp);
                if(!CollectionUtils.isEmpty(existsTask)){
                    return existsTask.get(0).getTaskNo();
                }
            }

            if("DAILY_TIMES".equals(taskTemplate.getProcessType())){
                TaskRecordQueryParam taskRecordQueryParamTmp = new TaskRecordQueryParam();
                taskRecordQueryParamTmp.setInnerUser(request.getInnerUserId());
                taskRecordQueryParamTmp.setOuterUser(request.getOuterUserId());
                taskRecordQueryParamTmp.setApp(request.getApp());
                taskRecordQueryParamTmp.setStatusList(Arrays.asList("processing","finish"));
                taskRecordQueryParamTmp.setTaskTemplateId(taskTemplate.getTemplateCode());
                taskRecordQueryParamTmp.setCreatedStart(DateUtils.getStartOfToday());
                taskRecordQueryParamTmp.setCreatedEnd(DateUtils.getEndOfToday());
                List<TaskRecord> existsTask = taskRecordRepository.queryList(taskRecordQueryParamTmp);
                if(!CollectionUtils.isEmpty(existsTask) && existsTask.size() >= templateContent.getJSONObject("context").getInteger("dailyTimes")){
                   throw new DomainException("This task can only be triggered " + templateContent.getJSONObject("context").getInteger("dailyTimes") + " times a day. ",403);
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
                    taskActionSingle.setStatus("init");
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

    public JSONObject triggerTask(TriggerTaskRequest request){
        JSONObject result = new JSONObject();
        result.put("isTaskFinish", false);
        final TaskRecord[] taskRecord = {taskRecordRepository.selectByTaskNo(request.getTaskNo(), false)};
        if(taskRecord[0] == null){
            throw new DomainException("task not found",404);
        }
        if(taskRecord[0].getStatus().equals("finish")){
            result.put("isTaskFinish", true);
            return result;
        }
        JSONObject taskRecordContent = JSON.parseObject(taskRecord[0].getContent());
        List<TaskRecordNodeDTO> taskRecordNodeDTOList = JSON.parseArray(taskRecordContent.getString("nodes"), TaskRecordNodeDTO.class);
        List<TaskRecordNodeDTO> notProcessNodes = taskRecordNodeDTOList.stream().filter(a->!a.getStatus().equals("finish")).toList();
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
                currentNode.setStatus("processing");
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
                        if(actionSingle.getStatus().equals("finish")){
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
                                        actionSingle.setStatus("finish");
                                        taskActionRepository.updateById(actionSingle);
                                    }catch (Throwable e){
                                        ERROR_LOGGER.error("trigger action error", e);
                                        throw new DomainException("trigger action error", 500);
                                    }
                                });
                            }catch (Throwable e){
                                actionSingle.setStatus("error");
                                taskActionRepository.updateById(actionSingle);
                                throw e;
                            }
                        }else {
                            //TODO
                        }
                    }
                    transactionTemplateCommon.executeWithoutResult(transactionStatus -> {
                        taskRecord[0] = taskRecordRepository.selectByTaskNo(request.getTaskNo(), true);
                        currentNode.setStatus("finish");
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
                taskRecord[0].setStatus("finish");
                taskRecordRepository.updateByTaskNo(taskRecord[0]);
            });
        }
        return result;
    }

    public void createAndTriggerTask(TaskCreateRequest request){
            String taskNo = createTask(request);
            TriggerTaskRequest triggerTaskRequest = new TriggerTaskRequest();
            triggerTaskRequest.setTaskNo(taskNo);
            triggerTaskRequest.setOperator(request.getOperator());
            triggerTask(triggerTaskRequest);
    }


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
                if(scoreBalanceMap.containsKey(taskRecordVOSingle.getTaskNo())){
                    taskRecordVOSingle.setScoreInfo(JSON.parseObject(JSON.toJSONString(scoreBalanceMap.get(taskRecordVOSingle.getTaskNo()))));
                }
            }
        }
        return result;
    }

    public List<TaskTemplateVO> userTaskTemplatesUse(TaskQueryRequest taskQueryRequest){
        List<TaskTemplateVO> result = new ArrayList<>();
        Map<String, TaskTemplateVO> taskTemplateVOMap = new HashMap<>();
        TaskRecordQueryParam taskRecordQueryParam = toQueryParam(taskQueryRequest);
        List<TaskRecord> taskRecords = taskRecordRepository.queryList(taskRecordQueryParam);
        TaskTemplateQueryParam taskTemplateQueryParam = new TaskTemplateQueryParam();
        taskTemplateQueryParam.setTemplateCodes(taskQueryRequest.getTemplateCodes());
        List<TaskTemplate> taskTemplateList = taskTemplateRepository.queryList(taskTemplateQueryParam);
        Map<String, TaskTemplate> taskTemplateMap = taskTemplateList.stream().collect(Collectors.toMap(TaskTemplate::getTemplateCode, b->b));
        if(!CollectionUtils.isEmpty(taskRecords)){
            for(TaskRecord taskRecord:taskRecords){
                TaskTemplate taskTemplate = taskTemplateMap.get(taskRecord.getTaskTemplateId());
                TaskTemplateVO taskTemplateVOSingle = null;
                if(!taskTemplateVOMap.containsKey(taskRecord.getTaskTemplateId())){
                    taskTemplateVOSingle = new TaskTemplateVO();
                    taskTemplateVOSingle.setTaskName(taskTemplate.getTemplateName());
                    taskTemplateVOSingle.setTaskTemplateId(taskRecord.getTaskTemplateId());
                    taskTemplateVOSingle.setTaskFinishCount(0);
                    taskTemplateVOSingle.setTaskCount(0);
                    taskTemplateVOSingle.setTaskType(taskTemplate.getProcessType());
                    taskTemplateVOMap.put(taskRecord.getTaskTemplateId(), taskTemplateVOSingle);
                }else{
                    taskTemplateVOSingle = taskTemplateVOMap.get(taskRecord.getTaskTemplateId());
                }
                if("ONCE".equals(taskTemplate.getProcessType())){
                    taskTemplateVOSingle.setTaskCount(taskTemplateVOSingle.getTaskCount()+1);
                    if("finish".equals(taskRecord.getStatus())){
                        taskTemplateVOSingle.setTaskFinishCount(taskTemplateVOSingle.getTaskFinishCount()+1);
                    }
                }else if("DAILY".equals(taskTemplate.getProcessType()) || "DAILY_TIMES".equals(taskTemplate.getProcessType())){
                   Date endOfToday = DateUtils.getEndOfToday();
                   Date startOfToday = DateUtils.getStartOfToday();
                   JSONObject templateContent = JSON.parseObject(taskTemplate.getContent());
                   JSONObject taskTemplateVOSingleContent = new JSONObject();
                   if(templateContent.getJSONObject("context").getInteger("dailyTimes") != null){
                       taskTemplateVOSingleContent.put("dailyTimesLimit", templateContent.getJSONObject("context").getInteger("dailyTimes"));
                   }
                   if(taskRecord.getCreatedAt().getTime() <= endOfToday.getTime() && taskRecord.getCreatedAt().getTime() >= startOfToday.getTime()){
                       taskTemplateVOSingle.setTaskCount(taskTemplateVOSingle.getTaskCount()+1);
                       if("finish".equals(taskRecord.getStatus())){
                           taskTemplateVOSingle.setTaskFinishCount(taskTemplateVOSingle.getTaskFinishCount()+1);
                       }
                   }
                    taskTemplateVOSingle.setContent(taskTemplateVOSingleContent);
                }
            }
            result = taskTemplateVOMap.values().stream().toList();
        }
        return result;
    }

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

    public TaskRecord convertToTaskRecord(String taskNo, TaskCreateRequest taskCreateRequest, List<TaskRecordNodeDTO> taskRecordNodeDTOList, JSONObject contextTemp, TaskTemplate taskTemplate){
        TaskRecord taskRecord = new TaskRecord();
        taskRecord.setStatus("processing");
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

    public static List<TaskRecordVO> toTaskRecordVOs(List<TaskRecord> taskRecords){
        List<TaskRecordVO> result = new ArrayList<>();
        if(!CollectionUtils.isEmpty(taskRecords)){
            for(TaskRecord taskRecord:taskRecords){
                TaskRecordVO taskRecordVO = toTaskRecordVO(taskRecord);
                result.add(taskRecordVO);
            }
        }
        return result;
    }

    public static TaskRecordVO toTaskRecordVO(TaskRecord taskRecord){
        TaskRecordVO taskRecordVO = new TaskRecordVO();
        taskRecordVO.setId(taskRecord.getId());
        taskRecordVO.setTaskNo(taskRecord.getTaskNo());
        taskRecordVO.setTaskTemplateId(taskRecord.getTaskTemplateId());
        taskRecordVO.setContent(taskRecord.getContent());
        taskRecordVO.setContext(taskRecord.getContext());
        taskRecordVO.setCreator(taskRecord.getCreator());
        taskRecordVO.setOperator(taskRecord.getOperator());
        taskRecordVO.setVersion(taskRecord.getVersion());
        taskRecordVO.setCreatedAt(taskRecord.getCreatedAt());
        taskRecordVO.setStatus(taskRecord.getStatus());
        taskRecordVO.setUpdatedAt(taskRecord.getUpdatedAt());
        taskRecordVO.setInnerUser(taskRecord.getInnerUser());
        taskRecordVO.setOuterUser(taskRecord.getOuterUser());
        taskRecordVO.setApp(taskRecord.getApp());
        JSONObject contentObj = JSON.parseObject(taskRecord.getContent());
        taskRecordVO.setTaskName(contentObj.getString("taskName"));
        return taskRecordVO;

    }

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
            actionSingle.put("status","init");
        }
        taskRecordNodeDTO.setActions(actions);
        JSONObject context = new JSONObject();
        taskRecordNodeDTO.setContext(context);
        taskRecordNodeDTO.setStatus("init");
        return taskRecordNodeDTO;
    }

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
