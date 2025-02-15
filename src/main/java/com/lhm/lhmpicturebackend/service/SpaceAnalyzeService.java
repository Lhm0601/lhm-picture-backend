package com.lhm.lhmpicturebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lhm.lhmpicturebackend.model.dto.space.analyze.*;
import com.lhm.lhmpicturebackend.model.entity.Space;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.lhm.lhmpicturebackend.model.vo.space.analyze.*;

import java.util.List;

public interface SpaceAnalyzeService extends IService<Space> {
    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);

    List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);

    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);

    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);

    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);

    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);

}
