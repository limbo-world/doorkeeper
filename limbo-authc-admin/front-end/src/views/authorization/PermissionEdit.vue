<template>
    <el-container>
        <el-main>
            <el-form :model="permission" label-width="80px" size="mini" class="edit-form"
                     :rules="rules" ref="editForm" :disabled="!!viewMode">
                <el-form-item label="编码">
                    <el-input v-model="permission.permCode" placeholder="权限编码，用于区分权限的唯一标识，不填写将自动生成"></el-input>
                </el-form-item>
                <el-form-item label="名称" prop="permName" :rules="{required: true, message: '请填写名称', trigger: 'blur'}">
                    <el-input v-model="permission.permName" placeholder="权限名称"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="permission.permDesc" placeholder="权限描述"></el-input>
                </el-form-item>
                <el-form-item label="API列表" >
                    <div class="pointer flex align-center">
                        <el-select v-model="httpMethod" placeholder="请求方式" class="max-width-100 margin-right-xs">
                            <el-option v-for="m in httpMethods" :key="m" :label="m" :value="m"></el-option>
                        </el-select>
                        <el-input v-model="api" placeholder="API路径，支持Ant风格" class="max-width-300"
                                  @keyup.enter.native="addApi" @keyup.up.native="prevHttpMethod"
                                  @keyup.down.native="nextHttpMethod"></el-input>
                        <i class="el-icon-plus margin-left-xs" @click="addApi"></i>
                    </div>
                </el-form-item>
                <el-form-item label=" ">
                    <el-tag v-for="(a, idx) in permission.apiList" :key="a" class="margin-right-xs margin-bottom-xs"
                            closable @close="permission.apiList.splice(idx, 1)">
                        {{a}}
                    </el-tag>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>
    const HttpMethods = [
        'GET', 'POST', 'PUT', 'DELETE'
    ];

    export default {
        props: {
            permission: {
                type: Object,
                default: () => {
                    return {
                        apiList: []
                    };
                }
            },

            viewMode: {
                type: Boolean,
                default: false,
            }
        },

        data() {
            return {
                httpMethods: HttpMethods,
                httpMethod: '',
                api: ''
            };
        },

        created() {
            pages.permissionEdit = this;
        },

        methods: {

            prevHttpMethod() {
                if (this.httpMethod) {
                    let idx = HttpMethods.indexOf(this.httpMethod);
                    this.httpMethod = idx === 0 ? '' : HttpMethods[idx - 1];
                } else {
                    this.httpMethod = HttpMethods[HttpMethods.length - 1];
                }
            },

            nextHttpMethod() {
                if (this.httpMethod) {
                    let idx = HttpMethods.indexOf(this.httpMethod);
                    this.httpMethod = idx === HttpMethods.length - 1 ? '' : HttpMethods[idx + 1];
                } else {
                    this.httpMethod = HttpMethods[0];
                }
            },

            addApi() {
                let api = this.api;
                if (!api) {
                    this.$message.warning('请填写API');
                    return;
                }

                if (this.httpMethod) {
                    api = this.httpMethod + ' ' + api;
                }

                this.permission.apiList.push(api);
                this.api = '';
                this.httpMethod = '';
            },

            clearData() {
                this.permission = {
                    apiList: []
                };

                if (this.$refs.editForm) {
                    this.$refs.editForm.clearValidate();
                }
            },

            savePermission() {
                if (this.viewMode) {
                    return this.$immediate();
                }

                const loading = this.$loading();
                return new Promise((resolve, reject) => {
                    this.$refs.editForm.validate(valid => {
                        if (!valid) {
                            reject();
                            return;
                        }

                        const permission = JSON.parse(JSON.stringify(this.permission));
                        const prom = !permission.isAdd
                            ? this.doUpdatePermission(permission)
                            : this.doAddPermission(permission);
                        prom.then(() => {
                            this.clearData();
                            resolve();
                        }).catch(reject);
                    });
                }).finally(() => loading.close())
            },

            doAddPermission(permission) {
                delete permission.isAdd;
                return this.$ajax.post('/permission', permission);
            },

            doUpdatePermission(permission) {
                return this.$ajax.put(`/permission/${permission.permCode}`, permission);
            },
        }
    }
</script>
