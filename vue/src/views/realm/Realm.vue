<template>
    <el-container class="realm-page">
        <el-main>
            <el-table :data="realms" size="mini">
                <el-table-column prop="realmId" label="ID"></el-table-column>
                <el-table-column prop="name" label="名称"></el-table-column>
                <el-table-column label="secret">
                    <template slot-scope="scope">
                        <div class="operations" v-if="scope.row.secret">
                            <span>{{scope.row.secret}}</span>
                            <el-link type="primary" @click="hideSecret(scope.$index)">隐藏</el-link>
                        </div>
                        <div class="operations" v-else>
                            <span>******</span>
                            <el-link type="primary" @click="getSecret(scope.row.realmId, scope.$index)">显示</el-link>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <div class="operations">
                            <template>
                                <i @click="()=>{realm={realmId: scope.row.realmId};dialogOpened = true}" class="el-icon-edit"></i>
                            </template>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>


        <el-dialog title="修改" :visible.sync="dialogOpened" width="50%" class="edit-dialog" :before-close="preventCloseWhenProcessing">
            <el-form :model="realm" label-width="80px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="secret">
                    <el-input v-model="realm.secret"></el-input>
                </el-form-item>
            </el-form>
            <el-footer class="text-right">
                <el-button @click="() => {realm = {}; dialogOpened = false;}" :disabled="dialogProcessing">取 消</el-button>
                <el-button type="primary" @click="updateRealm" :loading="dialogProcessing"
                           :disabled="dialogProcessing">确 定</el-button>
            </el-footer>
        </el-dialog>

    </el-container>
</template>



<script>
    import { mapState, mapActions } from 'vuex';

    export default {
        data() {
            return {
                realms: [],
                realm: {},

                dialogOpened: false,
                dialogProcessing: false,
            }
        },

        computed: {
            ...mapState('session', ['user']),
        },

        created() {
            pages.client = this;

            this.loadRealms();
        },

        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            loadRealms() {
                this.startProgress();
                this.$ajax.get(`/admin/realm`).then(response => {
                    this.realms = response.data;
                }).finally(() => this.stopProgress());
            },

            preventCloseWhenProcessing() {
                if (this.dialogProcessing) {
                    return false;
                }

                this.realm = {};
                this.dialogOpened = false;
            },

            updateRealm() {
                const loading = this.$loading();
                this.$ajax.put(`/admin/realm/${this.realm.realmId}`, this.realm).then(response => {
                    this.preventCloseWhenProcessing()
                    this.loadRealms();
                }).finally(() => loading.close());
            },

            getSecret(realmId, idx) {
                this.$ajax.get(`/admin/realm/${realmId}`).then(response => {
                    Vue.set(this.realms, idx, response.data);
                });
            },

            hideSecret(idx) {
                let realm = this.realms[idx];
                realm.secret = null;
                Vue.set(this.realms, idx, realm);
            },
        }

    }
</script>

<style lang="scss">
    .project-page {
        .el-table {
            .cell {
                min-height: 22px;
            }
        }

        .edit-dialog {
            .el-dialog {
                min-width: 500px;
            }
        }
    }
</style>
