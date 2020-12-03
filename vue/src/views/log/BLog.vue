<template>
    <el-container class="blog-page">

        <el-header class="padding-top-xs" height="50px">
            <el-form :inline="true" size="mini">
                <el-form-item label="项目">
                    <el-select v-model="queryForm.projectId" placeholder="输选择项目" clearable clearable>
                        <el-option v-for="p in projects" :key="p.projectId" :label="p.projectName" :value="p.projectId"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="操作人">
                    <el-select v-model="queryForm.accountId" placeholder="输选择操作人" clearable>
                        <el-option v-for="a in accounts" :key="a.accountId" :label="a.nick" :value="a.accountId"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="操作类型">
                    <el-select v-model="queryForm.type" placeholder="" clearable style="width: 80px">
                        <el-option v-for="t in logTypes.enums" :key="t.value" :label="t.label" :value="t.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="关键字">
                    <el-input v-model="queryForm.keyword" placeholder="输入关键字"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadLogs" size="mini" icon="el-icon-search">查询</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="logs" size="mini">
                <el-table-column label="项目" prop="projectName"></el-table-column>
                <el-table-column label="操作人" prop="accountNick"></el-table-column>
                <el-table-column label="操作类型">
                    <template slot-scope="scope">
                        <span>{{logTypes[scope.row.logType]}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作内容" prop="logName"></el-table-column>
                <el-table-column label="IP" prop="ip"></el-table-column>
                <el-table-column label="操作时间" prop="gmtCreated"></el-table-column>
            </el-table>
        </el-main>

        <el-footer>
            <el-pagination :page-size="queryForm.size" :total="queryForm.total" :current-page.sync="queryForm.current"
                           layout="prev, pager, next" background @current-change="loadLogs">
            </el-pagination>
        </el-footer>
    </el-container>
</template>



<script>
    import { mapActions } from 'vuex';

    const BLogTypes = {
        CREATE: '新增',
        RETRIEVE: '查询',
        UPDATE: '修改',
        DELETE: '删除',
        LOGIN: '登录',
        LOGOUT: '注销',

        enums: [
            { label: '新增', value: 'CREATE' },
            { label: '查询', value: 'RETRIEVE' },
            { label: '修改', value: 'UPDATE' },
            { label: '删除', value: 'DELETE' },
            { label: '登录', value: 'LOGIN' },
            { label: '注销', value: 'LOGOUT' },
        ]
    };

    export default {
        data() {
            return {
                queryForm: {
                    keyword: '',
                    current: 1,
                    size: 10,
                    total: -1,
                },

                projects: [],
                accounts: [],

                logs: [],
                logTypes: BLogTypes,
            };
        },

        created() {
            pages.blog = this;

            this.loadProjects();
            this.loadAccounts();
            this.loadLogs();
        },

        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            loadProjects() {
                this.$ajax.get('/project').then(response => {
                    this.projects = response.data;
                });
            },

            loadAccounts() {
                this.$ajax.get('/account').then(response => {
                    this.accounts = response.data;
                })
            },

            loadLogs() {
                this.startProgress();
                return this.$ajax.get('/b-log', {
                    params: this.queryForm
                }).then(response => {
                    const page = response.data;
                    if (page.total >= 0) {
                        this.queryForm.total = page.total;
                    }

                    this.logs = page.data;
                }).finally(() => this.stopProgress());
            },
        }

    }
</script>




<style></style>
