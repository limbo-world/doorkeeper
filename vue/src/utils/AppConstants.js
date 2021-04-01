/*
 * Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   	http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

const policyTypes = [
    {label: '角色', value: 'ROLE'},
    {label: '用户', value: 'USER'},
    {label: '参数', value: 'PARAM'},
    {label: '用户组', value: 'GROUP'},
];

const intentions = [
    {label: '允许', value: 'ALLOW'},
    {label: '拒绝', value: 'REFUSE'}
];

const httpMethod = [
    {label: 'GET', value: "GET"},
    {label: 'POST', value: "POST"},
    {label: 'PUT', value: "PUT"},
    {label: 'DELETE', value: "DELETE"},
    {label: 'HEAD', value: "HEAD"},
    {label: 'OPTIONS', value: "OPTIONS"},
    {label: 'TRACE', value: "TRACE"},
    {label: 'CONNECT', value: "CONNECT"},
]

const batchMethod = {
    GET: 'GET',
    SAVE: 'SAVE',
    UPDATE: 'UPDATE',
    DELETE: 'DELETE'
}

const enableTypes = [
    {label: '已启用', value: true},
    {label: '未启用', value: false},
];

const logics = [
    {label: '满足所有', value: 'ALL'},
    {label: '满足一个', value: 'ONE'},
    {label: '满足大于不满足', value: 'MORE'},
];

export default {
    policyTypes,
    intentions,
    batchMethod,
    httpMethod,
    enableTypes,
    logics,
}
