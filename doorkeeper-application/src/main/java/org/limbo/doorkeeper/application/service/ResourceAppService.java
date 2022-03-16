package org.limbo.doorkeeper.application.service;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.limbo.doorkeeper.api.constants.ApiConstants;
import org.limbo.doorkeeper.api.constants.UriMethod;
import org.limbo.doorkeeper.api.dto.param.query.ResourceCheckParam;
import org.limbo.doorkeeper.api.dto.vo.ResourceVO;
import org.limbo.doorkeeper.core.component.checker.CheckResult;
import org.limbo.doorkeeper.core.component.checker.ResourceChecker;
import org.limbo.doorkeeper.core.domian.aggregate.Permission;
import org.limbo.doorkeeper.core.domian.aggregate.Uri;
import org.limbo.doorkeeper.core.domian.policy.Policy;
import org.limbo.doorkeeper.core.repository.*;
import org.limbo.doorkeeper.infrastructure.dao.ResourceDao;
import org.limbo.doorkeeper.infrastructure.dto.PermissionResourceDTO;
import org.limbo.doorkeeper.infrastructure.dto.ResourceQueryCommand;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yuansheng
 * @since 2022/2/2
 */
@Service
public class ResourceAppService {

    @Resource
    private LabelRepository labelRepository;

    @Resource
    private ResourceRepository resourceRepository;

    @Resource
    private UriRepository uriRepository;

    @Resource
    private PolicyRepository policyRepository;

    @Resource
    private PermissionRepository permissionRepository;

    @Resource
    private ResourceDao resourceDao;

    /**
     * 根据查询
     *
     * @return
     */
    public List<ResourceVO> check() {
        // 找到需要的资源id
        List<Long> resourceIds = findResourceIds(null, null, null); // todo
        // 通过资源id找到需要的权限id
        List<PermissionResourceDTO> resourcePermissionRelations = Optional.ofNullable(resourceDao.listPermissionIdsByResourceIds(resourceIds))
                .orElse(Collections.emptyList());
        List<Long> permissionIds = resourcePermissionRelations.stream()
                .map(PermissionResourceDTO::getPermissionId)
                .collect(Collectors.toList());
        Map<Long, List<Long>> resourcePermissionRelationMap = resourcePermissionRelations.stream()
                .collect(Collectors.toMap(PermissionResourceDTO::getPermissionId,
                        permissionResource -> Lists.newArrayList(permissionResource.getPermissionId()),
                        (longs, longs2) -> {
                            longs.addAll(longs2);
                            return longs;
                        }));
        // 通过权限id找到对应需要的策略id
        List<Permission> permissionAggregates = Optional.ofNullable(permissionRepository.list(permissionIds))
                .orElse(Collections.emptyList());
        List<Long> policyIds = permissionAggregates.stream()
                .map(Permission::getPolicyIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        Map<Long, Permission> permissionMap = permissionAggregates.stream()
                .collect(Collectors.toMap(Permission::getId, permissionAggregate -> permissionAggregate));
        // 找到需要的策略
        List<Policy> policies = Optional.ofNullable(policyRepository.listByIds(policyIds))
                .orElse(Collections.emptyList());
        Map<Long, Policy> policyMap = policies.stream().collect(Collectors.toMap(Policy::getId, policy -> policy));

        ResourceChecker resourceChecker = new ResourceChecker();
        CheckResult<List<Long>> check = resourceChecker.check();
        return null;
    }

    /**
     * 根据参数获取需要校验的资源ID
     *
     * @return 返回资源列表
     */
    public List<Long> findResourceIds(Long realmId, Long namespaceId, ResourceCheckParam checkParam) {
        // 获取uri类型label
        List<Long> uriLabelIds = findLabelIdsByUri(realmId, namespaceId, checkParam.getUris());

        // 获取普通label
        List<Long> normalLabelIds = findLabelIds(realmId, namespaceId, checkParam.getAndTags(), checkParam.getOrTags());

        List<Long> resourceIds = Optional.ofNullable(checkParam.getResourceIds()).orElse(new ArrayList<>());
        Optional.ofNullable(uriLabelIds).ifPresent(resourceIds::addAll);
        Optional.ofNullable(normalLabelIds).ifPresent(resourceIds::addAll);

        return resourceDao.findResourceIds(ResourceQueryCommand.builder()
                .realmId(realmId)
                .namespaceId(namespaceId)
                .resourceIds(resourceIds)
                .names(checkParam.getNames())
                .isEnabled(true) // todo 参数传递过来
                .build()
        );
    }

    public List<Long> findLabelIdsByUri(Long realmId, Long namespaceId, List<String> uriStrings) {
        List<Long> uriIds = new ArrayList<>();

        if (CollectionUtils.isEmpty(uriStrings)) {
            return uriIds;
        }

        // client拥有的全部uri资源
        List<Uri> uris = uriRepository.listByRealmAndNamespace(realmId, namespaceId);
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
