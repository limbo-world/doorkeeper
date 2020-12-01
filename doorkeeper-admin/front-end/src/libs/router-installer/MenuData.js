export const MenuRoute = [{
    "menuCode": "001",
    "menuName": "项目管理",
    "icon": "el-icon-setting",
    "children": [{
        "menuCode": "001001",
        "menuName": "项目列表",
        "icon": "fa fa-project-diagram",
        "route": "/project/project",
        "auth": ""
    }]
}, {
    "menuCode": "002",
    "menuName": "账户管理",
    "icon": "el-icon-lock",
    "children": [{
        "menuCode": "002001",
        "menuName": "账户列表",
        "icon": "el-icon-user",
        "route": "/account/account"
    }]
}, {
    "menuCode": "003",
    "menuName": "授权管理",
    "icon": "el-icon-key",
    "children": [{
        "menuCode": "003001",
        "menuName": "Api列表",
        "icon": "el-icon-connection",
        "route": "/authorization/api"
    }, {
        "menuCode": "003002",
        "menuName": "权限列表",
        "icon": "el-icon-coin",
        "route": "/authorization/permission"
    }, {
        "menuCode": "003003",
        "menuName": "角色列表",
        "icon": "el-icon-postcard",
        "route": "/authorization/role"
    }]
}];
