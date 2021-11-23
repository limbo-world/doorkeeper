1. cache抽象
2. dao层
5. core，用户依赖此包，即可做各种操作
6. springboot start
7. 组的概念，用户组、角色组、资源组
8. init操作
9. 资源和用户、角色、用户组的正反方向查询
10. policy关联表可以变为一张，根据类型找具体数据
11. tag等可能导致死锁的情况

RealmDO
List<UserDO>
List<ClientDO>
List<GroupDO>
List<RoleDO>

UserDO
List<GroupDO>
List<RoleDO>

GroupDO
List<RoleDO>

RoleDO
List<UserDo>
List<GroupDo>

ClientDO
List<Resource>
List<Policy>
List<Permission>

Resource
List<Tag>
List<Uri>
List<Children>

Policy

Permission
List<Resource>
List<Policy>
