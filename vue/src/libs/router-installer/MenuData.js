export const MenuRoute = [{
    "menuCode": "001",
    "menuName": "权限管理",
    "icon": "el-icon-setting",
    "children": [{
        "menuCode": "001001",
        "menuName": "委托方",
        "icon": "fa fa-project-diagram",
        "route": "/client/client",
    },{
        "menuCode": "001002",
        "menuName": "域角色",
        "icon": "el-icon-c-scale-to-original",
        "route": "/role/role",
    }]
}, {
    "menuCode": "002",
    "menuName": "账户管理",
    "icon": "el-icon-lock",
    "children": [{
        "menuCode": "002001",
        "menuName": "账户列表",
        "icon": "el-icon-user",
        "route": "/account/account",
    }]
}];
