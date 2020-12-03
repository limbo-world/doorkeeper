export const MenuRoute = [{
    "menuCode": "001",
    "menuName": "项目管理",
    "icon": "el-icon-setting",
    "children": [{
        "menuCode": "001001",
        "menuName": "项目列表",
        "icon": "fa fa-project-diagram",
        "route": "/project/project",
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
        // "auth": "{role.1000002}"
    }]
}, {
    "menuCode": "003",
    "menuName": "授权管理",
    "icon": "el-icon-key",
    "children": [{
        "menuCode": "003001",
        "menuName": "权限列表",
        "icon": "el-icon-coin",
        "route": "/authorization/permission",
        // "auth": "{role.1000004}"
    }, {
        "menuCode": "003002",
        "menuName": "角色列表",
        "icon": "el-icon-postcard",
        "route": "/authorization/role",
        // "auth": "{role.1000003}"
    }]
}];
