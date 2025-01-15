package com.lhm.lhmpicturebackend.api.aliyun;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.lhm.lhmpicturebackend.api.aliyun.model.CreateOutPaintingTaskRequest;
import com.lhm.lhmpicturebackend.api.aliyun.model.CreateOutPaintingTaskResponse;
import com.lhm.lhmpicturebackend.api.aliyun.model.GetOutPaintingTaskResponse;
import com.lhm.lhmpicturebackend.exception.BusinessException;
import com.lhm.lhmpicturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AliYunAiApi {
    // 读取配置文件
    @Value("${aliYunAi.apiKey}")
    private String apiKey;

    // 创建任务地址
    public static final String CREATE_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

    // 查询任务状态
    public static final String GET_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    /**
     * 创建任务
     *
     * @param createOutPaintingTaskRequest
     * @return
     */
    public CreateOutPaintingTaskResponse createOutPaintingTask(CreateOutPaintingTaskRequest createOutPaintingTaskRequest) {
        if (createOutPaintingTaskRequest == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "扩图参数为空");
        }
        if (StrUtil.isBlank(createOutPaintingTaskRequest.getInput().getImageUrl())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "图片 URL 不能为空");
        }

        log.info("开始创建扩图任务，图片 URL: {}", createOutPaintingTaskRequest.getInput().getImageUrl());
        try (HttpResponse httpResponse = HttpRequest.post(CREATE_OUT_PAINTING_TASK_URL)
                .header(Header.AUTHORIZATION, "Bearer " + apiKey)
                .header("X-DashScope-Async", "enable")
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .body(JSONUtil.toJsonStr(createOutPaintingTaskRequest))
                .execute()) {

            // 记录完整的响应信息
            log.info("第三方服务响应: {}", httpResponse.body());

            if (!httpResponse.isOk()) {
                log.error("创建任务失败，图片 URL: {}, 响应: {}", createOutPaintingTaskRequest.getInput().getImageUrl(), httpResponse.body());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
            }

            CreateOutPaintingTaskResponse response = JSONUtil.toBean(httpResponse.body(), CreateOutPaintingTaskResponse.class);
            String errorCode = response.getCode();
            if (StrUtil.isNotBlank(errorCode)) {
                String errorMessage = response.getMessage();
                log.error("AI 扩图失败，图片 URL: {}, errorCode: {}, errorMessage: {}", createOutPaintingTaskRequest.getInput().getImageUrl(), errorCode, errorMessage);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图接口响应异常: " + errorMessage);
            }

            log.info("创建任务成功，taskId: {}", response.getOutput().getTaskId());
            return response;
        } catch (Exception e) {
            log.error("创建任务异常，图片 URL: {}", createOutPaintingTaskRequest.getInput().getImageUrl(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建任务异常");
        }
    }

    /**
     * 查询创建的任务
     *
     * @param taskId
     * @return
     */
    public GetOutPaintingTaskResponse getOutPaintingTask(String taskId) {
        if (StrUtil.isBlank(taskId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务 id 不能为空");
        }

        log.info("开始查询任务状态，taskId: {}", taskId);
        try (HttpResponse httpResponse = HttpRequest.get(String.format(GET_OUT_PAINTING_TASK_URL, taskId))
                .header(Header.AUTHORIZATION, "Bearer " + apiKey)
                .execute()) {

            // 记录完整的响应信息
            log.info("第三方服务响应: {}", httpResponse.body());

            if (!httpResponse.isOk()) {
                log.error("获取任务失败，taskId: {}, 响应: {}", taskId, httpResponse.body());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取任务失败");
            }

            log.info("任务查询成功，taskId: {}", taskId);
            return JSONUtil.toBean(httpResponse.body(), GetOutPaintingTaskResponse.class);
        } catch (Exception e) {
            log.error("任务查询异常，taskId: {}", taskId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "任务查询异常");
        }
    }
}
