package org.limbo.doorkeeper.core.domain.service;

import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.ApiConstants;
import org.limbo.doorkeeper.api.constants.UriMethod;
import org.limbo.doorkeeper.api.dto.param.query.ResourceCheckParam;
import org.limbo.doorkeeper.core.domain.component.checker.CheckResult;
import org.limbo.doorkeeper.core.domain.component.checker.ResourceChecker;
import org.limbo.doorkeeper.core.domain.constants.IDRelationEnum;
import org.limbo.doorkeeper.core.domain.dto.ResourceQueryDTO;
import org.limbo.doorkeeper.core.domain.entity.IDRelation;
import org.limbo.doorkeeper.core.domain.entity.Permission;
import org.limbo.doorkeeper.core.domain.entity.Uri;
import org.limbo.doorkeeper.core.domain.entity.policy.Policy;
import org.limbo.doorkeeper.core.domain.repository.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yuansheng
 * @since 2022/2/2
 */
@Service
public class ResourceCheckService {

    @Resource
    private LabelRepository labelRepository;

    @Resource
    private ResourceRepository resourceRepository;

    @Resource
    private IDRelationRepository idRelationRepository;

    @Resource
    private PolicyRepository policyRepository;

    @Resource
    private PermissionRepository permissionRepository;

    public CheckResult<List<Long>> check() {
        // 找到需要的资源id
        List<Long> resourceIds = null;
        // 通过资源id找到需要的权限id
        List<IDRelation> resourcePermissionRelations = idRelationRepository.list(resourceIds, IDRelationEnum.RESOURCE_PERMISSION);
        List<Long> permissionIds = null;
        Map<Long, IDRelation> resourcePermissionRelationMap = null;
        if (CollectionUtils.isNotEmpty(resourcePermissionRelations)) {
            permissionIds = resourcePermissionRelations.stream()
                    .map(IDRelation::getRelatedIds)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            resourcePermissionRelationMap = resourcePermissionRelations.stream().collect(Collectors.toMap(IDRelation::getId, idRelation -> idRelation));
        }
        // 通过权限id找到对应需要的策略id
        List<Long> policyIds = null;
        Map<Long, Permission> permissionMap = null;
        List<Permission> permissions = permissionRepository.list(permissionIds);
        if (CollectionUtils.isNotEmpty(permissions)) {
            policyIds = permissions.stream()
                    .map(Permission::getPolicyIds)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            permissionMap = permissions.stream().collect(Collectors.toMap(Permission::getId, permission -> permission));
        }
        // 找到需要的策略
        List<Policy> policies = policyRepository.listByIds(policyIds);
        Map<Long, Policy> policyMap = null;
        if (CollectionUtils.isNotEmpty(policies)) {
            policyMap = policies.stream().collect(Collectors.toMap(Policy::getId, policy -> policy));
        }

        ResourceChecker resourceChecker = new ResourceChecker();
        return resourceChecker.check();
    }

    /**
     * 根据参数获取需要校验的资源
     *
     * @return 返回资源列表
     */
    private List<Long> findResourceIds(Long realmId, Long namespaceId, ResourceCheckParam checkParam) {
        // 获取uri类型label
        List<Long> uriLabelIds = findLabelIdsByUri(realmId, namespaceId, checkParam.getUris());

        // 获取普通label
        List<Long> normalLabelIds = findLabelIds(realmId, namespaceId, checkParam.getAndTags(), checkParam.getOrTags());

        List<Long> resourceIds = Optional.ofNullable(checkParam.getResourceIds()).orElse(new ArrayList<>());
        Optional.ofNullable(uriLabelIds).ifPresent(resourceIds::addAll);
        Optional.ofNullable(normalLabelIds).ifPresent(resourceIds::addAll);

        return resourceRepository.findResourceIds(ResourceQueryDTO.builder()
                .realmId(realmId)
                .namespaceId(namespaceId)
                .resourceIds(resourceIds)
                .names(checkParam.getNames())
                .isEnabled(true)
                .build()
        );
    }

    public List<Long> findLabelIdsByUri(Long realmId, Long namespaceId, List<String> uriStrings) {
        List<Long> uriIds = new ArrayList<>();

        if (CollectionUtils.isEmpty(uriStrings)) {
            return uriIds;
        }

        // client拥有的全部uri资源
        List<Uri> uris = labelRepository.listByRealmAndNamespace(realmId, namespaceId);
        // 根据路径和请求方式，获取资源ID
        // 对于所有的uri 如果匹配 checkParam 其中的某一项 则表示对应资源需要返回
        for (Uri uri : uris) {
            for (String uriString : uriStrings) {
                String requestMethod = UriMethod.ALL.getValue();
                String requestUri;
                if (uriString.contains(ApiConstants.KV_DELIMITER)) {
                    String[] split = uriString.split(ApiConstants.KV_DELIMITER);
                    requestMethod = split[0];
                    requestUri = split[1];
                } else {
                    requestUri = uriString;
                }

                // 判断是否匹配方法和路径
                if (uri.match(UriMethod.parse(requestMethod), requestUri.trim())) {
                    uriIds.add(uri.getId());
                    break;
                }
            }
        }
        return uriIds;
    }

    public List<Long> findLabelIds(Long realmId, Long namespaceId, List<String> andTags, List<String> orTags) {
        return null;
    }
}
