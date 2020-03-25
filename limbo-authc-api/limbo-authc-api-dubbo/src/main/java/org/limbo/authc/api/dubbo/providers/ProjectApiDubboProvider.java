package org.limbo.authc.api.dubbo.providers;

import org.apache.dubbo.config.annotation.Service;
import org.limbo.authc.api.interfaces.apis.ProjectApi;
import org.limbo.authc.api.interfaces.beans.Page;
import org.limbo.authc.api.interfaces.beans.Response;
import org.limbo.authc.api.interfaces.beans.po.ProjectPO;
import org.limbo.authc.api.interfaces.beans.vo.ProjectVO;
import org.limbo.authc.core.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author devil
 * @date 2020/3/10
 */
@Service(version = "${dubbo.service.version}", validation = "true")
public class ProjectApiDubboProvider implements ProjectApi {

    @Autowired
    private ProjectService projectService;

    @Override
    public Response<ProjectVO> add(ProjectVO.AddParam param) {
        return Response.ok(projectService.addProject(param, true));
    }

    @Override
    public Response<ProjectVO> prepareAdd(ProjectVO.AddParam param) {
        return Response.ok(projectService.addProject(param, false));
    }

    @Override
    public Response<Boolean> commitAdd(ProjectVO.AddParam param) {
        // 更新激活状态
        ProjectPO project = new ProjectPO();
        project.setProjectId(param.getProjectId());
        project.setIsActivated(true);
        projectService.updateById(project);
        return Response.ok(true);
    }

    @Override
    public Response<List<ProjectVO>> list() {
        return Response.ok(projectService.listProject());
    }

    @Override
    public Response<Page<ProjectVO>> query(ProjectVO.QueryParam param) {
        return Response.ok(projectService.queryProjectPage(param));
    }

    @Override
    public Response<ProjectVO> get(ProjectVO.GetParam param) {
        return Response.ok(projectService.get(param.getProjectId()));
    }

    @Override
    public Response<String> getSecret(ProjectVO.GetParam param) {
        ProjectPO project = projectService.getById(param.getProjectId());
        return Response.ok(project.getProjectSecret());
    }

    @Override
    public Response<Integer> update(ProjectVO.UpdateParam param) {
        return Response.ok(projectService.updateProject(param));
    }

    @Override
    public Response<ProjectVO> delete(ProjectVO.DeleteParam param) {
        return Response.ok(projectService.deleteProject(param.getProjectId()));
    }
}
