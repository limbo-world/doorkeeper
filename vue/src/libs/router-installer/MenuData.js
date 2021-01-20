export const MenuRoute = [{
    "menuCode": "001",
    "menuName": "配置管理",
    "icon": "el-icon-setting",
    "children": [{
        "menuCode": "001001",
        "menuName": "域",
        "icon": "fa fa-server",
        "route": "/realm/realm",
        "dk": true
    }, {
        "menuCode": "001002",
        "menuName": "委托方",
        "icon": "fa fa-project-diagram",
        "route": "/client/client",
    }, {
        "menuCode": "001003",
        "menuName": "域角色",
        "icon": "el-icon-c-scale-to-original",
        "route": "/role/role",
    }]
}, {
    "menuCode": "002",
    "menuName": "用户管理",
    "icon": "el-icon-lock",
    "children": [{
        "menuCode": "002001",
        "menuName": "用户",
        "icon": "el-icon-user",
        "route": "/user/user",
    }, {
        "menuCode": "002002",
        "menuName": "用户组",
        "icon": "fa fa-users",
        "route": "/group/group",
    }]
}];
